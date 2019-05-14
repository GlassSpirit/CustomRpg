package rpgloot.entities

import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.Minecraft
import com.teamwizardry.librarianlib.features.network.TargetRadius
import com.teamwizardry.librarianlib.features.network.TargetServer
import net.minecraft.client.resources.I18n
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.item.EntityItem
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.IInventory
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumHand
import net.minecraft.util.EnumParticleTypes
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.TextComponentTranslation
import net.minecraft.world.World
import rpgloot.RPGLoot
import rpgloot.network.CorpseSyncPacket
import java.util.*

class EntityCorpse(worldIn: World) : Entity(worldIn), IInventory {

    private var drops: MutableList<ItemStack> = ArrayList()
    private var owner: UUID? = null
    var oldEntityData: NBTTagCompound = NBTTagCompound()
        private set
    private var dispose: Boolean = false

    var entityClass: String
        get() = oldEntityData.getString("entityClass")
        set(entClass) = oldEntityData.setString("entityClass", entClass)

    init {
        oldEntityData = NBTTagCompound()
        entityClass = ""
        setSize(1.0f, 1.0f)
    }

    constructor(worldIn: World, entityLivingBase: EntityLivingBase, owner: EntityPlayer?, entityDrops: List<EntityItem>) : this(worldIn) {
        copyData(entityLivingBase)
        if (RPGLoot.config.looting) {
            addDrops(entityDrops)
        }

        setLocationAndAngles(entityLivingBase.posX, entityLivingBase.posY, entityLivingBase.posZ, 0.0f, 0.0f)
        if (owner != null) {
            this.owner = owner.uniqueID
        }

    }

    override fun entityInit() {}

    override fun canBeCollidedWith(): Boolean {
        return true
    }

    private fun addDrops(itemEntities: List<EntityItem>) {
        for (itemEntity in itemEntities) {
            addDrop(itemEntity.item)
        }
    }

    private fun copyData(entityLivingBase: EntityLivingBase) {
        entityClass = entityLivingBase.javaClass.canonicalName
        entityLivingBase.writeToNBT(oldEntityData)
        oldEntityData.setBoolean("NoAI", true)
        oldEntityData.removeTag("Fire")
        oldEntityData.setShort("HurtTime", 0)
    }

    public override fun writeEntityToNBT(tagCompound: NBTTagCompound) {
        tagCompound.setTag("entityData", oldEntityData)
        tagCompound.setBoolean("hasOwner", owner != null)
        if (owner != null) {
            tagCompound.setString("UUID", owner!!.toString())
        }

        if (drops.isNotEmpty()) {
            tagCompound.setInteger("dropCount", drops.size)
            val dropsTag = NBTTagCompound()

            for (i in drops.indices) {
                if (drops[i] != ItemStack.EMPTY) {
                    val dropTag = NBTTagCompound()
                    drops[i].writeToNBT(dropTag)
                    dropsTag.setTag("drop:$i", dropTag)
                }
            }

            tagCompound.setTag("drops", dropsTag)
        }

    }

    public override fun readEntityFromNBT(tagCompound: NBTTagCompound) {
        if (tagCompound.getBoolean("hasOwner"))
            owner = UUID.fromString(tagCompound.getString("UUID"))
        else
            owner = null

        oldEntityData = tagCompound.getCompoundTag("entityData")
        if (tagCompound.hasKey("dropCount")) {
            val count = tagCompound.getInteger("dropCount")
            drops = ArrayList()
            val dropsTag = tagCompound.getCompoundTag("drops")

            for (i in 0 until count) {
                drops.add(ItemStack(dropsTag.getCompoundTag("drop:$i")))
            }
        }

    }

    override fun processInitialInteract(player: EntityPlayer, hand: EnumHand): Boolean {
        if (!RPGLoot.config.looting)
            return false
        if (!world.isRemote) {
            return if (!isUsableByPlayer(player)) {
                player.sendMessage(TextComponentTranslation(I18n.format("RPGLoot.message.notOwner")))
                false
            } else {
                if (lootToPlayer(player))
                    dispose()
                true
            }

        }
        return false
    }

    private fun addDrop(itemStack: ItemStack) {
        if (itemStack != ItemStack.EMPTY) {
            for (currentStack in drops) {
                if (ItemStack.areItemStacksEqual(currentStack, itemStack) && currentStack.count < inventoryStackLimit) {
                    val remainder = inventoryStackLimit - currentStack.count
                    if (itemStack.count < remainder) {
                        currentStack.grow(itemStack.count)
                        return
                    }

                    currentStack.count = inventoryStackLimit
                    itemStack.shrink(remainder)
                }
            }
            drops.add(itemStack)
        }

    }

    override fun onEntityUpdate() {
        super.onEntityUpdate()
        if ((entityClass.isEmpty()) && world.isRemote) {
            RPGLoot.networkChannel.send(TargetServer, CorpseSyncPacket(this))
        }

        if (world.isRemote && RPGLoot.config.looting && Minecraft().player.uniqueID == owner && rand.nextBoolean() && drops.size > 0) {
            val xPosition = posX.toFloat() - 0.5f
            val yPosition = posY.toFloat() + 0.2f
            val z = posZ.toFloat() - 0.5f
            val var1 = rand.nextFloat() * 0.5f * 2.0f - rand.nextInt(1)
            val var2 = rand.nextFloat() * 0.5f * 2.0f - rand.nextInt(1)
            val var3 = rand.nextFloat() * 0.5f * 3.0f - rand.nextInt(2)
            world.spawnParticle(EnumParticleTypes.VILLAGER_HAPPY, xPosition.toDouble() + var1.toDouble(),
                    yPosition.toDouble() + var3.toDouble(), z.toDouble() + var2.toDouble(), 0.5, 0.5, 0.5)
        } else {
            val decayTime = RPGLoot.config.decayTime
            if (decayTime > -1 && ticksExisted / 20 / 60 > decayTime || dispose) {
                setDead()
            }
        }
    }

    override fun getSizeInventory(): Int {
        return drops.size
    }

    override fun getStackInSlot(index: Int): ItemStack {
        return if (index >= drops.size) ItemStack.EMPTY else drops[index]
    }

    override fun decrStackSize(index: Int, count: Int): ItemStack {
        val currentStack = getStackInSlot(index)
        return if (currentStack != ItemStack.EMPTY) {
            if (currentStack.count <= count) {
                drops.remove(currentStack)
                currentStack
            } else {
                val itemstack = currentStack.splitStack(count)
                if (currentStack.count == 0) {
                    drops.remove(currentStack)
                }
                itemstack
            }
        } else
            ItemStack.EMPTY
    }

    override fun setInventorySlotContents(index: Int, stack: ItemStack) {
        if (index < drops.size) {
            drops[index] = stack
        } else
            drops.add(stack)
    }

    override fun getDisplayName(): ITextComponent? {
        return null
    }

    override fun hasCustomName(): Boolean {
        return false
    }

    override fun getInventoryStackLimit(): Int {
        return 64
    }

    override fun markDirty() {
        for (i in drops.indices) {
            if (drops[i] == ItemStack.EMPTY) {
                drops.removeAt(i)
            }
        }

        RPGLoot.networkChannel.send(TargetRadius(world, vec(posX, posY, posZ), 64), CorpseSyncPacket(this))
    }

    override fun isUsableByPlayer(player: EntityPlayer): Boolean {
        return owner == null || owner == player.uniqueID
    }

    override fun openInventory(arg0: EntityPlayer) {}

    override fun closeInventory(arg0: EntityPlayer) {}

    override fun isItemValidForSlot(index: Int, stack: ItemStack): Boolean {
        return true
    }

    override fun canRenderOnFire(): Boolean {
        return false
    }

    override fun canBePushed(): Boolean {
        return false
    }

    fun lootToPlayer(player: EntityPlayer?): Boolean {
        if (player != null) {
            val iterator = drops.iterator()

            while (iterator.hasNext()) {
                if (!player.inventory.addItemStackToInventory(iterator.next())) {
                    player.sendMessage(TextComponentTranslation(I18n.format("RPGLoot.message.inventoryFull")))
                    return false
                }

                iterator.remove()
            }

            return true
        } else
            return false
    }

    fun dispose() {
        dispose = true
    }

    override fun removeStackFromSlot(index: Int): ItemStack {
        val out = getStackInSlot(index)
        setInventorySlotContents(index, ItemStack.EMPTY)
        return out
    }

    override fun getField(arg0: Int): Int {
        return 0
    }

    override fun setField(arg0: Int, arg1: Int) {}

    override fun getFieldCount(): Int {
        return 0
    }

    override fun clear() {
        for (i in 0 until sizeInventory) {
            setInventorySlotContents(i, ItemStack.EMPTY)
        }
    }

    override fun isEmpty(): Boolean {
        for (slot in 0 until sizeInventory) {
            val item = getStackInSlot(slot)
            if (!item.isEmpty) {
                return false
            }
        }
        return true
    }

}

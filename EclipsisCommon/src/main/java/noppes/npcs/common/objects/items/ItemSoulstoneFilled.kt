package noppes.npcs.common.objects.items

import com.teamwizardry.librarianlib.features.base.item.ItemMod
import net.minecraft.client.resources.I18n
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityList
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.TextComponentTranslation
import net.minecraft.util.text.TextFormatting
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import noppes.npcs.api.constants.RoleType
import noppes.npcs.controllers.data.PlayerData
import noppes.npcs.common.entity.EntityNPCInterface
import noppes.npcs.roles.RoleCompanion
import noppes.npcs.roles.RoleFollower

class ItemSoulstoneFilled : ItemMod("soulstone_filled") {
    init {
        setMaxStackSize(1)
    }

    @SideOnly(Side.CLIENT)
    override fun addInformation(stack: ItemStack, world: World?, list: MutableList<String>, flag: ITooltipFlag) {
        val compound = stack.tagCompound
        if (compound == null || !compound.hasKey("Entity", 10)) {
            list.add(TextFormatting.RED.toString() + "Error")
            return
        }
        var name = I18n.format(compound.getString("Name"))
        if (compound.hasKey("DisplayName"))
            name = compound.getString("DisplayName") + " (" + name + ")"
        list.add(TextFormatting.BLUE.toString() + name)

        if (stack.tagCompound!!.hasKey("ExtraText")) {
            var text = ""
            val split = compound.getString("ExtraText").split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            for (s in split)
                text += I18n.format(s)
            list.add(text)
        }
    }

    override fun onItemUse(player: EntityPlayer, world: World, pos: BlockPos, hand: EnumHand, side: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): EnumActionResult {
        if (world.isRemote)
            return EnumActionResult.SUCCESS
        val stack = player.getHeldItem(hand)
        if (spawn(player, stack, world, pos) == null)
            return EnumActionResult.FAIL

        if (!player.capabilities.isCreativeMode)
            stack.splitStack(1)
        return EnumActionResult.SUCCESS
    }

    companion object {
        fun spawn(player: EntityPlayer?, stack: ItemStack, world: World, pos: BlockPos?): Entity? {
            if (world.isRemote)
                return null
            if (stack.tagCompound == null || !stack.tagCompound!!.hasKey("Entity", 10))
                return null
            val compound = stack.tagCompound!!.getCompoundTag("Entity")
            val entity = EntityList.createEntityFromNBT(compound, world) ?: return null
            entity.setPosition(pos!!.x + 0.5, (pos.y.toFloat() + 1f + 0.2f).toDouble(), pos.z + 0.5)
            if (entity is EntityNPCInterface) {
                entity.ais.setStartPos(pos)
                entity.health = entity.maxHealth
                entity.setPosition((pos.x.toFloat() + 0.5f).toDouble(), entity.startYPos, (pos.z.toFloat() + 0.5f).toDouble())

                if (entity.advanced.role == RoleType.COMPANION && player != null) {
                    val data = PlayerData.get(player)
                    if (data.hasCompanion())
                        return null
                    (entity.roleInterface as RoleCompanion).setOwner(player)
                    data.setCompanion(entity)
                }
                if (entity.advanced.role == RoleType.FOLLOWER && player != null) {
                    (entity.roleInterface as RoleFollower).setOwner(player)
                }
            }
            if (!world.spawnEntity(entity)) {
                player!!.sendMessage(TextComponentTranslation("error.failedToSpawn"))
                return null
            }
            return entity
        }
    }
}

package noppes.npcs.common.objects.items

import com.teamwizardry.librarianlib.features.base.item.ItemMod
import net.minecraft.entity.EntityList
import net.minecraft.entity.EntityLiving
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.passive.EntityAnimal
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagString
import noppes.npcs.CustomNpcsPermissions
import noppes.npcs.NoppesUtilServer
import noppes.npcs.api.constants.RoleType
import noppes.npcs.common.CustomNpcsConfig
import noppes.npcs.common.objects.CreativeTabNpcs
import noppes.npcs.common.objects.NpcObjects
import noppes.npcs.controllers.ServerCloneController
import noppes.npcs.common.entity.EntityNPCInterface
import noppes.npcs.roles.RoleCompanion
import noppes.npcs.roles.RoleFollower
import ru.glassspirit.eclipsis.kotlin.setModCreativeTab

class ItemSoulstoneEmpty : ItemMod("soulstone_empty") {
    init {
        this.setMaxStackSize(64)
        this.setModCreativeTab(CreativeTabNpcs)
    }

    fun store(entity: EntityLivingBase, stack: ItemStack, player: EntityPlayer): Boolean {
        if (!hasPermission(entity, player) || entity is EntityPlayer)
            return false
        val stone = ItemStack(NpcObjects.soulstoneFull!!)
        val compound = NBTTagCompound()
        if (!entity.writeToNBTAtomically(compound))
            return false
        ServerCloneController.Instance.cleanTags(compound)
        stone.setTagInfo("Entity", compound)

        var name = EntityList.getEntityString(entity)
        if (name == null)
            name = "generic"
        stone.setTagInfo("Name", NBTTagString("entity.$name.name"))
        if (entity is EntityNPCInterface) {
            stone.setTagInfo("DisplayName", NBTTagString(entity.getName()))
            if (entity.advanced.role == RoleType.COMPANION) {
                val role = entity.roleInterface as RoleCompanion
                stone.setTagInfo("ExtraText", NBTTagString("companion.stage,: ," + role.stage.name))
            }
        } else if (entity is EntityLiving && entity.hasCustomName())
            stone.setTagInfo("DisplayName", NBTTagString(entity.getCustomNameTag()))
        NoppesUtilServer.GivePlayerItem(player, player, stone)

        if (!player.capabilities.isCreativeMode) {
            stack.splitStack(1)
            if (stack.count <= 0)
                player.inventory.deleteStack(stack)
        }

        entity.isDead = true
        return true
    }

    fun hasPermission(entity: EntityLivingBase, player: EntityPlayer): Boolean {
        if (NoppesUtilServer.isOp(player))
            return true
        if (CustomNpcsPermissions.hasPermission(player, CustomNpcsPermissions.SOULSTONE_ALL))
            return true
        if (entity is EntityNPCInterface) {
            if (entity.advanced.role == RoleType.COMPANION) {
                val role = entity.roleInterface as RoleCompanion
                if (role.getOwner() === player)
                    return true
            }
            if (entity.advanced.role == RoleType.FOLLOWER) {
                val role = entity.roleInterface as RoleFollower
                if (role.getOwner() === player)
                    return !role.refuseSoulStone
            }
            return CustomNpcsConfig.SoulStoneNPCs
        }
        return if (entity is EntityAnimal) CustomNpcsConfig.SoulStoneAnimals else false

    }
}

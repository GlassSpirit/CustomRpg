package noppes.npcs.common.objects.items

import com.teamwizardry.librarianlib.features.base.block.BlockModDoor
import com.teamwizardry.librarianlib.features.base.block.ItemModDoor
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import noppes.npcs.NoppesUtilServer
import noppes.npcs.constants.EnumGuiType
import noppes.npcs.constants.EnumPacketServer
import noppes.npcs.util.IPermission

class ItemScriptedDoor(block: BlockModDoor) : ItemModDoor(block, "scripted_door_item"), IPermission {

    init {
        maxStackSize = 1
    }

    override fun onItemUse(playerIn: EntityPlayer, worldIn: World, pos: BlockPos, hand: EnumHand, side: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): EnumActionResult {
        val res = super.onItemUse(playerIn, worldIn, pos, hand, side, hitX, hitY, hitZ)
        if (res == EnumActionResult.SUCCESS && !worldIn.isRemote) {
            val newPos = pos.up()
            NoppesUtilServer.sendOpenGui(playerIn, EnumGuiType.ScriptDoor, null, newPos.x, newPos.y, newPos.z)
            return EnumActionResult.SUCCESS
        }
        return res
    }

    override fun onItemUseFinish(stack: ItemStack, worldIn: World?, playerIn: EntityLivingBase?): ItemStack {
        return stack
    }

    override fun isAllowed(e: EnumPacketServer): Boolean {
        return e == EnumPacketServer.ScriptDoorDataSave
    }
}

package noppes.npcs.common.objects.items

import com.teamwizardry.librarianlib.features.base.item.ItemMod
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.ActionResult
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumHand
import net.minecraft.world.World
import noppes.npcs.common.CustomNpcs
import noppes.npcs.common.objects.CreativeTabNpcs
import noppes.npcs.constants.EnumGuiType
import noppes.npcs.constants.EnumPacketServer
import noppes.npcs.util.IPermission
import ru.glassspirit.eclipsis.kotlin.setModCreativeTab

class ItemNpcScripter : ItemMod("scripter"), IPermission {
    init {
        maxStackSize = 1
        setFull3D()
        this.setModCreativeTab(CreativeTabNpcs)
    }

    override fun onItemRightClick(world: World, player: EntityPlayer, hand: EnumHand): ActionResult<ItemStack> {
        val itemstack = player.getHeldItem(hand)
        if (!world.isRemote || hand != EnumHand.MAIN_HAND)
            return ActionResult(EnumActionResult.SUCCESS, itemstack)
        CustomNpcs.proxy.openGui(0, 0, 0, EnumGuiType.ScriptPlayers, player)
        return ActionResult(EnumActionResult.SUCCESS, itemstack)
    }

    override fun isAllowed(e: EnumPacketServer): Boolean {
        return e == EnumPacketServer.ScriptDataGet || e == EnumPacketServer.ScriptDataSave ||
                e == EnumPacketServer.ScriptBlockDataSave || e == EnumPacketServer.ScriptDoorDataSave ||
                e == EnumPacketServer.ScriptPlayerGet || e == EnumPacketServer.ScriptPlayerSave ||
                e == EnumPacketServer.ScriptForgeGet || e == EnumPacketServer.ScriptForgeSave
    }
}

package noppes.npcs.common.objects.items

import com.teamwizardry.librarianlib.features.base.item.ItemMod
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.ActionResult
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.TextComponentTranslation
import net.minecraft.world.World
import noppes.npcs.CustomNpcsPermissions
import noppes.npcs.NoppesUtilServer
import noppes.npcs.common.CustomNpcs
import noppes.npcs.common.CustomNpcsConfig
import noppes.npcs.common.objects.CreativeTabNpcs
import noppes.npcs.constants.EnumGuiType
import noppes.npcs.constants.EnumPacketServer
import noppes.npcs.common.entity.EntityCustomNpc
import noppes.npcs.util.CustomNpcsScheduler
import noppes.npcs.util.IPermission
import ru.glassspirit.eclipsis.kotlin.setModCreativeTab

class ItemNpcWand : ItemMod("wand"), IPermission {
    init {
        maxStackSize = 1
        setFull3D()
        this.setModCreativeTab(CreativeTabNpcs)
    }

    override fun onItemRightClick(world: World, player: EntityPlayer, hand: EnumHand): ActionResult<ItemStack> {
        val itemstack = player.getHeldItem(hand)
        if (!world.isRemote)
            return ActionResult(EnumActionResult.SUCCESS, itemstack)
        CustomNpcs.proxy.openGui(0, 0, 0, EnumGuiType.NpcRemote, player)
        return ActionResult(EnumActionResult.SUCCESS, itemstack)
    }

    override fun getMaxItemUseDuration(par1ItemStack: ItemStack?): Int {
        return 72000
    }

    override fun onItemUse(player: EntityPlayer, world: World, bpos: BlockPos, hand: EnumHand, side: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): EnumActionResult {
        if (world.isRemote)
            return EnumActionResult.SUCCESS

        if (CustomNpcsConfig.OpsOnly && !player.server!!.playerList.canSendCommands(player.gameProfile)) {
            player.sendMessage(TextComponentTranslation("availability.permission"))
        } else if (CustomNpcsPermissions.hasPermission(player, CustomNpcsPermissions.NPC_CREATE)) {
            val npc = EntityCustomNpc(world)
            npc.ais.setStartPos(bpos.up())
            npc.setLocationAndAngles((bpos.x.toFloat() + 0.5f).toDouble(), npc.startYPos, (bpos.z.toFloat() + 0.5f).toDouble(), player.rotationYaw, player.rotationPitch)

            world.spawnEntity(npc)
            npc.health = npc.maxHealth

            CustomNpcsScheduler.runTack({ NoppesUtilServer.sendOpenGui(player, EnumGuiType.MainMenuDisplay, npc) }, 100)
        } else
            player.sendMessage(TextComponentTranslation("availability.permission"))
        return EnumActionResult.SUCCESS
    }

    override fun isAllowed(e: EnumPacketServer): Boolean {
        return true
    }
}

package noppes.npcs.common.objects.items

import com.teamwizardry.librarianlib.features.base.item.ItemMod
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.ActionResult
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.util.text.TextComponentString
import net.minecraft.world.World
import noppes.npcs.CustomNpcsPermissions
import noppes.npcs.NoppesUtilServer
import noppes.npcs.common.CustomNpcsConfig
import noppes.npcs.constants.EnumGuiType
import noppes.npcs.constants.EnumPacketServer
import noppes.npcs.common.entity.EntityNPCInterface
import noppes.npcs.util.IPermission


class ItemNpcPather : ItemMod("pather"), IPermission {
    init {
        maxStackSize = 1
    }

    override fun onItemRightClick(world: World, player: EntityPlayer, hand: EnumHand): ActionResult<ItemStack> {
        val itemstack = player.getHeldItem(hand)
        if (world.isRemote || !CustomNpcsPermissions.hasPermission(player, CustomNpcsPermissions.TOOL_MOUNTER))
            return ActionResult(EnumActionResult.PASS, itemstack)
        val npc = getNpc(itemstack, world)
        if (npc != null)
            NoppesUtilServer.sendOpenGui(player, EnumGuiType.MovingPath, npc)
        return ActionResult(EnumActionResult.SUCCESS, itemstack)
    }

    override fun onItemUse(player: EntityPlayer, world: World, bpos: BlockPos, hand: EnumHand, side: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): EnumActionResult {
        if (world.isRemote || !CustomNpcsPermissions.hasPermission(player, CustomNpcsPermissions.TOOL_MOUNTER))
            return EnumActionResult.FAIL
        val stack = player.getHeldItem(hand)
        val npc = getNpc(stack, world) ?: return EnumActionResult.PASS
        val list = npc.ais.movingPath
        val pos = list[list.size - 1]

        val x = bpos.x
        val y = bpos.y
        val z = bpos.z
        list.add(intArrayOf(x, y, z))

        val d3 = (x - pos[0]).toDouble()
        val d4 = (y - pos[1]).toDouble()
        val d5 = (z - pos[2]).toDouble()
        val distance = MathHelper.sqrt(d3 * d3 + d4 * d4 + d5 * d5).toDouble()

        player.sendMessage(TextComponentString("Added point x:" + x + " y:" + y + " z:" + z + " to npc " + npc.name))
        if (distance > CustomNpcsConfig.NpcNavRange)
            player.sendMessage(TextComponentString("Warning: point is too far away from previous point. Max block walk distance = " + CustomNpcsConfig.NpcNavRange))

        return EnumActionResult.SUCCESS
    }

    private fun getNpc(item: ItemStack, world: World): EntityNPCInterface? {
        if (world.isRemote || item.tagCompound == null)
            return null

        val entity = world.getEntityByID(item.tagCompound!!.getInteger("NPCID"))
        return if (entity == null || entity !is EntityNPCInterface) null else entity

    }

    override fun isAllowed(e: EnumPacketServer): Boolean {
        return e == EnumPacketServer.MovingPathGet || e == EnumPacketServer.MovingPathSave
    }
}

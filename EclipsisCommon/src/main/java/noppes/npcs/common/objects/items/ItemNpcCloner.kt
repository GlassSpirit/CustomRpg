package noppes.npcs.common.objects.items

import com.teamwizardry.librarianlib.features.base.item.ItemMod
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import noppes.npcs.NoppesUtilServer
import noppes.npcs.common.objects.CreativeTabNpcs
import noppes.npcs.constants.EnumGuiType
import noppes.npcs.constants.EnumPacketServer
import noppes.npcs.util.IPermission
import ru.glassspirit.eclipsis.kotlin.setModCreativeTab

class ItemNpcCloner : ItemMod("mobcloner"), IPermission {
    init {
        maxStackSize = 1
        setFull3D()
        this.setModCreativeTab(CreativeTabNpcs)
    }

    override fun onItemUse(player: EntityPlayer, world: World, pos: BlockPos, hand: EnumHand, side: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): EnumActionResult {
        if (!world.isRemote)
            NoppesUtilServer.sendOpenGui(player, EnumGuiType.MobSpawner, null, pos.x, pos.y, pos.z)
        return EnumActionResult.SUCCESS
    }

    override fun isAllowed(e: EnumPacketServer): Boolean {
        return e == EnumPacketServer.CloneList || e == EnumPacketServer.SpawnMob || e == EnumPacketServer.MobSpawner ||
                e == EnumPacketServer.ClonePreSave || e == EnumPacketServer.CloneRemove || e == EnumPacketServer.CloneSave
    }
}

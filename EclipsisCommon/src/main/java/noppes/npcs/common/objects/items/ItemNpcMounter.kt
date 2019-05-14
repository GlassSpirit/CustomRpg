package noppes.npcs.common.objects.items

import com.teamwizardry.librarianlib.features.base.item.ItemMod
import noppes.npcs.common.objects.CreativeTabNpcs
import noppes.npcs.constants.EnumPacketServer
import noppes.npcs.util.IPermission
import ru.glassspirit.eclipsis.kotlin.setModCreativeTab


class ItemNpcMounter : ItemMod("mounter"), IPermission {
    init {
        maxStackSize = 1
        this.setModCreativeTab(CreativeTabNpcs)
    }

    override fun isAllowed(e: EnumPacketServer): Boolean {
        return e == EnumPacketServer.SpawnRider || e == EnumPacketServer.PlayerRider || e == EnumPacketServer.CloneList
    }
}

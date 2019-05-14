package noppes.npcs.common.objects.items

import com.teamwizardry.librarianlib.features.base.item.ItemMod
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.event.entity.player.PlayerInteractEvent
import noppes.npcs.Server
import noppes.npcs.common.objects.CreativeTabNpcs
import noppes.npcs.constants.EnumGuiType
import noppes.npcs.constants.EnumPacketClient
import noppes.npcs.constants.EnumPacketServer
import noppes.npcs.util.IPermission
import ru.glassspirit.eclipsis.kotlin.setModCreativeTab

class ItemNbtBook : ItemMod("nbt_book"), IPermission {
    init {
        maxStackSize = 1
        this.setModCreativeTab(CreativeTabNpcs)
    }

    fun blockEvent(event: PlayerInteractEvent.RightClickBlock) {
        Server.sendData(event.entityPlayer as EntityPlayerMP, EnumPacketClient.GUI, EnumGuiType.NbtBook, event.pos.x, event.pos.y, event.pos.z)

        val state = event.world.getBlockState(event.pos)
        val data = NBTTagCompound()
        val tile = event.world.getTileEntity(event.pos)
        tile?.writeToNBT(data)

        val compound = NBTTagCompound()
        compound.setTag("Data", data)
        Server.sendData(event.entityPlayer as EntityPlayerMP, EnumPacketClient.GUI_DATA, compound)
    }

    fun entityEvent(event: PlayerInteractEvent.EntityInteract) {
        Server.sendData(event.entityPlayer as EntityPlayerMP, EnumPacketClient.GUI, EnumGuiType.NbtBook, 0, 0, 0)

        val data = NBTTagCompound()
        event.target.writeToNBTAtomically(data)
        val compound = NBTTagCompound()
        compound.setInteger("EntityId", event.target.entityId)
        compound.setTag("Data", data)
        Server.sendData(event.entityPlayer as EntityPlayerMP, EnumPacketClient.GUI_DATA, compound)
    }

    override fun isAllowed(e: EnumPacketServer): Boolean {
        return e == EnumPacketServer.NbtBookSaveEntity || e == EnumPacketServer.NbtBookSaveBlock
    }
}

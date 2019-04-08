package ru.glassspirit.cnpcs.client.gui

import com.teamwizardry.librarianlib.features.network.TargetServer
import net.minecraft.nbt.NBTTagCompound
import noppes.npcs.client.Client
import noppes.npcs.client.gui.script.GuiScriptInterface
import noppes.npcs.constants.EnumPacketServer
import noppes.npcs.controllers.data.Dialog
import ru.glassspirit.cnpcs.data.IMixinAvailability
import ru.glassspirit.cnpcs.network.PacketGuiScriptAvailability
import ru.glassspirit.eclipsis.EclipsisMod

class GuiScriptAvailability(private val dialog: Dialog) : GuiScriptInterface() {

    init {
        this.handler = dialog.availability as IMixinAvailability
        val data = NBTTagCompound()
        data.setBoolean("getScripts", true)
        data.setInteger("dialogId", dialog.id)
        EclipsisMod.CHANNEL.send(TargetServer, PacketGuiScriptAvailability(data))
    }

    override fun setGuiData(compound: NBTTagCompound) {
        (dialog.availability as IMixinAvailability).readScriptFromNbt(compound)
        super.setGuiData(compound)
    }

    override fun save() {
        super.save()
        Client.sendData(EnumPacketServer.DialogSave, this.dialog.category.id, this.dialog.writeToNBT(NBTTagCompound()))
    }
}

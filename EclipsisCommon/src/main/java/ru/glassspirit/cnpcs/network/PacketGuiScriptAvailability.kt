package ru.glassspirit.cnpcs.network

import com.teamwizardry.librarianlib.features.kotlin.Minecraft
import com.teamwizardry.librarianlib.features.network.TargetPlayers
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.nbt.NBTTagCompound
import noppes.npcs.controllers.DialogController
import noppes.npcs.controllers.ScriptController
import ru.glassspirit.cnpcs.client.gui.GuiScriptAvailability
import ru.glassspirit.cnpcs.data.IMixinAvailability
import ru.glassspirit.eclipsis.EclipsisMod
import ru.glassspirit.eclipsis.network.NBTPacket

class PacketGuiScriptAvailability() : NBTPacket() {

    constructor(compound: NBTTagCompound) : this() {
        data.merge(compound)
    }

    override fun server(player: EntityPlayerMP) {
        val dialogId = data.getInteger("dialogId")
        val getScripts = data.getBoolean("getScripts")
        val dialog = DialogController.instance.dialogs[dialogId]
        if (dialog != null) {
            if (getScripts) {
                //Получили запрос с клиента на получение текста скрипта, должны выслать ответ с NBT скрипта
                val availability = dialog.availability as IMixinAvailability
                val data = NBTTagCompound()
                data.setInteger("dialogId", dialogId)
                data.setTag("Languages", ScriptController.Instance.nbtLanguages())
                availability.writeScriptToNbt(data)
                EclipsisMod.CHANNEL.send(TargetPlayers(player), PacketGuiScriptAvailability(data))
            } else {
                //Получили запрос с клиента на сохранение текста скрипта, должны сохранить
                val availability = dialog.availability as IMixinAvailability
                availability.readScriptFromNbt(data)
            }
        }
    }

    override fun client(player: EntityPlayer) {
        if (Minecraft().currentScreen is GuiScriptAvailability) {
            val gui = Minecraft().currentScreen as GuiScriptAvailability
            gui.setGuiData(data)
        }
    }

}

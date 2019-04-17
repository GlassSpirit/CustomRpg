package noppes.npcs.client.gui.script;

import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.client.Client;
import noppes.npcs.constants.EnumPacketServer;
import noppes.npcs.controllers.data.PlayerScriptData;

public class GuiScriptPlayers extends GuiScriptInterface {
    private PlayerScriptData script = new PlayerScriptData(null);

    public GuiScriptPlayers() {
        handler = script;
        Client.sendData(EnumPacketServer.ScriptPlayerGet);
    }

    @Override
    public void setGuiData(NBTTagCompound compound) {
        script.readFromNBT(compound);
        super.setGuiData(compound);
    }

    @Override
    public void save() {
        super.save();
        Client.sendData(EnumPacketServer.ScriptPlayerSave, script.writeToNBT(new NBTTagCompound()));
    }
}

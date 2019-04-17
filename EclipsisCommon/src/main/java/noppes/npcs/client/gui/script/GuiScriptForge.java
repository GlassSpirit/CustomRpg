package noppes.npcs.client.gui.script;

import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.client.Client;
import noppes.npcs.constants.EnumPacketServer;
import noppes.npcs.controllers.data.ForgeScriptData;

public class GuiScriptForge extends GuiScriptInterface {

    private ForgeScriptData script = new ForgeScriptData();

    public GuiScriptForge() {
        handler = script;
        Client.sendData(EnumPacketServer.ScriptForgeGet);
    }

    @Override
    public void setGuiData(NBTTagCompound compound) {
        script.readFromNBT(compound);
        super.setGuiData(compound);
    }

    @Override
    public void save() {
        super.save();
        Client.sendData(EnumPacketServer.ScriptForgeSave, script.writeToNBT(new NBTTagCompound()));
    }
}

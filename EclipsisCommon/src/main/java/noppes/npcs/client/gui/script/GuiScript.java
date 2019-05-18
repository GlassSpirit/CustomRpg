package noppes.npcs.client.gui.script;

import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.client.Client;
import noppes.npcs.constants.EnumPacketServer;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.entity.data.DataScript;

public class GuiScript extends GuiScriptInterface {
    private DataScript script;

    public GuiScript(EntityNPCInterface npc) {
        handler = script = npc.script;
        Client.sendData(EnumPacketServer.ScriptDataGet);
    }

    @Override
    public void setGuiData(NBTTagCompound compound) {
        script.readFromNBT(compound);
        super.setGuiData(compound);
    }

    @Override
    public void save() {
        super.save();
        Client.sendData(EnumPacketServer.ScriptDataSave, script.writeToNBT(new NBTTagCompound()));
    }
}

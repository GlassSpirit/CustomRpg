package noppes.npcs.client.gui.script;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.CustomItems;
import noppes.npcs.api.wrapper.ItemScriptedWrapper;
import noppes.npcs.client.Client;
import noppes.npcs.constants.EnumPacketServer;

public class GuiScriptItem extends GuiScriptInterface {

    private ItemScriptedWrapper item;

    public GuiScriptItem(EntityPlayer player) {
        handler = item = new ItemScriptedWrapper(new ItemStack(CustomItems.scripted_item));
        Client.sendData(EnumPacketServer.ScriptItemDataGet);
    }

    @Override
    public void setGuiData(NBTTagCompound compound) {
        item.setMCNbt(compound);
        super.setGuiData(compound);
    }

    @Override
    public void save() {
        super.save();
        Client.sendData(EnumPacketServer.ScriptItemDataSave, item.getMCNbt());
    }
}

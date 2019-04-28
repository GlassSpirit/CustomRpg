package noppes.npcs.client.gui.script;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import noppes.npcs.objects.blocks.tiles.TileScriptedDoor;
import noppes.npcs.client.Client;
import noppes.npcs.constants.EnumPacketServer;

public class GuiScriptDoor extends GuiScriptInterface {
    private TileScriptedDoor script;

    public GuiScriptDoor(int x, int y, int z) {
        handler = script = (TileScriptedDoor) player.world.getTileEntity(new BlockPos(x, y, z));
        Client.sendData(EnumPacketServer.ScriptDoorDataGet, x, y, z);
    }

    @Override
    public void setGuiData(NBTTagCompound compound) {
        script.setNBT(compound);
        super.setGuiData(compound);
    }

    @Override
    public void save() {
        super.save();
        BlockPos pos = script.getPos();
        Client.sendData(EnumPacketServer.ScriptDoorDataSave, pos.getX(), pos.getY(), pos.getZ(), script.getNBT(new NBTTagCompound()));
    }
}

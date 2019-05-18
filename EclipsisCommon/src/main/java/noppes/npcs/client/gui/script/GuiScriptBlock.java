package noppes.npcs.client.gui.script;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import noppes.npcs.blocks.tiles.TileScripted;
import noppes.npcs.client.Client;
import noppes.npcs.constants.EnumPacketServer;

public class GuiScriptBlock extends GuiScriptInterface {

    private TileScripted script;

    public GuiScriptBlock(int x, int y, int z) {
        handler = script = (TileScripted) player.world.getTileEntity(new BlockPos(x, y, z));
        Client.sendData(EnumPacketServer.ScriptBlockDataGet, x, y, z);
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
        Client.sendData(EnumPacketServer.ScriptBlockDataSave, pos.getX(), pos.getY(), pos.getZ(), script.getNBT(new NBTTagCompound()));
    }
}

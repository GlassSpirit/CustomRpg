package noppes.npcs.objects.blocks.tiles;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

import java.util.HashMap;
import java.util.Map;

public class TileNpcEntity extends TileEntity {
    public Map<String, Object> tempData = new HashMap<String, Object>();

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        NBTTagCompound extraData = compound.getCompoundTag("ExtraData");
        if (extraData.getSize() > 0) {
            getTileData().setTag("CustomNPCsData", extraData);
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        return super.writeToNBT(compound);
    }
}
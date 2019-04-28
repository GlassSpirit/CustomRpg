package noppes.npcs.objects.blocks.tiles;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;

public class TileCopy extends TileEntity {

    public short length = 10;
    public short width = 10;
    public short height = 10;

    public String name = "";

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);

        length = compound.getShort("Length");
        width = compound.getShort("Width");
        height = compound.getShort("Height");

        name = compound.getString("Name");
    }


    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setShort("Length", length);
        compound.setShort("Width", width);
        compound.setShort("Height", height);

        compound.setString("Name", name);
        return super.writeToNBT(compound);
    }

    @Override
    public void handleUpdateTag(NBTTagCompound compound) {
        length = compound.getShort("Length");
        width = compound.getShort("Width");
        height = compound.getShort("Height");
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        handleUpdateTag(pkt.getNbtCompound());
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(pos, 0, getUpdateTag());
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setInteger("x", this.pos.getX());
        compound.setInteger("y", this.pos.getY());
        compound.setInteger("z", this.pos.getZ());
        compound.setShort("Length", length);
        compound.setShort("Width", width);
        compound.setShort("Height", height);
        return compound;
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + width + 1, pos.getY() + height + 1, pos.getZ() + length + 1);
    }
}

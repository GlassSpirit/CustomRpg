package noppes.npcs.controllers.data;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import noppes.npcs.api.entity.data.role.IRoleTransporter.ITransportLocation;

public class TransportLocation implements ITransportLocation {
    public int id = -1;
    public String name = "default name";
    public BlockPos pos;

    public int type = 0;
    public int dimension = 0;

    public TransportCategory category;

    public void readNBT(NBTTagCompound compound) {
        if (compound == null)
            return;
        id = compound.getInteger("Id");
        pos = new BlockPos(compound.getDouble("PosX"), compound.getDouble("PosY"), compound.getDouble("PosZ"));
        type = compound.getInteger("Type");
        dimension = compound.getInteger("Dimension");
        name = compound.getString("Name");
    }

    public NBTTagCompound writeNBT() {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setInteger("Id", id);
        compound.setDouble("PosX", pos.getX());
        compound.setDouble("PosY", pos.getY());
        compound.setDouble("PosZ", pos.getZ());
        compound.setInteger("Type", type);
        compound.setInteger("Dimension", dimension);
        compound.setString("Name", name);
        return compound;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public int getDimension() {
        return dimension;
    }

    @Override
    public int getX() {
        return pos.getX();
    }

    @Override
    public int getY() {
        return pos.getY();
    }

    @Override
    public int getZ() {
        return pos.getZ();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getType() {
        return type;
    }

    public boolean isDefault() {
        return type == 1;
    }
}

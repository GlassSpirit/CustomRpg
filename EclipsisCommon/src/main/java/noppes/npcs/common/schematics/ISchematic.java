package noppes.npcs.common.schematics;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;

public interface ISchematic {

    short getWidth();

    short getHeight();

    short getLength();

    int getTileEntitySize();

    NBTTagCompound getTileEntity(int i);

    String getName();

    IBlockState getBlockState(int x, int y, int z);

    IBlockState getBlockState(int i);

    NBTTagCompound getNBT();
}

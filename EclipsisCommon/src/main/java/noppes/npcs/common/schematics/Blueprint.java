package noppes.npcs.common.schematics;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

/**
 * @author JTK222
 */
public class Blueprint implements ISchematic {

    private List<String> requiredMods;
    private short sizeX, sizeY, sizeZ;
    private short palleteSize;
    private IBlockState[] pallete;
    private String name;
    private String[] architects;

    private short[][][] structure;
    private NBTTagCompound[] tileEntities;

    public Blueprint(short sizeX, short sizeY, short sizeZ, short palleteSize, IBlockState[] pallete, short[][][] structure, NBTTagCompound[] tileEntities, List<String> requiredMods) {
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.sizeZ = sizeZ;
        this.palleteSize = palleteSize;
        this.pallete = pallete;
        this.structure = structure;
        this.tileEntities = tileEntities;
        this.requiredMods = requiredMods;
    }

    public void build(World world, BlockPos pos) {
        IBlockState[] pallete = this.getPallete();
        short[][][] structure = this.getStructure();
        for (short y = 0; y < this.getSizeY(); y++) {
            for (short z = 0; z < this.getSizeZ(); z++) {
                for (short x = 0; x < this.getSizeX(); x++) {
                    IBlockState state = pallete[structure[y][z][x] & 0xFFFF];
                    if (state.getBlock() == Blocks.STRUCTURE_VOID)
                        continue;
                    if (state.isFullCube())
                        world.setBlockState(pos.add(x, y, z), state, 2);
                }
            }
        }
        for (short y = 0; y < this.getSizeY(); y++) {
            for (short z = 0; z < this.getSizeZ(); z++) {
                for (short x = 0; x < this.getSizeX(); x++) {
                    IBlockState state = pallete[structure[y][z][x]];
                    if (state.getBlock() == Blocks.STRUCTURE_VOID)
                        continue;
                    if (!state.isFullCube())
                        world.setBlockState(pos.add(x, y, z), state, 2);
                }
            }
        }
        if (this.getTileEntities() != null) {
            for (NBTTagCompound tag : this.getTileEntities()) {
                TileEntity te = world.getTileEntity(pos.add(tag.getShort("x"), tag.getShort("y"), tag.getShort("z")));
                tag.setInteger("x", pos.getX() + tag.getShort("x"));
                tag.setInteger("y", pos.getY() + tag.getShort("y"));
                tag.setInteger("z", pos.getZ() + tag.getShort("z"));
                te.deserializeNBT(tag);
            }
        }
    }

    public short getSizeX() {
        return sizeX;
    }

    public short getSizeY() {
        return sizeY;
    }

    public short getSizeZ() {
        return sizeZ;
    }

    public short getPalleteSize() {
        return palleteSize;
    }

    public IBlockState[] getPallete() {
        return pallete;
    }

    public short[][][] getStructure() {
        return structure;
    }

    public NBTTagCompound[] getTileEntities() {
        return tileEntities;
    }

    public List<String> getRequiredMods() {
        return requiredMods;
    }


    @Override
    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public String[] getArchitects() {
        return architects;
    }


    public void setArchitects(String[] architects) {
        this.architects = architects;
    }

    @Override
    public short getWidth() {
        return getSizeX();
    }

    @Override
    public short getHeight() {
        return getSizeZ();
    }

    @Override
    public short getLength() {
        return getSizeY();
    }

    @Override
    public int getTileEntitySize() {
        return tileEntities.length;
    }

    @Override
    public NBTTagCompound getTileEntity(int i) {
        return tileEntities[i];
    }

    @Override
    public IBlockState getBlockState(int x, int y, int z) {
        return pallete[structure[y][z][x]];
    }

    @Override
    public IBlockState getBlockState(int i) {
        int x = (i % getWidth());
        int z = ((i - x) / getWidth()) % getLength();
        int y = (((i - x) / getWidth()) - z) / getLength();
        return getBlockState(x, y, z);
    }

    @Override
    public NBTTagCompound getNBT() {
        return BlueprintUtil.writeBlueprintToNBT(this);
    }
}
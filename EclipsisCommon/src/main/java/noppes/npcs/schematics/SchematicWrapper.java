package noppes.npcs.schematics;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SchematicWrapper {
    public static final int buildSize = 10000;

    private BlockPos offset = BlockPos.ORIGIN;
    private BlockPos start = BlockPos.ORIGIN;

    public ISchematic schema;

    public int buildPos, size;

    public int rotation = 0;

    private World world;

    public boolean isBuilding = false;
    public boolean firstLayer = true;

    private Map<ChunkPos, NBTTagCompound>[] tileEntities;

    public SchematicWrapper(ISchematic schematic) {
        this.schema = schematic;

        size = schematic.getWidth() * schematic.getHeight() * schematic.getLength();

        tileEntities = new Map[schematic.getHeight()];
        for (int i = 0; i < schematic.getTileEntitySize(); i++) {
            NBTTagCompound teTag = schematic.getTileEntity(i);
            int x = teTag.getInteger("x");
            int y = teTag.getInteger("y");
            int z = teTag.getInteger("z");
            Map<ChunkPos, NBTTagCompound> map = tileEntities[y];
            if (map == null)
                tileEntities[y] = map = new HashMap<ChunkPos, NBTTagCompound>();
            map.put(new ChunkPos(x, z), teTag);
        }
    }

    public void load(Schematic s) {

    }

    public void init(BlockPos pos, World world, int rotation) {
        start = pos;
        this.world = world;
        this.rotation = rotation;
    }

    public void offset(int x, int y, int z) {
        offset = new BlockPos(x, y, z);
    }

    public void build() {
        if (world == null || !isBuilding)//you should init first
            return;

        long endPos = buildPos + buildSize;
        if (endPos > size)
            endPos = size;

        for (; buildPos < endPos; buildPos++) {
            int x = (buildPos % schema.getWidth());
            int z = ((buildPos - x) / schema.getWidth()) % schema.getLength();
            int y = (((buildPos - x) / schema.getWidth()) - z) / schema.getLength();
            if (firstLayer)
                place(x, y, z, 1);
            else
                place(x, y, z, 2);
        }
        if (buildPos >= size) {
            if (firstLayer) {
                firstLayer = false;
                buildPos = 0;
            } else {
                isBuilding = false;
            }
        }
    }

    /**
     * @param flag 0:any, 1:normal, 2:not normal
     */
    public void place(int x, int y, int z, int flag) {
        IBlockState state = schema.getBlockState(x, y, z);
        if (state == null || flag == 1 && !state.isFullBlock() && state.getBlock() != Blocks.AIR || flag == 2 && (state.isFullBlock() || state.getBlock() == Blocks.AIR))
            return;
        int rotation = this.rotation / 90;
        BlockPos pos = start.add(rotatePos(x, y, z, rotation));
        state = rotationState(state, rotation);
        world.setBlockState(pos, state, 2);
        if (state.getBlock() instanceof ITileEntityProvider) {
            TileEntity tile = world.getTileEntity(pos);
            if (tile != null) {
                NBTTagCompound comp = getTileEntity(x, y, z, pos);
                if (comp != null)
                    tile.readFromNBT(comp);
            }
        }
//        Chunk chunk = world.getChunkFromBlockCoords(pos);
//        chunk.setBlockState(pos, b.getStateFromMeta(blockDataArray[i]));
//        chunk.setChunkModified();
    }


    public IBlockState rotationState(IBlockState state, int rotation) {
        if (rotation == 0)
            return state;
        Set<IProperty<?>> set = state.getProperties().keySet();
        for (IProperty prop : set) {
            if (!(prop instanceof PropertyDirection))
                continue;
            EnumFacing direction = (EnumFacing) state.getValue(prop);
            if (direction == EnumFacing.UP || direction == EnumFacing.DOWN)
                continue;
            for (int i = 0; i < rotation; i++) {
                direction = direction.rotateY();
            }
            return state.withProperty(prop, direction);
        }

        return state;
    }

    public NBTTagCompound getTileEntity(int x, int y, int z, BlockPos pos) {
        if (y >= tileEntities.length || tileEntities[y] == null)
            return null;
        NBTTagCompound compound = tileEntities[y].get(new ChunkPos(x, z));
        if (compound == null)
            return null;
        compound = compound.copy();
        compound.setInteger("x", pos.getX());
        compound.setInteger("y", pos.getY());
        compound.setInteger("z", pos.getZ());
        return compound;
    }

    public NBTTagCompound getNBTSmall() {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setShort("Width", schema.getWidth());
        compound.setShort("Height", schema.getHeight());
        compound.setShort("Length", schema.getLength());
        compound.setString("SchematicName", schema.getName());

        NBTTagList list = new NBTTagList();

        for (int i = 0; i < size && i < 25000; i++) {
            IBlockState state = schema.getBlockState(i);
            if (state.getBlock() == Blocks.AIR || state.getBlock() == Blocks.STRUCTURE_VOID)
                list.appendTag(new NBTTagCompound());
            else
                list.appendTag(NBTUtil.writeBlockState(new NBTTagCompound(), schema.getBlockState(i)));
        }
        compound.setTag("Data", list);
        return compound;
    }

    public BlockPos rotatePos(int x, int y, int z, int rotation) {
        if (rotation == 1)
            return new BlockPos(schema.getLength() - z - 1, y, x);
        else if (rotation == 2)
            return new BlockPos(schema.getWidth() - x - 1, y, schema.getLength() - z - 1);
        else if (rotation == 3)
            return new BlockPos(z, y, schema.getWidth() - x - 1);
        return new BlockPos(x, y, z);
    }

    public int getPercentage() {
        double l = (buildPos + (firstLayer ? 0 : size));
        return (int) (l / size * 50);
    }

}

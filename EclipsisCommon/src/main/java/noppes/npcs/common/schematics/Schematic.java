package noppes.npcs.common.schematics;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.common.objects.NpcObjects;

public class Schematic implements ISchematic {

    public String name;
    public short width, height, length;

    private NBTTagList entityList;
    public NBTTagList tileList;

    public short[] blockArray;
    public byte[] blockDataArray;

    public Schematic(String name) {
        this.name = name;
    }

    public void load(final NBTTagCompound compound) {
        width = compound.getShort("Width");
        height = compound.getShort("Height");
        length = compound.getShort("Length");

        byte[] addId = compound.hasKey("AddBlocks") ? compound.getByteArray("AddBlocks") : new byte[0];
        setBlockBytes(compound.getByteArray("Blocks"), addId);

        blockDataArray = compound.getByteArray("Data");
        entityList = compound.getTagList("Entities", (byte) 10);
        tileList = compound.getTagList("TileEntities", (byte) 10);
    }

    @Override
    public NBTTagCompound getNBT() {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setShort("Width", width);
        compound.setShort("Height", height);
        compound.setShort("Length", length);

        byte[][] arr = getBlockBytes();
        compound.setByteArray("Blocks", arr[0]);
        if (arr.length > 1)
            compound.setByteArray("AddBlocks", arr[1]);
        compound.setByteArray("Data", blockDataArray);

        //compound.setTag("Entities", entityList);
        compound.setTag("TileEntities", tileList);
        return compound;
    }

    public void setBlockBytes(byte[] blockId, byte[] addId) {
        blockArray = new short[blockId.length];

        for (int index = 0; index < blockId.length; index++) {
            short id = (short) (blockId[index] & 0xFF);
            if ((index >> 1) < addId.length) {
                if ((index & 1) == 0)
                    id += (short) ((addId[index >> 1] & 0x0F) << 8);
                else
                    id += (short) ((addId[index >> 1] & 0xF0) << 4);
            }
            blockArray[index] = id;
        }
    }

    public byte[][] getBlockBytes() {
        byte[] blocks = new byte[blockArray.length];
        byte[] addBlocks = null;

        for (int i = 0; i < blocks.length; i++) {
            short id = blockArray[i];
            if (id > 255) {
                if (addBlocks == null) {
                    addBlocks = new byte[(blocks.length >> 1) + 1];
                }
                if ((i & 1) == 0)
                    addBlocks[i >> 1] = (byte) (addBlocks[i >> 1] & 0xF0 | (id >> 8) & 0xF);
                else
                    addBlocks[i >> 1] = (byte) (addBlocks[i >> 1] & 0xF | ((id >> 8) & 0xF) << 4);
            }
            blocks[i] = (byte) id;
        }
        if (addBlocks == null)
            return new byte[][]{blocks};
        return new byte[][]{blocks, addBlocks};
    }

    public int xyzToIndex(int x, int y, int z) {
        return (y * length + z) * width + x;
    }

    @Override
    public IBlockState getBlockState(int x, int y, int z) {
        int i = xyzToIndex(x, y, z);
        Block b = Block.getBlockById(blockArray[i]);
        if (b == null)
            return Blocks.AIR.getDefaultState();
        return b.getStateFromMeta(blockDataArray[i]);
    }

    @Override
    public IBlockState getBlockState(int i) {
        Block b = Block.getBlockById(blockArray[i]);
        if (b == null)
            return Blocks.AIR.getDefaultState();
        return b.getStateFromMeta(blockDataArray[i]);
    }

    @Override
    public short getWidth() {
        return width;
    }

    @Override
    public short getHeight() {
        return height;
    }

    @Override
    public short getLength() {
        return length;
    }

    @Override
    public int getTileEntitySize() {
        if (entityList == null)
            return 0;
        return entityList.tagCount();
    }

    @Override
    public NBTTagCompound getTileEntity(int i) {
        return entityList.getCompoundTagAt(i);
    }

    @Override
    public String getName() {
        return name;
    }

    public static Schematic Create(World world, String name, BlockPos pos, short height, short width, short length) {
        Schematic schema = new Schematic(name);
        schema.height = height;
        schema.width = width;
        schema.length = length;
        int size = height * width * length;

        schema.blockArray = new short[size];
        schema.blockDataArray = new byte[size];

        NoppesUtilServer.NotifyOPs("Creating schematic at: " + pos + " might lag slightly");

        schema.tileList = new NBTTagList();
        for (int i = 0; i < size; i++) {
            int x = (i % width);
            int z = ((i - x) / width) % length;
            int y = (((i - x) / width) - z) / length;

            IBlockState state = world.getBlockState(pos.add(x, y, z));
            if (state.getBlock() == Blocks.AIR || state.getBlock() == NpcObjects.copyBlock)
                continue;
            schema.blockArray[i] = (short) Block.REGISTRY.getIDForObject(state.getBlock());
            schema.blockDataArray[i] = (byte) state.getBlock().getMetaFromState(state);

            if (state.getBlock() instanceof ITileEntityProvider) {
                TileEntity tile = world.getTileEntity(pos.add(x, y, z));
                NBTTagCompound compound = new NBTTagCompound();
                tile.writeToNBT(compound);
                compound.setInteger("x", x);
                compound.setInteger("y", y);
                compound.setInteger("z", z);
                schema.tileList.appendTag(compound);
            }
        }
        return schema;
    }
}
package noppes.npcs.api.wrapper;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTPrimitive;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fluids.BlockFluidBase;
import noppes.npcs.api.*;
import noppes.npcs.api.block.IBlock;
import noppes.npcs.api.entity.data.IData;
import noppes.npcs.objects.blocks.BlockScripted;
import noppes.npcs.objects.blocks.BlockScriptedDoor;
import noppes.npcs.objects.blocks.tiles.TileNpcEntity;
import noppes.npcs.util.LRUHashMap;

import java.util.Map;

public class BlockWrapper implements IBlock {
    private static final Map<String, BlockWrapper> blockCache = new LRUHashMap<>(400);
    protected final IWorld world;
    protected final Block block;
    protected final BlockPos pos;
    protected final BlockPosWrapper bPos;
    protected TileEntity tile;
    protected TileNpcEntity storage;

    private final IData tempdata = new IData() {

        @Override
        public void remove(String key) {
            if (storage == null)
                return;
            storage.tempData.remove(key);
        }

        @Override
        public void put(String key, Object value) {
            if (storage == null)
                return;
            storage.tempData.put(key, value);
        }

        @Override
        public boolean has(String key) {
            if (storage == null)
                return false;
            return storage.tempData.containsKey(key);
        }

        @Override
        public Object get(String key) {
            if (storage == null)
                return null;
            return storage.tempData.get(key);
        }

        @Override
        public void clear() {
            if (storage == null)
                return;
            storage.tempData.clear();
        }

        @Override
        public String[] getKeys() {
            return storage.tempData.keySet().toArray(new String[storage.tempData.size()]);
        }
    };

    private final IData storeddata = new IData() {

        @Override
        public void put(String key, Object value) {
            NBTTagCompound compound = getNBT();
            if (compound == null)
                return;
            if (value instanceof Number)
                compound.setDouble(key, ((Number) value).doubleValue());
            else if (value instanceof String)
                compound.setString(key, (String) value);
        }

        @Override
        public Object get(String key) {
            NBTTagCompound compound = getNBT();
            if (compound == null)
                return null;
            if (!compound.hasKey(key))
                return null;
            NBTBase base = compound.getTag(key);
            if (base instanceof NBTPrimitive)
                return ((NBTPrimitive) base).getDouble();
            return ((NBTTagString) base).getString();
        }

        @Override
        public void remove(String key) {
            NBTTagCompound compound = getNBT();
            if (compound == null)
                return;
            compound.removeTag(key);
        }

        @Override
        public boolean has(String key) {
            NBTTagCompound compound = getNBT();
            if (compound == null)
                return false;
            return compound.hasKey(key);
        }

        @Override
        public void clear() {
            if (tile == null)
                return;
            tile.getTileData().setTag("CustomNPCsData", new NBTTagCompound());
        }

        private NBTTagCompound getNBT() {
            if (tile == null)
                return null;
            NBTTagCompound compound = tile.getTileData().getCompoundTag("CustomNPCsData");
            if (compound.isEmpty() && !tile.getTileData().hasKey("CustomNPCsData")) {
                tile.getTileData().setTag("CustomNPCsData", compound);
            }
            return compound;
        }

        @Override
        public String[] getKeys() {
            NBTTagCompound compound = getNBT();
            if (compound == null)
                return new String[0];
            return compound.getKeySet().toArray(new String[compound.getKeySet().size()]);
        }
    };

    protected BlockWrapper(World world, Block block, BlockPos pos) {
        this.world = NpcAPI.Instance().getIWorld((WorldServer) world);
        this.block = block;
        this.pos = pos;
        this.bPos = new BlockPosWrapper(pos);
        this.setTile(world.getTileEntity(pos));
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
    public IPos getPos() {
        return bPos;
    }

    @Override
    public int getMetadata() {
        return block.getMetaFromState(world.getMCWorld().getBlockState(pos));
    }

    @Override
    public void setMetadata(int i) {
        world.getMCWorld().setBlockState(pos, block.getStateFromMeta(i), 3);
    }

    @Override
    public void remove() {
        world.getMCWorld().setBlockToAir(pos);
    }

    @Override
    public boolean isRemoved() {
        IBlockState state = world.getMCWorld().getBlockState(pos);
        if (state == null)
            return true;
        return state.getBlock() != block;
    }

    @Override
    public boolean isAir() {
        return block.isAir(world.getMCWorld().getBlockState(pos), world.getMCWorld(), pos);
    }

    @Override
    public BlockWrapper setBlock(String name) {
        Block block = Block.REGISTRY.getObject(new ResourceLocation(name));
        if (block == null)
            return this;
        world.getMCWorld().setBlockState(pos, block.getDefaultState());
        return new BlockWrapper(world.getMCWorld(), block, pos);
    }

    @Override
    public BlockWrapper setBlock(IBlock block) {
        world.getMCWorld().setBlockState(pos, block.getMCBlock().getDefaultState());
        return new BlockWrapper(world.getMCWorld(), block.getMCBlock(), pos);
    }

    @Override
    public boolean isContainer() {
        if (tile == null || !(tile instanceof IInventory))
            return false;
        return ((IInventory) tile).getSizeInventory() > 0;
    }

    @Override
    public IContainer getContainer() {
        if (!isContainer())
            throw new CustomNPCsException("This block is not a container");
        return NpcAPI.Instance().getIContainer((IInventory) tile);
    }

    @Override
    public IData getTempdata() {
        return tempdata;
    }

    @Override
    public IData getStoreddata() {
        return storeddata;
    }

    @Override
    public String getName() {
        return Block.REGISTRY.getNameForObject(block) + "";
    }

    @Override
    public String getDisplayName() {
        if (tile == null)
            return getName();
        return tile.getDisplayName().getUnformattedText();
    }

    @Override
    public IWorld getWorld() {
        return world;
    }

    @Override
    public Block getMCBlock() {
        return block;
    }

    @Deprecated
    public static IBlock createNew(World world, BlockPos pos, IBlockState state) {
        Block block = state.getBlock();
        String key = state.toString() + pos.toString();
        BlockWrapper b = blockCache.get(key);
        if (b != null) {
            b.setTile(world.getTileEntity(pos));
            return b;
        }

        if (block instanceof BlockScripted)
            b = new BlockScriptedWrapper(world, block, pos);
        else if (block instanceof BlockScriptedDoor)
            b = new BlockScriptedDoorWrapper(world, block, pos);
        else if (block instanceof BlockFluidBase)
            b = new BlockFluidContainerWrapper(world, block, pos);
        else
            b = new BlockWrapper(world, block, pos);
        blockCache.put(key, b);

        return b;
    }

    public static void clearCache() {
        blockCache.clear();
    }

    @Override
    public boolean hasTileEntity() {
        return tile != null;
    }

    private void setTile(TileEntity tile) {
        this.tile = tile;
        if (tile instanceof TileNpcEntity)
            storage = (TileNpcEntity) tile;
    }

    @Override
    public INbt getTileEntityNBT() {
        NBTTagCompound compound = new NBTTagCompound();
        tile.writeToNBT(compound);
        return NpcAPI.Instance().getINbt(compound);
    }

    @Override
    public void setTileEntityNBT(INbt nbt) {
        tile.readFromNBT(nbt.getMCNBT());
    }

    @Override
    public TileEntity getMCTileEntity() {
        return tile;
    }

    @Override
    public void blockEvent(int type, int data) {
        world.getMCWorld().addBlockEvent(pos, getMCBlock(), type, data);
    }
}

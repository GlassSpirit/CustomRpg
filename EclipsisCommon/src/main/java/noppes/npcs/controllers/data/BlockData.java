package noppes.npcs.controllers.data;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;

public class BlockData {
    public BlockPos pos;
    public IBlockState state;
    public NBTTagCompound tile;

    private ItemStack stack;

    public BlockData(BlockPos pos, IBlockState state, NBTTagCompound tile) {
        this.pos = pos;
        this.state = state;
        this.tile = tile;
    }

    public NBTTagCompound getNBT() {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setInteger("BuildX", pos.getX());
        compound.setInteger("BuildY", pos.getY());
        compound.setInteger("BuildZ", pos.getZ());
        compound.setString("Block", Block.REGISTRY.getNameForObject(state.getBlock()).toString());
        compound.setInteger("Meta", state.getBlock().getMetaFromState(state));
        if (tile != null)
            compound.setTag("Tile", tile);
        return compound;
    }

    public static BlockData getData(NBTTagCompound compound) {
        BlockPos pos = new BlockPos(compound.getInteger("BuildX"), compound.getInteger("BuildY"), compound.getInteger("BuildZ"));
        Block b = Block.getBlockFromName(compound.getString("Block"));
        if (b == null)
            return null;
        IBlockState state = b.getStateFromMeta(compound.getInteger("Meta"));
        NBTTagCompound tile = null;
        if (compound.hasKey("Tile"))
            tile = compound.getCompoundTag("Tile");
        return new BlockData(pos, state, tile);

    }

    public ItemStack getStack() {
        if (stack == null)
            stack = new ItemStack(state.getBlock(), 1, state.getBlock().damageDropped(state));
        return stack;
    }
}

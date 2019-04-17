package noppes.npcs.api.wrapper;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import noppes.npcs.api.IPos;

public class BlockPosWrapper implements IPos {

    private BlockPos blockPos;

    public BlockPosWrapper(BlockPos pos) {
        this.blockPos = pos;
    }

    @Override
    public int getX() {
        return blockPos.getX();
    }

    @Override
    public int getY() {
        return blockPos.getY();
    }

    @Override
    public int getZ() {
        return blockPos.getZ();
    }

    @Override
    public IPos up() {
        return new BlockPosWrapper(blockPos.up());
    }

    @Override
    public IPos up(int n) {
        return new BlockPosWrapper(blockPos.up(n));
    }

    @Override
    public IPos down() {
        return new BlockPosWrapper(blockPos.down());
    }

    @Override
    public IPos down(int n) {
        return new BlockPosWrapper(blockPos.down(n));
    }

    @Override
    public IPos north() {
        return new BlockPosWrapper(blockPos.north());
    }

    @Override
    public IPos north(int n) {
        return new BlockPosWrapper(blockPos.north(n));
    }

    @Override
    public IPos east() {
        return new BlockPosWrapper(blockPos.north());
    }

    @Override
    public IPos east(int n) {
        return new BlockPosWrapper(blockPos.north(n));
    }

    @Override
    public IPos south() {
        return new BlockPosWrapper(blockPos.north());
    }

    @Override
    public IPos south(int n) {
        return new BlockPosWrapper(blockPos.north(n));
    }

    @Override
    public IPos west() {
        return new BlockPosWrapper(blockPos.north());
    }

    @Override
    public IPos west(int n) {
        return new BlockPosWrapper(blockPos.north(n));
    }

    @Override
    public IPos add(int x, int y, int z) {
        return new BlockPosWrapper(blockPos.add(x, y, z));
    }

    @Override
    public IPos add(IPos pos) {
        return new BlockPosWrapper(blockPos.add(pos.getMCBlockPos()));
    }

    @Override
    public IPos subtract(int x, int y, int z) {
        return new BlockPosWrapper(blockPos.add(-x, -y, -z));
    }

    @Override
    public IPos subtract(IPos pos) {
        return new BlockPosWrapper(blockPos.add(-pos.getX(), -pos.getY(), -pos.getZ()));
    }

    @Override
    public IPos offset(int direction) {
        return new BlockPosWrapper(blockPos.offset(EnumFacing.VALUES[direction]));
    }

    @Override
    public IPos offset(int direction, int n) {
        return new BlockPosWrapper(blockPos.offset(EnumFacing.VALUES[direction], n));
    }

    @Override
    public BlockPos getMCBlockPos() {
        return blockPos;
    }

    @Override
    public double[] normalize() {
        double d = Math.sqrt(blockPos.getX() * blockPos.getX() + blockPos.getY() * blockPos.getY() + blockPos.getZ() * blockPos.getZ());
        return new double[]{getX() / d, getY() / d, getZ() / d};
    }

    @Override
    public double distanceTo(IPos pos) {
        double d0 = this.getX() - pos.getX();
        double d1 = this.getY() - pos.getY();
        double d2 = this.getZ() - pos.getZ();
        return Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
    }
}

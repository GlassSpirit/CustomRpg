package noppes.npcs.blocks.tiles;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import noppes.npcs.CustomNpcs;
import noppes.npcs.blocks.BlockNpcRedstone;
import noppes.npcs.controllers.data.Availability;

import java.util.List;

public class TileRedstoneBlock extends TileNpcEntity implements ITickable {
    public int onRange = 12;
    public int offRange = 20;

    public int onRangeX = 12;
    public int onRangeY = 12;
    public int onRangeZ = 12;

    public int offRangeX = 20;
    public int offRangeY = 20;
    public int offRangeZ = 20;

    public boolean isDetailed = false;

    public Availability availability = new Availability();

    public boolean isActivated = false;

    private int ticks = 10;

    @Override
    public void update() {
        if (this.world.isRemote)
            return;
        ticks--;
        if (ticks > 0)
            return;
        ticks = onRange > 10 ? 20 : 10;
        Block block = this.getBlockType();
        if (block == null || block instanceof BlockNpcRedstone == false) {
            return;
        }

        if (CustomNpcs.FreezeNPCs) {
            if (isActivated)
                setActive(block, false);
            return;
        }
        if (!isActivated) {
            int x = isDetailed ? onRangeX : onRange;
            int y = isDetailed ? onRangeY : onRange;
            int z = isDetailed ? onRangeZ : onRange;
            List<EntityPlayer> list = getPlayerList(x, y, z);
            if (list.isEmpty())
                return;
            for (EntityPlayer player : list) {
                if (availability.isAvailable(player)) {
                    setActive(block, true);
                    return;
                }
            }
        } else {
            int x = isDetailed ? offRangeX : offRange;
            int y = isDetailed ? offRangeY : offRange;
            int z = isDetailed ? offRangeZ : offRange;
            List<EntityPlayer> list = getPlayerList(x, y, z);
            for (EntityPlayer player : list) {
                if (availability.isAvailable(player))
                    return;
            }
            setActive(block, false);
        }

    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
        return oldState.getBlock() != newState.getBlock();
    }

    private void setActive(Block block, boolean bo) {
        isActivated = bo;
        IBlockState state = block.getDefaultState().withProperty(BlockNpcRedstone.ACTIVE, isActivated);
        world.setBlockState(pos, state, 2);
        markDirty();
        world.notifyBlockUpdate(pos, state, state, 3);
        block.onBlockAdded(world, pos, state);
    }

    private List<EntityPlayer> getPlayerList(int x, int y, int z) {
        return world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1).grow(x, y, z));
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        onRange = compound.getInteger("BlockOnRange");
        offRange = compound.getInteger("BlockOffRange");

        isDetailed = compound.getBoolean("BlockIsDetailed");
        if (compound.hasKey("BlockOnRangeX")) {
            isDetailed = true;
            onRangeX = compound.getInteger("BlockOnRangeX");
            onRangeY = compound.getInteger("BlockOnRangeY");
            onRangeZ = compound.getInteger("BlockOnRangeZ");

            offRangeX = compound.getInteger("BlockOffRangeX");
            offRangeY = compound.getInteger("BlockOffRangeY");
            offRangeZ = compound.getInteger("BlockOffRangeZ");
        }

        if (compound.hasKey("BlockActivated"))
            isActivated = compound.getBoolean("BlockActivated");

        availability.readFromNBT(compound);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setInteger("BlockOnRange", onRange);
        compound.setInteger("BlockOffRange", offRange);
        compound.setBoolean("BlockActivated", isActivated);
        compound.setBoolean("BlockIsDetailed", isDetailed);

        if (isDetailed) {
            compound.setInteger("BlockOnRangeX", onRangeX);
            compound.setInteger("BlockOnRangeY", onRangeY);
            compound.setInteger("BlockOnRangeZ", onRangeZ);

            compound.setInteger("BlockOffRangeX", offRangeX);
            compound.setInteger("BlockOffRangeY", offRangeY);
            compound.setInteger("BlockOffRangeZ", offRangeZ);
        }


        availability.writeToNBT(compound);
        return super.writeToNBT(compound);
    }
}

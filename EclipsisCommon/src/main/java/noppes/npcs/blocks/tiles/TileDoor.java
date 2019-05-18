package noppes.npcs.blocks.tiles;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import noppes.npcs.CustomItems;

public class TileDoor extends TileNpcEntity implements ITickable {
    public int ticksExisted = 0;
    public Block blockModel = CustomItems.scriptedDoor;
    public boolean needsClientUpdate = false;

    public TileEntity renderTile;
    public boolean renderTileErrored = true;
    public ITickable renderTileUpdate = null;

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
        return oldState.getBlock() != newState.getBlock();
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        setDoorNBT(compound);
    }

    public void setDoorNBT(NBTTagCompound compound) {
        blockModel = Block.REGISTRY.getObject(new ResourceLocation(compound.getString("ScriptDoorBlockModel")));
        if (blockModel == null || !(blockModel instanceof BlockDoor))
            blockModel = CustomItems.scriptedDoor;
        renderTileUpdate = null;
        renderTile = null;
        renderTileErrored = false;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        getDoorNBT(compound);
        return super.writeToNBT(compound);
    }


    public NBTTagCompound getDoorNBT(NBTTagCompound compound) {
        compound.setString("ScriptDoorBlockModel", Block.REGISTRY.getNameForObject(blockModel) + "");

        return compound;
    }

    public void setItemModel(Block block) {
        if (block == null || !(block instanceof BlockDoor))
            block = CustomItems.scriptedDoor;
        if (blockModel == block)
            return;
        blockModel = block;
        needsClientUpdate = true;
    }

    @Override
    public void update() {
        if (renderTileUpdate != null) {
            try {
                renderTileUpdate.update();
            } catch (Exception e) {
                renderTileUpdate = null;
            }
        }

        ticksExisted++;
        if (ticksExisted >= 10) {
            ticksExisted = 0;
            if (needsClientUpdate) {
                markDirty();
                IBlockState state = world.getBlockState(pos);
                world.notifyBlockUpdate(pos, state, state, 3);
                needsClientUpdate = false;
            }
        }
    }


    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        handleUpdateTag(pkt.getNbtCompound());
    }

    @Override
    public void handleUpdateTag(NBTTagCompound compound) {
        setDoorNBT(compound);
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
        getDoorNBT(compound);
        return compound;
    }
}

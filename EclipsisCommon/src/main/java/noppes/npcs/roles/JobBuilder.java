package noppes.npcs.roles;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.entity.data.role.IJobBuilder;
import noppes.npcs.api.item.IItemStack;
import noppes.npcs.blocks.tiles.TileBuilder;
import noppes.npcs.controllers.data.BlockData;
import noppes.npcs.entity.EntityNPCInterface;

import java.util.Stack;

public class JobBuilder extends JobInterface implements IJobBuilder {
    public TileBuilder build = null;
    private BlockPos possibleBuildPos = null;
    private Stack<BlockData> placingList = null;
    private BlockData placing = null;

    private int tryTicks = 0;
    private int ticks = 0;

    public JobBuilder(EntityNPCInterface npc) {
        super(npc);
        overrideMainHand = true;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        if (build != null) {
            compound.setInteger("BuildX", build.getPos().getX());
            compound.setInteger("BuildY", build.getPos().getY());
            compound.setInteger("BuildZ", build.getPos().getZ());
            if (placingList != null && !placingList.isEmpty()) {
                NBTTagList list = new NBTTagList();
                for (BlockData data : placingList) {
                    list.appendTag(data.getNBT());
                }
                if (placing != null)
                    list.appendTag(placing.getNBT());
                compound.setTag("Placing", list);
            }
        }
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        if (compound.hasKey("BuildX")) {
            possibleBuildPos = new BlockPos(compound.getInteger("BuildX"), compound.getInteger("BuildY"), compound.getInteger("BuildZ"));
        }
        if (possibleBuildPos != null && compound.hasKey("Placing")) {
            Stack<BlockData> placing = new Stack<>();
            NBTTagList list = compound.getTagList("Placing", 10);
            for (int i = 0; i < list.tagCount(); i++) {
                BlockData data = BlockData.getData(list.getCompoundTagAt(i));
                if (data != null)
                    placing.add(data);
            }
            this.placingList = placing;
        }
        npc.ais.doorInteract = 1;
    }

    @Override
    public IItemStack getMainhand() {
        String name = npc.getJobData();
        ItemStack item = stringToItem(name);
        if (item.isEmpty())
            return npc.inventory.weapons.get(0);
        return NpcAPI.Instance().getIItemStack(item);
    }

    @Override
    public boolean aiShouldExecute() {
        if (possibleBuildPos != null) {
            TileEntity tile = npc.world.getTileEntity(possibleBuildPos);
            if (tile instanceof TileBuilder) {
                build = (TileBuilder) tile;
            } else
                placingList.clear();
            possibleBuildPos = null;
        }
        return build != null;
    }

    @Override
    public void aiUpdateTask() {
        if (build.finished && placingList == null || !build.enabled || build.isInvalid()) {
            build = null;
            npc.getNavigator().tryMoveToXYZ(npc.getStartXPos(), npc.getStartYPos(), npc.getStartZPos(), 1);
            return;
        }
        if (ticks++ < 10)
            return;
        ticks = 0;
        if ((placingList == null || placingList.isEmpty()) && placing == null) {
            placingList = build.getBlock();
            npc.setJobData("");
            return;
        }
        if (placing == null) {
            placing = placingList.pop();
            if (placing.state.getBlock() == Blocks.STRUCTURE_VOID) {
                placing = null;
                return;
            }
            tryTicks = 0;
            npc.setJobData(blockToString(placing));

        }
        npc.getNavigator().tryMoveToXYZ(placing.pos.getX(), placing.pos.getY() + 1, placing.pos.getZ(), 1);
        if (tryTicks++ > 40 || npc.nearPosition(placing.pos)) {
            BlockPos blockPos = placing.pos;
            placeBlock();
            if (tryTicks > 40) {
                blockPos = NoppesUtilServer.GetClosePos(blockPos, npc.world);
                npc.setPositionAndUpdate(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5);
            }
        }
    }

    private String blockToString(BlockData data) {
        if (data.state.getBlock() == Blocks.AIR)
            return Items.IRON_PICKAXE.getRegistryName().toString();
        return itemToString(data.getStack());
    }

    @Override
    public void resetTask() {
        reset();
    }

    @Override
    public void reset() {
        build = null;
        npc.setJobData("");
    }

    public void placeBlock() {
        if (placing == null)
            return;
        npc.getNavigator().clearPath();
        npc.swingArm(EnumHand.MAIN_HAND);
        npc.world.setBlockState(placing.pos, placing.state, 2);
        if (placing.state.getBlock() instanceof ITileEntityProvider && placing.tile != null) {
            TileEntity tile = npc.world.getTileEntity(placing.pos);
            if (tile != null) {
                try {
                    tile.readFromNBT(placing.tile);
                } catch (Exception e) {

                }
            }
        }
        placing = null;
    }

    @Override
    public boolean isBuilding() {
        return build != null && build.enabled && !build.finished && build.started;
    }
}

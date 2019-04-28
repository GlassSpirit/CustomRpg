package noppes.npcs.objects.blocks.tiles;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import noppes.npcs.NBTTags;
import noppes.npcs.api.constants.JobType;
import noppes.npcs.controllers.SchematicController;
import noppes.npcs.controllers.data.Availability;
import noppes.npcs.controllers.data.BlockData;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.roles.JobBuilder;
import noppes.npcs.schematics.SchematicWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class TileBuilder extends TileEntity implements ITickable {
    private SchematicWrapper schematic = null;
    public int rotation = 0;
    public int yOffest = 0;
    public boolean enabled = false;
    public boolean started = false;
    public boolean finished = false;
    public Availability availability = new Availability();
    private Stack<Integer> positions = new Stack<Integer>();
    private Stack<Integer> positionsSecond = new Stack<Integer>();

    public static BlockPos DrawPos = null;
    public static boolean Compiled = false;

    private int ticks = 20;

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        if (compound.hasKey("SchematicName")) {
            schematic = SchematicController.Instance.load(compound.getString("SchematicName"));
        }

        Stack<Integer> positions = new Stack<Integer>();
        positions.addAll(NBTTags.getIntegerList(compound.getTagList("Positions", 10)));
        this.positions = positions;

        positions = new Stack<Integer>();
        positions.addAll(NBTTags.getIntegerList(compound.getTagList("PositionsSecond", 10)));
        this.positionsSecond = positions;

        readPartNBT(compound);
    }

    public void readPartNBT(NBTTagCompound compound) {
        rotation = compound.getInteger("Rotation");
        yOffest = compound.getInteger("YOffset");
        enabled = compound.getBoolean("Enabled");
        started = compound.getBoolean("Started");
        finished = compound.getBoolean("Finished");
        availability.readFromNBT(compound.getCompoundTag("Availability"));
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        if (schematic != null) {
            compound.setString("SchematicName", schematic.schema.getName());
        }
        compound.setTag("Positions", NBTTags.nbtIntegerCollection(new ArrayList<Integer>(positions)));
        compound.setTag("PositionsSecond", NBTTags.nbtIntegerCollection(new ArrayList<Integer>(positionsSecond)));
        writePartNBT(compound);
        return compound;
    }

    public NBTTagCompound writePartNBT(NBTTagCompound compound) {
        compound.setInteger("Rotation", rotation);
        compound.setInteger("YOffset", yOffest);
        compound.setBoolean("Enabled", enabled);
        compound.setBoolean("Started", started);
        compound.setBoolean("Finished", finished);
        compound.setTag("Availability", availability.writeToNBT(new NBTTagCompound()));
        return compound;
    }

    @SideOnly(value = Side.CLIENT)
    public void setDrawSchematic(SchematicWrapper schematics) {
        this.schematic = schematics;
    }

    public void setSchematic(SchematicWrapper schematics) {
        this.schematic = schematics;
        if (schematics == null) {
            positions.clear();
            positionsSecond.clear();
            return;
        }
        Stack<Integer> positions = new Stack<Integer>();
        for (int y = 0; y < schematics.schema.getHeight(); y++) {
            for (int z = 0; z < schematics.schema.getLength() / 2; z++) {
                for (int x = 0; x < schematics.schema.getWidth() / 2; x++) {
                    positions.add(0, xyzToIndex(x, y, z));
                }
            }
            for (int z = 0; z < schematics.schema.getLength() / 2; z++) {
                for (int x = schematics.schema.getWidth() / 2; x < schematics.schema.getWidth(); x++) {
                    positions.add(0, xyzToIndex(x, y, z));
                }
            }
            for (int z = schematics.schema.getLength() / 2; z < schematics.schema.getLength(); z++) {
                for (int x = 0; x < schematics.schema.getWidth() / 2; x++) {
                    positions.add(0, xyzToIndex(x, y, z));
                }
            }
            for (int z = schematics.schema.getLength() / 2; z < schematics.schema.getLength(); z++) {
                for (int x = schematics.schema.getWidth() / 2; x < schematics.schema.getWidth(); x++) {
                    positions.add(0, xyzToIndex(x, y, z));
                }
            }
        }
        this.positions = positions;
        positionsSecond.clear();
    }

    public int xyzToIndex(int x, int y, int z) {
        return (y * schematic.schema.getLength() + z) * schematic.schema.getWidth() + x;
    }

    public SchematicWrapper getSchematic() {
        return schematic;
    }

    public boolean hasSchematic() {
        return schematic != null;
    }

    @Override
    public void update() {
        if (this.world.isRemote || !hasSchematic() || finished)
            return;
        ticks--;
        if (ticks > 0)
            return;
        ticks = 200;
        if (positions.isEmpty() && positionsSecond.isEmpty()) {
            finished = true;
            return;
        }

        if (!started) {
            for (EntityPlayer player : getPlayerList()) {
                if (availability.isAvailable(player)) {
                    started = true;
                    break;
                }
            }
            if (!started)
                return;
        }

        List<EntityNPCInterface> list = world.getEntitiesWithinAABB(EntityNPCInterface.class, new AxisAlignedBB(getPos(), getPos()).grow(32, 32, 32));
        for (EntityNPCInterface npc : list) {
            if (npc.advanced.job == JobType.BUILDER) {
                JobBuilder job = (JobBuilder) npc.jobInterface;
                if (job.build == null) {
                    job.build = this;
                }

            }
        }
    }

    private List<EntityPlayer> getPlayerList() {
        return world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1).grow(10, 10, 10));
    }

    public Stack<BlockData> getBlock() {
        if (!enabled || finished || !hasSchematic())
            return null;
        boolean bo = positions.isEmpty();
        Stack<BlockData> list = new Stack<BlockData>();
        int size = schematic.schema.getWidth() * schematic.schema.getLength() / 4;
        if (size > 30)
            size = 30;
        for (int i = 0; i < size; i++) {
            if (positions.isEmpty() && !bo || positionsSecond.isEmpty() && bo)
                return list;

            int pos = bo ? positionsSecond.pop() : positions.pop();
            if (pos >= schematic.size) {
                continue;
            }
            int x = (pos % schematic.schema.getWidth());
            int z = ((pos - x) / schematic.schema.getWidth()) % schematic.schema.getLength();
            int y = (((pos - x) / schematic.schema.getWidth()) - z) / schematic.schema.getLength();

            IBlockState state = schematic.schema.getBlockState(x, y, z);
            if (!state.isFullBlock() && !bo && state.getBlock() != Blocks.AIR) {
                positionsSecond.add(0, pos);
                continue;
            }

            BlockPos blockPos = getPos().add(1, yOffest, 1).add(schematic.rotatePos(x, y, z, rotation));

            IBlockState original = world.getBlockState(blockPos);
            if (Block.getStateId(state) == Block.getStateId(original)) //If block is already set ignore
                continue;

            state = schematic.rotationState(state, rotation);
            NBTTagCompound tile = null;
            if (state.getBlock() instanceof ITileEntityProvider) {
                tile = schematic.getTileEntity(x, y, z, blockPos);
            }
            list.add(0, new BlockData(blockPos, state, tile));
        }
        return list;
    }

    public static void SetDrawPos(BlockPos pos) {
        DrawPos = pos;
        Compiled = false;
    }
}

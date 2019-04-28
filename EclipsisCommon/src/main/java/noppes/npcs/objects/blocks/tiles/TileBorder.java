package noppes.npcs.objects.blocks.tiles;

import com.google.common.base.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import noppes.npcs.controllers.data.Availability;
import noppes.npcs.objects.NpcObjects;
import noppes.npcs.objects.blocks.BlockBorder;

import java.util.List;

public class TileBorder extends TileNpcEntity implements Predicate, ITickable {
    public Availability availability = new Availability();
    public AxisAlignedBB boundingbox;
    public int rotation = 0;
    public int height = 10;
    public String message = "availability.areaNotAvailble";

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        readExtraNBT(compound);
        getWorld().setBlockState(this.getPos(), NpcObjects.borderBlock.getDefaultState().withProperty(BlockBorder.ROTATION, rotation));
    }

    public void readExtraNBT(NBTTagCompound compound) {
        availability.readFromNBT(compound.getCompoundTag("BorderAvailability"));
        rotation = compound.getInteger("BorderRotation");
        height = compound.getInteger("BorderHeight");
        message = compound.getString("BorderMessage");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        writeExtraNBT(compound);
        return super.writeToNBT(compound);
    }

    public void writeExtraNBT(NBTTagCompound compound) {
        compound.setTag("BorderAvailability", availability.writeToNBT(new NBTTagCompound()));
        compound.setInteger("BorderRotation", rotation);
        compound.setInteger("BorderHeight", height);
        compound.setString("BorderMessage", message);
    }

    @Override
    public void update() {
        if (world.isRemote)
            return;
        AxisAlignedBB box = new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + height + 1, pos.getZ() + 1);
        List<Entity> list = world.getEntitiesWithinAABB(Entity.class, box, this);
        for (Entity entity : list) {
            if (entity instanceof EntityEnderPearl) {
                EntityEnderPearl pearl = (EntityEnderPearl) entity;
                if (pearl.getThrower() instanceof EntityPlayer && !availability.isAvailable((EntityPlayer) pearl.getThrower()))
                    entity.isDead = true;
                continue;
            }
            EntityPlayer player = (EntityPlayer) entity;
            if (availability.isAvailable(player))
                continue;
            BlockPos pos2 = new BlockPos(pos);
            if (rotation == 2) {
                pos2 = pos2.south();
            } else if (rotation == 0) {
                pos2 = pos2.north();
            } else if (rotation == 1) {
                pos2 = pos2.east();
            } else if (rotation == 3) {
                pos2 = pos2.west();
            }
            while (!world.isAirBlock(pos2)) {
                pos2 = pos2.up();
            }
            player.setPositionAndUpdate(pos2.getX() + 0.5, pos2.getY(), pos2.getZ() + 0.5);
            if (!message.isEmpty())
                player.sendStatusMessage(new TextComponentTranslation(message), true);
        }
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        handleUpdateTag(pkt.getNbtCompound());
    }

    @Override
    public void handleUpdateTag(NBTTagCompound compound) {
        rotation = compound.getInteger("Rotation");
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
        compound.setInteger("Rotation", rotation);
        return compound;
    }

    public boolean isEntityApplicable(Entity var1) {
        return var1 instanceof EntityPlayerMP || var1 instanceof EntityEnderPearl;
    }

    @Override
    public boolean apply(Object ob) {
        return isEntityApplicable((Entity) ob);
    }
}

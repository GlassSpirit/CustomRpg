package noppes.npcs.roles;

import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.CustomNpcs;
import noppes.npcs.api.entity.ICustomNpc;
import noppes.npcs.api.entity.data.role.IJobFollower;
import noppes.npcs.entity.EntityNPCInterface;

import java.util.List;

public class JobFollower extends JobInterface implements IJobFollower {
    public EntityNPCInterface following = null;
    private int ticks = 40;
    private int range = 20;
    public String name = "";

    public JobFollower(EntityNPCInterface npc) {
        super(npc);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setString("FollowingEntityName", name);
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        name = compound.getString("FollowingEntityName");

    }

    @Override
    public boolean aiShouldExecute() {
        if (npc.isAttacking())
            return false;

        ticks--;
        if (ticks > 0)
            return false;

        ticks = 10;
        following = null;
        List<EntityNPCInterface> list = npc.world.getEntitiesWithinAABB(EntityNPCInterface.class, npc.getEntityBoundingBox().grow(getRange(), getRange(), getRange()));
        for (EntityNPCInterface entity : list) {
            if (entity == npc || entity.isKilled())
                continue;
            if (entity.display.getName().equalsIgnoreCase(name)) {
                following = entity;
                break;
            }
        }

        return false;
    }

    private int getRange() {
        if (range > CustomNpcs.NpcNavRange)
            return CustomNpcs.NpcNavRange;
        return range;
    }

    @Override
    public boolean isFollowing() {
        return following != null;
    }

    public void reset() {
    }

    public void resetTask() {
        following = null;
    }

    public boolean hasOwner() {
        return !name.isEmpty();
    }

    @Override
    public String getFollowing() {
        return name;
    }

    @Override
    public void setFollowing(String name) {
        this.name = name;
    }

    @Override
    public ICustomNpc getFollowingNpc() {
        if (following == null)
            return null;
        return following.wrappedNPC;
    }
}

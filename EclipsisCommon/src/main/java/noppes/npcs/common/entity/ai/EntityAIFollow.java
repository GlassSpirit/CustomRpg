package noppes.npcs.common.entity.ai;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import noppes.npcs.api.constants.AnimationType;
import noppes.npcs.constants.AiMutex;
import noppes.npcs.common.entity.EntityNPCInterface;

public class EntityAIFollow extends EntityAIBase {
    private EntityNPCInterface npc;
    private EntityLivingBase owner;
    public int updateTick = 0;

    public EntityAIFollow(EntityNPCInterface npc) {
        this.npc = npc;
        this.setMutexBits(AiMutex.PASSIVE + AiMutex.LOOK);
    }

    @Override
    public boolean shouldExecute() {
        if (!canExcute())
            return false;
        return !npc.isInRange(owner, npc.followRange());
    }

    public boolean canExcute() {
        return npc.isEntityAlive() && npc.isFollower() && !npc.isAttacking() && (owner = npc.getOwner()) != null && npc.ais.animationType != AnimationType.SIT;
    }

    @Override
    public void startExecuting() {
        updateTick = 10;
    }

    @Override
    public boolean shouldContinueExecuting() {
        return !this.npc.getNavigator().noPath() && !npc.isInRange(owner, 2) && canExcute();
    }

    @Override
    public void resetTask() {
        this.owner = null;
        this.npc.getNavigator().clearPath();
    }

    @Override
    public void updateTask() {
        updateTick++;
        if (updateTick < 10)
            return;
        updateTick = 0;
        this.npc.getLookHelper().setLookPositionWithEntity(owner, 10.0F, (float) this.npc.getVerticalFaceSpeed());

        double distance = npc.getDistanceSq(owner);
        double speed = 1 + distance / 150;
        if (speed > 3)
            speed = 3;
        if (owner.isSprinting())
            speed += 0.5f;
        if (this.npc.getNavigator().tryMoveToEntityLiving(owner, speed) || npc.isInRange(owner, 16))
            return;

        npc.tpTo(owner);
    }
}

package noppes.npcs.common.entity.ai;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.util.math.Vec3d;
import noppes.npcs.constants.AiMutex;
import noppes.npcs.common.entity.EntityNPCInterface;

public class EntityAIDodgeShoot extends EntityAIBase {
    private EntityNPCInterface entity;
    private double x;
    private double y;
    private double zPosition;

    public EntityAIDodgeShoot(EntityNPCInterface iNpc) {
        this.entity = iNpc;
        this.setMutexBits(AiMutex.PASSIVE);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    @Override
    public boolean shouldExecute() {
        EntityLivingBase var1 = this.entity.getAttackTarget();

        if (var1 == null || !var1.isEntityAlive()) {
            return false;
        }
        if (this.entity.inventory.getProjectile() == null) {
            return false;
        } else if (this.entity.getRangedTask() == null) {
            return false;
        } else {

            Vec3d vec = this.entity.getRangedTask().hasFired() ? RandomPositionGenerator.findRandomTarget(this.entity, 4, 1) : null;

            if (vec == null) {
                return false;
            } else {
                this.x = vec.x;
                this.y = vec.y;
                this.zPosition = vec.z;
                return true;
            }
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    @Override
    public boolean shouldContinueExecuting() {
        return !this.entity.getNavigator().noPath();
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    @Override
    public void startExecuting() {
        this.entity.getNavigator().tryMoveToXYZ(this.x, this.y, this.zPosition, 1.2D);
    }

    /**
     * Updates the task
     */
    @Override
    public void updateTask() {
        if (this.entity.getAttackTarget() != null)
            this.entity.getLookHelper().setLookPositionWithEntity(this.entity.getAttackTarget(), 30.0F, 30.0F);
    }
}

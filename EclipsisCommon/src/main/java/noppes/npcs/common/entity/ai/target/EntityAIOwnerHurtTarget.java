package noppes.npcs.common.entity.ai.target;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAITarget;
import noppes.npcs.constants.AiMutex;
import noppes.npcs.common.entity.EntityNPCInterface;

public class EntityAIOwnerHurtTarget extends EntityAITarget {
    EntityNPCInterface npc;
    EntityLivingBase theTarget;
    private int field_142050_e;

    public EntityAIOwnerHurtTarget(EntityNPCInterface npc) {
        super(npc, false);
        this.npc = npc;
        this.setMutexBits(AiMutex.PASSIVE);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    @Override
    public boolean shouldExecute() {
        if (!this.npc.isFollower() || !(npc.roleInterface != null && npc.roleInterface.defendOwner())) {
            return false;
        } else {
            EntityLivingBase entitylivingbase = this.npc.getOwner();

            if (entitylivingbase == null) {
                return false;
            } else {
                this.theTarget = entitylivingbase.getLastAttackedEntity();
                int i = entitylivingbase.getLastAttackedEntityTime();
                return i != this.field_142050_e && this.isSuitableTarget(this.theTarget, false);
            }
        }
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    @Override
    public void startExecuting() {
        this.taskOwner.setAttackTarget(this.theTarget);
        EntityLivingBase entitylivingbase = this.npc.getOwner();

        if (entitylivingbase != null) {
            this.field_142050_e = entitylivingbase.getLastAttackedEntityTime();
        }

        super.startExecuting();
    }
}
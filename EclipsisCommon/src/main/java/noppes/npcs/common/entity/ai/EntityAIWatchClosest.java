package noppes.npcs.common.entity.ai;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import noppes.npcs.constants.AiMutex;
import noppes.npcs.common.entity.EntityNPCInterface;

public class EntityAIWatchClosest extends EntityAIBase {
    private EntityNPCInterface npc;

    /**
     * The closest entity which is being watched by this one.
     */
    protected Entity closestEntity;
    private float field_75333_c;
    private int lookTime;
    private float field_75331_e;
    private Class watchedClass;

    public EntityAIWatchClosest(EntityNPCInterface par1EntityLiving, Class par2Class, float par3) {
        this.npc = par1EntityLiving;
        this.watchedClass = par2Class;
        this.field_75333_c = par3;
        this.field_75331_e = 0.002F;
        this.setMutexBits(AiMutex.LOOK);
    }

    @Override
    public boolean shouldExecute() {
        if (this.npc.getRNG().nextFloat() >= this.field_75331_e || npc.isInteracting()) {
            return false;
        }

        if (this.npc.getAttackTarget() != null) {
            this.closestEntity = this.npc.getAttackTarget();
        }

        if (this.watchedClass == EntityPlayer.class) {
            this.closestEntity = this.npc.world.getClosestPlayerToEntity(this.npc, (double) this.field_75333_c);
        } else {
            this.closestEntity = this.npc.world.findNearestEntityWithinAABB(this.watchedClass, this.npc.getEntityBoundingBox().grow((double) this.field_75333_c, 3.0D, (double) this.field_75333_c), this.npc);
            if (this.closestEntity != null) {
                return this.npc.canSee(this.closestEntity);
            }
        }

        return this.closestEntity != null;

    }

    @Override
    public boolean shouldContinueExecuting() {
        if (npc.isInteracting() || npc.isAttacking() || !this.closestEntity.isEntityAlive() || !npc.isEntityAlive())
            return false;
        return this.npc.isInRange(this.closestEntity, field_75333_c) && this.lookTime > 0;
    }

    @Override
    public void startExecuting() {
        this.lookTime = 60 + this.npc.getRNG().nextInt(60);
    }

    @Override
    public void resetTask() {
        this.closestEntity = null;
    }

    @Override
    public void updateTask() {
        this.npc.getLookHelper().setLookPosition(this.closestEntity.posX, this.closestEntity.posY + (double) this.closestEntity.getEyeHeight(), this.closestEntity.posZ, 10.0F, (float) this.npc.getVerticalFaceSpeed());
        --this.lookTime;
    }
}

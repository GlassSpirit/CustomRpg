package noppes.npcs.common.entity.ai;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import noppes.npcs.constants.AiMutex;
import noppes.npcs.common.entity.EntityNPCInterface;

public class EntityAIAttackTarget extends EntityAIBase {
    private World world;
    private EntityNPCInterface npc;
    private EntityLivingBase entityTarget;

    private int attackTick;

    private Path entityPathEntity;
    private int field_75445_i;
    private boolean navOverride = false;

    public EntityAIAttackTarget(EntityNPCInterface par1EntityLiving) {
        this.attackTick = 0;
        this.npc = par1EntityLiving;
        this.world = par1EntityLiving.world;
        this.setMutexBits(this.navOverride ? AiMutex.PATHING : AiMutex.LOOK + AiMutex.PASSIVE);
    }

    @Override
    public boolean shouldExecute() {
        EntityLivingBase entitylivingbase = this.npc.getAttackTarget();

        if (entitylivingbase == null || !entitylivingbase.isEntityAlive()) {
            return false;
        }

        int melee = this.npc.stats.ranged.getMeleeRange();
        if (this.npc.inventory.getProjectile() != null && (melee <= 0 || !this.npc.isInRange(entitylivingbase, melee))) {
            return false;
        }

        this.entityTarget = entitylivingbase;
        this.entityPathEntity = this.npc.getNavigator().getPathToEntityLiving(entitylivingbase);
        return this.entityPathEntity != null;

    }

    @Override
    public boolean shouldContinueExecuting() {
        this.entityTarget = this.npc.getAttackTarget();
        if (entityTarget == null)
            entityTarget = this.npc.getRevengeTarget();

        if (entityTarget == null || !entityTarget.isEntityAlive())
            return false;
        if (!npc.isInRange(entityTarget, npc.stats.aggroRange))
            return false;
        int melee = this.npc.stats.ranged.getMeleeRange();
        if (melee > 0 && !npc.isInRange(entityTarget, melee))
            return false;

        return this.npc.isWithinHomeDistanceFromPosition(new BlockPos(entityTarget));
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    @Override
    public void startExecuting() {
        if (!navOverride)
            this.npc.getNavigator().setPath(this.entityPathEntity, 1.3D);
        this.field_75445_i = 0;
    }

    /**
     * Resets the task
     */
    @Override
    public void resetTask() {
        this.entityPathEntity = null;
        this.entityTarget = null;
        this.npc.getNavigator().clearPath();
    }

    /**
     * Updates the task
     */
    @Override
    public void updateTask() {
        this.npc.getLookHelper().setLookPositionWithEntity(this.entityTarget, 30.0F, 30.0F);

        if (!navOverride && --this.field_75445_i <= 0) {
            this.field_75445_i = 4 + this.npc.getRNG().nextInt(7);
            this.npc.getNavigator().tryMoveToEntityLiving(this.entityTarget, 1.3f);
        }

        this.attackTick = Math.max(this.attackTick - 1, 0);
        double y = this.entityTarget.posY;
        if (this.entityTarget.getEntityBoundingBox() != null)
            y = this.entityTarget.getEntityBoundingBox().minY;
        double distance = this.npc.getDistanceSq(this.entityTarget.posX, y, this.entityTarget.posZ);
        double range = npc.stats.melee.getRange() * npc.stats.melee.getRange() + entityTarget.width;
        double minRange = this.npc.width * 2.0F * this.npc.width * 2.0F + entityTarget.width;
        if (minRange > range)
            range = minRange;

        if (distance <= range && (npc.canSee(this.entityTarget) || distance < minRange)) {
            if (this.attackTick <= 0) {
                this.attackTick = this.npc.stats.melee.getDelay();
                npc.swingArm(EnumHand.MAIN_HAND);
                this.npc.attackEntityAsMob(this.entityTarget);
            }
        }
    }

    public void navOverride(boolean nav) {
        this.navOverride = nav;
        this.setMutexBits(this.navOverride ? AiMutex.PATHING : AiMutex.LOOK + AiMutex.PASSIVE);
    }
}

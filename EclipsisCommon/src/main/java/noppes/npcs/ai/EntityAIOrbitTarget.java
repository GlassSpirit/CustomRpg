package noppes.npcs.ai;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.math.MathHelper;
import noppes.npcs.constants.AiMutex;
import noppes.npcs.entity.EntityNPCInterface;

import java.util.Random;

public class EntityAIOrbitTarget extends EntityAIBase {
    private EntityNPCInterface npc;
    private EntityLivingBase targetEntity;
    private double movePosX;
    private double movePosY;
    private double movePosZ;
    private double speed;
    private float distance;
    private int delay = 0;
    private float angle = 0;
    private int direction = 1;
    private float targetDistance;
    private boolean decay;
    private boolean canNavigate = true;
    private float decayRate = 1.0F;
    private int tick = 0;

    public EntityAIOrbitTarget(EntityNPCInterface par1EntityCreature, double par2, boolean par5) {
        this.npc = par1EntityCreature;
        this.speed = par2;
        this.decay = par5;
        this.setMutexBits(AiMutex.PASSIVE + AiMutex.LOOK);
    }

    @Override
    public boolean shouldExecute() {
        if (--delay > 0) {
            return false;
        }
        delay = 10;
        this.targetEntity = this.npc.getAttackTarget();

        if (this.targetEntity == null) {
            return false;
        }
        if (decay)
            distance = npc.ais.getTacticalRange();
        else
            distance = npc.stats.ranged.getRange();

        return !npc.isInRange(targetEntity, distance / 2) && (this.npc.inventory.getProjectile() != null || npc.isInRange(targetEntity, distance));
    }

    @Override
    public boolean shouldContinueExecuting() {
        return this.targetEntity.isEntityAlive() && !npc.isInRange(targetEntity, distance / 2) && npc.isInRange(targetEntity, distance * 1.5) && !this.npc.isInWater() && this.canNavigate;
    }

    @Override
    public void resetTask() {
        this.npc.getNavigator().clearPath();
        this.delay = 60;
        if (this.npc.getRangedTask() != null) {
            this.npc.getRangedTask().navOverride(false);
        }
    }

    @Override
    public void startExecuting() {
        this.canNavigate = true;
        Random random = this.npc.getRNG();
        this.direction = random.nextInt(10) > 5 ? 1 : -1;
        this.decayRate = random.nextFloat() + (this.distance / 16.0F);
        this.targetDistance = this.npc.getDistance(this.targetEntity);
        double d0 = this.npc.posX - this.targetEntity.posX;
        double d1 = this.npc.posZ - this.targetEntity.posZ;
        this.angle = (float) (Math.atan2(d1, d0) * 180.0F / Math.PI);
        if (this.npc.getRangedTask() != null) {
            this.npc.getRangedTask().navOverride(true);
        }
    }

    @Override
    public void updateTask() {
        this.npc.getLookHelper().setLookPositionWithEntity(targetEntity, 30.0F, 30.0F);
        if (this.npc.getNavigator().noPath() && this.tick >= 0) {
            if (this.npc.onGround && !this.npc.isInWater()) {
                double d0 = this.targetDistance * (double) (MathHelper.cos(angle / 180.0F * (float) Math.PI));
                double d1 = this.targetDistance * (double) (MathHelper.sin(angle / 180.0F * (float) Math.PI));
                this.movePosX = this.targetEntity.posX + d0;
                this.movePosY = this.targetEntity.getEntityBoundingBox().maxY;
                this.movePosZ = this.targetEntity.posZ + d1;
                this.npc.getNavigator().tryMoveToXYZ(this.movePosX, this.movePosY, this.movePosZ, this.speed);
                this.angle += 15.0F * this.direction;
                this.tick = MathHelper.ceil(this.npc.getDistance(this.movePosX, this.movePosY, this.movePosZ) / (this.npc.getSpeed() / 20.0F));
                if (this.decay) {
                    this.targetDistance -= decayRate;
                }
            }
        }

        if (this.tick >= 0)
            this.tick--;
    }

}

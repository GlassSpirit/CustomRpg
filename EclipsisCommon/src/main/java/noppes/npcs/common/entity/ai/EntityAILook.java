package noppes.npcs.common.entity.ai;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import noppes.npcs.constants.AiMutex;
import noppes.npcs.common.entity.EntityNPCInterface;

import java.util.Iterator;

public class EntityAILook extends EntityAIBase {
    private final EntityNPCInterface npc;
    private int idle = 0;
    private double lookX;
    private double lookZ;
    boolean rotatebody;

    private boolean forced = false;
    private Entity forcedEntity = null;

    public EntityAILook(EntityNPCInterface npc) {
        this.npc = npc;
        this.setMutexBits(AiMutex.LOOK);
    }

    @Override
    public boolean shouldExecute() {
        return !npc.isAttacking() && npc.getNavigator().noPath() && !npc.isPlayerSleeping() && npc.isEntityAlive();
    }

    @Override
    public void startExecuting() {
        rotatebody = npc.ais.getStandingType() == 0 || npc.ais.getStandingType() == 3;
    }

    public void rotate(Entity entity) {
        forced = true;
        forcedEntity = entity;
    }

    public void rotate(int degrees) {
        forced = true;
        npc.rotationYawHead = npc.rotationYaw = npc.renderYawOffset = degrees;
    }

    @Override
    public void resetTask() {
        rotatebody = false;
        forced = false;
        forcedEntity = null;
    }

    @Override
    public void updateTask() {
        Entity lookat = null;
        if (forced && forcedEntity != null) {
            lookat = forcedEntity;
        } else if (npc.isInteracting()) {
            Iterator<EntityLivingBase> ita = npc.interactingEntities.iterator();
            double closestDistance = 12;
            while (ita.hasNext()) {
                EntityLivingBase entity = ita.next();
                double distance = entity.getDistanceSq(npc);
                if (distance < closestDistance) {
                    closestDistance = entity.getDistanceSq(npc);
                    lookat = entity;
                } else if (distance > 12)
                    ita.remove();
            }
        } else if (npc.ais.getStandingType() == 2) {
            lookat = npc.world.getClosestPlayerToEntity(npc, 16);
        }

        if (lookat != null) {
            npc.getLookHelper().setLookPositionWithEntity(lookat, 10F, npc.getVerticalFaceSpeed());
            return;
        }

        if (rotatebody) {
            if (idle == 0 && npc.getRNG().nextFloat() < 0.004F) {
                double var1 = (Math.PI * 2D) * this.npc.getRNG().nextDouble();
                if (npc.ais.getStandingType() == 3)
                    var1 = Math.PI / 180 * npc.ais.orientation + Math.PI * 0.2 + Math.PI * 0.6 * this.npc.getRNG().nextDouble();
                this.lookX = Math.cos(var1);
                this.lookZ = Math.sin(var1);
                this.idle = 20 + this.npc.getRNG().nextInt(20);
            }
            if (idle > 0) {
                idle--;
                npc.getLookHelper().setLookPosition(npc.posX + lookX, npc.posY + npc.getEyeHeight(), npc.posZ + lookZ, 10F, npc.getVerticalFaceSpeed());
            }
        }

        if (npc.ais.getStandingType() == 1 && !forced) {
            npc.rotationYawHead = npc.rotationYaw = npc.renderYawOffset = npc.ais.orientation;
        }
    }
}

package noppes.npcs.ai;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import noppes.npcs.CustomNpcsConfig;
import noppes.npcs.ai.selector.NPCInteractSelector;
import noppes.npcs.constants.AiMutex;
import noppes.npcs.controllers.data.Line;
import noppes.npcs.entity.EntityNPCInterface;

import java.util.Iterator;
import java.util.List;

public class EntityAIWander extends EntityAIBase {
    private EntityNPCInterface entity;
    public final NPCInteractSelector selector;
    private double x;
    private double y;
    private double zPosition;
    private EntityNPCInterface nearbyNPC;

    public EntityAIWander(EntityNPCInterface npc) {
        this.entity = npc;
        this.setMutexBits(AiMutex.PASSIVE);
        selector = new NPCInteractSelector(npc);
    }

    @Override
    public boolean shouldExecute() {
        if (this.entity.getIdleTime() >= 100 || !entity.getNavigator().noPath() || entity.isInteracting() ||
                this.entity.ais.movingPause && this.entity.getRNG().nextInt(80) != 0) {
            return false;
        }
        if (entity.ais.npcInteracting && this.entity.getRNG().nextInt(this.entity.ais.movingPause ? 6 : 16) == 1)
            nearbyNPC = getNearbyNPC();

        if (nearbyNPC != null) {
            this.x = MathHelper.floor(nearbyNPC.posX);
            this.y = MathHelper.floor(nearbyNPC.posY);
            this.zPosition = MathHelper.floor(nearbyNPC.posZ);
            nearbyNPC.addInteract(entity);
        } else {
            Vec3d vec = getVec();
            if (vec == null) {
                return false;
            } else {
                this.x = vec.x;
                this.y = vec.y;
                if (entity.ais.movementType == 1)
                    this.y = entity.getStartYPos() + entity.getRNG().nextFloat() * 0.75 * entity.ais.walkingRange;
                this.zPosition = vec.z;
            }
        }
        return true;
    }

    @Override
    public void updateTask() {
        if (nearbyNPC != null) {
            nearbyNPC.getNavigator().clearPath();
        }
    }

    private EntityNPCInterface getNearbyNPC() {
        List<Entity> list = entity.world.getEntitiesInAABBexcluding(entity, entity.getEntityBoundingBox().grow(entity.ais.walkingRange, entity.ais.walkingRange > 7 ? 7 : entity.ais.walkingRange, entity.ais.walkingRange), selector);
        Iterator<Entity> ita = list.iterator();
        while (ita.hasNext()) {
            EntityNPCInterface npc = (EntityNPCInterface) ita.next();
            if (!npc.ais.stopAndInteract || npc.isAttacking() || !npc.isEntityAlive() || entity.faction.isAggressiveToNpc(npc))
                ita.remove();
        }

        if (list.isEmpty())
            return null;

        return (EntityNPCInterface) list.get(entity.getRNG().nextInt(list.size()));
    }

    private Vec3d getVec() {
        if (entity.ais.walkingRange > 0) {
            BlockPos start = new BlockPos(this.entity.getStartXPos(), this.entity.getStartYPos(), this.entity.getStartZPos());
            int distance = (int) MathHelper.sqrt(this.entity.getDistanceSq(start));
            int range = this.entity.ais.walkingRange - distance;
            if (range > CustomNpcsConfig.NpcNavRange)
                range = CustomNpcsConfig.NpcNavRange;
            if (range < 3) {
                range = this.entity.ais.walkingRange;
                if (range > CustomNpcsConfig.NpcNavRange)
                    range = CustomNpcsConfig.NpcNavRange;
                Vec3d pos2 = new Vec3d((entity.posX + start.getX()) / 2, (entity.posY + start.getY()) / 2, (entity.posZ + start.getZ()) / 2);
                return RandomPositionGenerator.findRandomTargetBlockTowards(entity, distance / 2, distance / 2 > 7 ? 7 : distance / 2, pos2);
            } else {
                return RandomPositionGenerator.findRandomTarget(this.entity, range / 2, range / 2 > 7 ? 7 : range / 2);
            }
        }
        return RandomPositionGenerator.findRandomTarget(this.entity, CustomNpcsConfig.NpcNavRange, 7);
    }

    @Override
    public boolean shouldContinueExecuting() {
        if (nearbyNPC != null && (!selector.apply(nearbyNPC) || entity.isInRange(nearbyNPC, entity.width)))
            return false;
        return !this.entity.getNavigator().noPath() && this.entity.isEntityAlive() && !entity.isInteracting();
    }

    @Override
    public void startExecuting() {
        this.entity.getNavigator().tryMoveToXYZ(this.x, this.y, this.zPosition, 1);
    }

    @Override
    public void resetTask() {
        if (nearbyNPC != null && entity.isInRange(nearbyNPC, 3.5)) {
            EntityNPCInterface talk = entity;
            if (entity.getRNG().nextBoolean())
                talk = nearbyNPC;
            Line line = talk.advanced.getNPCInteractLine();
            if (line == null)
                line = new Line(".........");
            line.hideText = true;
            talk.saySurrounding(line);

            entity.addInteract(nearbyNPC);
            nearbyNPC.addInteract(entity);
        }
        nearbyNPC = null;
    }
}

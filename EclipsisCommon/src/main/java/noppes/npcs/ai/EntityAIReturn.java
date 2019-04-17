package noppes.npcs.ai;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import noppes.npcs.CustomNpcs;
import noppes.npcs.constants.AiMutex;
import noppes.npcs.entity.EntityNPCInterface;

public class EntityAIReturn extends EntityAIBase {
    public static final int MaxTotalTicks = 600;
    private final EntityNPCInterface npc;
    private int stuckTicks = 0;
    private int totalTicks = 0;
    private double endPosX;
    private double endPosY;
    private double endPosZ;
    private boolean wasAttacked = false;
    private double[] preAttackPos;
    private int stuckCount = 0;

    public EntityAIReturn(EntityNPCInterface npc) {
        this.npc = npc;
        this.setMutexBits(AiMutex.PASSIVE);
    }

    @Override
    public boolean shouldExecute() {
        if (npc.hasOwner() || !npc.ais.shouldReturnHome() || npc.isKilled() || !npc.getNavigator().noPath() || npc.isInteracting()) {
            return false;
        }

        if (npc.ais.findShelter == 0 && (!npc.world.isDaytime() || npc.world.isRaining()) && !npc.world.provider.hasSkyLight()) {
            BlockPos pos = new BlockPos(npc.getStartXPos(), npc.getStartYPos(), npc.getStartZPos());
            if (npc.world.canSeeSky(pos) || npc.world.getLight(pos) <= 8) {
                return false;
            }
        } else if (npc.ais.findShelter == 1 && npc.world.isDaytime()) {
            BlockPos pos = new BlockPos(npc.getStartXPos(), npc.getStartYPos(), npc.getStartZPos());
            if (npc.world.canSeeSky(pos)) {
                return false;
            }
        }

        if (npc.isAttacking()) {
            if (!wasAttacked) {
                wasAttacked = true;
                preAttackPos = new double[]{npc.posX, npc.posY, npc.posZ};
            }
            return false;
        }
        if (!npc.isAttacking() && wasAttacked) {
            return true;
        }
        if (npc.ais.getMovingType() == 2 && npc.ais.getDistanceSqToPathPoint() < CustomNpcs.NpcNavRange * CustomNpcs.NpcNavRange)
            return false;

        if (npc.ais.getMovingType() == 1) {
            double x = npc.posX - npc.getStartXPos();
            double z = npc.posX - npc.getStartZPos();

            return !npc.isInRange(npc.getStartXPos(), -1, npc.getStartZPos(), npc.ais.walkingRange);
        }
        if (npc.ais.getMovingType() == 0)
            return !this.npc.isVeryNearAssignedPlace();

        return false;
    }

    @Override
    public boolean shouldContinueExecuting() {
        if (npc.isFollower() || npc.isKilled() || npc.isAttacking() || npc.isVeryNearAssignedPlace() || npc.isInteracting())
            return false;
        if (npc.getNavigator().noPath() && wasAttacked && !isTooFar())
            return false;
        return totalTicks <= MaxTotalTicks;
    }

    @Override
    public void updateTask() {
        totalTicks++;
        if (totalTicks > MaxTotalTicks) {
            npc.setPosition(endPosX, endPosY, endPosZ);
            npc.getNavigator().clearPath();
            return;
        }

        if (stuckTicks > 0) {
            stuckTicks--;
        } else if (npc.getNavigator().noPath()) {
            stuckCount++;
            stuckTicks = 10;
            if (totalTicks > 30 && wasAttacked && isTooFar() || stuckCount > 5) {
                npc.setPosition(endPosX, endPosY, endPosZ);
                npc.getNavigator().clearPath();
            } else
                navigate(stuckCount % 2 == 1);
        } else {
            stuckCount = 0;
        }
    }

    private boolean isTooFar() {
        int allowedDistance = npc.stats.aggroRange * 2;
        if (npc.ais.getMovingType() == 1)
            allowedDistance += npc.ais.walkingRange;

        double x = npc.posX - endPosX;
        double z = npc.posX - endPosZ;
        return x * x + z * z > allowedDistance * allowedDistance;
    }

    @Override
    public void startExecuting() {
        stuckTicks = 0;
        totalTicks = 0;
        stuckCount = 0;
        navigate(false);
    }

    private void navigate(boolean towards) {
        if (!wasAttacked) {
            endPosX = npc.getStartXPos();
            endPosY = npc.getStartYPos();
            endPosZ = npc.getStartZPos();
        } else {
            endPosX = preAttackPos[0];
            endPosY = preAttackPos[1];
            endPosZ = preAttackPos[2];
        }
        double posX = endPosX;
        double posY = endPosY;
        double posZ = endPosZ;
        double range = npc.getDistance(posX, posY, posZ);
        if (range > CustomNpcs.NpcNavRange || towards) {
            int distance = (int) range;
            if (distance > CustomNpcs.NpcNavRange)
                distance = CustomNpcs.NpcNavRange / 2;
            else
                distance /= 2;
            if (distance > 2) {
                Vec3d start = new Vec3d(posX, posY, posZ);
                Vec3d pos = RandomPositionGenerator.findRandomTargetBlockTowards(npc, distance, distance / 2 > 7 ? 7 : distance / 2, start);
                if (pos != null) {
                    posX = pos.x;
                    posY = pos.y;
                    posZ = pos.z;
                }
            }
        }
        npc.getNavigator().clearPath();
        npc.getNavigator().tryMoveToXYZ(posX, posY, posZ, 1);
    }

    @Override
    public void resetTask() {
        wasAttacked = false;
        this.npc.getNavigator().clearPath();
    }
}

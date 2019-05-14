package noppes.npcs.common.entity.data;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.math.BlockPos;
import noppes.npcs.util.NBTTags;
import noppes.npcs.api.CustomNPCsException;
import noppes.npcs.api.constants.AnimationType;
import noppes.npcs.api.constants.JobType;
import noppes.npcs.api.constants.TacticalType;
import noppes.npcs.api.entity.data.INPCAi;
import noppes.npcs.common.entity.EntityNPCInterface;
import noppes.npcs.roles.JobBuilder;
import noppes.npcs.roles.JobFarmer;

import java.util.ArrayList;
import java.util.List;

public class DataAI implements INPCAi {
    private EntityNPCInterface npc;

    public int onAttack = 0; //0:fight 1:panic 2:retreat 3:nothing
    public int doorInteract = 2; //0:break 1:open 2:nothing
    public int findShelter = 2;
    public boolean canSwim = true;
    public boolean reactsToFire = false;
    public boolean avoidsWater = false;
    public boolean avoidsSun = false;
    public boolean returnToStart = true;
    public boolean directLOS = true;
    public boolean canLeap = false;
    public boolean canSprint = false;
    public boolean stopAndInteract = true;
    public boolean attackInvisible = false;
    public int tacticalVariant = TacticalType.DEFAULT;
    private int tacticalRadius = 8;

    public int movementType = 0; //0:ground, 1:flying, 2:swimming

    public int animationType = AnimationType.NORMAL;
    private int standingType = 0;
    private int movingType = 0;
    public boolean npcInteracting = true;

    public int orientation = 0;
    public float bodyOffsetX = 5, bodyOffsetY = 5, bodyOffsetZ = 5;
    public int walkingRange = 10;
    private int moveSpeed = 5;

    private List<int[]> movingPath = new ArrayList<>();
    private BlockPos startPos = null;
    public int movingPos = 0;
    public int movingPattern = 0; // 0:Looping 1:Backtracking

    public boolean movingPause = true;

    public DataAI(EntityNPCInterface npc) {
        this.npc = npc;
    }

    public void readToNBT(NBTTagCompound compound) {
        canSwim = compound.getBoolean("CanSwim");
        reactsToFire = compound.getBoolean("ReactsToFire");
        setAvoidsWater(compound.getBoolean("AvoidsWater"));
        avoidsSun = compound.getBoolean("AvoidsSun");
        returnToStart = compound.getBoolean("ReturnToStart");
        onAttack = compound.getInteger("OnAttack");
        doorInteract = compound.getInteger("DoorInteract");
        findShelter = compound.getInteger("FindShelter");
        directLOS = compound.getBoolean("DirectLOS");
        canLeap = compound.getBoolean("CanLeap");
        canSprint = compound.getBoolean("CanSprint");
        tacticalRadius = compound.getInteger("TacticalRadius");
        movingPause = compound.getBoolean("MovingPause");
        npcInteracting = compound.getBoolean("npcInteracting");
        stopAndInteract = compound.getBoolean("stopAndInteract");

        movementType = compound.getInteger("MovementType");

        animationType = compound.getInteger("MoveState");
        standingType = compound.getInteger("StandingState");
        movingType = compound.getInteger("MovingState");
        tacticalVariant = compound.getInteger("TacticalVariant");

        orientation = compound.getInteger("Orientation");
        bodyOffsetY = compound.getFloat("PositionOffsetY");
        bodyOffsetZ = compound.getFloat("PositionOffsetZ");
        bodyOffsetX = compound.getFloat("PositionOffsetX");
        walkingRange = compound.getInteger("WalkingRange");
        setWalkingSpeed(compound.getInteger("MoveSpeed"));

        setMovingPath(NBTTags.getIntegerArraySet(compound.getTagList("MovingPathNew", 10)));
        movingPos = compound.getInteger("MovingPos");
        movingPattern = compound.getInteger("MovingPatern");

        attackInvisible = compound.getBoolean("AttackInvisible");

        if (compound.hasKey("StartPosNew")) {
            int[] startPos = compound.getIntArray("StartPosNew");
            this.startPos = new BlockPos(startPos[0], startPos[1], startPos[2]);
        }
    }

    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setBoolean("CanSwim", canSwim);
        compound.setBoolean("ReactsToFire", reactsToFire);
        compound.setBoolean("AvoidsWater", avoidsWater);
        compound.setBoolean("AvoidsSun", avoidsSun);
        compound.setBoolean("ReturnToStart", returnToStart);
        compound.setInteger("OnAttack", onAttack);
        compound.setInteger("DoorInteract", doorInteract);
        compound.setInteger("FindShelter", findShelter);
        compound.setBoolean("DirectLOS", directLOS);
        compound.setBoolean("CanLeap", canLeap);
        compound.setBoolean("CanSprint", canSprint);
        compound.setInteger("TacticalRadius", tacticalRadius);
        compound.setBoolean("MovingPause", movingPause);
        compound.setBoolean("npcInteracting", npcInteracting);
        compound.setBoolean("stopAndInteract", stopAndInteract);

        compound.setInteger("MoveState", animationType);
        compound.setInteger("StandingState", standingType);
        compound.setInteger("MovingState", movingType);
        compound.setInteger("TacticalVariant", tacticalVariant);

        compound.setInteger("MovementType", movementType);

        compound.setInteger("Orientation", orientation);
        compound.setFloat("PositionOffsetX", bodyOffsetX);
        compound.setFloat("PositionOffsetY", bodyOffsetY);
        compound.setFloat("PositionOffsetZ", bodyOffsetZ);
        compound.setInteger("WalkingRange", walkingRange);
        compound.setInteger("MoveSpeed", moveSpeed);

        compound.setTag("MovingPathNew", NBTTags.nbtIntegerArraySet(movingPath));
        compound.setInteger("MovingPos", movingPos);
        compound.setInteger("MovingPatern", movingPattern);

        setAvoidsWater(avoidsWater);

        compound.setIntArray("StartPosNew", getStartArray());

        compound.setBoolean("AttackInvisible", attackInvisible);

        return compound;
    }

    public List<int[]> getMovingPath() {
        if (movingPath.isEmpty() && startPos != null)
            movingPath.add(getStartArray());
        return movingPath;
    }

    public void setMovingPath(List<int[]> list) {
        movingPath = list;
        if (!movingPath.isEmpty()) {
            int[] startPos = movingPath.get(0);
            this.startPos = new BlockPos(startPos[0], startPos[1], startPos[2]);
        }
    }

    public BlockPos startPos() {
        if (startPos == null)
            startPos = new BlockPos(npc);
        return startPos;
    }

    private int[] getStartArray() {
        BlockPos pos = startPos();
        return new int[]{pos.getX(), pos.getY(), pos.getZ()};
    }

    public int[] getCurrentMovingPath() {
        List<int[]> list = getMovingPath();

        if (list.size() == 1) {
            return list.get(0);
        }
        if (movingPos >= list.size())
            movingPos = 0;

        return list.get(movingPos % list.size());
    }

    public void incrementMovingPath() {
        List<int[]> list = getMovingPath();
        if (list.size() == 1) {
            movingPos = 0;
        } else if (movingPattern == 0) {
            movingPos++;
            movingPos = movingPos % list.size();
        } else if (movingPattern == 1) {
            movingPos++;
            int size = list.size() * 2 - 2;
            movingPos = movingPos % size;
        }
    }


    public void decreaseMovingPath() {
        List<int[]> list = getMovingPath();
        if (list.size() == 1) {
            movingPos = 0;
        } else if (movingPattern == 0) {
            movingPos--;
            if (movingPos < 0)
                movingPos += list.size();
        } else if (movingPattern == 1) {
            movingPos--;
            if (movingPos < 0) {
                int size = list.size() * 2 - 2;
                movingPos += size;
            }
        }
    }

    public double getDistanceSqToPathPoint() {
        int[] pos = getCurrentMovingPath();
        return npc.getDistanceSq(pos[0] + 0.5, pos[1], pos[2] + 0.5);
    }

    public void setStartPos(BlockPos pos) {
        startPos = pos;
    }

    @Override
    public void setReturnsHome(boolean bo) {
        returnToStart = bo;
    }

    @Override
    public boolean getReturnsHome() {
        return returnToStart;
    }

    public boolean shouldReturnHome() {
        if (npc.advanced.job == JobType.BUILDER && ((JobBuilder) npc.jobInterface).isBuilding())
            return false;
        if (npc.advanced.job == JobType.FARMER && ((JobFarmer) npc.jobInterface).isPlucking())
            return false;

        return returnToStart;
    }

    @Override
    public int getAnimation() {
        return animationType;
    }

    @Override
    public int getCurrentAnimation() {
        return npc.currentAnimation;
    }

    @Override
    public void setAnimation(int type) {
        animationType = type;
    }

    @Override
    public int getRetaliateType() {
        return onAttack;
    }

    @Override
    public void setRetaliateType(int type) {
        if (type < 0 || type > 3)
            throw new CustomNPCsException("Unknown retaliation type: " + type);

        onAttack = type;
        npc.updateAI = true;
    }

    @Override
    public int getMovingType() {
        return movingType;
    }

    @Override
    public void setMovingType(int type) {
        if (type < 0 || type > 2)
            throw new CustomNPCsException("Unknown pather type: " + type);
        movingType = type;
        npc.updateAI = true;
    }

    @Override
    public int getStandingType() {
        return standingType;
    }

    @Override
    public void setStandingType(int type) {
        if (type < 0 || type > 3)
            throw new CustomNPCsException("Unknown standing type: " + type);
        standingType = type;
        npc.updateAI = true;
    }

    @Override
    public boolean getAttackInvisible() {
        return attackInvisible;
    }

    @Override
    public void setAttackInvisible(boolean attack) {
        attackInvisible = attack;
    }

    @Override
    public int getWanderingRange() {
        return walkingRange;
    }

    @Override
    public void setWanderingRange(int range) {
        if (range < 1 || range > 50)
            throw new CustomNPCsException("Bad wandering range: " + range);
        walkingRange = range;
    }

    @Override
    public boolean getInteractWithNPCs() {
        return npcInteracting;
    }

    @Override
    public void setInteractWithNPCs(boolean interact) {
        npcInteracting = interact;
    }

    @Override
    public boolean getStopOnInteract() {
        return stopAndInteract;
    }

    @Override
    public void setStopOnInteract(boolean stopOnInteract) {
        stopAndInteract = stopOnInteract;
    }

    @Override
    public int getWalkingSpeed() {
        return this.moveSpeed;
    }

    @Override
    public void setWalkingSpeed(int speed) {
        if (speed < 0 || speed > 10)
            throw new CustomNPCsException("Wrong speed: " + speed);
        this.moveSpeed = speed;
        npc.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(npc.getSpeed());
        npc.getEntityAttribute(SharedMonsterAttributes.FLYING_SPEED).setBaseValue(npc.getSpeed() * 10);
    }

    @Override
    public int getMovingPathType() {
        return movingPattern;
    }

    @Override
    public boolean getMovingPathPauses() {
        return movingPause;
    }

    @Override
    public void setMovingPathType(int type, boolean pauses) {
        if (type < 0 && type > 1)
            throw new CustomNPCsException("Moving path type: " + type);
        movingPattern = type;
        movingPause = pauses;
    }


    /**
     * @return 0:Break, 1:Open, 2:Disabled
     */
    @Override
    public int getDoorInteract() {
        return doorInteract;
    }

    /**
     * @param type 0:Break, 1:Open, 2:Disabled
     */
    @Override
    public void setDoorInteract(int type) {
        doorInteract = type;
        npc.updateAI = true;
    }

    @Override
    public boolean getCanSwim() {
        return canSwim;
    }

    @Override
    public void setCanSwim(boolean canSwim) {
        this.canSwim = canSwim;
    }

    @Override
    public int getSheltersFrom() {
        return findShelter;
    }

    @Override
    public void setSheltersFrom(int type) {
        findShelter = type;
        npc.updateAI = true;
    }

    @Override
    public boolean getAttackLOS() {
        return directLOS;
    }

    @Override
    public void setAttackLOS(boolean enabled) {
        directLOS = enabled;
        npc.updateAI = true;
    }

    @Override
    public boolean getAvoidsWater() {
        return avoidsWater;
    }

    @Override
    public void setAvoidsWater(boolean enabled) {
        if (npc.getNavigator() instanceof PathNavigateGround) {
            npc.setPathPriority(PathNodeType.WATER, enabled ? PathNodeType.WATER.getPriority() : 0.0F);
        }
        this.avoidsWater = enabled;
    }

    @Override
    public boolean getLeapAtTarget() {
        return canLeap;
    }

    @Override
    public void setLeapAtTarget(boolean leap) {
        this.canLeap = leap;
        npc.updateAI = true;
    }

    @Override
    public int getTacticalType() {
        return tacticalVariant;
    }

    @Override
    public void setTacticalType(int type) {
        this.tacticalVariant = type;
        npc.updateAI = true;
    }

    @Override
    public int getTacticalRange() {
        return tacticalRadius;
    }

    @Override
    public void setTacticalRange(int range) {
        this.tacticalRadius = range;
    }

    @Override
    public int getNavigationType() {
        return movementType;
    }

    @Override
    public void setNavigationType(int type) {
        movementType = type;
    }
}

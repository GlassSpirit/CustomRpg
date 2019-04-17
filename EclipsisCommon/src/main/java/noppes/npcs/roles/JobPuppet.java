package noppes.npcs.roles;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
import noppes.npcs.api.CustomNPCsException;
import noppes.npcs.api.entity.data.role.IJobPuppet;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.util.ValueUtil;

public class JobPuppet extends JobInterface implements IJobPuppet {
    public PartConfig head = new PartConfig();
    public PartConfig larm = new PartConfig();
    public PartConfig rarm = new PartConfig();
    public PartConfig body = new PartConfig();
    public PartConfig lleg = new PartConfig();
    public PartConfig rleg = new PartConfig();

    public PartConfig head2 = new PartConfig();
    public PartConfig larm2 = new PartConfig();
    public PartConfig rarm2 = new PartConfig();
    public PartConfig body2 = new PartConfig();
    public PartConfig lleg2 = new PartConfig();
    public PartConfig rleg2 = new PartConfig();

    public boolean whileStanding = true;
    public boolean whileAttacking = false;
    public boolean whileMoving = false;

    public boolean animate = false;
    public int animationSpeed = 4;

    public JobPuppet(EntityNPCInterface npc) {
        super(npc);
    }

    @Override
    public IJobPuppetPart getPart(int part) {
        if (part == 0) {
            return head;
        } else if (part == 1) {
            return larm;
        } else if (part == 2) {
            return rarm;
        } else if (part == 3) {
            return body;
        } else if (part == 4) {
            return lleg;
        } else if (part == 5) {
            return rleg;
        } else if (part == 6) {
            return head2;
        } else if (part == 7) {
            return larm2;
        } else if (part == 8) {
            return rarm2;
        } else if (part == 9) {
            return body2;
        } else if (part == 10) {
            return lleg2;
        } else if (part == 11) {
            return rleg2;
        }

        throw new CustomNPCsException("Unknown part " + part);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setTag("PuppetHead", head.writeNBT());
        compound.setTag("PuppetLArm", larm.writeNBT());
        compound.setTag("PuppetRArm", rarm.writeNBT());
        compound.setTag("PuppetBody", body.writeNBT());
        compound.setTag("PuppetLLeg", lleg.writeNBT());
        compound.setTag("PuppetRLeg", rleg.writeNBT());

        compound.setTag("PuppetHead2", head2.writeNBT());
        compound.setTag("PuppetLArm2", larm2.writeNBT());
        compound.setTag("PuppetRArm2", rarm2.writeNBT());
        compound.setTag("PuppetBody2", body2.writeNBT());
        compound.setTag("PuppetLLeg2", lleg2.writeNBT());
        compound.setTag("PuppetRLeg2", rleg2.writeNBT());

        compound.setBoolean("PuppetStanding", whileStanding);
        compound.setBoolean("PuppetAttacking", whileAttacking);
        compound.setBoolean("PuppetMoving", whileMoving);
        compound.setBoolean("PuppetAnimate", animate);

        compound.setInteger("PuppetAnimationSpeed", animationSpeed);
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        head.readNBT(compound.getCompoundTag("PuppetHead"));
        larm.readNBT(compound.getCompoundTag("PuppetLArm"));
        rarm.readNBT(compound.getCompoundTag("PuppetRArm"));
        body.readNBT(compound.getCompoundTag("PuppetBody"));
        lleg.readNBT(compound.getCompoundTag("PuppetLLeg"));
        rleg.readNBT(compound.getCompoundTag("PuppetRLeg"));

        head2.readNBT(compound.getCompoundTag("PuppetHead2"));
        larm2.readNBT(compound.getCompoundTag("PuppetLArm2"));
        rarm2.readNBT(compound.getCompoundTag("PuppetRArm2"));
        body2.readNBT(compound.getCompoundTag("PuppetBody2"));
        lleg2.readNBT(compound.getCompoundTag("PuppetLLeg2"));
        rleg2.readNBT(compound.getCompoundTag("PuppetRLeg2"));

        whileStanding = compound.getBoolean("PuppetStanding");
        whileAttacking = compound.getBoolean("PuppetAttacking");
        whileMoving = compound.getBoolean("PuppetMoving");

        setIsAnimated(compound.getBoolean("PuppetAnimate"));
        setAnimationSpeed(compound.getInteger("PuppetAnimationSpeed"));
    }

    @Override
    public boolean aiShouldExecute() {
        return false;
    }

    private int prevTicks = 0;

    private int startTick = 0;
    private float val = 0;
    private float valNext = 0;

    private float calcRotation(float r, float r2, float partialTicks) {
        if (!animate)
            return r;
        if (prevTicks != npc.ticksExisted) {
            float speed = 0;
            if (animationSpeed == 0)
                speed = 40;
            else if (animationSpeed == 1)
                speed = 24;
            else if (animationSpeed == 2)
                speed = 13;
            else if (animationSpeed == 3)
                speed = 10;
            else if (animationSpeed == 4)
                speed = 7;
            else if (animationSpeed == 5)
                speed = 4;
            else if (animationSpeed == 6)
                speed = 3;
            else if (animationSpeed == 7)
                speed = 2;

            int ticks = npc.ticksExisted - startTick;
            val = 1 - (MathHelper.cos(ticks / speed * (float) Math.PI / 2) + 1) / 2;
            valNext = 1 - (MathHelper.cos((ticks + 1) / speed * (float) Math.PI / 2) + 1) / 2;
            prevTicks = npc.ticksExisted;
        }
        float f = val + (valNext - val) * partialTicks;
        return r + (r2 - r) * f;
    }

    public float getRotationX(PartConfig part1, PartConfig part2, float partialTicks) {
        return calcRotation(part1.rotationX, part2.rotationX, partialTicks);
    }

    public float getRotationY(PartConfig part1, PartConfig part2, float partialTicks) {
        return calcRotation(part1.rotationY, part2.rotationY, partialTicks);
    }

    public float getRotationZ(PartConfig part1, PartConfig part2, float partialTicks) {
        return calcRotation(part1.rotationZ, part2.rotationZ, partialTicks);
    }

    @Override
    public void reset() {
        val = 0;
        valNext = 0;
        prevTicks = 0;
        startTick = npc.ticksExisted;
    }

    public void delete() {
    }

    public boolean isActive() {
        if (!npc.isEntityAlive())
            return false;

        return whileAttacking && npc.isAttacking() || whileMoving && npc.isWalking() || whileStanding && !npc.isWalking();
    }

    public class PartConfig implements IJobPuppetPart {
        public float rotationX = 0f;
        public float rotationY = 0f;
        public float rotationZ = 0f;

        public boolean disabled = false;

        public NBTTagCompound writeNBT() {
            NBTTagCompound compound = new NBTTagCompound();
            compound.setFloat("RotationX", rotationX);
            compound.setFloat("RotationY", rotationY);
            compound.setFloat("RotationZ", rotationZ);

            compound.setBoolean("Disabled", disabled);
            return compound;
        }

        public void readNBT(NBTTagCompound compound) {
            rotationX = ValueUtil.correctFloat(compound.getFloat("RotationX"), -1f, 1f);
            rotationY = ValueUtil.correctFloat(compound.getFloat("RotationY"), -1f, 1f);
            rotationZ = ValueUtil.correctFloat(compound.getFloat("RotationZ"), -1f, 1f);

            disabled = compound.getBoolean("Disabled");
        }

        @Override
        public int getRotationX() {
            return (int) (rotationX + 1 * 180);
        }

        @Override
        public int getRotationY() {
            return (int) (rotationY + 1 * 180);
        }

        @Override
        public int getRotationZ() {
            return (int) (rotationZ + 1 * 180);
        }

        @Override
        public void setRotation(int x, int y, int z) {
            disabled = false;
            rotationX = ValueUtil.correctFloat(x / 180f - 1, -1f, 1f);
            rotationY = ValueUtil.correctFloat(y / 180f - 1, -1f, 1f);
            rotationZ = ValueUtil.correctFloat(z / 180f - 1, -1f, 1f);
            JobPuppet.this.npc.updateClient = true;
        }
    }

    @Override
    public boolean getIsAnimated() {
        return animate;
    }

    @Override
    public void setIsAnimated(boolean bo) {
        animate = bo;
        if (!bo) {
            val = 0;
            valNext = 0;
            prevTicks = 0;
        } else {
            startTick = npc.ticksExisted;
        }
        this.npc.updateClient = true;
    }

    @Override
    public int getAnimationSpeed() {
        return animationSpeed;
    }

    @Override
    public void setAnimationSpeed(int speed) {
        animationSpeed = ValueUtil.CorrectInt(speed, 0, 7);
        this.npc.updateClient = true;
    }
}

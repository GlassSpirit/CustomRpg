package noppes.npcs.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityChairMount extends Entity {

    public EntityChairMount(World world) {
        super(world);
        setSize(0, 0);
    }

    @Override
    public double getMountedYOffset() {
        return 0.5f;
    }

    @Override
    protected void entityInit() {
    }

    @Override
    public void onEntityUpdate() {
        super.onEntityUpdate();
        if (this.world != null && !this.world.isRemote && getPassengers().isEmpty())
            isDead = true;
    }

    @Override
    public boolean isEntityInvulnerable(DamageSource source) {
        return true;
    }

    @Override
    public boolean isInvisible() {
        return true;
    }

    @Override
    public void move(MoverType type, double x, double y, double z) {
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound tagCompound) {
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound tagCompound) {
    }

    @Override
    public boolean canBeCollidedWith() {
        return false;
    }

    @Override
    public boolean canBePushed() {
        return false;
    }

    @Override
    public void fall(float distance, float damageMultiplier) {
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void setPositionAndRotationDirect(double p_70056_1_, double p_70056_3_, double p_70056_5_, float p_70056_7_, float p_70056_8_, int p_70056_9_, boolean bo) {
        this.setPosition(p_70056_1_, p_70056_3_, p_70056_5_);
        this.setRotation(p_70056_7_, p_70056_8_);
    }
}

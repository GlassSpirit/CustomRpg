package noppes.npcs.entity.data;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.api.constants.PotionEffectType;
import noppes.npcs.api.entity.data.INPCMelee;
import noppes.npcs.entity.EntityNPCInterface;

public class DataMelee implements INPCMelee {

    private EntityNPCInterface npc;

    private int attackStrength = 5;
    private int attackSpeed = 20;
    private int attackRange = 2;
    private int knockback = 0;

    private int potionType = PotionEffectType.NONE;
    private int potionDuration = 5; //20 = 1 second
    private int potionAmp = 0;

    public DataMelee(EntityNPCInterface npc) {
        this.npc = npc;
    }

    public void readFromNBT(NBTTagCompound compound) {
        attackSpeed = compound.getInteger("AttackSpeed");
        setStrength(compound.getInteger("AttackStrenght"));
        attackRange = compound.getInteger("AttackRange");
        knockback = compound.getInteger("KnockBack");

        potionType = compound.getInteger("PotionEffect");
        potionDuration = compound.getInteger("PotionDuration");
        potionAmp = compound.getInteger("PotionAmp");
    }

    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setInteger("AttackStrenght", attackStrength);
        compound.setInteger("AttackSpeed", attackSpeed);
        compound.setInteger("AttackRange", attackRange);
        compound.setInteger("KnockBack", knockback);

        compound.setInteger("PotionEffect", potionType);
        compound.setInteger("PotionDuration", potionDuration);
        compound.setInteger("PotionAmp", potionAmp);

        return compound;
    }

    @Override
    public int getStrength() {
        return attackStrength;
    }

    @Override
    public void setStrength(int strength) {
        attackStrength = strength;
        npc.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(attackStrength);
    }

    @Override
    public int getDelay() {
        return attackSpeed;
    }

    @Override
    public void setDelay(int speed) {
        attackSpeed = speed;
    }

    @Override
    public int getRange() {
        return attackRange;
    }

    @Override
    public void setRange(int range) {
        attackRange = range;

    }

    @Override
    public int getKnockback() {
        return knockback;
    }

    @Override
    public void setKnockback(int knockback) {
        this.knockback = knockback;
    }

    /**
     * @see PotionEffectType
     */
    @Override
    public int getEffectType() {
        return potionType;
    }

    @Override
    public int getEffectTime() {
        return potionDuration;
    }

    @Override
    public int getEffectStrength() {
        return potionAmp;
    }

    @Override
    public void setEffect(int type, int strength, int time) {
        potionType = type;
        potionDuration = time;
        potionAmp = strength;
    }
}

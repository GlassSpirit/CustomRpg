package noppes.npcs.common.entity.data;

import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.api.CustomNPCsException;
import noppes.npcs.api.entity.data.INPCMelee;
import noppes.npcs.api.entity.data.INPCRanged;
import noppes.npcs.api.entity.data.INPCStats;
import noppes.npcs.common.entity.EntityNPCInterface;
import noppes.npcs.util.ValueUtil;

public class DataStats implements INPCStats {
    public int aggroRange = 16;

    public int maxHealth = 20;
    public int respawnTime = 20;
    public int spawnCycle = 0;
    public boolean hideKilledBody = false;

    public DataResistances resistances = new DataResistances();

    public boolean immuneToFire = false;
    public boolean potionImmune = false;
    public boolean canDrown = true;
    public boolean burnInSun = false;
    public boolean noFallDamage = false;
    public boolean ignoreCobweb = false;

    public int healthRegen = 1;
    public int combatRegen = 0;

    public EnumCreatureAttribute creatureType = EnumCreatureAttribute.UNDEFINED;

    public DataMelee melee;
    public DataRanged ranged;

    private EntityNPCInterface npc;

    public DataStats(EntityNPCInterface npc) {
        this.npc = npc;
        melee = new DataMelee(npc);
        ranged = new DataRanged(npc);
    }

    public void readToNBT(NBTTagCompound compound) {
        resistances.readToNBT(compound.getCompoundTag("DataResistances"));
        setMaxHealth(compound.getInteger("MaxHealth"));
        hideKilledBody = compound.getBoolean("HideBodyWhenKilled");
        aggroRange = compound.getInteger("AggroRange");
        respawnTime = compound.getInteger("RespawnTime");
        spawnCycle = compound.getInteger("SpawnCycle");
        creatureType = EnumCreatureAttribute.values()[compound.getInteger("CreatureType")];
        healthRegen = compound.getInteger("HealthRegen");
        combatRegen = compound.getInteger("CombatRegen");

        immuneToFire = compound.getBoolean("ImmuneToFire");
        potionImmune = compound.getBoolean("PotionImmune");
        canDrown = compound.getBoolean("CanDrown");
        burnInSun = compound.getBoolean("BurnInSun");
        noFallDamage = compound.getBoolean("NoFallDamage");
        npc.setImmuneToFire(immuneToFire);
        ignoreCobweb = compound.getBoolean("IgnoreCobweb");

        melee.readFromNBT(compound);
        ranged.readFromNBT(compound);
    }

    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setTag("DataResistances", resistances.writeToNBT());
        compound.setInteger("MaxHealth", maxHealth);
        compound.setInteger("AggroRange", aggroRange);
        compound.setBoolean("HideBodyWhenKilled", hideKilledBody);
        compound.setInteger("RespawnTime", respawnTime);
        compound.setInteger("SpawnCycle", spawnCycle);
        compound.setInteger("CreatureType", creatureType.ordinal());
        compound.setInteger("HealthRegen", healthRegen);
        compound.setInteger("CombatRegen", combatRegen);

        compound.setBoolean("ImmuneToFire", immuneToFire);
        compound.setBoolean("PotionImmune", potionImmune);
        compound.setBoolean("CanDrown", canDrown);
        compound.setBoolean("BurnInSun", burnInSun);
        compound.setBoolean("NoFallDamage", noFallDamage);
        compound.setBoolean("IgnoreCobweb", ignoreCobweb);

        melee.writeToNBT(compound);
        ranged.writeToNBT(compound);

        return compound;
    }

    @Override
    public void setMaxHealth(int maxHealth) {
        if (maxHealth == this.maxHealth)
            return;
        this.maxHealth = maxHealth;
        npc.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(maxHealth);
        npc.updateClient = true;
    }

    @Override
    public int getMaxHealth() {
        return maxHealth;
    }

    @Override
    public float getResistance(int type) {
        if (type == 0)
            return resistances.melee;
        if (type == 1)
            return resistances.arrow;
        if (type == 2)
            return resistances.explosion;
        if (type == 3)
            return resistances.knockback;
        return 1;
    }

    @Override
    public void setResistance(int type, float value) {
        value = ValueUtil.correctFloat(value, 0, 2);
        if (type == 0)
            resistances.melee = value;
        else if (type == 1)
            resistances.arrow = value;
        else if (type == 2)
            resistances.explosion = value;
        else if (type == 3)
            resistances.knockback = value;

    }


    @Override
    public int getCombatRegen() {
        return combatRegen;
    }

    @Override
    public void setCombatRegen(int regen) {
        combatRegen = regen;
    }

    @Override
    public int getHealthRegen() {
        return healthRegen;
    }

    @Override
    public void setHealthRegen(int regen) {
        healthRegen = regen;
    }

    @Override
    public INPCMelee getMelee() {
        return melee;
    }

    @Override
    public INPCRanged getRanged() {
        return ranged;
    }

    @Override
    public boolean getImmune(int type) {
        if (type == 0)
            return potionImmune;
        if (type == 1)
            return !noFallDamage;
        if (type == 2)
            return burnInSun;
        if (type == 3)
            return immuneToFire;
        if (type == 4)
            return !canDrown;
        if (type == 5)
            return ignoreCobweb;

        throw new CustomNPCsException("Unknown immune type: " + type);
    }

    @Override
    public void setImmune(int type, boolean bo) {
        if (type == 0)
            potionImmune = bo;
        else if (type == 1)
            noFallDamage = !bo;
        else if (type == 2)
            burnInSun = bo;
        else if (type == 3)
            npc.setImmuneToFire(bo);
        else if (type == 4)
            canDrown = !bo;
        else if (type == 5)
            ignoreCobweb = bo;
        else
            throw new CustomNPCsException("Unknown immune type: " + type);
    }

    @Override
    public int getCreatureType() {
        return creatureType.ordinal();
    }

    @Override
    public void setCreatureType(int type) {
        creatureType = EnumCreatureAttribute.values()[type];
    }

    @Override
    public int getRespawnType() {
        return spawnCycle;
    }

    @Override
    public void setRespawnType(int type) {
        spawnCycle = type;
    }

    @Override
    public int getRespawnTime() {
        return respawnTime;
    }

    @Override
    public void setRespawnTime(int seconds) {
        respawnTime = seconds;
    }

    @Override
    public boolean getHideDeadBody() {
        return hideKilledBody;
    }

    @Override
    public void setHideDeadBody(boolean hide) {
        hideKilledBody = hide;
        npc.updateClient = true;
    }

    @Override
    public int getAggroRange() {
        return aggroRange;
    }

    @Override
    public void setAggroRange(int range) {
        aggroRange = range;
    }
}

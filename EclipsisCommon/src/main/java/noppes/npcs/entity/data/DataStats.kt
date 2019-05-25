package noppes.npcs.entity.data

import net.minecraft.entity.EnumCreatureAttribute
import net.minecraft.entity.SharedMonsterAttributes
import net.minecraft.nbt.NBTTagCompound
import noppes.npcs.Resistances
import noppes.npcs.api.CustomNPCsException
import noppes.npcs.api.entity.data.INPCStats
import noppes.npcs.entity.EntityNPCInterface
import noppes.npcs.util.ValueUtil

class DataStats(private val npc: EntityNPCInterface) : INPCStats {

    override var maxHealth: Int = 20
        set(value) {
            if (maxHealth == this.maxHealth)
                return
            field = value
            npc.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).baseValue = maxHealth.toDouble()
            npc.updateClient = true
        }

    override var combatRegen = 0
    override var healthRegen = 1

    override var melee: DataMelee = DataMelee(npc)
    override var ranged: DataRanged = DataRanged(npc)

    override var creatureType = EnumCreatureAttribute.UNDEFINED.ordinal

    override var respawnTime: Int = 20
    override var respawnType: Int = 0

    override var hideDeadBody = false
        set(value) {
            field = value
            npc.updateClient = true
        }

    override var aggroRange: Int = 16

    override var level: Int = 1

    var resistances = Resistances()

    var immuneToFire = false
    var potionImmune = false
    var canDrown = true
    var burnInSun = false
    var noFallDamage = false
    var ignoreCobweb = false

    fun readToNBT(compound: NBTTagCompound) {
        maxHealth = (compound.getInteger("MaxHealth"))
        combatRegen = compound.getInteger("CombatRegen")
        healthRegen = compound.getInteger("HealthRegen")
        melee.readFromNBT(compound)
        ranged.readFromNBT(compound)
        creatureType = compound.getInteger("CreatureType")
        respawnTime = compound.getInteger("RespawnTime")
        respawnType = compound.getInteger("SpawnCycle")
        hideDeadBody = compound.getBoolean("HideBodyWhenKilled")
        aggroRange = compound.getInteger("AggroRange")
        level = compound.getInteger("Level")

        resistances.readFromNBT(compound.getCompoundTag("Resistances"))
        immuneToFire = compound.getBoolean("ImmuneToFire")
        npc.isImmuneToFire = immuneToFire
        potionImmune = compound.getBoolean("PotionImmune")
        canDrown = compound.getBoolean("CanDrown")
        burnInSun = compound.getBoolean("BurnInSun")
        noFallDamage = compound.getBoolean("NoFallDamage")
        ignoreCobweb = compound.getBoolean("IgnoreCobweb")
    }

    fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {

        compound.setInteger("MaxHealth", maxHealth)
        compound.setInteger("HealthRegen", healthRegen)
        compound.setInteger("CombatRegen", combatRegen)
        melee.writeToNBT(compound)
        ranged.writeToNBT(compound)
        compound.setInteger("CreatureType", creatureType)
        compound.setInteger("RespawnTime", respawnTime)
        compound.setInteger("SpawnCycle", respawnType)
        compound.setBoolean("HideBodyWhenKilled", hideDeadBody)
        compound.setInteger("AggroRange", aggroRange)
        compound.setInteger("Level", level)

        compound.setTag("Resistances", resistances.writeToNBT())
        compound.setBoolean("ImmuneToFire", immuneToFire)
        compound.setBoolean("PotionImmune", potionImmune)
        compound.setBoolean("CanDrown", canDrown)
        compound.setBoolean("BurnInSun", burnInSun)
        compound.setBoolean("NoFallDamage", noFallDamage)
        compound.setBoolean("IgnoreCobweb", ignoreCobweb)

        return compound
    }

    override fun getResistance(type: Int): Float {
        if (type == 0)
            return resistances.melee
        if (type == 1)
            return resistances.arrow
        if (type == 2)
            return resistances.explosion
        return if (type == 3) resistances.knockback else 1f
    }

    override fun setResistance(type: Int, value: Float) {
        var value = value
        value = ValueUtil.correctFloat(value, 0f, 2f)
        if (type == 0)
            resistances.melee = value
        else if (type == 1)
            resistances.arrow = value
        else if (type == 2)
            resistances.explosion = value
        else if (type == 3)
            resistances.knockback = value

    }

    override fun getImmune(type: Int): Boolean {
        if (type == 0)
            return potionImmune
        if (type == 1)
            return !noFallDamage
        if (type == 2)
            return burnInSun
        if (type == 3)
            return immuneToFire
        if (type == 4)
            return !canDrown
        if (type == 5)
            return ignoreCobweb

        throw CustomNPCsException("Unknown immune type: $type")
    }

    override fun setImmune(type: Int, bo: Boolean) {
        if (type == 0)
            potionImmune = bo
        else if (type == 1)
            noFallDamage = !bo
        else if (type == 2)
            burnInSun = bo
        else if (type == 3)
            npc.isImmuneToFire = bo
        else if (type == 4)
            canDrown = !bo
        else if (type == 5)
            ignoreCobweb = bo
        else
            throw CustomNPCsException("Unknown immune type: $type")
    }

}

package noppes.npcs.api.entity.data

interface INPCStats {

    var maxHealth: Int

    /**
     * combat health regen per second
     */
    var combatRegen: Int

    /**
     * health regen per second when not in combat
     */
    var healthRegen: Int

    val melee: INPCMelee

    val ranged: INPCRanged

    /**
     * (0=Normal, 1=Undead, 2=Arthropod) Only used for damage calculations with enchants
     */
    var creatureType: Int

    /**
     * 0:Yes, 1:Day, 2:Night, 3:No, 4:Naturally
     */
    var respawnType: Int

    var respawnTime: Int

    var hideDeadBody: Boolean

    var aggroRange: Int

    var level: Int

    /**
     * @param type 0:Melee, 1:Ranged, 2:Explosion, 3:Knockback
     * @return Returns value between 0 and 2. 0 being no resistance so increased damage and 2 being fully resistant. Normal is 1
     */
    fun getResistance(type: Int): Float

    fun setResistance(type: Int, value: Float)

    /**
     * @param type 0:Potion, 1:Falldamage, 2:Sunburning, 3:Fire, 4:Drowning, 5:Cobweb
     */
    fun getImmune(type: Int): Boolean

    /**
     * @param type 0:Potion, 1:Falldamage, 2:Sunburning, 3:Fire, 4:Drowning, 5:Cobweb
     */
    fun setImmune(type: Int, bo: Boolean)

}

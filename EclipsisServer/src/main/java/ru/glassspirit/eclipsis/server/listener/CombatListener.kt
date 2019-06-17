package ru.glassspirit.eclipsis.server.listener

import cz.neumimto.rpg.NtRpgPlugin
import cz.neumimto.rpg.events.damage.IEntityWeaponDamageEarlyEvent
import org.spongepowered.api.Sponge
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.Order
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource
import org.spongepowered.api.event.filter.cause.First
import ru.glassspirit.eclipsis.server.EclipsisPlugin
import ru.glassspirit.eclipsis.server.properties.EclipsisProperties

object CombatListener {

    init {
        Sponge.getEventManager().registerListeners(EclipsisPlugin.INSTANCE, this)
    }

    @Listener(order = Order.EARLY)
    fun checkAccuracyDodge(event: IEntityWeaponDamageEarlyEvent, @First source: EntityDamageSource) {
        val attackerAccuracy = NtRpgPlugin.GlobalScope.entityService.get(source.source).getProperty(EclipsisProperties.accuracy)
        val targetDodge = event.target.getProperty(EclipsisProperties.dodge)

        if (Math.random() * 100 > attackerAccuracy - targetDodge) {
            event.isCancelled = true
        }
    }

    @Listener(order = Order.LATE)
    fun checkCriticalDamage(event: IEntityWeaponDamageEarlyEvent, @First source: EntityDamageSource) {
        val attackerCritChance = NtRpgPlugin.GlobalScope.entityService.get(source.source).getProperty(EclipsisProperties.crit_chance)
        val attackerCritMultiplier = NtRpgPlugin.GlobalScope.entityService.get(source.source).getProperty(EclipsisProperties.crit_mult)

        if (Math.random() * 100 < attackerCritChance) {
            event.damage *= attackerCritMultiplier
        }
    }

}
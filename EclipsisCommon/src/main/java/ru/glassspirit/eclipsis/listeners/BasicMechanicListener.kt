package ru.glassspirit.eclipsis.listeners

import net.minecraftforge.event.entity.player.AttackEntityEvent
import net.minecraftforge.event.entity.player.CriticalHitEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.eventhandler.Event
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import ru.glassspirit.eclipsis.EclipsisMod

@Mod.EventBusSubscriber(modid = EclipsisMod.MODID)
object BasicMechanicListener {

    /**
     * Disables all minecraft critical hits
     */
    @JvmStatic
    @SubscribeEvent
    fun onCritical(event: CriticalHitEvent) {
        event.result = Event.Result.DENY
    }

    /**
     * Disables damage when cooldown is not ready
     */
    @JvmStatic
    @SubscribeEvent
    fun onPlayerAttackEntity(event: AttackEntityEvent) {
        if (event.entityPlayer.ticksSinceLastSwing / event.entityPlayer.cooldownPeriod < 1) {
            event.isCanceled = true
        }
    }

}
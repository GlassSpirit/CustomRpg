package ru.glassspirit.eclipsis.listeners

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

}
package ru.glassspirit.eclipsis.server.listener

import net.minecraftforge.event.entity.living.LivingHurtEvent
import net.minecraftforge.event.entity.player.AttackEntityEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.eventhandler.EventPriority
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import org.spongepowered.api.Sponge
import org.spongepowered.api.data.key.Keys
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.cause.entity.damage.DamageTypes
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource
import org.spongepowered.api.event.entity.DamageEntityEvent
import org.spongepowered.api.event.filter.cause.First
import ru.glassspirit.eclipsis.EclipsisMod
import ru.glassspirit.eclipsis.damage.EclipsisDamageSource
import ru.glassspirit.eclipsis.server.EclipsisPlugin

@Mod.EventBusSubscriber(modid = EclipsisMod.MODID)
object BasicMechanicServerListener {

    init {
        Sponge.getEventManager().registerListeners(EclipsisPlugin.INSTANCE, this)
    }

    /**
     * Disables damage when cooldown is not ready
     */
    @JvmStatic
    @SubscribeEvent
    fun onPlayerAttackEntity(event: AttackEntityEvent) {
        if (event.entityPlayer.ticksSinceLastSwing / event.entityPlayer.cooldownPeriod < 0.99991) {
            event.isCanceled = true
        }
    }

    /**
     * Handles poison damage done to player since it is not doable with sponge
     */
    @JvmStatic
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    fun environmentDamage(event: LivingHurtEvent) {
        if (event.entityLiving is Player) {
            if (event.source == EclipsisDamageSource.POISON) {
                event.amount = event.entityLiving.health / 5
            }
        }
    }

    /**
     * Handles all environmental damage done to player
     */
    @Listener
    fun environmentDamage(event: DamageEntityEvent, @First player: Player, @First source: DamageSource) {

        //Fall damage handler
        if (source.type == DamageTypes.FALL) {
            if (player.get(Keys.FALL_DISTANCE).isPresent) {
                event.baseDamage = player.maxHealth().get() / 50 * player.get(Keys.FALL_DISTANCE).get()
            }
        }

        //Drown damage handler
        if (source.type == DamageTypes.DROWN) {
            event.baseDamage = player.maxHealth().get() / 30
        }

        //Lava damage handler
        if (source.type == DamageTypes.MAGMA) {
            event.baseDamage = player.maxHealth().get() / 60
        }

        //Void damage handler
        if (source.type == DamageTypes.VOID) {
            event.baseDamage = player.maxHealth().get() / 20
        }
    }

}
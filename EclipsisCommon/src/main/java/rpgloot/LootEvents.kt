package rpgloot

import net.minecraft.entity.player.EntityPlayer
import net.minecraftforge.event.entity.living.LivingDropsEvent
import net.minecraftforge.fml.common.eventhandler.EventPriority
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import rpgloot.entities.EntityCorpse

class LootEvents {

    @SubscribeEvent(priority = EventPriority.LOWEST)
    fun onLivingDrops(event: LivingDropsEvent) {
        if (event.entityLiving != null && !event.entity.world.isRemote && RPGLoot.config.looting && event.drops.size > 0) {
            event.entity.world.spawnEntity(EntityCorpse(event.entity.world, event.entityLiving,
                    if (event.source.trueSource is EntityPlayer) event.source.trueSource as EntityPlayer
                    else null, event.drops))
            event.isCanceled = true
        }
    }

}

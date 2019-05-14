package rpgloot

import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.registry.EntityRegistry
import rpgloot.entities.EntityCorpse

open class CommonProxy {

    open fun init(event: FMLInitializationEvent) {
        MinecraftForge.EVENT_BUS.register(LootEvents())
        EntityRegistry.registerModEntity(ResourceLocation("rpgloot:corpse"),
                EntityCorpse::class.java, "rpgloot_corpse", 0,
                RPGLoot, 64, 20, false)
    }
}

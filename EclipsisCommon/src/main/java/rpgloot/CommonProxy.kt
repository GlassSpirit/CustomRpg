package rpgloot

import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.registry.EntityRegistry
import rpgloot.entities.EntCorpse
import rpgloot.packets.CorpseSyncPacket

open class CommonProxy {

    open fun register() {
        MinecraftForge.EVENT_BUS.register(Events())
        EntityRegistry.registerModEntity(ResourceLocation("rpgloot:corpse"), EntCorpse::class.java, "rpgloot_corpse", 0,
                RPGLoot, 64, 20, false)
    }

    open fun handleCorpseSyncPacket(message: CorpseSyncPacket) {}
}

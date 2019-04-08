package rpgloot.client

import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.entity.Render
import net.minecraft.entity.Entity
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.client.registry.RenderingRegistry
import rpgloot.CommonProxy
import rpgloot.entities.EntCorpse
import rpgloot.packets.CorpseSyncPacket

class ClientProxy : CommonProxy() {

    private fun registerEntityRenderer(entityClass: Class<out Entity>, render: Render<*>) {
        RenderingRegistry.registerEntityRenderingHandler(entityClass, render)
    }

    override fun register() {
        super.register()
        registerEntityRenderer(EntCorpse::class.java, RenderCorpse(Minecraft.getMinecraft().renderManager))
        MinecraftForge.EVENT_BUS.register(ClientEvents())
    }

    override fun handleCorpseSyncPacket(message: CorpseSyncPacket) {
        val world = Minecraft.getMinecraft().world
        val entity = world.getEntityByID(message.corpseID)
        if (entity != null && entity is EntCorpse) {
            entity.readFromNBT(message.corpseTag!!)
        }

    }

    fun playSound(sound: String) {}
}

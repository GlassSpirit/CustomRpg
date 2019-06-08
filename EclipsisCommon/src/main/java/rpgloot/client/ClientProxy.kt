package rpgloot.client

import com.teamwizardry.librarianlib.features.kotlin.Minecraft
import net.minecraft.entity.player.EntityPlayer
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.client.registry.RenderingRegistry
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import rpgloot.CommonProxy
import rpgloot.entities.EntityCorpse

class ClientProxy : CommonProxy() {

    override fun init(event: FMLInitializationEvent) {
        super.init(event)
        RenderingRegistry.registerEntityRenderingHandler(EntityCorpse::class.java, RenderCorpse(Minecraft().renderManager))
        MinecraftForge.EVENT_BUS.register(ClientEvents())
    }

    override fun getClientPlayer(): EntityPlayer? {
        return Minecraft().player
    }
}

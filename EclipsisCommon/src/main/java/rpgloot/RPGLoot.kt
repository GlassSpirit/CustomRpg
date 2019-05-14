package rpgloot

import com.teamwizardry.librarianlib.features.network.Channel
import com.teamwizardry.librarianlib.features.network.PacketHandler
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.Mod.EventHandler
import net.minecraftforge.fml.common.SidedProxy
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.relauncher.Side
import org.apache.logging.log4j.Logger
import rpgloot.network.CorpseSyncPacket

@Mod(modid = RPGLoot.MODID, name = RPGLoot.MODNAME, version = RPGLoot.VERSION,
        dependencies = "after:eclipsis", modLanguageAdapter = RPGLoot.ADAPTER)
object RPGLoot {
    const val MODID = "rpgloot"
    const val MODNAME = "RPGLoot"
    const val VERSION = "GlassSpirit <3"
    const val ADAPTER = "net.shadowfacts.forgelin.KotlinAdapter"

    lateinit var logger: Logger

    @SidedProxy(clientSide = "rpgloot.client.ClientProxy", serverSide = "rpgloot.CommonProxy")
    lateinit var proxy: CommonProxy

    lateinit var networkChannel: Channel

    lateinit var config: Config
        private set

    @EventHandler
    fun preInit(event: FMLPreInitializationEvent) {
        logger = event.modLog
        config = Config(event.suggestedConfigurationFile)
    }

    @EventHandler
    fun init(event: FMLInitializationEvent) {
        proxy.init(event)

        networkChannel = PacketHandler.getChannel(MODID)
        PacketHandler.register(CorpseSyncPacket::class.java, Side.CLIENT)
        PacketHandler.register(CorpseSyncPacket::class.java, Side.SERVER)
    }

    @EventHandler
    fun postInit(event: FMLPostInitializationEvent) {

    }
}

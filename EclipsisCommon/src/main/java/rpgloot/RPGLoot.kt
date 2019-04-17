package rpgloot

import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.Mod.EventHandler
import net.minecraftforge.fml.common.SidedProxy
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.network.NetworkRegistry
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper
import net.minecraftforge.fml.relauncher.Side
import rpgloot.packets.CorpseSyncPacket
import rpgloot.packets.DisposePacket
import rpgloot.packets.LootPacket
import rpgloot.packets.ReqCorpseSyncPacket

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

    lateinit var packetHandler: SimpleNetworkWrapper

    lateinit var config: Config
        private set

    @EventHandler
    fun preInit(event: FMLPreInitializationEvent) {
        logger = Logger(event.modLog)
        config = Config(event.suggestedConfigurationFile)
    }

    @EventHandler
    fun init(event: FMLInitializationEvent) {
        proxy.register()
        setUpPacketHandler()
    }

    @EventHandler
    fun postInit(event: FMLPostInitializationEvent) {

    }

    private fun setUpPacketHandler() {
        packetHandler = NetworkRegistry.INSTANCE.newSimpleChannel(MODID)
        packetHandler.registerMessage<LootPacket, IMessage>(LootPacket.HANDLER::class.java, LootPacket::class.java, 0, Side.SERVER)
        packetHandler.registerMessage<DisposePacket, IMessage>(DisposePacket.HANDLER::class.java, DisposePacket::class.java, 1, Side.SERVER)
        packetHandler.registerMessage<ReqCorpseSyncPacket, IMessage>(ReqCorpseSyncPacket.HANDLER::class.java, ReqCorpseSyncPacket::class.java, 2, Side.SERVER)
        packetHandler.registerMessage<CorpseSyncPacket, IMessage>(CorpseSyncPacket.HANDLER::class.java, CorpseSyncPacket::class.java, 3, Side.CLIENT)
    }
}

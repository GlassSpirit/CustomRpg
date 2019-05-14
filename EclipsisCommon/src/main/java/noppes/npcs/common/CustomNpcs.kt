package noppes.npcs.common

import net.minecraft.client.Minecraft
import net.minecraft.server.MinecraftServer
import net.minecraftforge.common.util.FakePlayer
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.Mod.EventHandler
import net.minecraftforge.fml.common.SidedProxy
import net.minecraftforge.fml.common.event.*
import net.minecraftforge.fml.common.network.FMLEventChannel
import noppes.npcs.LogWriter
import noppes.npcs.api.wrapper.WrapperNpcAPI
import noppes.npcs.common.entity.EntityNPCInterface
import noppes.npcs.controllers.*
import noppes.npcs.controllers.data.Availability
import noppes.npcs.server.command.CommandNoppes
import java.io.File

@Mod(modid = CustomNpcs.MODID, name = CustomNpcs.MODNAME, version = CustomNpcs.VERSION, modLanguageAdapter = CustomNpcs.ADAPTER)
object CustomNpcs {

    const val MODID = "customnpcs"
    const val MODNAME = "CustomNPCs"
    const val VERSION = "GlassSpirit <3"
    const val ADAPTER = "net.shadowfacts.forgelin.KotlinAdapter"
    private const val CLIENT = "noppes.npcs.client.ClientProxy"
    private const val SERVER = "noppes.npcs.common.CommonProxy"

    @SidedProxy(clientSide = CLIENT, serverSide = SERVER)
    lateinit var proxy: CommonProxy

    @JvmField
    var FreezeNPCs = false

    lateinit var Dir: File

    lateinit var Channel: FMLEventChannel
    lateinit var ChannelPlayer: FMLEventChannel

    @JvmField
    var NoppesCommand = CommandNoppes()

    @JvmField
    var VerboseDebug = false

    lateinit var Server: MinecraftServer

    val worldSaveDirectory: File
        get() = getWorldSaveDirectory(null)

    @EventHandler
    fun preInit(event: FMLPreInitializationEvent) {
        proxy.preInit(event)
    }

    @EventHandler
    fun init(event: FMLInitializationEvent) {
        proxy.init(event)
    }

    @EventHandler
    fun serverAboutToStart(event: FMLServerAboutToStartEvent) {
        Server = event.server

        Availability.scoreboardValues.clear()
        ChunkController.instance.clear()
        FactionController.instance.load()
        PlayerDataController()
        TransportController()
        GlobalDataController()
        SpawnController()
        LinkedNpcController()
        MassBlockController()
        ScriptController.Instance.loadCategories()
        ScriptController.Instance.loadStoredData()
        ScriptController.Instance.loadPlayerScripts()
        ScriptController.Instance.loadForgeScripts()
        ScriptController.HasStart = false

        WrapperNpcAPI.clearCache()
    }

    //Loading items in the about to start event was corrupting items with a damage value
    @EventHandler
    fun started(event: FMLServerStartedEvent) {
        RecipeController.instance.load()
        BankController()
        DialogController.instance.load()
        QuestController.instance.load()
        ScriptController.HasStart = true
        ServerCloneController.Instance = ServerCloneController()
    }

    @EventHandler
    fun stopped(event: FMLServerStoppedEvent) {
        ServerCloneController.Instance = null
        //ItemScripted.Resources.clear()
    }

    @EventHandler
    fun serverStarting(event: FMLServerStartingEvent) {
        event.registerServerCommand(NoppesCommand)
        EntityNPCInterface.ChatEventPlayer = FakePlayer(event.server.getWorld(0), EntityNPCInterface.ChatEventProfile)
        EntityNPCInterface.CommandPlayer = FakePlayer(event.server.getWorld(0), EntityNPCInterface.CommandProfile)
    }

    fun getWorldSaveDirectory(s: String?): File {
        try {
            var dir = File(".")
            if (!Server.isDedicatedServer)
                dir = File(Minecraft.getMinecraft().gameDir, "saves")
            dir = File(File(dir, Server.folderName), "customnpcs")
            if (s != null) {
                dir = File(dir, s)
            }
            if (!dir.exists()) {
                dir.mkdirs()
            }
            return dir

        } catch (e: Exception) {
            LogWriter.error("Error getting worldsave", e)
        }
        return File(".")
    }
}

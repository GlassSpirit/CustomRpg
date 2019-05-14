package ru.glassspirit.eclipsis

import com.teamwizardry.librarianlib.features.network.Channel
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.SidedProxy
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.network.NetworkRegistry

@Mod(modid = EclipsisMod.MODID, name = EclipsisMod.MODNAME, version = EclipsisMod.VERSION,
        dependencies = EclipsisMod.DEPENDENCIES, modLanguageAdapter = EclipsisMod.ADAPTER)
object EclipsisMod {

    const val MODID = "eclipsis"
    const val MODNAME = "Eclipsis"
    const val VERSION = "GlassSpirit <3"
    const val DEPENDENCIES = "required-after:librarianlib"
    const val ADAPTER = "net.shadowfacts.forgelin.KotlinAdapter"
    private const val CLIENT = "ru.glassspirit.eclipsis.client.ClientProxy"
    private const val SERVER = "ru.glassspirit.eclipsis.CommonProxy"

    @SidedProxy(clientSide = CLIENT, serverSide = SERVER)
    lateinit var proxy: CommonProxy

    @Mod.EventHandler
    fun preInit(event: FMLPreInitializationEvent) {
        proxy.preInit(event)
    }

    @Mod.EventHandler
    fun init(event: FMLInitializationEvent) {
        proxy.init(event)
    }

    @Mod.EventHandler
    fun postInit(event: FMLPostInitializationEvent) {
        proxy.postInit(event)
        EclipsisPluginConfigurationReflection
    }

    @JvmField
    val CHANNEL = Channel(NetworkRegistry.INSTANCE.newSimpleChannel("eclipsis"))
}

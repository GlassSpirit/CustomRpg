package ru.glassspirit.conquest

import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.SidedProxy
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent

@Mod(modid = EclipsisConquestMod.MODID, name = EclipsisConquestMod.MODNAME, version = EclipsisConquestMod.VERSION,
        dependencies = EclipsisConquestMod.DEPENDENCIES, modLanguageAdapter = EclipsisConquestMod.ADAPTER)
object EclipsisConquestMod {

    const val MODID = "conquest"
    const val MODNAME = "EclipsisConquest"
    const val VERSION = "GlassSpirit <3"
    const val DEPENDENCIES = "required-after:eclipsis"
    const val ADAPTER = "net.shadowfacts.forgelin.KotlinAdapter"
    private const val CLIENT = "ru.glassspirit.conquest.ConquestClientProxy"
    private const val SERVER = "ru.glassspirit.conquest.ConquestCommonProxy"

    @SidedProxy(clientSide = CLIENT, serverSide = SERVER)
    lateinit var proxy: ConquestCommonProxy

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
    }

}
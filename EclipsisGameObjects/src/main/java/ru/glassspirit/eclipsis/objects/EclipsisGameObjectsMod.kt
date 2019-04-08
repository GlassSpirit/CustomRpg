package ru.glassspirit.eclipsis.objects

import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.SidedProxy
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent

@Mod(modid = EclipsisGameObjectsMod.MODID, name = EclipsisGameObjectsMod.MODNAME, version = EclipsisGameObjectsMod.VERSION,
        dependencies = EclipsisGameObjectsMod.DEPENDENCIES, modLanguageAdapter = EclipsisGameObjectsMod.ADAPTER)
object EclipsisGameObjectsMod {

    const val MODID = "eclipsis_objects"
    const val MODNAME = "EclipsisObjects"
    const val VERSION = "GlassSpirit <3"
    const val DEPENDENCIES = "required-after:eclipsis;"
    const val ADAPTER = "net.shadowfacts.forgelin.KotlinAdapter"
    private const val CLIENT = "ru.glassspirit.eclipsis.objects.ObjectsClientProxy"
    private const val SERVER = "ru.glassspirit.eclipsis.objects.ObjectsCommonProxy"

    @SidedProxy(clientSide = EclipsisGameObjectsMod.CLIENT, serverSide = EclipsisGameObjectsMod.SERVER)
    lateinit var proxy: ObjectsCommonProxy

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
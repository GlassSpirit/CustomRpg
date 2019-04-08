package ru.glassspirit.eclipsis.objects

import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent

open class ObjectsCommonProxy {

    open fun preInit(event: FMLPreInitializationEvent) {
        CustomRpgTab
        EclipsisBlocks
        EclipsisItems
    }

    open fun init(event: FMLInitializationEvent) {

    }

    open fun postInit(event: FMLPostInitializationEvent) {

    }

}

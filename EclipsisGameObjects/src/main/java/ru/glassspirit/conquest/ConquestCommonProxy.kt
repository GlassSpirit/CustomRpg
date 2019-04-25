package ru.glassspirit.conquest

import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent

open class ConquestCommonProxy {

    open fun preInit(event: FMLPreInitializationEvent) {
        ConquestTabs
        ConquestBlocks
        ConquestItems
    }

    open fun init(event: FMLInitializationEvent) {

    }

    open fun postInit(event: FMLPostInitializationEvent) {

    }

}
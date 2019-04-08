package ru.glassspirit.eclipsis.proxy

import com.google.common.collect.BiMap
import net.minecraftforge.fml.common.FMLCommonHandler
import net.minecraftforge.fml.common.ModContainer
import net.minecraftforge.fml.common.event.*


class ClientProxy : CommonProxy() {

    companion object {
        @JvmField
        var loadingStep: Int = 0
        @JvmField
        var loadingPercent: Float = 0F
        private var prevLoadingStep = 0
        private var loadedMods = 0

        @JvmStatic
        fun onModEvent(activeModList: List<ModContainer>, modObjectList: BiMap<ModContainer, Any>?, stateEvent: FMLEvent) {
            if (FMLCommonHandler.instance().side.isClient && ((stateEvent is FMLPreInitializationEvent)
                            or (stateEvent is FMLPostInitializationEvent)
                            or (stateEvent is FMLInitializationEvent)
                            or (stateEvent is FMLLoadCompleteEvent))) {
                loadingStep = when (stateEvent) {
                    is FMLPreInitializationEvent -> {
                        1
                    }
                    is FMLInitializationEvent -> {
                        5
                    }
                    is FMLPostInitializationEvent -> {
                        6
                    }
                    else -> {
                        7
                    }
                }
                if (prevLoadingStep != loadingStep) {
                    prevLoadingStep = loadingStep
                    loadedMods = 0
                }
                loadedMods++
                loadingPercent = (loadedMods / activeModList.size).toFloat()
            }

        }
    }

    override fun preInit(event: FMLPreInitializationEvent) {
        super.preInit(event)
    }

    override fun init(event: FMLInitializationEvent) {
        super.init(event)
    }

    override fun postInit(event: FMLPostInitializationEvent) {
        super.postInit(event)
    }

}

package ru.glassspirit.eclipsis.client

import net.minecraft.client.settings.KeyBinding
import net.minecraftforge.fml.client.registry.ClientRegistry
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import org.lwjgl.input.Keyboard
import ru.glassspirit.eclipsis.CommonProxy


class ClientProxy : CommonProxy() {

    companion object {
        @JvmField
        val testKey = KeyBinding("test key", Keyboard.KEY_Y, "test")
    }

    override fun preInit(event: FMLPreInitializationEvent) {
        super.preInit(event)
    }

    override fun init(event: FMLInitializationEvent) {
        super.init(event)
        ClientRegistry.registerKeyBinding(testKey)
    }

    override fun postInit(event: FMLPostInitializationEvent) {
        super.postInit(event)
    }

}

package ru.glassspirit.eclipsis.client.listeners

import com.teamwizardry.librarianlib.features.kotlin.Minecraft
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import net.minecraftforge.fml.relauncher.Side
import ru.glassspirit.eclipsis.EclipsisMod
import ru.glassspirit.eclipsis.client.ClientProxy
import ru.glassspirit.eclipsis.client.gui.GuiSkills

@Mod.EventBusSubscriber(value = [Side.CLIENT], modid = EclipsisMod.MODID)
object KeyListener {

    private val mc = Minecraft()

    @JvmStatic
    @SubscribeEvent
    fun onClientTick(event: TickEvent.ClientTickEvent) {
        if (ClientProxy.testKey.isPressed) {
            mc.displayGuiScreen(GuiSkills())
        }
    }

}
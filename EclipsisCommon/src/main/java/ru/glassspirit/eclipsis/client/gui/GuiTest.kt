package ru.glassspirit.eclipsis.client.gui

import com.teamwizardry.librarianlib.features.animator.Animator
import com.teamwizardry.librarianlib.features.animator.animations.BasicAnimation
import com.teamwizardry.librarianlib.features.gui.EnumMouseButton
import com.teamwizardry.librarianlib.features.gui.GuiBase
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents
import com.teamwizardry.librarianlib.features.gui.components.ComponentRect
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.Minecraft
import com.teamwizardry.librarianlib.features.kotlin.height
import com.teamwizardry.librarianlib.features.kotlin.width
import net.minecraft.client.gui.ScaledResolution
import java.awt.Color

class GuiTest : GuiBase(0, 0) {
    private val animator = Animator()

    override fun initGui() {
        super.initGui()
        val background = ComponentRect(0, 0, ScaledResolution(mc).scaledWidth, ScaledResolution(mc).scaledHeight)
        background.color(Color.GRAY)
        fullscreenComponents.add(background)

        background.transform.anchor = vec(background.width, background.height)
        val anim = BasicAnimation(background.transform, "rotate")
        anim.duration = 200f
        anim.repeatCount = -1
        anim.to = 2 * Math.PI
        //animator.add(anim)

        mainComponents.BUS.hook(GuiComponentEvents.MouseUpEvent::class.java) { event ->
            if (event.button == EnumMouseButton.RIGHT)
                Minecraft().displayGuiScreen(null)
        }
        mainComponents.BUS.hook(GuiComponentEvents.KeyDownEvent::class.java) { event ->
            if (event.keyCode == Minecraft().gameSettings.keyBindInventory.keyCode)
                Minecraft().displayGuiScreen(null)
        }
    }

    override fun doesGuiPauseGame(): Boolean {
        return false
    }
}
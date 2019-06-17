package ru.glassspirit.eclipsis.client.gui

import com.teamwizardry.librarianlib.features.animator.Animator
import com.teamwizardry.librarianlib.features.container.ContainerBase
import com.teamwizardry.librarianlib.features.gui.components.ComponentSprite
import com.teamwizardry.librarianlib.features.guicontainer.GuiContainerBase
import com.teamwizardry.librarianlib.features.kotlin.Minecraft
import com.teamwizardry.librarianlib.features.kotlin.toRl
import com.teamwizardry.librarianlib.features.sprite.Sprite

abstract class GuiContainerMain(container: ContainerBase) : GuiContainerBase(container, Minecraft().displayWidth, Minecraft().displayHeight) {
    private val animator = Animator()

    override fun initGui() {
        super.initGui()
        addFrame()
    }

    override fun doesGuiPauseGame(): Boolean {
        return false
    }

    protected fun addFrame() {
        val frameStartX = (width * 0.12).toInt()
        val frameStartY = (height * 0.04).toInt()
        val frameWidth = (width * 0.75).toInt()
        val frameHeight = (height * 0.8).toInt()

        val frame = ComponentSprite(Sprite("eclipsis:textures/gui/frame.png".toRl()), frameStartX, frameStartY, frameWidth, frameHeight)
        mainComponents.add(frame)
    }
}
package ru.glassspirit.eclipsis.client.gui

import com.teamwizardry.librarianlib.features.gui.components.ComponentText

class GuiSkills : GuiMain() {
    override fun initGui() {
        super.initGui()
        addSwitchPanel()
        addFrame()
        val text = ComponentText(0, 0)
        mainComponents.add(text)
    }
}
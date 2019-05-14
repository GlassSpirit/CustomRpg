package ru.glassspirit.eclipsis.client.gui

import com.teamwizardry.librarianlib.features.gui.GuiOverlay
import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import java.util.function.BooleanSupplier
import java.util.function.Consumer

class RpgHud : GuiComponent(0, 0) {
    init {
        GuiOverlay.getOverlayComponent(BooleanSupplier { true }, Consumer { this })
    }
}
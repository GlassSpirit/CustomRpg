package ru.glassspirit.eclipsis.client.gui

import com.teamwizardry.librarianlib.features.animator.Animator
import com.teamwizardry.librarianlib.features.gui.GuiBase
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents
import com.teamwizardry.librarianlib.features.gui.components.ComponentSprite
import com.teamwizardry.librarianlib.features.kotlin.Minecraft
import net.minecraft.client.renderer.GlStateManager
import ru.glassspirit.eclipsis.client.ClientProxy

abstract class GuiMain : GuiBase(Minecraft().displayWidth, Minecraft().displayHeight) {
    protected val animator = Animator()

    override fun initGui() {
        super.initGui()
        mainComponents.BUS.hook(GuiComponentEvents.KeyDownEvent::class.java) { event ->
            if (event.keyCode == mc.gameSettings.keyBindInventory.keyCode || event.keyCode == ClientProxy.testKey.keyCode)
                Minecraft().displayGuiScreen(null)
        }
    }

    override fun doesGuiPauseGame(): Boolean {
        return false
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        GlStateManager.disableAlpha()
        super.drawScreen(mouseX, mouseY, partialTicks)
    }

    protected fun addFrame() {
        val frameStartX = (width * 0.12).toInt()
        val frameStartY = (height * 0.04).toInt()
        val frameWidth = (width * 0.75).toInt()
        val frameHeight = (height * 0.8).toInt()

        val frame = ComponentSprite(Textures.GUI_FRAME, frameStartX, frameStartY, frameWidth, frameHeight)
        mainComponents.add(frame)
    }

    protected fun addSwitchPanel() {
        val panelStartX = (width * 0.07).toInt()
        val panelStartY = (height * 0.25).toInt()
        val panelWidth = (width * 0.05).toInt()
        val panelSlotHeight = (height * 0.09).toInt()

        for (i in 0..4) {
            val comp = ComponentSprite(Textures.GUI_SWITCH_PANEL_SLOT, panelStartX, panelStartY + (panelSlotHeight + 1) * i, panelWidth, panelSlotHeight)
            mainComponents.add(comp)
        }

        val character = ComponentSprite(Textures.GUI_SWITCH_PANEL_CHARACTER, panelStartX, panelStartY + (panelSlotHeight + 1) * 0, panelWidth, panelSlotHeight)
        character.BUS.hook(GuiComponentEvents.MouseInEvent::class.java) {
            character.sprite = Textures.GUI_SWITCH_PANEL_CHARACTER_HOVER
        }
        character.BUS.hook(GuiComponentEvents.MouseOutEvent::class.java) {
            character.sprite = Textures.GUI_SWITCH_PANEL_CHARACTER
        }
        character.BUS.hook(GuiComponentEvents.MouseClickEvent::class.java) {
            mc.displayGuiScreen(null)
        }

        val skills = ComponentSprite(Textures.GUI_SWITCH_PANEL_SKILLS, panelStartX, panelStartY + (panelSlotHeight + 1) * 1, panelWidth, panelSlotHeight)
        skills.BUS.hook(GuiComponentEvents.MouseInEvent::class.java) {
            skills.sprite = Textures.GUI_SWITCH_PANEL_SKILLS_HOVER
        }
        skills.BUS.hook(GuiComponentEvents.MouseOutEvent::class.java) {
            skills.sprite = Textures.GUI_SWITCH_PANEL_SKILLS
        }
        skills.BUS.hook(GuiComponentEvents.MouseClickEvent::class.java) {
            mc.displayGuiScreen(GuiSkills())
        }

        val quests = ComponentSprite(Textures.GUI_SWITCH_PANEL_QUESTS, panelStartX, panelStartY + (panelSlotHeight + 1) * 2, panelWidth, panelSlotHeight)
        quests.BUS.hook(GuiComponentEvents.MouseInEvent::class.java) {
            quests.sprite = Textures.GUI_SWITCH_PANEL_QUESTS_HOVER
        }
        quests.BUS.hook(GuiComponentEvents.MouseOutEvent::class.java) {
            quests.sprite = Textures.GUI_SWITCH_PANEL_QUESTS
        }
        quests.BUS.hook(GuiComponentEvents.MouseClickEvent::class.java) {
            mc.displayGuiScreen(null)
        }

        val party = ComponentSprite(Textures.GUI_SWITCH_PANEL_PARTY, panelStartX, panelStartY + (panelSlotHeight + 1) * 3, panelWidth, panelSlotHeight)
        party.BUS.hook(GuiComponentEvents.MouseInEvent::class.java) {
            party.sprite = Textures.GUI_SWITCH_PANEL_PARTY_HOVER
        }
        party.BUS.hook(GuiComponentEvents.MouseOutEvent::class.java) {
            party.sprite = Textures.GUI_SWITCH_PANEL_PARTY
        }
        party.BUS.hook(GuiComponentEvents.MouseClickEvent::class.java) {
            mc.displayGuiScreen(null)
        }

        val dungeons = ComponentSprite(Textures.GUI_SWITCH_PANEL_DUNGEONS, panelStartX, panelStartY + (panelSlotHeight + 1) * 4, panelWidth, panelSlotHeight)
        dungeons.BUS.hook(GuiComponentEvents.MouseInEvent::class.java) {
            dungeons.sprite = Textures.GUI_SWITCH_PANEL_DUNGEONS_HOVER
        }
        dungeons.BUS.hook(GuiComponentEvents.MouseOutEvent::class.java) {
            dungeons.sprite = Textures.GUI_SWITCH_PANEL_DUNGEONS
        }
        dungeons.BUS.hook(GuiComponentEvents.MouseClickEvent::class.java) {
            mc.displayGuiScreen(null)
        }

        mainComponents.add(character)
        mainComponents.add(skills)
        mainComponents.add(quests)
        mainComponents.add(party)
        mainComponents.add(dungeons)
    }
}
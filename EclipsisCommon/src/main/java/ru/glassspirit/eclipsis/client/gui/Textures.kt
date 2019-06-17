package ru.glassspirit.eclipsis.client.gui

import com.teamwizardry.librarianlib.features.sprite.Sprite
import com.teamwizardry.librarianlib.features.sprite.Texture
import net.minecraft.util.ResourceLocation

object Textures {

    //Pathes
    private val ECLIPSIS_GUI_TEXTURES = "eclipsis:textures/gui/"

    //TextureAtlases
    val GUI_SWITCH_PANEL_TEXTURE = Texture(ResourceLocation(ECLIPSIS_GUI_TEXTURES + "gui_switch_panel.png"))

    //Sprites

    //gui_switch_panel
    val GUI_SWITCH_PANEL_SLOT = Sprite(GUI_SWITCH_PANEL_TEXTURE, 0, 0, 68, 68, IntArray(0), 0, 0)
    val GUI_SWITCH_PANEL_CHARACTER = Sprite(GUI_SWITCH_PANEL_TEXTURE, 68, 0, 68, 68, IntArray(0), 0, 0)
    val GUI_SWITCH_PANEL_QUESTS = Sprite(GUI_SWITCH_PANEL_TEXTURE, 136, 0, 68, 68, IntArray(0), 0, 0)
    val GUI_SWITCH_PANEL_SKILLS = Sprite(GUI_SWITCH_PANEL_TEXTURE, 204, 0, 68, 68, IntArray(0), 0, 0)
    val GUI_SWITCH_PANEL_PARTY = Sprite(GUI_SWITCH_PANEL_TEXTURE, 272, 0, 68, 68, IntArray(0), 0, 0)
    val GUI_SWITCH_PANEL_DUNGEONS = Sprite(GUI_SWITCH_PANEL_TEXTURE, 340, 0, 68, 68, IntArray(0), 0, 0)
    val GUI_SWITCH_PANEL_CHARACTER_HOVER = Sprite(GUI_SWITCH_PANEL_TEXTURE, 68, 68, 68, 68, IntArray(0), 0, 0)
    val GUI_SWITCH_PANEL_QUESTS_HOVER = Sprite(GUI_SWITCH_PANEL_TEXTURE, 136, 68, 68, 68, IntArray(0), 0, 0)
    val GUI_SWITCH_PANEL_SKILLS_HOVER = Sprite(GUI_SWITCH_PANEL_TEXTURE, 204, 68, 68, 68, IntArray(0), 0, 0)
    val GUI_SWITCH_PANEL_PARTY_HOVER = Sprite(GUI_SWITCH_PANEL_TEXTURE, 272, 68, 68, 68, IntArray(0), 0, 0)
    val GUI_SWITCH_PANEL_DUNGEONS_HOVER = Sprite(GUI_SWITCH_PANEL_TEXTURE, 340, 68, 68, 68, IntArray(0), 0, 0)

    //frame
    val GUI_FRAME = Sprite(ResourceLocation(ECLIPSIS_GUI_TEXTURES + "gui_frame.png"))
}

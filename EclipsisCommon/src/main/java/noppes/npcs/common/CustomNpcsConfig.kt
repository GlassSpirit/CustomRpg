package noppes.npcs.common

import noppes.npcs.common.config.ConfigLoader
import noppes.npcs.common.config.ConfigProp
import java.io.File

object CustomNpcsConfig {
    private lateinit var config: ConfigLoader

    fun loadConfig(dir: File) {
        config = ConfigLoader(this::class.java, dir, "CustomNpcs")
        config.loadConfig()
    }

    fun updateConfig() {
        config.updateConfig()
    }

    @ConfigProp(info = "Whether scripting is enabled or not")
    @JvmField
    var EnableScripting = true

    @ConfigProp(info = "Arguments given to the Nashorn scripting library")
    @JvmField
    var NashornArguments = ""

    @ConfigProp(info = "Disable Chat Bubbles")
    @JvmField
    var EnableChatBubbles = true

    @ConfigProp(info = "Navigation search range for NPCs. Not recommended to increase if you have a slow pc or on a server")
    @JvmField
    var NpcNavRange = 32

    @ConfigProp(info = "Set to true if you want the dialog command option to be able to use op commands like tp etc")
    @JvmField
    var NpcUseOpCommands = false

    @ConfigProp
    @JvmField
    var FixUpdateFromPre_1_12 = false

    @ConfigProp(info = "If you are running sponge and you want to disable the permissions set this to true")
    @JvmField
    var DisablePermissions = false

    @ConfigProp
    @JvmField
    var SceneButtonsEnabled = true

    @ConfigProp
    @JvmField
    var EnableDefaultEyes = true

    @ConfigProp(info = "Only ops can create and edit npcs")
    @JvmField
    var OpsOnly = false

    @ConfigProp(info = "Default interact line. Leave empty to not have one")
    @JvmField
    var DefaultInteractLine = "Hello @p"

    @ConfigProp(info = "Number of chunk loading npcs that can be active at the same time")
    @JvmField
    var ChuckLoaders = 20

    @ConfigProp(info = "Normal players can use soulstone on animals")
    @JvmField
    var SoulStoneAnimals = true

    @ConfigProp(info = "Normal players can use soulstone on all npcs")
    @JvmField
    var SoulStoneNPCs = false

    @ConfigProp(info = "Type 0 = Normal, Type 1 = Solid")
    @JvmField
    var HeadWearType = 1

    @ConfigProp(info = "When set to Minecraft it will use minecrafts font, when Default it will use OpenSans. Can only use fonts installed on your PC")
    @JvmField
    var FontType = "Default"

    @ConfigProp(info = "Font size for custom fonts (doesn't work with minecrafts font)")
    @JvmField
    var FontSize = 18
}
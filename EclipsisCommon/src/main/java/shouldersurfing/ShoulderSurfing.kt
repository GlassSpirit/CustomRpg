package shouldersurfing

import net.minecraft.client.Minecraft
import net.minecraftforge.common.config.Configuration
import net.minecraftforge.fml.client.registry.ClientRegistry
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.Mod.Instance
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.apache.logging.log4j.LogManager

/**
 * @author Joshua Powers <jsh.powers></jsh.powers>@yahoo.com>
 * @version 1.10
 * @since 2013-11-18
 */

@SideOnly(Side.CLIENT)
@Mod(modid = ShoulderSurfing.MODID, name = ShoulderSurfing.NAME, version = ShoulderSurfing.VERSION, modLanguageAdapter = ShoulderSurfing.ADAPTER, clientSideOnly = true)
object ShoulderSurfing {

    lateinit var CONFIG: Configuration

    const val NAME = "Shoulder Surfing"
    const val MODID = "shouldersurfing"
    const val VERSION = "1.12"
    const val ADAPTER = "net.shadowfacts.forgelin.KotlinAdapter"
    const val DEVELOPERS = "Joshua Powers, Exopandora (for 1.8+)"
    val LOGGER = LogManager.getLogger("Shoulder Surfing")

    @Instance(ShoulderSurfing.MODID)
    lateinit var INSTACE: ShoulderSurfing

    private var shadersEnabled = false

    @Mod.EventHandler
    fun preInit(event: FMLPreInitializationEvent) {
        CONFIG = Configuration(event.suggestedConfigurationFile)
        CONFIG.load()

        this.syncConfig()

        ClientRegistry.registerKeyBinding(ShoulderSettings.KEYBIND_ROTATE_CAMERA_LEFT)
        ClientRegistry.registerKeyBinding(ShoulderSettings.KEYBIND_ROTATE_CAMERA_RIGHT)
        ClientRegistry.registerKeyBinding(ShoulderSettings.KEYBIND_ZOOM_CAMERA_OUT)
        ClientRegistry.registerKeyBinding(ShoulderSettings.KEYBIND_ZOOM_CAMERA_IN)
        ClientRegistry.registerKeyBinding(ShoulderSettings.KEYBIND_SWAP_SHOULDER)

        when (ShoulderSettings.DEFAULT_PERSPECTIVE) {
            "first person" -> Minecraft.getMinecraft().gameSettings.thirdPersonView = 0
            "third person" -> Minecraft.getMinecraft().gameSettings.thirdPersonView = 1
            "front third person" -> Minecraft.getMinecraft().gameSettings.thirdPersonView = 2
        }
    }

    @Mod.EventHandler
    fun loadComplete(event: FMLLoadCompleteEvent) {
        try {
            Class.forName("shadersmod.client.Shaders")
            this.shadersEnabled = true
        } catch (e: Exception) {
            this.shadersEnabled = false
        }

    }

    fun syncConfig() {
        ShoulderSettings.IS_DYNAMIC_CROSSHAIR_ENABLED = CONFIG.get(Configuration.CATEGORY_GENERAL, "Dynamic Crosshair", ShoulderSettings.IS_DYNAMIC_CROSSHAIR_ENABLED, "If enabled, then the crosshair moves around to line up with the block you are facing.").getBoolean(ShoulderSettings.IS_DYNAMIC_CROSSHAIR_ENABLED)
        ShoulderCamera.SHOULDER_ROTATION_YAW = CONFIG.get(Configuration.CATEGORY_GENERAL, "Rotation Offset", ShoulderCamera.SHOULDER_ROTATION_YAW.toDouble(), "Third person camera rotation").getDouble(ShoulderCamera.SHOULDER_ROTATION_YAW.toDouble()).toFloat()
        ShoulderCamera.SHOULDER_ZOOM_MOD = CONFIG.get(Configuration.CATEGORY_GENERAL, "Zoom Offset", ShoulderCamera.SHOULDER_ZOOM_MOD.toDouble(), "Third person camera zoom").getDouble(ShoulderCamera.SHOULDER_ZOOM_MOD.toDouble()).toFloat()
        ShoulderSettings.TRACE_TO_HORIZON_LAST_RESORT = CONFIG.get(Configuration.CATEGORY_GENERAL, "Always Show Crosshair", ShoulderSettings.TRACE_TO_HORIZON_LAST_RESORT, "Whether or not to show a crosshair in the center of the screen if nothing is in range of you").getBoolean(ShoulderSettings.TRACE_TO_HORIZON_LAST_RESORT)
        ShoulderSettings.USE_CUSTOM_RAYTRACE_DISTANCE = CONFIG.get(Configuration.CATEGORY_GENERAL, "Show Crosshair Farther", ShoulderSettings.USE_CUSTOM_RAYTRACE_DISTANCE, "Whether or not to show the crosshairs farther than normal").getBoolean(ShoulderSettings.USE_CUSTOM_RAYTRACE_DISTANCE)
        ShoulderSettings.HIDE_PLAYER_IF_TOO_CLOSE_TO_CAMERA = CONFIG.get(Configuration.CATEGORY_GENERAL, "Keep Camera Out Of Head", ShoulderSettings.HIDE_PLAYER_IF_TOO_CLOSE_TO_CAMERA, "Whether or not to hide the player model if the camera gets too close to it").getBoolean(ShoulderSettings.HIDE_PLAYER_IF_TOO_CLOSE_TO_CAMERA)
        ShoulderSettings.ENABLE_CROSSHAIR = CONFIG.get(Configuration.CATEGORY_GENERAL, "Third Person Crosshair", ShoulderSettings.ENABLE_CROSSHAIR, "Enable or disable the crosshair in third person").getBoolean(ShoulderSettings.ENABLE_CROSSHAIR)
        ShoulderSettings.ENABLE_ATTACK_INDICATOR = CONFIG.get(Configuration.CATEGORY_GENERAL, "Third Person Attack Indicator", ShoulderSettings.ENABLE_ATTACK_INDICATOR, "Enable or disable the attack indicator in third person").getBoolean(ShoulderSettings.ENABLE_ATTACK_INDICATOR)
        ShoulderSettings.IGNORE_BLOCKS_WITHOUT_COLLISION = CONFIG.get(Configuration.CATEGORY_GENERAL, "Ignore Blocks Without Collision", ShoulderSettings.IGNORE_BLOCKS_WITHOUT_COLLISION, "Whether or not the camera ignores blocks without collision").getBoolean(ShoulderSettings.IGNORE_BLOCKS_WITHOUT_COLLISION)
        ShoulderSettings.DEFAULT_PERSPECTIVE = CONFIG.get(Configuration.CATEGORY_GENERAL, "Default Perspective", ShoulderSettings.DEFAULT_PERSPECTIVE, "The default perspective when you init the game", arrayOf("First person", "Third person", "Front third person", "Shoulder surfing")).string

        if (ShoulderSurfing.CONFIG.hasChanged()) {
            ShoulderSurfing.CONFIG.save()
        }
    }

    fun areShadersEnabled(): Boolean {
        return this.shadersEnabled
    }

}
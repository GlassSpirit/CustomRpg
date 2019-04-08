package shouldersurfing

import net.minecraft.client.settings.KeyBinding
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.lwjgl.input.Keyboard

/**
 * @author Joshua Powers <jsh.powers></jsh.powers>@yahoo.com>
 * @version 1.3
 * @since 2013-01-14
 */
@SideOnly(Side.CLIENT)
object ShoulderSettings {
    var KEYBIND_ROTATE_CAMERA_LEFT = KeyBinding("Camera left", Keyboard.KEY_LEFT, "key.categories.misc")
    var KEYBIND_ROTATE_CAMERA_RIGHT = KeyBinding("Camera right", Keyboard.KEY_RIGHT, "key.categories.misc")
    var KEYBIND_ZOOM_CAMERA_OUT = KeyBinding("Camera closer", Keyboard.KEY_UP, "key.categories.misc")
    var KEYBIND_ZOOM_CAMERA_IN = KeyBinding("Camera farther", Keyboard.KEY_DOWN, "key.categories.misc")
    var KEYBIND_SWAP_SHOULDER = KeyBinding("Swap shoulder", Keyboard.KEY_O, "key.categories.misc")

    /**
     * Whether or not zooming is unlimited
     */
    var IS_ZOOM_UNLIMITED = false
    var ZOOM_MINIMUM = 0.3f
    var ZOOM_MAXIMUM = 2.0f

    /**
     * Whether or not rotation is unlimited
     */
    var IS_ROTATION_UNLIMITED = false
    var ROTATION_MINIMUM = -60.0f
    var ROTATION_MAXIMUM = 60.0f

    var HIDE_PLAYER_IF_TOO_CLOSE_TO_CAMERA = true

    /**
     * Distance to raytrace to find the player's line of eye sight and whether
     * or not we use this custom distance. If we are not using the distance
     * here, then the player's block break length is used.
     */
    var USE_CUSTOM_RAYTRACE_DISTANCE = true
    var RAYTRACE_DISTANCE = 400.0f

    /**
     * If the ray trace hits nothing, assume it hit the horizon
     */
    var TRACE_TO_HORIZON_LAST_RESORT = true

    /**
     * Whether or not the dynamic crosshair is enabled
     */
    var IS_DYNAMIC_CROSSHAIR_ENABLED = true

    var ENABLE_CROSSHAIR = true
    var ENABLE_ATTACK_INDICATOR = true

    /**
     * Whether or not the camera distance in third person has to be adjusted when
     * the ray trace hits a block without collision
     */
    var IGNORE_BLOCKS_WITHOUT_COLLISION = true

    var DEFAULT_PERSPECTIVE = "third person"
}

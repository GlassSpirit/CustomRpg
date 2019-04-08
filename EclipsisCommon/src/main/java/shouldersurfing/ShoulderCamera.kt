package shouldersurfing

import com.teamwizardry.librarianlib.features.kotlin.Minecraft
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

@SideOnly(Side.CLIENT)
object ShoulderCamera {
    /**
     * The number of degrees to rotate the camera
     */
    var SHOULDER_ROTATION_YAW: Float = 0f
    var SHOULDER_ROTATION_YAW_ANIMATION: Float = 0f

    /**
     * The number of degrees to rotate the camera
     */
    var SHOULDER_ROTATION_PITCH: Float = 0f

    /**
     * How much the camera view distance should change
     */
    var SHOULDER_ZOOM_MOD = 0.7f
    var SHOULDER_ZOOM_MOD_ANIMATION: Float = 0f

    fun adjustCameraLeft() {
        if (ShoulderSettings.IS_ROTATION_UNLIMITED || SHOULDER_ROTATION_YAW < ShoulderSettings.ROTATION_MAXIMUM) {
            SHOULDER_ROTATION_YAW += 0.5f * getFpsMultiplier()
            SHOULDER_ROTATION_YAW_ANIMATION += 0.5f * getFpsMultiplier()
        }
    }

    fun adjustCameraRight() {
        if (ShoulderSettings.IS_ROTATION_UNLIMITED || SHOULDER_ROTATION_YAW > ShoulderSettings.ROTATION_MINIMUM) {
            SHOULDER_ROTATION_YAW -= 0.5f * getFpsMultiplier()
            SHOULDER_ROTATION_YAW_ANIMATION -= 0.5f * getFpsMultiplier()
        }
    }

    fun adjustCameraIn() {
        if (ShoulderSettings.IS_ZOOM_UNLIMITED || SHOULDER_ZOOM_MOD < ShoulderSettings.ZOOM_MAXIMUM) {
            SHOULDER_ZOOM_MOD += 0.005f * getFpsMultiplier()
            SHOULDER_ZOOM_MOD_ANIMATION += 0.005f * getFpsMultiplier()
        }
    }

    fun adjustCameraOut() {
        if (ShoulderSettings.IS_ZOOM_UNLIMITED || SHOULDER_ZOOM_MOD > ShoulderSettings.ZOOM_MINIMUM) {
            SHOULDER_ZOOM_MOD -= 0.005f * getFpsMultiplier()
            SHOULDER_ZOOM_MOD_ANIMATION -= 0.005f * getFpsMultiplier()
        }
    }

    fun swapShoulder() {
        SHOULDER_ROTATION_YAW = -SHOULDER_ROTATION_YAW
    }

    fun isThirdPersonView(): Boolean {
        return Minecraft().gameSettings.thirdPersonView == 1
    }

    fun getFpsMultiplier(): Float {
        val normal = 60f / net.minecraft.client.Minecraft.getDebugFPS()
        return if (normal <= 1f) {
            normal
        } else {
            1f
        }
    }
}

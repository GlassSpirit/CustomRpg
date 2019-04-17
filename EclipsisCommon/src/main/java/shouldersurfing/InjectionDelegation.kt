package shouldersurfing

import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.Minecraft
import net.minecraft.entity.Entity
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.RayTraceResult
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import shouldersurfing.math.RayTracer
import shouldersurfing.math.VectorConverter

/**
 * @author Joshua Powers <jsh.powers></jsh.powers>@yahoo.com>
 * @version 1.0
 * @since 2013-01-14
 *
 *
 * Injected code is delegated here
 */
@SideOnly(Side.CLIENT)
object InjectionDelegation {

    private var CAMERA_DISTANCE = 0.0

    /**
     * Called by injected code to modify the camera rotation yaw
     */
    val shoulderRotationYaw: Float
        get() = if (ShoulderCamera.isThirdPersonView()) {
            ShoulderCamera.SHOULDER_ROTATION_YAW - ShoulderCamera.SHOULDER_ROTATION_YAW_ANIMATION
        } else 0f

    /**
     * Called by injected code to modify the camera rotation pitch
     */
    val shoulderRotationPitch: Float
        get() = if (ShoulderCamera.isThirdPersonView()) {
            ShoulderCamera.SHOULDER_ROTATION_PITCH
        } else 0f

    /**
     * Called by injected code to modify the camera zoom
     */
    val shoulderZoomMod: Float
        get() = if (ShoulderCamera.isThirdPersonView()) {
            ShoulderCamera.SHOULDER_ZOOM_MOD - ShoulderCamera.SHOULDER_ZOOM_MOD_ANIMATION
        } else 1.0f

    /**
     * Called by injected code to project a raytrace hit to the screen
     */
    fun calculateRayTraceProjection() {
        if (RayTracer.rayTraceHit != null) {
            RayTracer.projectedVector = VectorConverter.project2D(RayTracer.rayTraceHit!!)
            RayTracer.rayTraceHit = null
        }
    }

    /**
     * Called by injected code to determine whether the camera is too close to
     * the player
     */
    fun verifyReverseBlockDist(distance: Double) {
        if (distance < 0.80 && ShoulderSettings.HIDE_PLAYER_IF_TOO_CLOSE_TO_CAMERA) {
            RayTracer.skipPlayerRender = true
        }
    }

    /**
     * Called by injected code to perform the ray trace
     */
    fun getRayTraceResult(world: World, vec1: Vec3d, vec2: Vec3d): RayTraceResult? {
        return if (ShoulderSettings.IGNORE_BLOCKS_WITHOUT_COLLISION) {
            world.rayTraceBlocks(vec1, vec2, false, true, false)
        } else world.rayTraceBlocks(vec1, vec2)
    }

    /**
     * Called by injected code to get the maximum possible distance for the camera
     */
    fun checkDistance(distance: Double, yaw: Float, posX: Double, posY: Double, posZ: Double, cameraXoffset: Double, cameraZoffset: Double, cameraYoffset: Double): Double {
        if (ShoulderCamera.isThirdPersonView()) {
            var result = distance
            val radiant = (Math.PI / 180f).toFloat()
            val offset = InjectionDelegation.shoulderRotationYaw
            val newYaw = yaw - offset

            val length = MathHelper.cos((-90.0f - offset) * radiant) * distance
            val addX = MathHelper.cos(newYaw * radiant) * length
            val addZ = MathHelper.sin(newYaw * radiant) * length

            for (i in 0..7) {
                var offsetX = ((i and 1) * 2 - 1).toFloat()
                var offsetY = ((i shr 1 and 1) * 2 - 1).toFloat()
                var offsetZ = ((i shr 2 and 1) * 2 - 1).toFloat()

                offsetX *= 0.1f
                offsetY *= 0.1f
                offsetZ *= 0.1f

                val raytraceresult = getRayTraceResult(Minecraft().world,
                        vec(posX + offsetX, posY + offsetY, posZ + offsetZ),
                        vec(posX - (cameraXoffset + addX) + offsetX + offsetZ, posY - cameraYoffset + offsetY, posZ - (cameraZoffset + addZ) + offsetZ))

                if (raytraceresult != null) {
                    val newDistance = raytraceresult.hitVec.distanceTo(vec(posX, posY, posZ))

                    if (newDistance < result) {
                        result = newDistance
                    }
                }
            }

            CAMERA_DISTANCE = result
            return result
        }

        CAMERA_DISTANCE = distance
        return distance
    }

    fun getPositionEyes(entity: Entity, positionEyes: Vec3d): Vec3d {
        if (!ShoulderSettings.IS_DYNAMIC_CROSSHAIR_ENABLED) {
            val radiant = (Math.PI / 180f).toFloat()

            val length = MathHelper.cos((90f - InjectionDelegation.shoulderRotationYaw) * radiant) * CAMERA_DISTANCE
            val addX = MathHelper.cos(entity.rotationYaw * radiant) * length
            val addZ = MathHelper.sin(entity.rotationYaw * radiant) * length

            return positionEyes.add(Vec3d(addX, 0.0, addZ))
        }

        return positionEyes
    }
}

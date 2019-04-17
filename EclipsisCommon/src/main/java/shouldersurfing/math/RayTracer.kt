package shouldersurfing.math

import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.Minecraft
import net.minecraft.util.math.Vec2f
import net.minecraft.util.math.Vec3d
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import shouldersurfing.ShoulderCamera
import shouldersurfing.ShoulderSettings

@SideOnly(Side.CLIENT)
object RayTracer {
    var rayTraceInReach = false
    var skipPlayerRender = false
    var projectedVector: Vec2f? = null
    var rayTraceHit: Vec3d? = null

    private val mc = Minecraft()

    fun traceFromEyes(tick: Float) {
        this.projectedVector = null
        val entity = mc.renderViewEntity
        if (entity == null || mc.world == null || !ShoulderCamera.isThirdPersonView()) return

        val playerReach: Double = if (ShoulderSettings.USE_CUSTOM_RAYTRACE_DISTANCE) {
            ShoulderSettings.RAYTRACE_DISTANCE.toDouble()
        } else {
            mc.playerController.blockReachDistance.toDouble()
        }

        // block collision
        val result = entity.rayTrace(playerReach, tick)
        var blockDist = 0.0
        if (result != null) {
            this.rayTraceHit = result.hitVec
            blockDist = result.hitVec.distanceTo(vec(entity.posX, entity.posY, entity.posZ))
            this.rayTraceInReach = blockDist <= mc.playerController.blockReachDistance
        } else {
            this.rayTraceHit = null
        }

        // entity collision
        val renderViewPos = entity.getPositionEyes(tick)
        val sightVector = entity.getLook(tick)
        val sightRay = renderViewPos.add(sightVector.x * playerReach - 5.0, sightVector.y * playerReach, sightVector.z * playerReach)

        val entityList = mc.world.getEntitiesWithinAABBExcludingEntity(entity,
                entity.entityBoundingBox.expand(
                        sightVector.x * playerReach,
                        sightVector.y * playerReach,
                        sightVector.z * playerReach)
                        .expand(1.0, 1.0, 1.0))

        for (ent in entityList) {
            if (ent.canBeCollidedWith()) {
                val collisionSize = ent.collisionBorderSize

                val aabb = ent.entityBoundingBox.expand(collisionSize.toDouble(), collisionSize.toDouble(), collisionSize.toDouble())
                val potentialIntercept = aabb.calculateIntercept(renderViewPos, sightRay)

                if (potentialIntercept != null) {
                    val entityDist = potentialIntercept.hitVec.distanceTo(vec(entity.posX, entity.posY, entity.posZ))

                    if (entityDist < blockDist) {
                        this.rayTraceHit = potentialIntercept.hitVec
                        this.rayTraceInReach = entityDist <= mc.playerController.blockReachDistance
                    }
                }
            }
        }

    }
}

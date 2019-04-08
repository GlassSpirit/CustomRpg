package shouldersurfing.math

import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.Minecraft
import net.minecraft.util.math.Vec3d
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import shouldersurfing.ShoulderCamera
import shouldersurfing.ShoulderSettings
import shouldersurfing.renderer.ShoulderRenderBin

@SideOnly(Side.CLIENT)
object RayTracer {

    private val mc = Minecraft()

    fun traceFromEyes(tick: Float) {
        ShoulderRenderBin.projectedVector = null
        val entity = mc.renderViewEntity
        if (entity == null || mc.world == null) return
        if (ShoulderCamera.isThirdPersonView()) {
            val playerReach: Double = if (ShoulderSettings.USE_CUSTOM_RAYTRACE_DISTANCE) {
                ShoulderSettings.RAYTRACE_DISTANCE.toDouble()
            } else {
                mc.playerController.blockReachDistance.toDouble()
            }

            // block collision
            val omo = entity.rayTrace(playerReach, tick)
            var blockDist = 0.0

            if (omo != null) {
                ShoulderRenderBin.rayTraceHit = omo.hitVec
                blockDist = omo.hitVec.distanceTo(Vec3d(entity.posX, entity.posY, entity.posZ))
                ShoulderRenderBin.rayTraceInReach = blockDist <= mc.playerController.blockReachDistance.toDouble()
            } else {
                ShoulderRenderBin.rayTraceHit = null
            }

            // entity collision
            val renderViewPos = entity.getPositionEyes(tick)
            val sightVector = entity.getLook(tick)
            val sightRay = renderViewPos.add(sightVector.x * playerReach - 5, sightVector.y * playerReach, sightVector.z * playerReach)

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
                            ShoulderRenderBin.rayTraceHit = potentialIntercept.hitVec
                            ShoulderRenderBin.rayTraceInReach = entityDist <= mc.playerController.blockReachDistance.toDouble()
                        }
                    }
                }
            }
        }
    }
}

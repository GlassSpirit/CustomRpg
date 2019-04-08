package rpgloot.client

import com.mojang.authlib.GameProfile
import net.minecraft.client.entity.EntityOtherPlayerMP
import net.minecraft.client.renderer.entity.Render
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraft.entity.Entity
import net.minecraft.entity.monster.EntitySpider
import net.minecraft.entity.passive.EntityAmbientCreature
import net.minecraft.entity.passive.EntityAnimal
import net.minecraft.util.ResourceLocation
import net.minecraft.world.World
import org.lwjgl.opengl.GL11
import rpgloot.entities.EntCorpse
import java.util.*

class RenderCorpse constructor(manager: RenderManager) : Render<EntCorpse>(manager) {

    override fun doRender(entity: EntCorpse, xPosition: Double, yPosition: Double, z: Double, whoknows: Float, partialTicks: Float) {
        try {
            val entClass = entity.entityClass
            if (!entClass.isEmpty()) {
                GL11.glPushMatrix()
                GL11.glTranslatef(xPosition.toFloat(), yPosition.toFloat(), z.toFloat())
                val entInstance: Any
                if (entClass.contains("EntityPlayerMP")) {
                    val entClazz = UUID(entity.oldEntityData!!.getLong("UUIDMost"),
                            entity.oldEntityData!!.getLong("UUIDLeast"))
                    entInstance = EntityOtherPlayerMP(entity.world, GameProfile(entClazz, ""))
                    (entInstance as Entity).isSneaking = true
                } else {
                    val entClazz1 = Class.forName(entClass)
                    entInstance = entClazz1.getConstructor(World::class.java).newInstance(entity.world)
                    if (entity.oldEntityData != null) {
                        (entInstance as Entity).readFromNBT(entity.oldEntityData!!)
                    }
                }

                GL11.glTranslated(0.0, ((entInstance as Entity).entityBoundingBox.maxX - entInstance.entityBoundingBox.minX) / 2.0, 0.0)
                GL11.glRotatef(entInstance.prevRotationYaw.toInt().toFloat(), 0.0f, 1.0f, 0.0f)
                if (entInstance.entityBoundingBox.maxY > 1.5) {
                    GL11.glRotatef(-90.0f, 1.0f, 0.0f, 0.0f)
                }

                if (entInstance is EntitySpider) {
                    GL11.glRotatef(-90.0f, 1.0f, 0.0f, 0.0f)
                } else if (entInstance !is EntityAnimal && entInstance !is EntityAmbientCreature) {
                    if (entInstance is EntityOtherPlayerMP) {
                        GL11.glRotatef(180.0f, 1.0f, 0.0f, 0.0f)
                        GL11.glRotatef(90.0f, 0.0f, 0.0f, 1.0f)
                        GL11.glTranslatef(0.0f, 0.0f, ((entInstance as Entity).entityBoundingBox.maxZ - (entInstance as Entity).entityBoundingBox.minZ).toFloat() / 2.0f)
                    }
                } else {
                    GL11.glRotatef(90.0f, 0.0f, 1.0f, 0.0f)
                    GL11.glTranslatef((entInstance.entityBoundingBox.maxZ - entInstance.entityBoundingBox.minZ).toFloat() / 2.0f, 0.0f, 0.0f)
                }

                GL11.glTranslatef(0.0f, (-(entInstance.entityBoundingBox.maxY - entInstance.entityBoundingBox.minY)).toFloat() / 2.0f, 0.0f)
                renderManager.renderEntity(entInstance, 0.0, 0.0, 0.0, 0.0f, 0.0f, false)
                GL11.glPopMatrix()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun doRenderShadowAndFire(entityIn: Entity, x: Double, y: Double, z: Double, yaw: Float, partialTicks: Float) {
    }

    override fun getEntityTexture(entity: EntCorpse): ResourceLocation? {
        return null
    }
}

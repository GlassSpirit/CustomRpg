package shouldersurfing

import com.teamwizardry.librarianlib.features.kotlin.Minecraft
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Gui
import net.minecraft.client.gui.GuiIngame
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.OpenGlHelper
import net.minecraft.entity.EntityLivingBase
import net.minecraft.util.math.RayTraceResult.Type
import net.minecraft.util.math.Vec3d
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.client.event.RenderPlayerEvent
import net.minecraftforge.common.config.Configuration
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import shouldersurfing.math.RayTracer
import shouldersurfing.renderer.ShoulderRenderBin

@SideOnly(Side.CLIENT)
@Mod.EventBusSubscriber(modid = ShoulderSurfing.MODID)
object ShoulderEventHandler {

    /**
     * Holds the last coordinate drawing position
     */
    private var lastX = 0.0f
    private var lastY = 0.0f
    private var animLastX = 0.0f
    private var animLastY = 0.0f

    @JvmStatic
    @SubscribeEvent
    fun renderEvent(event: RenderTickEvent) {
        ShoulderRenderBin.skipPlayerRender = false
        RayTracer.traceFromEyes(1.0f)

        if (ShoulderRenderBin.rayTraceHit != null) {
            if (Minecraft.getMinecraft().player != null) {
                ShoulderRenderBin.rayTraceHit = ShoulderRenderBin.rayTraceHit!!.subtract(Vec3d(Minecraft.getMinecraft().player.posX, Minecraft.getMinecraft().player.posY, Minecraft.getMinecraft().player.posZ))
            }
        }

        if (Math.abs(ShoulderCamera.SHOULDER_ROTATION_YAW_ANIMATION) < 0.05f) ShoulderCamera.SHOULDER_ROTATION_YAW_ANIMATION = 0f
        if (ShoulderCamera.SHOULDER_ROTATION_YAW_ANIMATION > 0) {
            ShoulderCamera.SHOULDER_ROTATION_YAW_ANIMATION -= 0.05f * Math.abs(ShoulderCamera.SHOULDER_ROTATION_YAW_ANIMATION) / 0.5f
        } else if (ShoulderCamera.SHOULDER_ROTATION_YAW_ANIMATION < 0) {
            ShoulderCamera.SHOULDER_ROTATION_YAW_ANIMATION += 0.05f * Math.abs(ShoulderCamera.SHOULDER_ROTATION_YAW_ANIMATION) / 0.5f
        }

        if (Math.abs(ShoulderCamera.SHOULDER_ZOOM_MOD_ANIMATION) < 0.0005f) ShoulderCamera.SHOULDER_ZOOM_MOD_ANIMATION = 0f
        if (ShoulderCamera.SHOULDER_ZOOM_MOD_ANIMATION > 0) {
            ShoulderCamera.SHOULDER_ZOOM_MOD_ANIMATION -= 0.0005f * Math.abs(ShoulderCamera.SHOULDER_ZOOM_MOD_ANIMATION) / 0.005f
        } else if (ShoulderCamera.SHOULDER_ZOOM_MOD_ANIMATION < 0) {
            ShoulderCamera.SHOULDER_ZOOM_MOD_ANIMATION += 0.0005f * Math.abs(ShoulderCamera.SHOULDER_ZOOM_MOD_ANIMATION) / 0.005f
        }
    }

    @JvmStatic
    @SubscribeEvent
    fun keyPressedRender(event: RenderTickEvent) {
        if (Minecraft.getMinecraft().currentScreen == null) {
            if (ShoulderCamera.isThirdPersonView()) {
                if (ShoulderSettings.KEYBIND_ROTATE_CAMERA_LEFT.isKeyDown)
                    ShoulderCamera.adjustCameraLeft()
                if (ShoulderSettings.KEYBIND_ROTATE_CAMERA_RIGHT.isKeyDown)
                    ShoulderCamera.adjustCameraRight()
                if (ShoulderSettings.KEYBIND_ZOOM_CAMERA_IN.isKeyDown)
                    ShoulderCamera.adjustCameraIn()
                if (ShoulderSettings.KEYBIND_ZOOM_CAMERA_OUT.isKeyDown)
                    ShoulderCamera.adjustCameraOut()
                if (ShoulderSettings.KEYBIND_SWAP_SHOULDER.isPressed)
                    ShoulderCamera.swapShoulder()
            }
        }
    }

    @JvmStatic
    @SubscribeEvent
    fun keyPressed(event: TickEvent.ClientTickEvent) {
        if (Minecraft.getMinecraft().currentScreen == null) {
            if (ShoulderCamera.isThirdPersonView()) {
                ShoulderSurfing.CONFIG.get(Configuration.CATEGORY_GENERAL, "Rotation Offset", ShoulderCamera.SHOULDER_ROTATION_YAW.toDouble(), "Third person camera rotation").set(ShoulderCamera.SHOULDER_ROTATION_YAW.toDouble())
                ShoulderSurfing.CONFIG.get(Configuration.CATEGORY_GENERAL, "Zoom Offset", ShoulderCamera.SHOULDER_ZOOM_MOD.toDouble(), "Third person camera zoom").set(ShoulderCamera.SHOULDER_ZOOM_MOD.toDouble())
                ShoulderSurfing.CONFIG.save()
            }
        }
    }

    @JvmStatic
    @SubscribeEvent
    fun preRenderPlayer(event: RenderPlayerEvent.Pre) {
        if (ShoulderRenderBin.skipPlayerRender && (event.renderer.renderManager.playerViewY != 180f || Minecraft.getMinecraft().inGameHasFocus)) {
            if (event.isCancelable) {
                event.isCanceled = true
            }
        }
    }

    @JvmStatic
    @SubscribeEvent
    fun preRenderCrosshairs(event: RenderGameOverlayEvent.Pre) {
        if (event.type == RenderGameOverlayEvent.ElementType.CROSSHAIRS) {
            val tick = event.partialTicks
            val mc = Minecraft()
            val gui = mc.ingameGUI

            val resolution = ScaledResolution(Minecraft())

            val width = resolution.scaledWidth
            val height = resolution.scaledHeight
            val scale = resolution.scaleFactor.toFloat()

            if (animLastX == 0f || animLastY == 0f) {
                animLastX = width * scale / 2
                animLastY = height * scale / 2
            }

            if (mc.gameSettings.showDebugInfo && !mc.gameSettings.hideGUI && !mc.player.hasReducedDebug() && !mc.gameSettings.reducedDebugInfo) {
                GlStateManager.pushMatrix()
                GlStateManager.translate((width / 2).toFloat(), (height / 2).toFloat(), 300f)
                val entity = mc.renderViewEntity!!
                GlStateManager.rotate(entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * scale, -1.0f, 0.0f, 0.0f)
                GlStateManager.rotate(entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * scale, 0.0f, 1.0f, 0.0f)
                GlStateManager.scale(-1.0f, -1.0f, -1.0f)
                OpenGlHelper.renderDirections(10)
                GlStateManager.popMatrix()
            } else {
                if (ShoulderSettings.TRACE_TO_HORIZON_LAST_RESORT || mc.objectMouseOver == null || mc.objectMouseOver.typeOfHit != Type.MISS) {
                    if (!ShoulderCamera.isThirdPersonView() || !ShoulderSettings.IS_DYNAMIC_CROSSHAIR_ENABLED && ShoulderCamera.isThirdPersonView()) {
                        /** Default Crosshair  */

                        lastX = width * scale / 2
                        lastY = height * scale / 2

                        this.renderCrosshair(gui, resolution)
                    } else if (ShoulderCamera.isThirdPersonView()) {
                        /** Dynamic Crosshair  */

                        GlStateManager.pushMatrix()

                        var diffX = (width * scale / 2 - lastX) * tick
                        var diffY = (height * scale / 2 - lastY) * tick

                        if (ShoulderRenderBin.projectedVector != null) {
                            diffX = (ShoulderRenderBin.projectedVector!!.x - lastX) * tick
                            diffY = (ShoulderRenderBin.projectedVector!!.y - lastY) * tick
                        }

                        animLastX = diffX * 0.05f
                        animLastY = diffY * 0.05f

                        val crosshairWidth = (lastX + animLastX) / scale - 7
                        val crosshairHeight = (lastY + animLastY) / scale - 7

                        GlStateManager.scale(1.0f / scale, 1.0f / scale, 1.0f / scale)
                        GlStateManager.translate(crosshairWidth * scale, crosshairHeight * scale, 0.0f)
                        GlStateManager.scale(scale, scale, scale)
                        GlStateManager.translate((-width / 2 + 7).toFloat(), (-height / 2 + 7).toFloat(), 0.0f)

                        this.renderCrosshair(gui, resolution)

                        lastX += animLastX
                        lastY += animLastY

                        GlStateManager.popMatrix()
                    }
                }
            }

            /** SHORT-CIRCUIT THE RENDER  */
            if (event.isCancelable) {
                event.isCanceled = true
            }
        }
    }

    private fun renderCrosshair(gui: GuiIngame, resolution: ScaledResolution) {
        val mc = Minecraft()
        val width = resolution.scaledWidth
        val height = resolution.scaledHeight

        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)

        mc.textureManager.bindTexture(Gui.ICONS)

        GlStateManager.enableBlend()
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO)
        GlStateManager.enableAlpha()

        if (ShoulderSettings.ENABLE_CROSSHAIR || !ShoulderCamera.isThirdPersonView()) {
            gui.drawTexturedModalRect(width / 2 - 7, height / 2 - 7, 0, 0, 16, 16)
        }

        if (mc.gameSettings.attackIndicator == 1 && ShoulderSettings.ENABLE_ATTACK_INDICATOR) {
            val cooledAttackStrength = mc.player.getCooledAttackStrength(0.0f)
            var flag = false

            if (mc.pointedEntity is EntityLivingBase && cooledAttackStrength >= 1.0f) {
                flag = mc.player.cooldownPeriod > 5.0f
                flag = flag and mc.pointedEntity.isEntityAlive
            }

            val y = height / 2 - 7 + 16
            val x = width / 2 - 8

            if (flag) {
                gui.drawTexturedModalRect(x, y, 68, 94, 16, 16)
            } else if (cooledAttackStrength < 1.0f) {
                val offset = (cooledAttackStrength * 17.0f).toInt()
                gui.drawTexturedModalRect(x, y, 36, 94, 16, 4)
                gui.drawTexturedModalRect(x, y, 52, 94, offset, 4)
            }
        }

        GlStateManager.disableBlend()
    }

}

package rpgloot.client

import net.minecraft.client.gui.Gui
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.client.FMLClientHandler
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent
import org.lwjgl.opengl.GL11
import rpgloot.entities.EntCorpse

class ClientEvents {

    @SubscribeEvent
    fun drawLootIcon(event: RenderTickEvent) {
        val mc = FMLClientHandler.instance().client
        if (mc.currentScreen == null) {
            val tm = mc.textureManager
            val player = mc.player
            val world = mc.world
            if (!event.isCancelable && player != null && world != null) {
                val target = mc.objectMouseOver
                if (target != null && target.entityHit is EntCorpse) {
                    val corpse = target.entityHit as EntCorpse
                    val scale = ScaledResolution(mc)
                    val width = scale.scaledWidth
                    val height = scale.scaledHeight
                    if (corpse.isUsableByPlayer(player)) {
                        GL11.glColor3d(1.0, 1.0, 1.0)
                    }

                    tm.bindTexture(ResourceLocation("rpgloot", "textures/loot.png"))
                    Gui.drawModalRectWithCustomSizedTexture(width / 2 - 7, height / 2 - 7, 0.0f, 0.0f, 14, 14, 14.0f, 14.0f)
                }
            }
        }

    }
}

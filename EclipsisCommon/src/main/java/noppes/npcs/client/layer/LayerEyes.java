package noppes.npcs.client.layer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

public class LayerEyes extends LayerInterface {
    private BufferBuilder tessellator;

    public LayerEyes(RenderLiving render) {
        super(render);
    }

    @Override
    public void render(float par2, float par3, float par4, float par5, float par6, float par7) {
        if (!playerdata.eyes.isEnabled())
            return;
        GlStateManager.pushMatrix();
        model.bipedHead.postRender(0.0625F);
        GlStateManager.scale(par7, par7, -par7);
        GlStateManager.translate(0, (playerdata.eyes.type == 1 ? 1 : 2) - playerdata.eyes.eyePos, 0);

        GlStateManager.enableRescaleNormal();
        GlStateManager.shadeModel(7425);
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(
                GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.enableCull();
        GlStateManager.disableAlpha();
        GlStateManager.depthMask(false);
        int i = (npc.getBrightnessForRender());
        int j = i % 65536;
        int k = i / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) j, (float) k);
        Minecraft.getMinecraft().entityRenderer.setupFogColor(true);

        tessellator = Tessellator.getInstance().getBuffer();
        tessellator.begin(7, DefaultVertexFormats.POSITION_COLOR);
        drawLeft();
        drawRight();
        drawBrows();
        Tessellator.getInstance().draw();
        Minecraft.getMinecraft().entityRenderer.setupFogColor(false);

        GlStateManager.depthMask(true);
        GlStateManager.disableBlend();
        GlStateManager.shadeModel(7424);
        GlStateManager.enableAlpha();
        GlStateManager.disableCull();
        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();
        GlStateManager.enableTexture2D();
    }

    private void drawLeft() {
        if (playerdata.eyes.pattern == 2)
            return;
        drawRect(3, -5, 1, -4, 0xF6F6F6, 4.01);
        drawRect(2, -5, 1, -4, playerdata.eyes.color, 4.011);
        if (playerdata.eyes.glint && npc.isEntityAlive()) {
            drawRect(1.5, -4.9, 1.9, -4.5, 0xFFFFFFFF, 4.012);
        }
        if (playerdata.eyes.type == 1) {
            drawRect(3, -4, 1, -3, 0xFFFFFF, 4.01);
            drawRect(2, -4, 1, -3, playerdata.eyes.color, 4.011);
        }
    }

    private void drawRight() {
        if (playerdata.eyes.pattern == 1)
            return;
        drawRect(-3, -5, -1, -4, 0xF6F6F6, 4.01);
        drawRect(-2, -5, -1, -4, playerdata.eyes.color, 4.011);
        if (playerdata.eyes.glint && npc.isEntityAlive()) {
            drawRect(-1.5, -4.9, -1.1, -4.5, 0xFFFFFFFF, 4.012);
        }
        if (playerdata.eyes.type == 1) {
            drawRect(-3, -4, -1, -3, 0xFFFFFF, 4.01);
            drawRect(-2, -4, -1, -3, playerdata.eyes.color, 4.011);
        }
    }

    private void drawBrows() {
        float offsetY = 0;
        if (playerdata.eyes.blinkStart > 0 && npc.isEntityAlive()) {
            float f = (System.currentTimeMillis() - playerdata.eyes.blinkStart) / 150f;
            if (f > 1)
                f = 2 - f;
            if (f < 0) {
                playerdata.eyes.blinkStart = 0;
                f = 0;
            }
            offsetY = (playerdata.eyes.type == 1 ? 2 : 1) * f;
            drawRect(-3, -5, -1, -5 + offsetY, playerdata.eyes.skinColor, 4.013);
            drawRect(3, -5, 1, -5 + offsetY, playerdata.eyes.skinColor, 4.013);
        }
        if (playerdata.eyes.browThickness > 0) {
            float thickness = playerdata.eyes.browThickness / 10f;
            drawRect(-3, -5 + offsetY, -1, -5 - thickness + offsetY, playerdata.eyes.browColor, 4.014);
            drawRect(1, -5 + offsetY, 3, -5 - thickness + offsetY, playerdata.eyes.browColor, 4.014);
        }
    }


    public void drawRect(double x, double y, double x2, double y2, int color, double z) {
        double j1;

        if (x < x2) {
            j1 = x;
            x = x2;
            x2 = j1;
        }

        if (y < y2) {
            j1 = y;
            y = y2;
            y2 = j1;
        }

        float f1 = (float) (color >> 16 & 255) / 255.0F;
        float f2 = (float) (color >> 8 & 255) / 255.0F;
        float f3 = (float) (color & 255) / 255.0F;
        GlStateManager.color(1, 1, 1, 1);
        tessellator.pos(x, y, z).color(f1, f2, f3, 1).endVertex();
        tessellator.pos(x, y2, z).color(f1, f2, f3, 1).endVertex();
        tessellator.pos(x2, y2, z).color(f1, f2, f3, 1).endVertex();
        tessellator.pos(x2, y, z).color(f1, f2, f3, 1).endVertex();
    }

    @Override
    public void rotate(float par1, float par2, float par3, float par4, float par5, float par6) {

    }

}

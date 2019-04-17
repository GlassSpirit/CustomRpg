package noppes.npcs.client.renderer;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import noppes.npcs.api.constants.AnimationType;
import noppes.npcs.client.ImageDownloadAlt;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.entity.EntityNPCInterface;
import org.lwjgl.opengl.GL11;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Map;


public class RenderNPCInterface<T extends EntityNPCInterface> extends RenderLiving<T> {
    public static int LastTextureTick;

    public RenderNPCInterface(ModelBase model, float f) {
        super(Minecraft.getMinecraft().getRenderManager(), model, f);
    }

    @Override
    public void renderName(T npc, double d, double d1, double d2) {
        if (npc == null || !this.canRenderName(npc) || this.renderManager.renderViewEntity == null)
            return;

        double d0 = npc.getDistanceSq(this.renderManager.renderViewEntity);

        if (d0 > 8 * 8 * 8) {
            return;
        }

        if (npc.messages != null) {
            float height = ((npc.baseHeight / 5f) * npc.display.getSize());
            float offset = npc.height * (1.2f + (!npc.display.showName() ? 0 : npc.display.getTitle().isEmpty() ? 0.15f : 0.25f));

            npc.messages.renderMessages(d, d1 + offset, d2, 0.666667F * height, npc.isInRange(renderManager.renderViewEntity, 4));
        }
        float scale = (npc.baseHeight / 5f) * npc.display.getSize();
        if (npc.display.showName()) {
            renderLivingLabel(npc, (float) d, (float) d1 + npc.height - 0.06f * scale, (float) d2, 64, npc.getName(), npc.display.getTitle());
        }
    }

    @Override
    public void doRenderShadowAndFire(Entity par1Entity, double par2, double par4, double par6, float par8, float par9) {
        EntityNPCInterface npc = (EntityNPCInterface) par1Entity;
        this.shadowSize = npc.width;
        if (!npc.isKilled()) {
            super.doRenderShadowAndFire(par1Entity, par2, par4, par6, par8, par9);
        }
    }

    protected void renderLivingLabel(EntityNPCInterface npc, float d, float d1, float d2, int i, String name, String title) {
        FontRenderer fontrenderer = getFontRendererFromRenderManager();

        float f1 = (npc.baseHeight / 5f) * npc.display.getSize();
        float f2 = 0.01666667F * f1;
        GlStateManager.pushMatrix();
        GlStateManager.translate(d, d1, d2);
        GL11.glNormal3f(0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
        float height = f1 / 6.5f * 2;
        int color = npc.getFaction().color;
        GlStateManager.disableLighting();
        GlStateManager.depthMask(false);
        GlStateManager.translate(0, height, 0);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        if (!title.isEmpty()) {
            title = "<" + title + ">";
            float f3 = 0.01666667F * f1 * 0.6f;
            GlStateManager.translate(0, -f1 / 6.5f * 0.4f, 0);
            GlStateManager.scale(-f3, -f3, f3);
            fontrenderer.drawString(title, -fontrenderer.getStringWidth(title) / 2, 0, color);
            GlStateManager.scale(1 / -f3, 1 / -f3, 1 / f3);
            GlStateManager.translate(0, f1 / 6.5f * 0.85f, 0);
        }
        GlStateManager.scale(-f2, -f2, f2);

        if (npc.isInRange(renderManager.renderViewEntity, 4)) {
            GlStateManager.disableDepth();
            fontrenderer.drawString(name, -fontrenderer.getStringWidth(name) / 2, 0, color + 0x55000000);
            GlStateManager.enableDepth();
        }
        GlStateManager.depthMask(true);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        fontrenderer.drawString(name, -fontrenderer.getStringWidth(name) / 2, 0, color);
        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();
    }

    protected void renderColor(EntityNPCInterface npc) {
        if (npc.hurtTime <= 0 && npc.deathTime <= 0) {
            float red = (float) (npc.display.getTint() >> 16 & 255) / 255.0F;
            float green = (float) (npc.display.getTint() >> 8 & 255) / 255.0F;
            float blue = (float) (npc.display.getTint() & 255) / 255.0F;
            GlStateManager.color(red, green, blue, 1);
        }
    }

    private void renderLiving(T npc, double d, double d1, double d2, float xoffset, float yoffset, float zoffset) {
    }

    @Override
    protected void applyRotations(T npc, float f, float f1, float f2) {
        if (npc.isEntityAlive() && npc.isPlayerSleeping()) {
            GlStateManager.rotate(npc.ais.orientation, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(getDeathMaxRotation(npc), 0.0F, 0.0F, 1.0F);
            GlStateManager.rotate(270F, 0.0F, 1.0F, 0.0F);
        } else if (npc.isEntityAlive() && npc.currentAnimation == AnimationType.CRAWL) {
            GlStateManager.rotate(270.0F - f1, 0.0F, 1.0F, 0.0F);
            float scale = ((EntityCustomNpc) npc).display.getSize() / 5f;
            GlStateManager.translate(-scale + ((EntityCustomNpc) npc).modelData.getLegsY() * scale, 0.14f, 0);
            GlStateManager.rotate(270F, 0.0F, 0.0F, 1.0F);
            GlStateManager.rotate(270F, 0.0F, 1.0F, 0.0F);
        } else {
            super.applyRotations(npc, f, f1, f2);
        }
    }

    @Override
    protected void preRenderCallback(T npc, float f) {
        renderColor(npc);
        int size = npc.display.getSize();
        GlStateManager.scale((npc.scaleX / 5) * size, (npc.scaleY / 5) * size, (npc.scaleZ / 5) * size);
    }

    @Override
    public void doRender(T npc, double d, double d1, double d2, float f, float f1) {
        if (npc.isKilled() && npc.stats.hideKilledBody && npc.deathTime > 20) {
            return;
        }
        if ((npc.display.getBossbar() == 1 || npc.display.getBossbar() == 2 && npc.isAttacking()) && !npc.isKilled() && npc.deathTime <= 20 && npc.canSee(Minecraft.getMinecraft().player)) {

            //BossStatus.setBossStatus(npc, true);
        }

        if (npc.ais.getStandingType() == 3 && !npc.isWalking() && !npc.isInteracting()) {
            npc.prevRenderYawOffset = npc.renderYawOffset = npc.ais.orientation;
        }
        super.doRender(npc, d, d1, d2, f, f1);
    }

    @Override
    protected void renderModel(T npc, float par2, float par3, float par4, float par5, float par6, float par7) {
        super.renderModel(npc, par2, par3, par4, par5, par6, par7);
        if (!npc.display.getOverlayTexture().isEmpty()) {
            GlStateManager.depthFunc(GL11.GL_LEQUAL);
            if (npc.textureGlowLocation == null) {
                npc.textureGlowLocation = new ResourceLocation(npc.display.getOverlayTexture());
            }
            bindTexture(npc.textureGlowLocation);
            float f1 = 1.0F;
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GL11.GL_ONE, GL11.GL_ONE);
            GlStateManager.disableLighting();
            if (npc.isInvisible()) {
                GlStateManager.depthMask(false);
            } else {
                GlStateManager.depthMask(true);
            }
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.pushMatrix();
            GlStateManager.scale(1.001f, 1.001f, 1.001f);
            mainModel.render(npc, par2, par3, par4, par5, par6, par7);
            GlStateManager.popMatrix();
            GlStateManager.enableLighting();
            GlStateManager.color(1.0F, 1.0F, 1.0F, f1);


            GlStateManager.depthFunc(GL11.GL_LEQUAL);
            GlStateManager.disableBlend();
        }
    }

    @Override
    protected float handleRotationFloat(T npc, float par2) {
        if (npc.isKilled() || !npc.display.getHasLivingAnimation())
            return 0;
        return super.handleRotationFloat(npc, par2);
    }

    @Override
    protected void renderLivingAt(T npc, double d, double d1, double d2) {
        shadowSize = npc.display.getSize() / 10f;

        float xOffset = 0;
        float yOffset = npc.currentAnimation == AnimationType.NORMAL ? npc.ais.bodyOffsetY / 10 - 0.5f : 0;
        float zOffset = 0;

        if (npc.isEntityAlive()) {
            if (npc.isPlayerSleeping()) {
                xOffset = (float) -Math.cos(Math.toRadians(180 - npc.ais.orientation));
                zOffset = (float) -Math.sin(Math.toRadians(npc.ais.orientation));
                yOffset += 0.14f;
            } else if (npc.currentAnimation == AnimationType.SIT || npc.isRiding()) {
                yOffset -= 0.5f - ((EntityCustomNpc) npc).modelData.getLegsY() * 0.8f;
            }
        }
        xOffset = (xOffset / 5f) * npc.display.getSize();
        yOffset = (yOffset / 5f) * npc.display.getSize();
        zOffset = (zOffset / 5f) * npc.display.getSize();
        super.renderLivingAt(npc, d + xOffset, d1 + yOffset, d2 + zOffset);
    }

    @Override
    public ResourceLocation getEntityTexture(T npc) {
        if (npc.textureLocation == null) {
            if (npc.display.skinType == 0)// normal skin
                npc.textureLocation = new ResourceLocation(npc.display.getSkinTexture());
            else if (LastTextureTick < 5) { //fixes request flood somewhat
                return DefaultPlayerSkin.getDefaultSkinLegacy();
            } else if (npc.display.skinType == 1 && npc.display.playerProfile != null) { //player skin
                Minecraft minecraft = Minecraft.getMinecraft();
                Map map = minecraft.getSkinManager().loadSkinFromCache(npc.display.playerProfile);

                if (map.containsKey(Type.SKIN)) {
                    npc.textureLocation = minecraft.getSkinManager().loadSkin((MinecraftProfileTexture) map.get(Type.SKIN), Type.SKIN);
                }
            } else if (npc.display.skinType == 2) { // url skin
                try {
                    MessageDigest digest = MessageDigest.getInstance("MD5");
                    byte[] hash = digest.digest(npc.display.getSkinUrl().getBytes(StandardCharsets.UTF_8));
                    StringBuilder sb = new StringBuilder(2 * hash.length);
                    for (byte b : hash) {
                        sb.append(String.format("%02x", b & 0xff));
                    }
                    npc.textureLocation = new ResourceLocation("skins/" + sb.toString());
                    loadSkin(null, npc.textureLocation, npc.display.getSkinUrl());
                } catch (Exception ex) {

                }
            }
        }
        if (npc.textureLocation == null)
            return DefaultPlayerSkin.getDefaultSkinLegacy();
        return npc.textureLocation;
    }

    private void loadSkin(File file, ResourceLocation resource, String par1Str) {
        TextureManager texturemanager = Minecraft.getMinecraft().getTextureManager();
        if (texturemanager.getTexture(resource) != null)
            return;
        ITextureObject object = new ImageDownloadAlt(file, par1Str, DefaultPlayerSkin.getDefaultSkinLegacy(), new ImageBufferDownloadAlt());
        texturemanager.loadTexture(resource, object);
    }
}

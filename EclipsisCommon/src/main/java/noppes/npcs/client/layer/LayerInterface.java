package noppes.npcs.client.layer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;
import noppes.npcs.ModelData;
import noppes.npcs.ModelPartData;
import noppes.npcs.client.ClientProxy;
import noppes.npcs.entity.EntityCustomNpc;

public abstract class LayerInterface implements LayerRenderer {
    protected RenderLiving render;
    protected EntityCustomNpc npc;
    protected ModelData playerdata;
    public ModelBiped model;

    public LayerInterface(RenderLiving render) {
        this.render = render;
        model = (ModelBiped) render.getMainModel();
    }

    public void setColor(ModelPartData data, EntityLivingBase entity) {
    }

    public void preRender(ModelPartData data) {
        if (data.playerTexture)
            ClientProxy.Companion.bindTexture(npc.textureLocation);
        else
            ClientProxy.Companion.bindTexture(data.getResource());
        if (npc.hurtTime > 0 || npc.deathTime > 0) {
            return;
        }
        int color = data.color;
        if (npc.display.getTint() != 0xFFFFFF) {
            if (data.color != 0xFFFFFF)
                color = blend(data.color, npc.display.getTint(), 0.5f);
            else
                color = npc.display.getTint();
        }
        float red = (color >> 16 & 255) / 255f;
        float green = (color >> 8 & 255) / 255f;
        float blue = (color & 255) / 255f;
        GlStateManager.color(red, green, blue, npc.isInvisible() ? 0.15f : 0.99f);
    }

    private int blend(int color1, int color2, float ratio) {
        if (ratio >= 1f)
            return color2;

        if (ratio <= 0f)
            return color1;

        int aR = ((color1 & 0xff0000) >> 16);
        int aG = ((color1 & 0xff00) >> 8);
        int aB = (color1 & 0xff);

        int bR = ((color2 & 0xff0000) >> 16);
        int bG = ((color2 & 0xff00) >> 8);
        int bB = (color2 & 0xff);

        int R = (int) ((aR + (bR - aR) * ratio));
        int G = (int) ((aG + (bG - aG) * ratio));
        int B = (int) ((aB + (bB - aB) * ratio));

        return R << 16 | G << 8 | B;
    }

    @Override
    public void doRenderLayer(EntityLivingBase entity, float par2, float par3, float par8, float par4, float par5, float par6, float par7) {
        npc = (EntityCustomNpc) entity;
        if (npc.isInvisibleToPlayer(Minecraft.getMinecraft().player))
            return;
        playerdata = npc.modelData;

        model = (ModelBiped) render.getMainModel();
        rotate(par2, par3, par4, par5, par6, par7);

        GlStateManager.pushMatrix();

        if (entity.isInvisible()) {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 0.15F);
            GlStateManager.depthMask(false);
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(770, 771);
            GlStateManager.alphaFunc(516, 0.003921569F);
        }
        if (npc.hurtTime > 0 || npc.deathTime > 0) {
            GlStateManager.color(1, 0, 0, 0.3f);
        }
        if (npc.isSneaking()) {
            GlStateManager.translate(0.0F, 0.2F, 0.0F);
        }
        GlStateManager.enableRescaleNormal();
        render(par2, par3, par4, par5, par6, par7);
        GlStateManager.disableRescaleNormal();

        if (entity.isInvisible()) {
            GlStateManager.disableBlend();
            GlStateManager.alphaFunc(516, 0.1F);
            GlStateManager.depthMask(true);
        }
        GlStateManager.popMatrix();
    }

    public void setRotation(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }

    @Override
    public boolean shouldCombineTextures() {
        return false;
    }

    public abstract void render(float par2, float par3, float par4, float par5, float par6, float par7);

    public abstract void rotate(float par1, float par2, float par3, float par4, float par5, float par6);

}

package noppes.npcs.client.layer;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import noppes.npcs.ModelPartConfig;
import noppes.npcs.client.model.ModelPlayerAlt;
import noppes.npcs.constants.EnumParts;

public class LayerNpcCloak extends LayerInterface {


    public LayerNpcCloak(RenderLiving render) {
        super(render);
    }

    @Override
    public void render(float par2, float par3, float par4, float par5,
                       float par6, float par7) {
        if (npc.textureCloakLocation == null) {
            if (npc.display.getCapeTexture() == null || npc.display.getCapeTexture().isEmpty() || !(model instanceof ModelPlayerAlt))
                return;
            npc.textureCloakLocation = new ResourceLocation(npc.display.getCapeTexture());
        }
        GlStateManager.color(0, 0, 0);
        render.bindTexture(npc.textureCloakLocation);
        GlStateManager.pushMatrix();
        ModelPartConfig config = playerdata.getPartConfig(EnumParts.BODY);
        if (npc.isSneaking())
            GlStateManager.translate(0, 0.2f, 0);
        GlStateManager.translate(config.transX, config.transY, config.transZ);
        GlStateManager.translate(0.0F, 0.0F, 0.125F);
        double d = (npc.field_20066_r + (npc.field_20063_u - npc.field_20066_r) * (double) par7) - (npc.prevPosX + (npc.posX - npc.prevPosX) * (double) par7);
        double d1 = (npc.field_20065_s + (npc.field_20062_v - npc.field_20065_s) * (double) par7) - (npc.prevPosY + (npc.posY - npc.prevPosY) * (double) par7);
        double d2 = (npc.field_20064_t + (npc.field_20061_w - npc.field_20064_t) * (double) par7) - (npc.prevPosZ + (npc.posZ - npc.prevPosZ) * (double) par7);
        float f11 = npc.prevRenderYawOffset + (npc.renderYawOffset - npc.prevRenderYawOffset) * par7;
        double d3 = MathHelper.sin((f11 * 3.141593F) / 180F);
        double d4 = -MathHelper.cos((f11 * 3.141593F) / 180F);
        float f14 = (float) (d * d3 + d2 * d4) * 100F;
        float f15 = (float) (d * d4 - d2 * d3) * 100F;
        if (f14 < 0.0F) {
            f14 = 0.0F;
        }
        float f16 = npc.prevRotationYaw + (npc.rotationYaw - npc.prevRotationYaw) * par7;
        //f13 += MathHelper.sin((entityplayer.prevDistanceWalkedModified + (entityplayer.distanceWalkedModified - entityplayer.prevDistanceWalkedModified) * f) * 6F) * 32F * f16;
        float f13 = 5f;
        if (npc.isSneaking()) {
            f13 += 25F;
        }
        //System.out.println(entityplayer.prevDistanceWalkedModified);
        GlStateManager.rotate(6F + f14 / 2.0F + f13, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(f15 / 2.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(-f15 / 2.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(180F, 0.0F, 1.0F, 0.0F);

        ((ModelPlayerAlt) model).renderCape(0.0625F);

        GlStateManager.popMatrix();

    }

    @Override
    public void rotate(float par1, float par2, float par3, float par4,
                       float par5, float par6) {
        // TODO Auto-generated method stub

    }
}

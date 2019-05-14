package noppes.npcs.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.MathHelper;
import noppes.npcs.common.entity.EntityNpcDragon;

public class ModelNpcDragon extends ModelBase {

    private ModelRenderer head;
    private ModelRenderer neck;
    private ModelRenderer jaw;
    private ModelRenderer body;
    private ModelRenderer rearLeg;
    private ModelRenderer frontLeg;
    private ModelRenderer rearLegTip;
    private ModelRenderer frontLegTip;
    private ModelRenderer rearFoot;
    private ModelRenderer frontFoot;
    private ModelRenderer wing;
    private ModelRenderer wingTip;
    private float field_40317_s;


    public ModelNpcDragon(float scale) {
        textureWidth = 256;
        textureHeight = 256;
        setTextureOffset("body.body", 0, 0);
        setTextureOffset("wing.skin", -56, 88);
        setTextureOffset("wingtip.skin", -56, 144);
        setTextureOffset("rearleg.main", 0, 0);
        setTextureOffset("rearfoot.main", 112, 0);
        setTextureOffset("rearlegtip.main", 196, 0);
        setTextureOffset("head.upperhead", 112, 30);
        setTextureOffset("wing.bone", 112, 88);
        setTextureOffset("head.upperlip", 176, 44);
        setTextureOffset("jaw.jaw", 176, 65);
        setTextureOffset("frontleg.main", 112, 104);
        setTextureOffset("wingtip.bone", 112, 136);
        setTextureOffset("frontfoot.main", 144, 104);
        setTextureOffset("neck.box", 192, 104);
        setTextureOffset("frontlegtip.main", 226, 138);
        setTextureOffset("body.scale", 220, 53);
        setTextureOffset("head.scale", 0, 0);
        setTextureOffset("neck.scale", 48, 0);
        setTextureOffset("head.nostril", 112, 0);
        float f = -16.0F;
        this.head = new ModelRenderer(this, "head");
        this.head.addBox("upperlip", -6.0F, -1.0F, -24.0F, 12, 5, 16);
        this.head.addBox("upperhead", -8.0F, -8.0F, -10.0F, 16, 16, 16);
        this.head.mirror = true;
        this.head.addBox("scale", -5.0F, -12.0F, -4.0F, 2, 4, 6);
        this.head.addBox("nostril", -5.0F, -3.0F, -22.0F, 2, 2, 4);
        this.head.mirror = false;
        this.head.addBox("scale", 3.0F, -12.0F, -4.0F, 2, 4, 6);
        this.head.addBox("nostril", 3.0F, -3.0F, -22.0F, 2, 2, 4);
        this.jaw = new ModelRenderer(this, "jaw");
        this.jaw.setRotationPoint(0.0F, 4.0F, -8.0F);
        this.jaw.addBox("jaw", -6.0F, 0.0F, -16.0F, 12, 4, 16);
        this.head.addChild(this.jaw);
        this.neck = new ModelRenderer(this, "neck");
        this.neck.addBox("box", -5.0F, -5.0F, -5.0F, 10, 10, 10);
        this.neck.addBox("scale", -1.0F, -9.0F, -3.0F, 2, 4, 6);
        this.body = new ModelRenderer(this, "body");
        this.body.setRotationPoint(0.0F, 4.0F, 8.0F);
        this.body.addBox("body", -12.0F, 0.0F, -16.0F, 24, 24, 64);
        this.body.addBox("scale", -1.0F, -6.0F, -10.0F, 2, 6, 12);
        this.body.addBox("scale", -1.0F, -6.0F, 10.0F, 2, 6, 12);
        this.body.addBox("scale", -1.0F, -6.0F, 30.0F, 2, 6, 12);
        this.wing = new ModelRenderer(this, "wing");
        this.wing.setRotationPoint(-12.0F, 5.0F, 2.0F);
        this.wing.addBox("bone", -56.0F, -4.0F, -4.0F, 56, 8, 8);
        this.wing.addBox("skin", -56.0F, 0.0F, 2.0F, 56, 0, 56);
        this.wingTip = new ModelRenderer(this, "wingtip");
        this.wingTip.setRotationPoint(-56.0F, 0.0F, 0.0F);
        this.wingTip.addBox("bone", -56.0F, -2.0F, -2.0F, 56, 4, 4);
        this.wingTip.addBox("skin", -56.0F, 0.0F, 2.0F, 56, 0, 56);
        this.wing.addChild(this.wingTip);
        this.frontLeg = new ModelRenderer(this, "frontleg");
        this.frontLeg.setRotationPoint(-12.0F, 20.0F, 2.0F);
        this.frontLeg.addBox("main", -4.0F, -4.0F, -4.0F, 8, 24, 8);
        this.frontLegTip = new ModelRenderer(this, "frontlegtip");
        this.frontLegTip.setRotationPoint(0.0F, 20.0F, -1.0F);
        this.frontLegTip.addBox("main", -3.0F, -1.0F, -3.0F, 6, 24, 6);
        this.frontLeg.addChild(this.frontLegTip);
        this.frontFoot = new ModelRenderer(this, "frontfoot");
        this.frontFoot.setRotationPoint(0.0F, 23.0F, 0.0F);
        this.frontFoot.addBox("main", -4.0F, 0.0F, -12.0F, 8, 4, 16);
        this.frontLegTip.addChild(this.frontFoot);
        this.rearLeg = new ModelRenderer(this, "rearleg");
        this.rearLeg.setRotationPoint(-16.0F, 16.0F, 42.0F);
        this.rearLeg.addBox("main", -8.0F, -4.0F, -8.0F, 16, 32, 16);
        this.rearLegTip = new ModelRenderer(this, "rearlegtip");
        this.rearLegTip.setRotationPoint(0.0F, 32.0F, -4.0F);
        this.rearLegTip.addBox("main", -6.0F, -2.0F, 0.0F, 12, 32, 12);
        this.rearLeg.addChild(this.rearLegTip);
        this.rearFoot = new ModelRenderer(this, "rearfoot");
        this.rearFoot.setRotationPoint(0.0F, 31.0F, 4.0F);
        this.rearFoot.addBox("main", -9.0F, 0.0F, -20.0F, 18, 6, 24);
        this.rearLegTip.addChild(this.rearFoot);
    }

    @Override
    public void setLivingAnimations(EntityLivingBase entityliving, float f, float f1, float f2) {
        field_40317_s = f2;
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        EntityNpcDragon entitydragon = (EntityNpcDragon) entity;
        GlStateManager.pushMatrix();
        float f6 = entitydragon.field_40173_aw + (entitydragon.field_40172_ax - entitydragon.field_40173_aw) * field_40317_s;
        jaw.rotateAngleX = (float) (Math.sin(f6 * (float) Math.PI * 2.0F) + 1.0D) * 0.2F;
        float f7 = (float) (Math.sin(f6 * (float) Math.PI * 2.0F - 1.0F) + 1.0D);
        f7 = (f7 * f7 * 1.0F + f7 * 2.0F) * 0.05F;
        GlStateManager.translate(0.0F, f7 - 2.0F, -3F);
        GlStateManager.rotate(f7 * 2.0F, 1.0F, 0.0F, 0.0F);
        float f8 = -30F;
        float f9 = 22F;
        float f10 = 0.0F;
        float f11 = 1.5F;
        double[] ad = entitydragon.func_40160_a(6, field_40317_s);
        float f12 = func_40307_a(entitydragon.func_40160_a(5, field_40317_s)[0] - entitydragon.func_40160_a(10, field_40317_s)[0]);
        float f13 = func_40307_a(entitydragon.func_40160_a(5, field_40317_s)[0] + (double) (f12 / 2.0F));
        f8 += 2.0F;
        float f14 = 0.0F;
        float f15 = f6 * 3.141593F * 2.0F;
        f8 = 20F;
        f9 = -12F;
        for (int i = 0; i < 5; i++) {
            double[] ad3 = entitydragon.func_40160_a(5 - i, field_40317_s);
            f14 = (float) Math.cos((float) i * 0.45F + f15) * 0.15F;
            neck.rotateAngleY = ((func_40307_a(ad3[0] - ad[0]) * (float) Math.PI) / 180F) * f11;
            neck.rotateAngleX = f14 + (((float) (ad3[1] - ad[1]) * (float) Math.PI) / 180F) * f11 * 5F;
            neck.rotateAngleZ = ((-func_40307_a(ad3[0] - (double) f13) * (float) Math.PI) / 180F) * f11;
            neck.rotationPointY = f8;
            neck.rotationPointZ = f9;
            neck.rotationPointX = f10;
            f8 = (float) ((double) f8 + Math.sin(neck.rotateAngleX) * 10D);
            f9 = (float) ((double) f9 - Math.cos(neck.rotateAngleY) * Math.cos(neck.rotateAngleX) * 10D);
            f10 = (float) ((double) f10 - Math.sin(neck.rotateAngleY) * Math.cos(neck.rotateAngleX) * 10D);
            neck.render(f5);
        }

        head.rotationPointY = f8;
        head.rotationPointZ = f9;
        head.rotationPointX = f10;
        double[] ad1 = entitydragon.func_40160_a(0, field_40317_s);
        head.rotateAngleY = ((func_40307_a(ad1[0] - ad[0]) * (float) Math.PI) / 180F) * 1.0F;
        head.rotateAngleZ = ((-func_40307_a(ad1[0] - (double) f13) * (float) Math.PI) / 180F) * 1.0F;
        head.render(f5);
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0F, 1.0F, 0.0F);
        if (entitydragon.onGround)
            GlStateManager.rotate(-f12 * f11 * 0.3F, 0.0F, 0.0F, 1.0F);
        else
            GlStateManager.rotate(-f12 * f11 * 1.0F, 0.0F, 0.0F, 1.0F);

        GlStateManager.translate(0.0F, -1.18F, 0.0F);
        body.rotateAngleZ = 0.0F;
        body.render(f5);
        if (entitydragon.onGround) {
            for (int j = 0; j < 2; j++) {
                GlStateManager.enableCull();
                wing.rotateAngleX = 0.25f;
                wing.rotateAngleY = 0.95F;
                wing.rotateAngleZ = -0.5f;
                wingTip.rotateAngleZ = -0.4f;
                frontLeg.rotateAngleX = MathHelper.cos((float) (f * 0.6662F + (j == 0 ? 0 : Math.PI))) * 0.6F * f1 + 0.45f + f7 * 0.5f;
                frontLegTip.rotateAngleX = -1.3f - f7 * 1.2f;
                frontFoot.rotateAngleX = 0.85f + f7 * 0.5f;
                frontLeg.render(f5);

                rearLeg.rotateAngleX = MathHelper.cos((float) (f * 0.6662F + (j == 0 ? Math.PI : 0))) * 0.6F * f1 + 0.75f + f7 * 0.5f;
                rearLegTip.rotateAngleX = -1.6f - f7 * 0.8f;
                rearLegTip.rotationPointY = 20;
                rearLegTip.rotationPointZ = 2;
                rearFoot.rotateAngleX = 0.85f + f7 * 0.2f;

                rearLeg.render(f5);
                wing.render(f5);
                GlStateManager.scale(-1F, 1.0F, 1.0F);
                if (j == 0) {
                    GlStateManager.cullFace(GlStateManager.CullFace.FRONT);
                }
            }
        } else {
            for (int j = 0; j < 2; j++) {
                GlStateManager.enableCull();
                float f16 = f6 * (float) Math.PI * 2.0F;
                wing.rotateAngleX = 0.125F - (float) Math.cos(f16) * 0.2F;
                wing.rotateAngleY = 0.25F;
                wing.rotateAngleZ = (float) (Math.sin(f16) + 0.125D) * 0.8F;
                wingTip.rotateAngleZ = -(float) (Math.sin(f16 + 2.0F) + 0.5D) * 0.75F;
                rearLegTip.rotationPointY = 32;
                rearLegTip.rotationPointZ = -2;
                rearLeg.rotateAngleX = 1.0F + f7 * 0.1F;
                rearLegTip.rotateAngleX = 0.5F + f7 * 0.1F;
                rearFoot.rotateAngleX = 0.75F + f7 * 0.1F;
                frontLeg.rotateAngleX = 1.3F + f7 * 0.1F;
                frontLegTip.rotateAngleX = -0.5F - f7 * 0.1F;
                frontFoot.rotateAngleX = 0.75F + f7 * 0.1F;
                wing.render(f5);
                frontLeg.render(f5);
                rearLeg.render(f5);
                GlStateManager.scale(-1F, 1.0F, 1.0F);
                if (j == 0) {
                    GlStateManager.cullFace(GlStateManager.CullFace.FRONT);
                }
            }
        }
        GlStateManager.popMatrix();
        GlStateManager.cullFace(GlStateManager.CullFace.BACK);
        GlStateManager.disableCull();
        f14 = -(float) Math.sin(f6 * 3.141593F * 2.0F) * 0.0F;
        f15 = f6 * (float) Math.PI * 2.0F;
        f8 = 10F;
        f9 = 60F;
        f10 = 0.0F;
        ad = entitydragon.func_40160_a(11, field_40317_s);
        for (int k = 0; k < 12; k++) {
            double[] ad2 = entitydragon.func_40160_a(12 + k, field_40317_s);
            f14 = (float) ((double) f14 + Math.sin((float) k * 0.45F + f15) * 0.05000000074505806D);
            neck.rotateAngleY = ((func_40307_a(ad2[0] - ad[0]) * f11 + 180F) * (float) Math.PI) / 180F;
            neck.rotateAngleX = f14 + (((float) (ad2[1] - ad[1]) * (float) Math.PI) / 180F) * f11 * 5F;
            neck.rotateAngleZ = ((func_40307_a(ad2[0] - (double) f13) * (float) Math.PI) / 180F) * f11;
            neck.rotationPointY = f8;
            neck.rotationPointZ = f9;
            neck.rotationPointX = f10;
            f8 = (float) ((double) f8 + Math.sin(neck.rotateAngleX) * 10D);
            f9 = (float) ((double) f9 - Math.cos(neck.rotateAngleY) * Math.cos(neck.rotateAngleX) * 10D);
            f10 = (float) ((double) f10 - Math.sin(neck.rotateAngleY) * Math.cos(neck.rotateAngleX) * 10D);
            neck.render(f5);
        }

        GlStateManager.popMatrix();
        //}
    }

    private float func_40307_a(double d) {
        for (; d >= 180D; d -= 360D) {
        }
        for (; d < -180D; d += 360D) {
        }
        return (float) d;
    }
}

package noppes.npcs.client.layer;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.util.math.MathHelper;
import noppes.npcs.ModelPartData;
import noppes.npcs.client.model.Model2DRenderer;
import noppes.npcs.client.model.ModelPlaneRenderer;
import noppes.npcs.constants.EnumParts;

public class LayerBody extends LayerInterface {
    private Model2DRenderer lWing;
    private Model2DRenderer rWing;

    private Model2DRenderer breasts;
    private ModelRenderer breasts2;
    private ModelRenderer breasts3;

    private ModelPlaneRenderer skirt;

    private Model2DRenderer fin;

    public LayerBody(RenderLiving render) {
        super(render);
        createParts();
    }

    private void createParts() {
        lWing = new Model2DRenderer(model, 56, 16, 8, 16);
        lWing.mirror = true;
        lWing.setRotationPoint(2F, 2.5f, 1F);
        lWing.setRotationOffset(8, 14, 0);
        setRotation(lWing, 0.7141593F, -0.5235988F, -0.5090659F);


        rWing = new Model2DRenderer(model, 56, 16, 8, 16);
        rWing.setRotationPoint(-2F, 2.5f, 1F);
        rWing.setRotationOffset(-8, 14, 0);
        setRotation(rWing, 0.7141593F, 0.5235988F, 0.5090659F);


        breasts = new Model2DRenderer(model, 20f, 22, 8, 3);
        breasts.setRotationPoint(-3.6F, 5.2f, -3f);
        breasts.setScale(0.17f, 0.19f);
        breasts.setThickness(1);

        breasts2 = new ModelRenderer(model);
        Model2DRenderer bottom = new Model2DRenderer(model, 20f, 22, 8, 4);
        bottom.setRotationPoint(-3.6F, 5f, -3.1f);
        bottom.setScale(0.225f, 0.20f);
        bottom.setThickness(2f);
        bottom.rotateAngleX = -(float) (Math.PI / 10);
        breasts2.addChild(bottom);

        breasts3 = new ModelRenderer(model);

        Model2DRenderer right = new Model2DRenderer(model, 20f, 23, 3, 2);
        right.setRotationPoint(-3.8F, 5.3f, -3.6f);
        right.setScale(0.12f, 0.14f);
        right.setThickness(1.75f);
        breasts3.addChild(right);

        Model2DRenderer right2 = new Model2DRenderer(model, 20f, 22, 3, 1);
        right2.setRotationPoint(-3.79F, 4.1f, -3.14f);
        right2.setScale(0.06f, 0.07f);
        right2.setThickness(1.75f);
        right2.rotateAngleX = (float) (Math.PI / 9);
        breasts3.addChild(right2);

        Model2DRenderer right3 = new Model2DRenderer(model, 20f, 24, 3, 1);
        right3.setRotationPoint(-3.79F, 5.3f, -3.6f);
        right3.setScale(0.06f, 0.07f);
        right3.setThickness(1.75f);
        right3.rotateAngleX = (float) (-Math.PI / 9);
        breasts3.addChild(right3);

        Model2DRenderer right4 = new Model2DRenderer(model, 21f, 23, 1, 2);
        right4.setRotationPoint(-1.8f, 5.3f, -3.14f);
        right4.setScale(0.12f, 0.14f);
        right4.setThickness(1.75f);
        right4.rotateAngleY = (float) (Math.PI / 9);
        breasts3.addChild(right4);

        Model2DRenderer left = new Model2DRenderer(model, 25f, 23, 3, 2);
        left.setRotationPoint(0.8F, 5.3f, -3.6f);
        left.setScale(0.12f, 0.14f);
        left.setThickness(1.75f);
        breasts3.addChild(left);

        Model2DRenderer left2 = new Model2DRenderer(model, 25f, 22, 3, 1);
        left2.setRotationPoint(0.81F, 4.1f, -3.18f);
        left2.setScale(0.06f, 0.07f);
        left2.setThickness(1.75f);
        left2.rotateAngleX = (float) (Math.PI / 9);
        breasts3.addChild(left2);

        Model2DRenderer left3 = new Model2DRenderer(model, 25f, 24, 3, 1);
        left3.setRotationPoint(0.81F, 5.3f, -3.6f);
        left3.setScale(0.06f, 0.07f);
        left3.setThickness(1.75f);
        left3.rotateAngleX = (float) (-Math.PI / 9);
        breasts3.addChild(left3);

        Model2DRenderer left4 = new Model2DRenderer(model, 24f, 23, 1, 2);
        left4.setRotationPoint(0.8f, 5.3f, -3.6f);
        left4.setScale(0.12f, 0.14f);
        left4.setThickness(1.75f);
        left4.rotateAngleY = (float) (-Math.PI / 9);
        breasts3.addChild(left4);

        skirt = new ModelPlaneRenderer(model, 58, 18);
        skirt.addSidePlane(0, 0, 0, 9, 2);

        ModelPlaneRenderer part1 = new ModelPlaneRenderer(model, 58, 18);
        part1.addSidePlane(2, 0, 0, 9, 2);
        part1.rotateAngleY = -(float) (Math.PI / 2);
        skirt.addChild(part1);

        skirt.setRotationPoint(2.4F, 8.8F, 0F);
        setRotation(skirt, 0.3F, -0.2f, -0.2F);

        fin = new Model2DRenderer(model, 56, 20, 8, 12);
        fin.setRotationPoint(-0.5F, 12, 10);
        fin.setScale(0.74f);
        fin.rotateAngleY = (float) Math.PI / 2;
    }

    @Override
    public void render(float par2, float par3, float par4, float par5, float par6, float par7) {
        model.bipedBody.postRender(0.0625F);
        renderSkirt(par7);
        renderWings(par7);
        renderFin(par7);
        renderBreasts(par7);
    }

    private void renderWings(float par7) {
        ModelPartData data = playerdata.getPartData(EnumParts.WINGS);
        if (data == null)
            return;
        preRender(data);
        rWing.render(par7);
        lWing.render(par7);
    }

    private void renderSkirt(float par7) {
        ModelPartData data = playerdata.getPartData(EnumParts.SKIRT);
        if (data == null)
            return;
        preRender(data);
        GlStateManager.pushMatrix();
        GlStateManager.scale(1.7f, 1.04f, 1.6f);
        for (int i = 0; i < 10; i++) {
            GlStateManager.rotate(36, 0, 1, 0);
            skirt.render(par7);
        }
        GlStateManager.popMatrix();
    }

    private void renderFin(float par7) {
        ModelPartData data = playerdata.getPartData(EnumParts.FIN);
        if (data == null)
            return;
        preRender(data);
        fin.render(par7);
    }

    private void renderBreasts(float par7) {
        ModelPartData data = playerdata.getPartData(EnumParts.BREASTS);
        if (data == null)
            return;
        data.playerTexture = true;
        preRender(data);
        if (data.type == 0)
            breasts.render(par7);
        if (data.type == 1)
            breasts2.render(par7);
        if (data.type == 2)
            breasts3.render(par7);
    }

    @Override
    public void rotate(float par1, float par2, float par3, float par4, float par5, float par6) {
        rWing.rotateAngleX = 0.7141593F;
        rWing.rotateAngleZ = 0.5090659F;

        lWing.rotateAngleX = 0.7141593F;
        lWing.rotateAngleZ = -0.5090659F;

        float motion = Math.abs(MathHelper.sin(par1 * 0.033F + (float) Math.PI) * 0.4F) * par2;
        if (!npc.onGround || motion > 0.01) {
            float speed = (0.55f + 0.5f * motion);
            float y = MathHelper.sin(par3 * 0.55F);

            rWing.rotateAngleZ += y * 0.5f * speed;
            rWing.rotateAngleX += y * 0.5f * speed;

            lWing.rotateAngleZ -= y * 0.5f * speed;
            lWing.rotateAngleX += y * 0.5f * speed;
        } else {
            lWing.rotateAngleZ += MathHelper.cos(par3 * 0.09F) * 0.05F + 0.05F;
            rWing.rotateAngleZ -= MathHelper.cos(par3 * 0.09F) * 0.05F + 0.05F;
            lWing.rotateAngleX += MathHelper.sin(par3 * 0.067F) * 0.05F;
            rWing.rotateAngleX += MathHelper.sin(par3 * 0.067F) * 0.05F;
        }

        setRotation(skirt, 0.3F, -0.2f, -0.2F);
        skirt.rotateAngleX += model.bipedLeftArm.rotateAngleX * 0.04f;
        skirt.rotateAngleZ += model.bipedLeftArm.rotateAngleX * 0.06f;
        skirt.rotateAngleZ -= MathHelper.cos(par3 * 0.09F) * 0.04F - 0.05F;
    }

}

package noppes.npcs.client.layer;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.util.math.MathHelper;
import noppes.npcs.ModelPartConfig;
import noppes.npcs.ModelPartData;
import noppes.npcs.api.constants.AnimationType;
import noppes.npcs.client.model.part.legs.*;
import noppes.npcs.client.model.part.tails.*;
import noppes.npcs.constants.EnumParts;
import noppes.npcs.entity.EntityCustomNpc;

public class LayerLegs extends LayerInterface implements LayerPreRender {
    private ModelSpiderLegs spiderLegs;
    private ModelHorseLegs horseLegs;
    private ModelNagaLegs naga;
    private ModelDigitigradeLegs digitigrade;
    private ModelMermaidLegs mermaid;

    private ModelRenderer tail;
    private ModelCanineTail fox;
    private ModelRenderer dragon;
    private ModelRenderer squirrel;
    private ModelRenderer horse;
    private ModelRenderer fin;
    private ModelRenderer rodent;
    private ModelRenderer feathers;

    public LayerLegs(RenderLiving render) {
        super(render);
        createParts();
    }

    private void createParts() {
        spiderLegs = new ModelSpiderLegs(model);
        horseLegs = new ModelHorseLegs(model);
        naga = new ModelNagaLegs(model);
        mermaid = new ModelMermaidLegs(model);
        digitigrade = new ModelDigitigradeLegs(model);
        fox = new ModelCanineTail(model);

        tail = new ModelRenderer(model, 56, 21);
        tail.addBox(-1F, 0F, 0F, 2, 9, 2);
        tail.setRotationPoint(0F, 0, 1F);
        setRotation(tail, 0.8714253F, 0F, 0F);

        horse = new ModelRenderer(model);
        horse.setTextureSize(32, 32);
        horse.setRotationPoint(0, -1, 1);

        ModelRenderer tailBase = new ModelRenderer(model, 0, 26);
        tailBase.setTextureSize(32, 32);
        tailBase.addBox(-1.0F, -1.0F, 0.0F, 2, 2, 3);
        setRotation(tailBase, -1.134464F, 0.0F, 0.0F);
        horse.addChild(tailBase);
        ModelRenderer tailMiddle = new ModelRenderer(model, 0, 13);
        tailMiddle.setTextureSize(32, 32);
        tailMiddle.addBox(-1.5F, -2.0F, 3.0F, 3, 4, 7);
        setRotation(tailMiddle, -1.134464F, 0.0F, 0.0F);
        horse.addChild(tailMiddle);
        ModelRenderer tailTip = new ModelRenderer(model, 0, 0);
        tailTip.setTextureSize(32, 32);
        tailTip.addBox(-1.5F, -4.5F, 9.0F, 3, 4, 7);
        setRotation(tailTip, -1.40215F, 0.0F, 0.0F);
        horse.addChild(tailTip);
        horse.rotateAngleX = 0.5f;

        dragon = new ModelDragonTail(model);
        squirrel = new ModelSquirrelTail(model);
        fin = new ModelTailFin(model);
        rodent = new ModelRodentTail(model);
        feathers = new ModelFeatherTail(model);
    }

    @Override
    public void render(float par2, float par3, float par4, float par5, float par6, float par7) {
        renderLegs(par7);
        renderTails(par7);
    }

    private void renderTails(float par7) {
        ModelPartData data = playerdata.getPartData(EnumParts.TAIL);
        if (data == null)
            return;
        GlStateManager.pushMatrix();
        ModelPartConfig config = playerdata.getPartConfig(EnumParts.LEG_LEFT);
        GlStateManager.translate(config.transX * par7, config.transY + rotationPointY * par7, config.transZ * par7 + rotationPointZ * par7);
        GlStateManager.translate(0, 0, (config.scaleZ - 1) * 5 * par7);
        GlStateManager.scale(config.scaleX, config.scaleY, config.scaleZ);
        preRender(data);

        if (data.type == 0) {
            if (data.pattern == 1) {
                tail.rotationPointX = -0.5f;
                tail.rotateAngleY -= 0.2;
                tail.render(par7);
                tail.rotationPointX += 1;
                tail.rotateAngleY += 0.4;
                tail.render(par7);
                tail.rotationPointX = 0;
            } else
                tail.render(par7);
        } else if (data.type == 1) {
            dragon.render(par7);
        } else if (data.type == 2) {
            horse.render(par7);
        } else if (data.type == 3) {
            squirrel.render(par7);
        } else if (data.type == 4) {
            fin.render(par7);
        } else if (data.type == 5) {
            rodent.render(par7);
        } else if (data.type == 6) {
            feathers.render(par7);
        } else if (data.type == 7) {
            fox.render(par7);
        }
        GlStateManager.popMatrix();
    }

    private void renderLegs(float par7) {
        ModelPartData data = playerdata.getPartData(EnumParts.LEGS);
        if (data.type <= 0)
            return;
        GlStateManager.pushMatrix();
        ModelPartConfig config = playerdata.getPartConfig(EnumParts.LEG_LEFT);
        preRender(data);
        if (data.type == 1) {
            GlStateManager.translate(0, config.transY * 2, config.transZ * par7 + 0.04f);
            GlStateManager.scale(config.scaleX, config.scaleY, config.scaleZ);
            naga.render(par7);
        } else if (data.type == 2) {
            GlStateManager.translate(0, config.transY * 1.76f - 0.1 * config.scaleY, config.transZ * par7);
            GlStateManager.scale(1.06f, 1.06f, 1.06f);
            GlStateManager.scale(config.scaleX, config.scaleY, config.scaleZ);
            spiderLegs.render(par7);
        } else if (data.type == 3) {
            if (config.scaleY >= 1)
                GlStateManager.translate(0, config.transY * 1.76f, config.transZ * par7);
            else
                GlStateManager.translate(0, config.transY * 1.86f, config.transZ * par7);
            GlStateManager.scale(0.79f, 0.9f - config.scaleY / 10, 0.79f);
            GlStateManager.scale(config.scaleX, config.scaleY, config.scaleZ);
            horseLegs.render(par7);
        } else if (data.type == 4) {
            GlStateManager.translate(0, config.transY * 1.86f, config.transZ * par7);
            GlStateManager.scale(config.scaleX, config.scaleY, config.scaleZ);
            mermaid.render(par7);
        } else if (data.type == 5) {
            GlStateManager.translate(0, config.transY * 1.86f, config.transZ * par7);
            GlStateManager.scale(config.scaleX, config.scaleY, config.scaleZ);
            digitigrade.render(par7);
        }
        GlStateManager.popMatrix();
    }

    @Override
    public void rotate(float par1, float par2, float par3, float par4, float par5, float par6) {
        rotateLegs(par1, par2, par3, par4, par5, par6);
        rotateTail(par1, par2, par3, par4, par5, par6);
    }

    public void rotateLegs(float par1, float par2, float par3, float par4, float par5, float par6) {
        ModelPartData part = playerdata.getPartData(EnumParts.LEGS);
        if (part.type == 2) {
            spiderLegs.setRotationAngles(playerdata, par1, par2, par3, par4, par5, par6, npc);
        } else if (part.type == 3) {
            horseLegs.setRotationAngles(playerdata, par1, par2, par3, par4, par5, par6, npc);
        } else if (part.type == 1) {
            naga.isRiding = model.isRiding;
            naga.isSleeping = npc.isPlayerSleeping();
            naga.isCrawling = npc.currentAnimation == AnimationType.CRAWL;
            naga.isSneaking = model.isSneak;
            naga.setRotationAngles(par1, par2, par3, par4, par5, par6, npc);
        } else if (part.type == 4) {
            mermaid.setRotationAngles(par1, par2, par3, par4, par5, par6, npc);
        } else if (part.type == 5) {
            digitigrade.setRotationAngles(par1, par2, par3, par4, par5, par6, npc);
        }

    }

    float rotationPointZ;
    float rotationPointY;

    public void rotateTail(float par1, float par2, float par3, float par4, float par5, float par6) {
        ModelPartData part = playerdata.getPartData(EnumParts.LEGS);
        ModelPartData partTail = playerdata.getPartData(EnumParts.TAIL);
        ModelPartConfig config = playerdata.getPartConfig(EnumParts.LEG_LEFT);
        float rotateAngleY = MathHelper.cos(par1 * 0.6662F) * 0.2f * par2;
        float rotateAngleX = MathHelper.sin(par3 * 0.067F) * 0.05F;
        rotationPointZ = 0;
        rotationPointY = 11;

        if (part.type == 2) {
            rotationPointY = 12 + (config.scaleY - 1) * 3;
            rotationPointZ = 15 + (config.scaleZ - 1) * 10;
            if (npc.isPlayerSleeping() || npc.currentAnimation == AnimationType.CRAWL) {
                rotationPointY = 12 + 16 * config.scaleZ;
                rotationPointZ = 1f * config.scaleY;

                rotateAngleX = (float) (Math.PI / -4);
            }
        } else if (part.type == 3) {
            rotationPointY = 10;
            rotationPointZ = 16 + (config.scaleZ - 1) * 12;
        } else {
            rotationPointZ = (1 - config.scaleZ) * 1;
        }
        if (partTail != null) {
            if (partTail.type == 2)
                rotateAngleX += 0.5;
            if (partTail.type == 0)
                rotateAngleX += 0.87F;
            if (partTail.type == 7) {
                fox.setRotationAngles(par1, par2, par3, par4, par5, par6, npc);
            }
        }

        rotationPointZ += model.bipedRightLeg.rotationPointZ + 0.5f;
        fox.rotateAngleX = tail.rotateAngleX = feathers.rotateAngleX = dragon.rotateAngleX = squirrel.rotateAngleX = horse.rotateAngleX = fin.rotateAngleX = rodent.rotateAngleX = rotateAngleX;
        fox.rotateAngleY = tail.rotateAngleY = feathers.rotateAngleY = dragon.rotateAngleY = squirrel.rotateAngleY = horse.rotateAngleY = fin.rotateAngleY = rodent.rotateAngleY = rotateAngleY;
    }


    @Override
    public void preRender(EntityCustomNpc player) {
        this.npc = player;
        playerdata = player.modelData;
        ModelPartData data = playerdata.getPartData(EnumParts.LEGS);
        model.bipedLeftLeg.isHidden = model.bipedRightLeg.isHidden = data == null || data.type != 0;
    }

}

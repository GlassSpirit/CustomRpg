package noppes.npcs.client.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import noppes.npcs.ModelData;
import noppes.npcs.ModelPartConfig;
import noppes.npcs.api.constants.AnimationType;
import noppes.npcs.api.constants.JobType;
import noppes.npcs.client.model.animation.*;
import noppes.npcs.constants.EnumParts;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.roles.JobPuppet;

import java.util.*;

public class ModelPlayerAlt extends ModelPlayer {
    private ModelRenderer body, head;

    private Map<EnumParts, List<ModelScaleRenderer>> map = new HashMap<EnumParts, List<ModelScaleRenderer>>();


    public ModelPlayerAlt(float scale, boolean arms) {
        super(scale, arms);
        this.head = new ModelScaleRenderer(this, 24, 0, EnumParts.HEAD);
        this.head.addBox(-3.0F, -6.0F, -1.0F, 6, 6, 1, scale);
        this.body = new ModelScaleRenderer(this, 0, 0, EnumParts.BODY);
        this.body.setTextureSize(64, 32);
        this.body.addBox(-5.0F, 0.0F, -1.0F, 10, 16, 1, scale);

        ObfuscationReflectionHelper.setPrivateValue(ModelPlayer.class, this, head, 6);
        ObfuscationReflectionHelper.setPrivateValue(ModelPlayer.class, this, body, 5);

        this.bipedLeftArm = createScale(bipedLeftArm, EnumParts.ARM_LEFT);
        this.bipedRightArm = createScale(bipedRightArm, EnumParts.ARM_RIGHT);
        this.bipedLeftArmwear = createScale(bipedLeftArmwear, EnumParts.ARM_LEFT);
        this.bipedRightArmwear = createScale(bipedRightArmwear, EnumParts.ARM_RIGHT);

        this.bipedLeftLeg = createScale(bipedLeftLeg, EnumParts.LEG_LEFT);
        this.bipedRightLeg = createScale(bipedRightLeg, EnumParts.LEG_RIGHT);
        this.bipedLeftLegwear = createScale(bipedLeftLegwear, EnumParts.LEG_LEFT);
        this.bipedRightLegwear = createScale(bipedRightLegwear, EnumParts.LEG_RIGHT);


        this.bipedHead = createScale(bipedHead, EnumParts.HEAD);
        this.bipedHeadwear = createScale(bipedHeadwear, EnumParts.HEAD);
        this.bipedBody = createScale(bipedBody, EnumParts.BODY);
        this.bipedBodyWear = createScale(bipedBodyWear, EnumParts.BODY);
    }

    private ModelScaleRenderer createScale(ModelRenderer renderer, EnumParts part) {
        int textureX = ObfuscationReflectionHelper.getPrivateValue(ModelRenderer.class, renderer, 2);
        int textureY = ObfuscationReflectionHelper.getPrivateValue(ModelRenderer.class, renderer, 3);
        ModelScaleRenderer model = new ModelScaleRenderer(this, textureX, textureY, part);
        model.textureHeight = renderer.textureHeight;
        model.textureWidth = renderer.textureWidth;
        if (renderer.childModels != null)
            model.childModels = new ArrayList<ModelRenderer>(renderer.childModels);
        model.cubeList = new ArrayList<ModelBox>(renderer.cubeList);
        copyModelAngles(renderer, model);

        List<ModelScaleRenderer> list = map.get(part);
        if (list == null)
            map.put(part, list = new ArrayList<ModelScaleRenderer>());
        list.add(model);
        return model;
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entity) {
        //super.setRotationAngles(p_78087_1_, p_78087_2_, p_78087_3_, p_78087_4_, p_78087_5_, p_78087_6_, entity);
        EntityCustomNpc player = (EntityCustomNpc) entity;
        ModelData playerdata = player.modelData;

        for (EnumParts part : map.keySet()) {
            ModelPartConfig config = playerdata.getPartConfig(part);
            for (ModelScaleRenderer model : map.get(part)) {
                model.config = config;
            }
        }

        if (!isRiding)
            isRiding = player.currentAnimation == AnimationType.SIT;

        if (isSneak && (player.currentAnimation == AnimationType.CRAWL || player.isPlayerSleeping()))
            isSneak = false;
        if (player.currentAnimation == AnimationType.AIM)
            this.rightArmPose = ArmPose.BOW_AND_ARROW;
        isSneak = player.isSneaking();

        this.bipedBody.rotationPointX = this.bipedBody.rotationPointY = this.bipedBody.rotationPointZ = 0;
        this.bipedBody.rotateAngleX = this.bipedBody.rotateAngleY = this.bipedBody.rotateAngleZ = 0;

        this.bipedHeadwear.rotateAngleX = this.bipedHead.rotateAngleX = 0;
        this.bipedHeadwear.rotateAngleZ = this.bipedHead.rotateAngleZ = 0;

        this.bipedHeadwear.rotationPointX = this.bipedHead.rotationPointX = 0;
        this.bipedHeadwear.rotationPointY = this.bipedHead.rotationPointY = 0;
        this.bipedHeadwear.rotationPointZ = this.bipedHead.rotationPointZ = 0;

        this.bipedLeftLeg.rotateAngleX = 0;
        this.bipedLeftLeg.rotateAngleY = 0;
        this.bipedLeftLeg.rotateAngleZ = 0;
        this.bipedRightLeg.rotateAngleX = 0;
        this.bipedRightLeg.rotateAngleY = 0;
        this.bipedRightLeg.rotateAngleZ = 0;
        this.bipedLeftArm.rotationPointX = 0;
        this.bipedLeftArm.rotationPointY = 2;
        this.bipedLeftArm.rotationPointZ = 0;
        this.bipedRightArm.rotationPointX = 0;
        this.bipedRightArm.rotationPointY = 2;
        this.bipedRightArm.rotationPointZ = 0;

        try {
            super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entity);
        } catch (Exception e) {
        }

        if (player.isPlayerSleeping()) {
            if (bipedHead.rotateAngleX < 0) {
                bipedHead.rotateAngleX = 0;
                bipedHeadwear.rotateAngleX = 0;
            }
        } else if (player.currentAnimation == AnimationType.CRY)
            bipedHeadwear.rotateAngleX = bipedHead.rotateAngleX = 0.7f;
        else if (player.currentAnimation == AnimationType.HUG)
            AniHug.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entity, this);
        else if (player.currentAnimation == AnimationType.CRAWL)
            AniCrawling.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entity, this);
        else if (player.currentAnimation == AnimationType.WAVE) {
            AniWaving.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entity, this);
        } else if (player.currentAnimation == AnimationType.DANCE) {
            AniDancing.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entity, this);
        } else if (player.currentAnimation == AnimationType.BOW) {
            AniBow.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entity, this);
        } else if (player.currentAnimation == AnimationType.YES) {
            AniYes.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entity, this);
        } else if (player.currentAnimation == AnimationType.NO) {
            AniNo.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entity, this);
        } else if (player.currentAnimation == AnimationType.POINT) {
            AniPoint.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entity, this);
        } else if (isSneak)
            this.bipedBody.rotateAngleX = 0.5F / playerdata.getPartConfig(EnumParts.BODY).scaleY;


        if (player.advanced.job == JobType.PUPPET) {
            JobPuppet job = (JobPuppet) player.jobInterface;
            if (job.isActive()) {
                float pi = (float) Math.PI;
                float partialTicks = Minecraft.getMinecraft().getRenderPartialTicks();
                if (!job.head.disabled) {
                    bipedHeadwear.rotateAngleX = bipedHead.rotateAngleX = job.getRotationX(job.head, job.head2, partialTicks) * pi;
                    bipedHeadwear.rotateAngleY = bipedHead.rotateAngleY = job.getRotationY(job.head, job.head2, partialTicks) * pi;
                    bipedHeadwear.rotateAngleZ = bipedHead.rotateAngleZ = job.getRotationZ(job.head, job.head2, partialTicks) * pi;
                }

                if (!job.body.disabled) {
                    bipedBody.rotateAngleX = job.getRotationX(job.body, job.body2, partialTicks) * pi;
                    bipedBody.rotateAngleY = job.getRotationY(job.body, job.body2, partialTicks) * pi;
                    bipedBody.rotateAngleZ = job.getRotationZ(job.body, job.body2, partialTicks) * pi;
                }

                if (!job.larm.disabled) {
                    bipedLeftArm.rotateAngleX = job.getRotationX(job.larm, job.larm2, partialTicks) * pi;
                    bipedLeftArm.rotateAngleY = job.getRotationY(job.larm, job.larm2, partialTicks) * pi;
                    bipedLeftArm.rotateAngleZ = job.getRotationZ(job.larm, job.larm2, partialTicks) * pi;

                    if (player.display.getHasLivingAnimation()) {
                        this.bipedLeftArm.rotateAngleZ -= MathHelper.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
                        this.bipedLeftArm.rotateAngleX -= MathHelper.sin(ageInTicks * 0.067F) * 0.05F;
                    }
                }

                if (!job.rarm.disabled) {
                    bipedRightArm.rotateAngleX = job.getRotationX(job.rarm, job.rarm2, partialTicks) * pi;
                    bipedRightArm.rotateAngleY = job.getRotationY(job.rarm, job.rarm2, partialTicks) * pi;
                    bipedRightArm.rotateAngleZ = job.getRotationZ(job.rarm, job.rarm2, partialTicks) * pi;

                    if (player.display.getHasLivingAnimation()) {
                        this.bipedRightArm.rotateAngleZ += MathHelper.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
                        this.bipedRightArm.rotateAngleX += MathHelper.sin(ageInTicks * 0.067F) * 0.05F;
                    }
                }

                if (!job.rleg.disabled) {
                    bipedRightLeg.rotateAngleX = job.getRotationX(job.rleg, job.rleg2, partialTicks) * pi;
                    bipedRightLeg.rotateAngleY = job.getRotationY(job.rleg, job.rleg2, partialTicks) * pi;
                    bipedRightLeg.rotateAngleZ = job.getRotationZ(job.rleg, job.rleg2, partialTicks) * pi;
                }

                if (!job.lleg.disabled) {
                    bipedLeftLeg.rotateAngleX = job.getRotationX(job.lleg, job.lleg2, partialTicks) * pi;
                    bipedLeftLeg.rotateAngleY = job.getRotationY(job.lleg, job.lleg2, partialTicks) * pi;
                    bipedLeftLeg.rotateAngleZ = job.getRotationZ(job.lleg, job.lleg2, partialTicks) * pi;
                }
            }
        }

        copyModelAngles(this.bipedLeftLeg, this.bipedLeftLegwear);
        copyModelAngles(this.bipedRightLeg, this.bipedRightLegwear);
        copyModelAngles(this.bipedLeftArm, this.bipedLeftArmwear);
        copyModelAngles(this.bipedRightArm, this.bipedRightArmwear);
        copyModelAngles(this.bipedBody, this.bipedBodyWear);
        copyModelAngles(this.bipedHead, this.bipedHeadwear);
    }

    @Override
    public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        try {
            GlStateManager.pushMatrix();
            if (entityIn.isSneaking()) {
                GlStateManager.translate(0.0F, 0.2F, 0.0F);
            }
            this.bipedHead.render(scale);
            this.bipedBody.render(scale);
            this.bipedRightArm.render(scale);
            this.bipedLeftArm.render(scale);
            this.bipedRightLeg.render(scale);
            this.bipedLeftLeg.render(scale);
            this.bipedHeadwear.render(scale);

            this.bipedLeftLegwear.render(scale);
            this.bipedRightLegwear.render(scale);
            this.bipedLeftArmwear.render(scale);
            this.bipedRightArmwear.render(scale);
            this.bipedBodyWear.render(scale);

            GlStateManager.popMatrix();
        } catch (Exception e) {
        }
    }

    @Override
    protected EnumHandSide getMainHand(Entity entityIn) {
        if (entityIn instanceof EntityLivingBase && ((EntityLivingBase) entityIn).isSwingInProgress) {
            EntityLivingBase living = (EntityLivingBase) entityIn;
            if (living.swingingHand == EnumHand.MAIN_HAND)
                return EnumHandSide.RIGHT;
            return EnumHandSide.LEFT;
        }
        return super.getMainHand(entityIn);
    }

    @Override
    public ModelRenderer getRandomModelBox(Random random) {
        switch (random.nextInt(5)) {
            case 0:
                return bipedHead;
            case 1:
                return bipedBody;
            case 2:
                return bipedLeftArm;
            case 3:
                return bipedRightArm;
            case 4:
                return bipedLeftLeg;
            case 5:
                return bipedRightLeg;
        }
        return bipedHead;
    }
}

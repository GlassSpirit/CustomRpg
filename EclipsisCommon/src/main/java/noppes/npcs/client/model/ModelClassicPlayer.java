package noppes.npcs.client.model;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class ModelClassicPlayer extends ModelPlayerAlt {
    public ModelClassicPlayer(float scale) {
        super(scale, false);
    }

    @Override
    public void setRotationAngles(float par1, float par2, float par3, float par4, float par5, float par6, Entity entity) {
        super.setRotationAngles(par1, par2, par3, par4, par5, par6, entity);

        float j = 2.0f;

        if (entity.isSprinting()) {
            j = 1.0f;
        }

        bipedRightArm.rotateAngleX += MathHelper.cos(par1 * 0.6662F + (float) Math.PI) * j * par2;
        bipedLeftArm.rotateAngleX += MathHelper.cos(par1 * 0.6662F) * j * par2;
        bipedLeftArm.rotateAngleZ += (MathHelper.cos(par1 * 0.2812F) - 1.0F) * par2;
        bipedRightArm.rotateAngleZ += (MathHelper.cos(par1 * 0.2312F) + 1.0F) * par2;

        bipedLeftArmwear.rotateAngleX = bipedLeftArm.rotateAngleX;
        bipedLeftArmwear.rotateAngleY = bipedLeftArm.rotateAngleY;
        bipedLeftArmwear.rotateAngleZ = bipedLeftArm.rotateAngleZ;
        bipedRightArmwear.rotateAngleX = bipedRightArm.rotateAngleX;
        bipedRightArmwear.rotateAngleY = bipedRightArm.rotateAngleY;
        bipedRightArmwear.rotateAngleZ = bipedRightArm.rotateAngleZ;
    }
}

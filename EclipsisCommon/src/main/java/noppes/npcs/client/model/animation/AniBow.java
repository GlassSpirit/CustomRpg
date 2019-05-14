package noppes.npcs.client.model.animation;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import noppes.npcs.common.entity.EntityNPCInterface;

public class AniBow {
    public static void setRotationAngles(float par1, float par2, float par3, float par4, float par5, float par6, Entity entity, ModelBiped model) {
        float ticks = (entity.ticksExisted - ((EntityNPCInterface) entity).animationStart) / 10f;
        if (ticks > 1)
            ticks = 1;
        float ticks2 = (entity.ticksExisted + 1 - ((EntityNPCInterface) entity).animationStart) / 10f;
        if (ticks2 > 1)
            ticks2 = 1;
        ticks += (ticks2 - ticks) * Minecraft.getMinecraft().getRenderPartialTicks();

        model.bipedBody.rotateAngleX = ticks;
        model.bipedHead.rotateAngleX = ticks;
        model.bipedLeftArm.rotateAngleX = ticks;
        model.bipedRightArm.rotateAngleX = ticks;

        model.bipedBody.rotationPointZ = -ticks * 10;
        model.bipedBody.rotationPointY = ticks * 6;

        model.bipedHead.rotationPointZ = -ticks * 10;
        model.bipedHead.rotationPointY = ticks * 6;

        model.bipedLeftArm.rotationPointZ = -ticks * 10;
        model.bipedLeftArm.rotationPointY += ticks * 6;

        model.bipedRightArm.rotationPointZ = -ticks * 10;
        model.bipedRightArm.rotationPointY += ticks * 6;
    }
}

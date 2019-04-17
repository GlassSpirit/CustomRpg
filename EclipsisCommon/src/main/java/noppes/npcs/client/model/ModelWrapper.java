package noppes.npcs.client.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class ModelWrapper extends ModelBase {
    public ModelBase wrapped;
    public ResourceLocation texture;
    public ModelBase mainModelOld;

    @Override
    public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        Minecraft.getMinecraft().getRenderManager().renderEngine.bindTexture(texture);
        wrapped.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
    }
}

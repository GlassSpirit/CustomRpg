package noppes.npcs.client.model.part.horns;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;

public class ModelAntennasFront extends ModelRenderer {

    public ModelAntennasFront(ModelBiped base) {
        super(base);
        ModelRenderer rightantenna1 = new ModelRenderer(base, 60, 27);
        rightantenna1.addBox(0F, 0F, 0F, 1, 4, 1);
        rightantenna1.setRotationPoint(2F, -9.4F, -2F);
        setRotation(rightantenna1, 0.4014257F, 0.0698132F, 0.0698132F);
        addChild(rightantenna1);

        ModelRenderer leftantenna1 = new ModelRenderer(base, 56, 27);
        leftantenna1.mirror = true;
        leftantenna1.addBox(-1F, 0F, 0F, 1, 4, 1);
        leftantenna1.setRotationPoint(-2F, -9.4F, -2F);
        setRotation(leftantenna1, 0.4014257F, -0.0698132F, -0.0698132F);
        addChild(leftantenna1);

        ModelRenderer rightantenna2 = new ModelRenderer(base, 60, 27);
        rightantenna2.addBox(0F, 0F, 0F, 1, 4, 1);
        rightantenna2.setRotationPoint(3F, -10.2F, -5.3F);
        setRotation(rightantenna2, 1.22173F, -0.2094395F, 0.0698132F);
        addChild(rightantenna2);

        ModelRenderer leftantenna2 = new ModelRenderer(base, 56, 27);
        leftantenna2.mirror = true;
        leftantenna2.addBox(-1F, 0F, 0F, 1, 4, 1);
        leftantenna2.setRotationPoint(-3F, -10.2F, -5.3F);
        setRotation(leftantenna2, 1.22173F, 0.2094395F, -0.0698132F);
        addChild(leftantenna2);
    }

    private void setRotation(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }
}

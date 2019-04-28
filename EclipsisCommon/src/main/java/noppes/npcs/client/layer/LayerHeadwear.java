package noppes.npcs.client.layer;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import noppes.npcs.CustomNpcsConfig;
import noppes.npcs.client.ClientProxy;
import noppes.npcs.client.model.part.head.ModelHeadwear;
import noppes.npcs.entity.EntityCustomNpc;

public class LayerHeadwear extends LayerInterface implements LayerPreRender {
    private ModelHeadwear headwear;

    public LayerHeadwear(RenderLiving render) {
        super(render);
        headwear = new ModelHeadwear(model);
    }

    @Override
    public void render(float par2, float par3, float par4, float par5, float par6, float par7) {
        if (CustomNpcsConfig.HeadWearType != 1)
            return;

        if (npc.hurtTime <= 0 && npc.deathTime <= 0) {
            int color = npc.display.getTint();
            float red = (color >> 16 & 255) / 255f;
            float green = (color >> 8 & 255) / 255f;
            float blue = (color & 255) / 255f;
            GlStateManager.color(red, green, blue, 1);
        }
        ClientProxy.Companion.bindTexture(npc.textureLocation);
        model.bipedHead.postRender(par7);
        headwear.render(par7);
    }

    @Override
    public void rotate(float par2, float par3, float par4, float par5, float par6, float par7) {

    }

    @Override
    public void preRender(EntityCustomNpc player) {
        model.bipedHeadwear.isHidden = CustomNpcsConfig.HeadWearType == 1;
        headwear.config = null;
    }

}

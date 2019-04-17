package noppes.npcs.client.renderer;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import noppes.npcs.entity.EntityNPCInterface;

public class RenderNpcDragon<T extends EntityNPCInterface> extends RenderNPCInterface<T> {

    public RenderNpcDragon(ModelBase model, float f) {
        super(model, f);
    }

    @Override
    protected void preRenderCallback(T npc, float f) {
        GlStateManager.translate(0, 0, 0.6f / 5 * npc.display.getSize());
        super.preRenderCallback(npc, f);
    }
}

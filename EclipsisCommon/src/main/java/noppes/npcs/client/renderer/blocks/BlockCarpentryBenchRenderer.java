package noppes.npcs.client.renderer.blocks;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import noppes.npcs.client.model.blocks.ModelCarpentryBench;

public class BlockCarpentryBenchRenderer extends TileEntitySpecialRenderer {

    private final ModelCarpentryBench model = new ModelCarpentryBench();
    private static final ResourceLocation TEXTURE = new ResourceLocation("customnpcs", "textures/models/carpentrybench.png");

    @Override
    public void render(TileEntity te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        int rotation = 0;
        if (te != null && te.getPos() != BlockPos.ORIGIN) {
            rotation = te.getBlockMetadata() % 4;
        }
        GlStateManager.pushMatrix();
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.translate((float) x + 0.5f, (float) y + 1.4f, (float) z + 0.5f);
        GlStateManager.scale(0.95f, 0.95f, 0.95f);
        GlStateManager.rotate(180, 0, 0, 1);
        GlStateManager.rotate(90 * rotation, 0, 1, 0);
        this.bindTexture(TEXTURE);
        model.render(null, 0, 0, 0, 0, 0.0F, 0.0625F);
        GlStateManager.popMatrix();
    }

}

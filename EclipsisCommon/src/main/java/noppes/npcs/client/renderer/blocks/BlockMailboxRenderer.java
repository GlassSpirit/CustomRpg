package noppes.npcs.client.renderer.blocks;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import noppes.npcs.client.model.blocks.ModelMailboxUS;
import noppes.npcs.client.model.blocks.ModelMailboxWow;

public class BlockMailboxRenderer extends TileEntitySpecialRenderer {

    private final ModelMailboxUS model = new ModelMailboxUS();
    private final ModelMailboxWow model2 = new ModelMailboxWow();

    private static final ResourceLocation text1 = new ResourceLocation("customnpcs", "textures/models/mailbox1.png");
    private static final ResourceLocation text2 = new ResourceLocation("customnpcs", "textures/models/mailbox2.png");
    private static final ResourceLocation text3 = new ResourceLocation("customnpcs", "textures/models/mailbox3.png");

    private int type;

    public BlockMailboxRenderer(int i) {
        type = i;
    }

    @Override
    public void render(TileEntity te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        int meta = 0;
        int type = this.type;
        if (te != null && te.getPos() != BlockPos.ORIGIN) {
            meta = te.getBlockMetadata() | 4;
            type = te.getBlockMetadata() >> 2;
        }
        GlStateManager.pushMatrix();
        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.translate((float) x + 0.5f, (float) y + 1.5f, (float) z + 0.5f);
        GlStateManager.rotate(180, 0, 0, 1);
        GlStateManager.rotate(90 * meta, 0, 1, 0);
        if (type == 0) {
            this.bindTexture(text1);
            model.render(null, 0, 0, 0, 0, 0.0F, 0.0625F);
        }
        if (type == 1) {
            this.bindTexture(text2);
            model2.render(null, 0, 0, 0, 0, 0.0F, 0.0625F);
        }
        if (type == 2) {
            this.bindTexture(text3);
            model2.render(null, 0, 0, 0, 0, 0.0F, 0.0625F);
        }
        GlStateManager.popMatrix();
    }

}

package noppes.npcs.client.renderer.blocks;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import noppes.npcs.objects.NpcObjects;
import noppes.npcs.objects.blocks.tiles.TileCopy;
import noppes.npcs.schematics.Schematic;

public class BlockCopyRenderer extends BlockRendererInterface {
    private final static ItemStack item = new ItemStack(NpcObjects.copyBlock);
    public static Schematic schematic = null;
    public static BlockPos pos = null;

    @Override
    public void render(TileEntity var1, double x, double y,
                       double z, float var8, int blockDamage, float alpha) {
        TileCopy tile = (TileCopy) var1;
        GlStateManager.pushMatrix();
        GlStateManager.color(1, 1, 1, 1);

        RenderHelper.enableStandardItemLighting();
        GlStateManager.disableBlend();
        GlStateManager.translate(x, y, z);
        drawSelectionBox(new BlockPos(tile.width, tile.height, tile.length));
        GlStateManager.translate(0.5f, 0.5f, 0.5f);
        GlStateManager.color(1, 1, 1, 1);
        GlStateManager.rotate(180, 0, 1, 0);
        Minecraft.getMinecraft().getRenderItem().renderItem(item, ItemCameraTransforms.TransformType.NONE);
        GlStateManager.popMatrix();
    }


    public void drawSelectionBox(BlockPos pos) {
        GlStateManager.disableTexture2D();
        GlStateManager.disableLighting();
        GlStateManager.disableCull();
        GlStateManager.disableBlend();

        AxisAlignedBB bb = new AxisAlignedBB(BlockPos.ORIGIN, pos);
        GlStateManager.translate(0.001f, 0.001f, 0.001f);
        RenderGlobal.drawSelectionBoundingBox(bb, 1, 0, 0, 1);

        GlStateManager.enableTexture2D();
        GlStateManager.enableLighting();
        GlStateManager.enableCull();
        GlStateManager.disableBlend();
    }
}

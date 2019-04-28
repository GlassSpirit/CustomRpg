package noppes.npcs.client.renderer.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import noppes.npcs.client.TextBlockClient;
import noppes.npcs.objects.NpcObjects;
import noppes.npcs.objects.blocks.tiles.TileScripted;
import noppes.npcs.objects.blocks.tiles.TileScripted.TextPlane;

import java.util.Random;

public class BlockScriptedRenderer extends BlockRendererInterface {

    private static Random random = new Random();

    @Override
    public void render(TileEntity te, double x, double y, double z, float partialTicks, int blockDamage, float alpha) {
        TileScripted tile = (TileScripted) te;
        GlStateManager.pushMatrix();
        GlStateManager.disableBlend();

        RenderHelper.enableStandardItemLighting();
        GlStateManager.translate(x + 0.5, y, z + 0.5);
        if (overrideModel()) {
            GlStateManager.translate(0, 0.5, 0);
            renderItem(new ItemStack(NpcObjects.scriptedBlock));
        } else {
            GlStateManager.rotate(tile.rotationY, 0, 1, 0);
            GlStateManager.rotate(tile.rotationX, 1, 0, 0);
            GlStateManager.rotate(tile.rotationZ, 0, 0, 1);
            GlStateManager.scale(tile.scaleX, tile.scaleY, tile.scaleZ);
            Block b = tile.blockModel;
            if (b == null || b == Blocks.AIR) {
                GlStateManager.translate(0, 0.5, 0);
                renderItem(tile.itemModel);
            } else if (b == NpcObjects.scriptedBlock) {
                GlStateManager.translate(0, 0.5, 0);
                renderItem(tile.itemModel);
            } else {
                IBlockState state = b.getStateFromMeta(tile.itemModel.getItemDamage());
                renderBlock(tile, b, state);

                if (b.hasTileEntity(state) && !tile.renderTileErrored) {
                    try {
                        if (tile.renderTile == null) {
                            TileEntity entity = b.createTileEntity(getWorld(), state);
                            entity.setPos(tile.getPos());
                            entity.setWorld(getWorld());
                            ObfuscationReflectionHelper.setPrivateValue(TileEntity.class, entity, tile.itemModel.getItemDamage(), 5);
                            ObfuscationReflectionHelper.setPrivateValue(TileEntity.class, entity, b, 6);
                            tile.renderTile = entity;
                            if (entity instanceof ITickable) {
                                tile.renderTileUpdate = (ITickable) entity;
                            }
                        }
                        TileEntitySpecialRenderer renderer = TileEntityRendererDispatcher.instance.getRenderer(tile.renderTile);

                        if (renderer != null) {
                            renderer.render(tile.renderTile, -0.5, 0, -0.5, partialTicks, blockDamage, alpha);

                        } else
                            tile.renderTileErrored = true;
                    } catch (Exception e) {
                        tile.renderTileErrored = true;
                    }
                }
            }
        }
        GlStateManager.popMatrix();

        if (!tile.text1.text.isEmpty()) {
            drawText(tile.text1, x, y, z);
        }
        if (!tile.text2.text.isEmpty()) {
            drawText(tile.text2, x, y, z);
        }
        if (!tile.text3.text.isEmpty()) {
            drawText(tile.text3, x, y, z);
        }
        if (!tile.text4.text.isEmpty()) {
            drawText(tile.text4, x, y, z);
        }
        if (!tile.text5.text.isEmpty()) {
            drawText(tile.text5, x, y, z);
        }
        if (!tile.text6.text.isEmpty()) {
            drawText(tile.text6, x, y, z);
        }
    }

    private void drawText(TextPlane text1, double x, double y, double z) {
        if (text1.textBlock == null || text1.textHasChanged) {
            text1.textBlock = new TextBlockClient(text1.text, 336, true, Minecraft.getMinecraft().player);
            text1.textHasChanged = false;
        }
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.color(1, 1, 1);
        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5, y + 0.5, z + 0.5);
        GlStateManager.rotate(text1.rotationY, 0, 1, 0);
        GlStateManager.rotate(text1.rotationX, 1, 0, 0);
        GlStateManager.rotate(text1.rotationZ, 0, 0, 1);
        GlStateManager.scale(text1.scale, text1.scale, 1);
        GlStateManager.translate(text1.offsetX, text1.offsetY, text1.offsetZ);
        float f1 = 0.6666667F;
        float f3 = 0.0133F * f1;
        GlStateManager.translate(0.0F, 0.5f, 0.01F);
        GlStateManager.scale(f3, -f3, f3);
        GlStateManager.glNormal3f(0.0F, 0.0F, -1.0F * f3);
        GlStateManager.depthMask(false);
        FontRenderer fontrenderer = this.getFontRenderer();

        float lineOffset = 0;
        if (text1.textBlock.lines.size() < 14)
            lineOffset = (14f - text1.textBlock.lines.size()) / 2;
        for (int i = 0; i < text1.textBlock.lines.size(); i++) {
            String text = text1.textBlock.lines.get(i).getFormattedText();
            fontrenderer.drawString(text, -fontrenderer.getStringWidth(text) / 2, (int) ((lineOffset + i) * (fontrenderer.FONT_HEIGHT - 0.3)), 0);
        }

        GlStateManager.depthMask(true);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();
    }

    private void renderItem(ItemStack item) {
        Minecraft.getMinecraft().getRenderItem().renderItem(item, ItemCameraTransforms.TransformType.NONE);
    }

    private void renderBlock(TileScripted tile, Block b, IBlockState state) {
        GlStateManager.pushMatrix();
        this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.enableBlend();
        GlStateManager.disableCull();
        GlStateManager.translate(-0.5F, -0, 0.5F);

        Minecraft.getMinecraft().getBlockRendererDispatcher().renderBlockBrightness(state, 1);
        if (b.getTickRandomly() && random.nextInt(12) == 1)
            b.randomDisplayTick(state, tile.getWorld(), tile.getPos(), random);
        GlStateManager.popMatrix();
    }

    private boolean overrideModel() {
        ItemStack held = Minecraft.getMinecraft().player.getHeldItemMainhand();
        if (held == null)
            return false;

        return held.getItem() == NpcObjects.wand || held.getItem() == NpcObjects.scripter;
    }
}

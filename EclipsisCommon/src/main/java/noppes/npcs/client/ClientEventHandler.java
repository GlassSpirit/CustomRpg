package noppes.npcs.client;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import noppes.npcs.LogWriter;
import noppes.npcs.api.constants.MarkType;
import noppes.npcs.blocks.tiles.TileBuilder;
import noppes.npcs.client.renderer.MarkRenderer;
import noppes.npcs.controllers.data.MarkData;
import noppes.npcs.controllers.data.MarkData.Mark;
import noppes.npcs.schematics.SchematicWrapper;
import org.lwjgl.opengl.GL11;

public class ClientEventHandler {

    private int displayList = -1;

    @SubscribeEvent
    public void onRenderTick(RenderWorldLastEvent event) {
        EntityPlayer player = Minecraft.getMinecraft().player;
        if (TileBuilder.DrawPos == null || TileBuilder.DrawPos.distanceSq(player.getPosition()) > 1000000)
            return;
        TileEntity te = player.world.getTileEntity(TileBuilder.DrawPos);
        if (te == null || !(te instanceof TileBuilder))
            return;
        TileBuilder tile = (TileBuilder) te;
        SchematicWrapper schem = tile.getSchematic();
        if (schem == null)
            return;
        GlStateManager.pushMatrix();
        RenderHelper.enableStandardItemLighting();
        GlStateManager.translate(TileBuilder.DrawPos.getX() - TileEntityRendererDispatcher.staticPlayerX, TileBuilder.DrawPos.getY() - TileEntityRendererDispatcher.staticPlayerY + 0.01, TileBuilder.DrawPos.getZ() - TileEntityRendererDispatcher.staticPlayerZ);
        GlStateManager.translate(1, tile.yOffest, 1);
        if (tile.rotation % 2 == 0)
            drawSelectionBox(new BlockPos(schem.schema.getWidth(), schem.schema.getHeight(), schem.schema.getLength()));
        else
            drawSelectionBox(new BlockPos(schem.schema.getLength(), schem.schema.getHeight(), schem.schema.getWidth()));
        if (TileBuilder.Compiled) {
            GlStateManager.callList(this.displayList);
        } else {
            BlockRendererDispatcher dispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
            if (displayList >= 0)
                GLAllocation.deleteDisplayLists(displayList);
            displayList = GLAllocation.generateDisplayLists(1);
            GL11.glNewList(this.displayList, GL11.GL_COMPILE);
            try {
                for (int i = 0; i < schem.size && i < 25000; i++) {
                    int posX = (i % schem.schema.getWidth());
                    int posZ = ((i - posX) / schem.schema.getWidth()) % schem.schema.getLength();
                    int posY = (((i - posX) / schem.schema.getWidth()) - posZ) / schem.schema.getLength();

                    IBlockState state = schem.schema.getBlockState(posX, posY, posZ);
                    if (state.getRenderType() == EnumBlockRenderType.INVISIBLE)
                        continue;
                    BlockPos pos = schem.rotatePos(posX, posY, posZ, tile.rotation);
                    GlStateManager.pushMatrix();
                    GlStateManager.pushAttrib();
                    GlStateManager.enableRescaleNormal();
                    GlStateManager.translate(pos.getX(), pos.getY(), pos.getZ());
                    Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
                    GlStateManager.rotate(-90, 0, 1, 0);

                    state = schem.rotationState(state, tile.rotation);
                    try {
                        dispatcher.renderBlockBrightness(state, 1);
                        if (GL11.glGetError() != 0) {
                            break;
                        }
                    } catch (Exception e) {

                    } finally {
                        GlStateManager.popAttrib();
                        GlStateManager.disableRescaleNormal();
                        GlStateManager.popMatrix();
                    }

                }
            } catch (Exception e) {
                LogWriter.error("Error preview builder block", e);
            } finally {
                GL11.glEndList();
                if (GL11.glGetError() == 0)
                    TileBuilder.Compiled = true;
            }

        }
        RenderHelper.disableStandardItemLighting();
        GlStateManager.translate(-1, 0, -1);
        GlStateManager.popMatrix();
    }

    @SubscribeEvent
    public void post(RenderLivingEvent.Post event) {
        MarkData data = MarkData.get(event.getEntity());
        EntityPlayer player = Minecraft.getMinecraft().player;
        for (Mark m : data.marks) {
            if (m.getType() != MarkType.NONE && m.availability.isAvailable(player)) {
                MarkRenderer.render(event.getEntity(), event.getX(), event.getY(), event.getZ(), m);
                break;
            }
        }
    }

    public void drawSelectionBox(BlockPos pos) {
        GlStateManager.disableTexture2D();
        GlStateManager.disableLighting();
        GlStateManager.disableCull();
        GlStateManager.disableBlend();

        AxisAlignedBB bb = new AxisAlignedBB(BlockPos.ORIGIN, pos);

        RenderGlobal.drawSelectionBoundingBox(bb, 1, 0, 0, 1);

        GlStateManager.enableTexture2D();
        GlStateManager.enableLighting();
        GlStateManager.enableCull();
        GlStateManager.disableBlend();
    }
}

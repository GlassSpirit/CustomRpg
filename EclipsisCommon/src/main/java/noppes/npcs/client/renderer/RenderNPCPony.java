package noppes.npcs.client.renderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResource;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import noppes.npcs.client.model.ModelPony;
import noppes.npcs.client.model.ModelPonyArmor;
import noppes.npcs.entity.EntityNpcPony;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class RenderNPCPony<T extends EntityNpcPony> extends RenderNPCInterface<T> {

    private ModelPony modelBipedMain;
    private ModelPonyArmor modelArmorChestplate;
    private ModelPonyArmor modelArmor;

    public RenderNPCPony() {
        super(new ModelPony(0.0F), 0.5F);
        modelBipedMain = (ModelPony) mainModel;
        modelArmorChestplate = new ModelPonyArmor(1.0F);
        modelArmor = new ModelPonyArmor(0.5F);
    }


    @Override
    public ResourceLocation getEntityTexture(T pony) {
        boolean check = pony.textureLocation == null || pony.textureLocation != pony.checked;
        ResourceLocation loc = super.getEntityTexture(pony);
        if (check) {
            try {
                IResource resource = Minecraft.getMinecraft().getResourceManager().getResource(loc);
                BufferedImage bufferedimage = ImageIO.read(resource.getInputStream());

                pony.isPegasus = false;
                pony.isUnicorn = false;
                Color color = new Color(bufferedimage.getRGB(0, 0), true);
                Color color1 = new Color(249, 177, 49, 255);
                Color color2 = new Color(136, 202, 240, 255);
                Color color3 = new Color(209, 159, 228, 255);
                Color color4 = new Color(254, 249, 252, 255);
                if (color.equals(color1)) {
                }
                if (color.equals(color2)) {
                    pony.isPegasus = true;
                }
                if (color.equals(color3)) {
                    pony.isUnicorn = true;
                }
                if (color.equals(color4)) {
                    pony.isPegasus = true;
                    pony.isUnicorn = true;
                }
                pony.checked = loc;

            } catch (IOException e) {

            }
        }
        return loc;
    }

//    @Override
//    protected void rotatePlayer(EntityNPCInterface entityplayer, float f, float f1, float f2)
//    {
//        if(entityplayer.isEntityAlive() && entityplayer.isPlayerSleeping())
//        {
//            GlStateManager.rotate(entityplayer.orientation, 0.0F, 1.0F, 0.0F);
//            GlStateManager.translate(-1.25F, -0.875F, 0.0F);
//            GlStateManager.rotate(90F, 0.0F, 1.0F, 0.0F);
//        } else
//        {
//            GlStateManager.rotate(180F - f1, 0.0F, 1.0F, 0.0F);
//            if(entityplayer.deathTime > 0)
//            {
//                float f3 = ((((float)entityplayer.deathTime + f2) - 1.0F) / 20F) * 1.6F;
//                f3 = MathHelper.sqrt(f3);
//                if(f3 > 1.0F)
//                {
//                    f3 = 1.0F;
//                }
//                GlStateManager.rotate(f3 * getDeathMaxRotation(entityplayer), 0.0F, 0.0F, 1.0F);
//            }
//        }
//    }
//    protected void renderSpecials(EntityNpcPony entityplayer, float f)
//    {
//        super.renderEquippedItems(entityplayer, f);
//        if(!entityplayer.isPlayerSleeping())
//        {
//            if(entityplayer.isUnicorn)
//            {
//                renderDrop(this.renderManager, entityplayer, modelBipedMain.unicornarm, 1.0F, 0.35F, 0.5375F, -0.45F);
//            } else
//            {
//                renderDrop(this.renderManager, entityplayer, modelBipedMain.RightArm, 1.0F, -0.0625F, 0.8375F, 0.0625F);
//            }
//        }
//    }
//
//
//    protected void renderDrop(RenderManager rendermanager, EntityNpcPony entityplayer, ModelRenderer modelrenderer, float f, float f1, float f2, float f3)
//    {
//        ItemStack itemstack = entityplayer.getHeldItem();
//        if(itemstack == null)
//        {
//            return;
//        }
//        GlStateManager.pushMatrix();
//        if(modelrenderer != null)
//        {
//            modelrenderer.postRender(f * 0.0625F);
//        }
//        GlStateManager.translate(f1, f2, f3);
//        if(itemstack.getItem() instanceof ItemBlock && RenderBlocks.renderItemIn3d(Block.getBlockFromItem(itemstack.getItem()).getRenderType()))
//        {
//            GlStateManager.translate(0.0F, 0.1875F, -0.3125F);
//            GlStateManager.rotate(20F, 1.0F, 0.0F, 0.0F);
//            GlStateManager.rotate(45F, 0.0F, 1.0F, 0.0F);
//            float f4 = 0.375F * f;
//            GlStateManager.scale(f4, -f4, f4);
//        } else
//        if(itemstack.getItem() == Items.bow)
//        {
//            GlStateManager.translate(0.0F, 0.125F, 0.3125F);
//            GlStateManager.rotate(-20F, 0.0F, 1.0F, 0.0F);
//            float f5 = 0.625F * f;
//            GlStateManager.scale(f5, -f5, f5);
//            GlStateManager.rotate(-100F, 1.0F, 0.0F, 0.0F);
//            GlStateManager.rotate(45F, 0.0F, 1.0F, 0.0F);
//        } else
//        if(itemstack.getItem().isFull3D())
//        {
//            if(itemstack.getItem().shouldRotateAroundWhenRendering())
//            {
//                GlStateManager.rotate(180F, 0.0F, 0.0F, 1.0F);
//                GlStateManager.translate(0.0F, -0.125F, 0.0F);
//            }
//            GlStateManager.translate(0.0F, 0.1875F, 0.0F);
//            float f6 = 0.625F * f;
//            GlStateManager.scale(f6, -f6, f6);
//            GlStateManager.rotate(-100F, 1.0F, 0.0F, 0.0F);
//            GlStateManager.rotate(45F, 0.0F, 1.0F, 0.0F);
//        } else
//        {
//            GlStateManager.translate(0.25F, 0.1875F, -0.1875F);
//            float f7 = 0.375F * f;
//            GlStateManager.scale(f7, f7, f7);
//            GlStateManager.rotate(60F, 0.0F, 0.0F, 1.0F);
//            GlStateManager.rotate(-90F, 1.0F, 0.0F, 0.0F);
//            GlStateManager.rotate(20F, 0.0F, 0.0F, 1.0F);
//        }
//        if(itemstack.getItem() == Items.potionitem)
//        {
//            for (int j = 0; j <= 1; j++)
//            {
//                int k = itemstack.getItem().getColorFromItemStack(itemstack, j);
//                float f9 = (float)(k >> 16 & 0xff) / 255F;
//                float f10 = (float)(k >> 8 & 0xff) / 255F;
//                float f12 = (float)(k & 0xff) / 255F;
//                GlStateManager.color(f9, f10, f12, 1.0F);
//                renderManager.itemRenderer.renderItem(entityplayer, itemstack, j);
//            }
//        } else
//        {
//            rendermanager.itemRenderer.renderItem(entityplayer, itemstack, 0);
//        }
//        GlStateManager.popMatrix();
//    }

    @Override
    public void doRender(T pony, double d, double d1, double d2, float f, float f1) {
        ItemStack itemstack = pony.getHeldItemMainhand();
        //setRenderPassModel(modelBipedMain);

        modelArmorChestplate.heldItemRight = modelArmor.heldItemRight = modelBipedMain.heldItemRight = itemstack == null ? 0 : 1;
        modelArmorChestplate.isSneak = modelArmor.isSneak = modelBipedMain.isSneak = pony.isSneaking();
        modelArmorChestplate.isRiding = modelArmor.isRiding = modelBipedMain.isRiding = false;
        modelArmorChestplate.isSleeping = modelArmor.isSleeping = modelBipedMain.isSleeping = pony.isPlayerSleeping();
        modelArmorChestplate.isUnicorn = modelArmor.isUnicorn = modelBipedMain.isUnicorn = pony.isUnicorn;
        modelArmorChestplate.isPegasus = modelArmor.isPegasus = modelBipedMain.isPegasus = pony.isPegasus;
        if (pony.isSneaking()) {
            d1 -= 0.125D;
        }
        super.doRender(pony, d, d1, d2, f, f1);
        modelArmorChestplate.aimedBow = modelArmor.aimedBow = modelBipedMain.aimedBow = false;
        modelArmorChestplate.isRiding = modelArmor.isRiding = modelBipedMain.isRiding = false;
        modelArmorChestplate.isSneak = modelArmor.isSneak = modelBipedMain.isSneak = false;
        modelArmorChestplate.heldItemRight = modelArmor.heldItemRight = modelBipedMain.heldItemRight = 0;
    }

}

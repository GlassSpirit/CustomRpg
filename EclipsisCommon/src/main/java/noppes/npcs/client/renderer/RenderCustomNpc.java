package noppes.npcs.client.renderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelBiped.ArmPose;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.NPCRendererHelper;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.*;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.client.layer.*;
import noppes.npcs.client.model.ModelBipedAlt;
import noppes.npcs.controllers.PixelmonHelper;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.entity.EntityNPCInterface;
import org.lwjgl.opengl.GL11;

import java.util.List;

public class RenderCustomNpc<T extends EntityCustomNpc> extends RenderNPCInterface<T> {
    private float partialTicks;
    private EntityLivingBase entity;
    private RenderLivingBase renderEntity;
    public ModelBiped npcmodel;

    public RenderCustomNpc(ModelBiped model) {
        super(model, 0.5f);
        npcmodel = (ModelBiped) mainModel;
        layerRenderers.add(new LayerEyes(this));
        layerRenderers.add(new LayerHeadwear(this));
        layerRenderers.add(new LayerHead(this));
        layerRenderers.add(new LayerArms(this));
        layerRenderers.add(new LayerLegs(this));
        layerRenderers.add(new LayerBody(this));
        layerRenderers.add(new LayerNpcCloak(this));
        addLayer(new LayerHeldItem(this));
        addLayer(new LayerCustomHead(npcmodel.bipedHead));
        //addLayer(new LayerGlow(this));

        LayerBipedArmor armor = new LayerBipedArmor(this);
        addLayer(armor);
        ObfuscationReflectionHelper.setPrivateValue(LayerArmorBase.class, armor, new ModelBipedAlt(0.5f), 1);
        ObfuscationReflectionHelper.setPrivateValue(LayerArmorBase.class, armor, new ModelBipedAlt(1), 2);

    }

    @Override
    public void doRender(T npc, double d, double d1, double d2, float f, float partialTicks) {
        this.partialTicks = partialTicks;
        entity = npc.modelData.getEntity(npc);
        if (entity != null) {
            Render render = renderManager.getEntityRenderObject(entity);
            if (render instanceof RenderLivingBase)
                renderEntity = (RenderLivingBase) render;
            else {
                renderEntity = null;
                entity = null;
            }
        } else {
            renderEntity = null;
            List<LayerRenderer<T>> list = this.layerRenderers;
            for (LayerRenderer layer : list) {
                if (layer instanceof LayerPreRender) {
                    ((LayerPreRender) layer).preRender(npc);
                }
            }
        }

        npcmodel.rightArmPose = getPose(npc, npc.getHeldItemMainhand());
        npcmodel.leftArmPose = getPose(npc, npc.getHeldItemOffhand());
        super.doRender(npc, d, d1, d2, f, partialTicks);
    }

    public ArmPose getPose(T npc, ItemStack item) {
        if (NoppesUtilServer.IsItemStackNull(item))
            return ArmPose.EMPTY;

        if (npc.getItemInUseCount() > 0) {
            EnumAction enumaction = item.getItemUseAction();

            if (enumaction == EnumAction.BLOCK) {
                return ModelBiped.ArmPose.BLOCK;
            } else if (enumaction == EnumAction.BOW) {
                return ModelBiped.ArmPose.BOW_AND_ARROW;
            }
        }
        return ArmPose.ITEM;
    }

    @Override
    protected void renderModel(T npc, float par2, float par3, float par4, float par5, float par6, float par7) {
        if (renderEntity != null) {
            boolean flag = !npc.isInvisible();
            boolean flag1 = !flag && !npc.isInvisibleToPlayer(Minecraft.getMinecraft().player);
            if (!flag && !flag1)
                return;

            if (flag1) {
                GlStateManager.pushMatrix();
                GlStateManager.color(1.0F, 1.0F, 1.0F, 0.15F);
                GlStateManager.depthMask(false);
                GlStateManager.enableBlend();
                GlStateManager.blendFunc(770, 771);
                GlStateManager.alphaFunc(516, 0.003921569F);
            }

            ModelBase model = renderEntity.mainModel;
            if (PixelmonHelper.isPixelmon(entity)) {
                ModelBase pixModel = (ModelBase) PixelmonHelper.getModel(entity);
                if (pixModel != null) {
                    model = pixModel;
                    PixelmonHelper.setupModel(entity, pixModel);
                }
            }

            model.swingProgress = 1f;
            model.isRiding = entity.isRiding() && (entity.getRidingEntity() != null && entity.getRidingEntity().shouldRiderSit());
            model.setLivingAnimations(entity, par2, par3, partialTicks);
            model.setRotationAngles(par2, par3, par4, par5, par6, par7, entity);
            model.isChild = entity.isChild();

            NPCRendererHelper.renderModel(entity, par2, par3, par4, par5, par6, par7, renderEntity, model, getEntityTexture(npc));


            if (!npc.display.getOverlayTexture().isEmpty()) {
                GlStateManager.depthFunc(GL11.GL_LEQUAL);
                if (npc.textureGlowLocation == null) {
                    npc.textureGlowLocation = new ResourceLocation(npc.display.getOverlayTexture());
                }
                float f1 = 1.0F;
                GlStateManager.enableBlend();
                GlStateManager.blendFunc(GL11.GL_ONE, GL11.GL_ONE);
                GlStateManager.disableLighting();
                if (npc.isInvisible()) {
                    GlStateManager.depthMask(false);
                } else {
                    GlStateManager.depthMask(true);
                }
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                GlStateManager.pushMatrix();
                GlStateManager.scale(1.001f, 1.001f, 1.001f);
                NPCRendererHelper.renderModel(entity, par2, par3, par4, par5, par6, par7, renderEntity, model, npc.textureGlowLocation);
                GlStateManager.popMatrix();
                GlStateManager.enableLighting();
                GlStateManager.color(1.0F, 1.0F, 1.0F, f1);

                GlStateManager.depthFunc(GL11.GL_LEQUAL);
                GlStateManager.disableBlend();
            }

            if (flag1) {
                GlStateManager.disableBlend();
                GlStateManager.alphaFunc(516, 0.1F);
                GlStateManager.popMatrix();
                GlStateManager.depthMask(true);
            }
        } else {
            super.renderModel(npc, par2, par3, par4, par5, par6, par7);
        }
    }

    @Override
    protected void renderLayers(T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scaleIn) {
        if (entity != null && renderEntity != null) {
            NPCRendererHelper.drawLayers(entity, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scaleIn, renderEntity);
        } else {
            super.renderLayers(entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scaleIn);
        }
    }

    @Override
    protected void preRenderCallback(T npc, float f) {
        if (renderEntity != null) {
            renderColor(npc);
            int size = npc.display.getSize();
            if (entity instanceof EntityNPCInterface) {
                ((EntityNPCInterface) entity).display.setSize(5);
            }
            NPCRendererHelper.preRenderCallback(entity, f, renderEntity);
            npc.display.setSize(size);
            GlStateManager.scale(0.2f * npc.display.getSize(), 0.2f * npc.display.getSize(), 0.2f * npc.display.getSize());
        } else
            super.preRenderCallback(npc, f);
    }

    @Override
    protected float handleRotationFloat(T par1EntityLivingBase, float par2) {
        if (renderEntity != null) {
            return NPCRendererHelper.handleRotationFloat(entity, par2, renderEntity);
        }
        return super.handleRotationFloat(par1EntityLivingBase, par2);
    }
}
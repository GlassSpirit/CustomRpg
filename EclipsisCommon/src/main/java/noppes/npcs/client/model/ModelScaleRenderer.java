package noppes.npcs.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import noppes.npcs.ModelPartConfig;
import noppes.npcs.constants.EnumParts;
import org.lwjgl.opengl.GL11;

public class ModelScaleRenderer extends ModelRenderer {

    public boolean isCompiled;

    /**
     * The GL display list rendered by the Tessellator for this model
     */
    public int displayList;

    public ModelPartConfig config;

    public EnumParts part;

    public ModelScaleRenderer(ModelBase par1ModelBase, EnumParts part) {
        super(par1ModelBase);
        this.part = part;
    }

    public ModelScaleRenderer(ModelBase par1ModelBase, int par2, int par3, EnumParts part) {
        this(par1ModelBase, part);
        this.setTextureOffset(par2, par3);
    }

    public void setRotation(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }


    @Override
    public void render(float par1) {
        if (!showModel || isHidden)
            return;
        if (!isCompiled) {
            compile(par1);
        }
        GlStateManager.pushMatrix();
        this.postRender(par1);
        GlStateManager.callList(this.displayList);
        if (this.childModels != null) {
            for (int i = 0; i < this.childModels.size(); ++i) {
                this.childModels.get(i).render(par1);
            }
        }
        GlStateManager.popMatrix();
    }

    @Override
    public void postRender(float par1) {
        if (config != null)
            GlStateManager.translate(config.transX, config.transY, config.transZ);
        super.postRender(par1);
        if (config != null)
            GlStateManager.scale(config.scaleX, config.scaleY, config.scaleZ);
    }

    public void postRenderNoScale(float par1) {
        GlStateManager.translate(config.transX, config.transY, config.transZ);
        super.postRender(par1);
    }

    public void parentRender(float par1) {
        super.render(par1);
    }

    public void compile(float par1) {
        this.displayList = GLAllocation.generateDisplayLists(1);
        GlStateManager.glNewList(this.displayList, GL11.GL_COMPILE);
        BufferBuilder tessellator = Tessellator.getInstance().getBuffer();

        for (int i = 0; i < this.cubeList.size(); ++i) {
            this.cubeList.get(i).render(tessellator, par1);
        }

        GL11.glEndList();
        this.isCompiled = true;
    }

}

package noppes.npcs.client.gui.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import noppes.npcs.NoppesStringUtils;
import org.lwjgl.input.Mouse;

public class GuiNpcSlider extends GuiButton {
    private ISliderListener listener;
    public int id;

    public float sliderValue = 1.0F;

    public boolean dragging;

    public GuiNpcSlider(GuiScreen parent, int id, int xPos, int yPos, String displayString, float sliderValue) {
        super(id, xPos, yPos, 150, 20, NoppesStringUtils.translate(displayString));
        this.id = id;
        this.sliderValue = sliderValue;
        if (parent instanceof ISliderListener)
            listener = (ISliderListener) parent;
    }

    public GuiNpcSlider(GuiScreen parent, int id, int xPos, int yPos, float sliderValue) {
        this(parent, id, xPos, yPos, "", sliderValue);
        if (listener != null)
            listener.mouseDragged(this);
    }

    public GuiNpcSlider(GuiScreen parent, int id, int xPos, int yPos, int width, int height, float sliderValue) {
        this(parent, id, xPos, yPos, "", sliderValue);
        this.width = width;
        this.height = height;
        if (listener != null)
            listener.mouseDragged(this);
    }

    @Override
    public void mouseDragged(Minecraft mc, int par2, int par3) {
        if (!this.visible)
            return;

        mc.getTextureManager().bindTexture(BUTTON_TEXTURES);
        if (this.dragging) {
            this.sliderValue = (float) (par2 - (this.x + 4)) / (float) (this.width - 8);

            if (this.sliderValue < 0.0F) {
                this.sliderValue = 0.0F;
            }

            if (this.sliderValue > 1.0F) {
                this.sliderValue = 1.0F;
            }

            if (listener != null)
                listener.mouseDragged(this);

            if (!Mouse.isButtonDown(0)) {
                this.mouseReleased(0, 0);
            }
        }

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.drawTexturedModalRect(this.x + (int) (this.sliderValue * (float) (this.width - 8)), this.y, 0, 66, 4, 20);
        this.drawTexturedModalRect(this.x + (int) (this.sliderValue * (float) (this.width - 8)) + 4, this.y, 196, 66, 4, 20);
    }

    public String getDisplayString() {
        return displayString;
    }

    public void setString(String str) {
        displayString = NoppesStringUtils.translate(str);
    }

    @Override
    public boolean mousePressed(Minecraft par1Minecraft, int par2, int par3) {
        if (this.enabled && this.visible && par2 >= this.x && par3 >= this.y && par2 < this.x + this.width && par3 < this.y + this.height) {
            this.sliderValue = (float) (par2 - (this.x + 4)) / (float) (this.width - 8);

            if (this.sliderValue < 0.0F) {
                this.sliderValue = 0.0F;
            }

            if (this.sliderValue > 1.0F) {
                this.sliderValue = 1.0F;
            }

            if (listener != null)
                listener.mousePressed(this);

            this.dragging = true;
            return true;
        }
        return false;
    }

    @Override
    public void mouseReleased(int par1, int par2) {
        this.dragging = false;

        if (listener != null)
            listener.mouseReleased(this);
    }

    @Override
    public int getHoverState(boolean par1) {
        return 0;
    }
}

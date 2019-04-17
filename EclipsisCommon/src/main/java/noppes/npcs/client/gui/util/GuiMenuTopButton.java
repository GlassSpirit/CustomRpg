package noppes.npcs.client.gui.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;

public class GuiMenuTopButton extends GuiNpcButton {
    public static final ResourceLocation resource = new ResourceLocation("customnpcs", "textures/gui/menutopbutton.png");
    protected int height;
    public boolean active;
    public boolean hover = false;
    public boolean rotated = false;

    public IButtonListener listener;

    public GuiMenuTopButton(int i, int j, int k, String s) {
        super(i, j, k, I18n.translateToLocal(s));
        active = false;

        width = Minecraft.getMinecraft().fontRenderer.getStringWidth(displayString) + 12;
        height = 20;
    }

    public GuiMenuTopButton(int i, GuiButton parent, String s) {
        this(i, parent.x + parent.width, parent.y, s);
    }

    public GuiMenuTopButton(int i, GuiButton parent, String s,
                            IButtonListener listener) {
        this(i, parent, s);
        this.listener = listener;
    }

    @Override
    public int getHoverState(boolean flag) {
        byte byte0 = 1;
        if (active) {
            byte0 = 0;
        } else if (flag) {
            byte0 = 2;
        }
        return byte0;
    }

    @Override
    public void drawButton(Minecraft minecraft, int i, int j, float partialTicks) {
        if (!getVisible()) {
            return;
        }
        GlStateManager.pushMatrix();
        minecraft.renderEngine.bindTexture(resource);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        int height = this.height - (active ? 0 : 2);
        hover = i >= x && j >= y && i < x + getWidth() && j < y + height;
        int k = getHoverState(hover);
        drawTexturedModalRect(x, y, 0, k * 20, getWidth() / 2, height);
        drawTexturedModalRect(x + getWidth() / 2, y, 200 - getWidth() / 2, k * 20, getWidth() / 2, height);
        mouseDragged(minecraft, i, j);
        FontRenderer fontrenderer = minecraft.fontRenderer;
        if (rotated)
            GlStateManager.rotate(90, 1, 0, 0);
        if (active) {
            drawCenteredString(fontrenderer, displayString, x + getWidth() / 2, y + (height - 8) / 2, 0xffffa0);
        } else if (hover) {
            drawCenteredString(fontrenderer, displayString, x + getWidth() / 2, y + (height - 8) / 2, 0xffffa0);
        } else {
            drawCenteredString(fontrenderer, displayString, x + getWidth() / 2, y + (height - 8) / 2, 0xe0e0e0);
        }
        GlStateManager.popMatrix();
    }

    @Override
    protected void mouseDragged(Minecraft minecraft, int i, int j) {
    }

    @Override
    public void mouseReleased(int i, int j) {
    }

    @Override
    public boolean mousePressed(Minecraft minecraft, int i, int j) {
        boolean bo = !active && getVisible() && hover;
        if (bo && listener != null) {
            listener.actionPerformed(this);
            return false;
        }
        return bo;
    }
}

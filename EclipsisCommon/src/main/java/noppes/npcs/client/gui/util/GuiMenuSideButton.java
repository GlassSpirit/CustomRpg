package noppes.npcs.client.gui.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class GuiMenuSideButton extends GuiNpcButton {
    public static final ResourceLocation resource = new ResourceLocation("customnpcs", "textures/gui/menusidebutton.png");

    public boolean active;

    public GuiMenuSideButton(int i, int j, int k, String s) {
        this(i, j, k, 200, 20, s);
    }

    public GuiMenuSideButton(int i, int j, int k, int l, int i1, String s) {
        super(i, j, k, l, i1, s);
        active = false;
    }

    @Override
    public int getHoverState(boolean flag) {
        if (active)
            return 0;
        return 1;
    }

    @Override
    public void drawButton(Minecraft minecraft, int i, int j, float partialTicks) {
        if (!visible) {
            return;
        }
        FontRenderer fontrenderer = minecraft.fontRenderer;
        minecraft.renderEngine.bindTexture(resource);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        int width = this.width + (active ? 2 : 0);
        hovered = i >= x && j >= y && i < x + width && j < y + height;
        int k = getHoverState(hovered);
        drawTexturedModalRect(x, y, 0, k * 22, width, height);
        mouseDragged(minecraft, i, j);

        String text = "";
        float maxWidth = width * 0.75f;
        if (fontrenderer.getStringWidth(displayString) > maxWidth) {
            for (int h = 0; h < displayString.length(); h++) {
                char c = displayString.charAt(h);
                if (fontrenderer.getStringWidth(text + c) > maxWidth)
                    break;
                text += c;
            }
            text += "...";
        } else
            text = displayString;
        if (active) {
            drawCenteredString(fontrenderer, text, x + width / 2, y + (height - 8) / 2, 0xffffa0);
        } else if (hovered) {
            drawCenteredString(fontrenderer, text, x + width / 2, y + (height - 8) / 2, 0xffffa0);
        } else {
            drawCenteredString(fontrenderer, text, x + width / 2, y + (height - 8) / 2, 0xe0e0e0);
        }
    }

    @Override
    protected void mouseDragged(Minecraft minecraft, int i, int j) {
    }

    @Override
    public void mouseReleased(int i, int j) {

    }

    @Override
    public boolean mousePressed(Minecraft minecraft, int i, int j) {
        return !active && visible && hovered;
    }
}

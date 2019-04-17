package noppes.npcs.client.gui.util;

import net.minecraft.client.Minecraft;

public class GuiColorButton extends GuiNpcButton {
    public int color;

    public GuiColorButton(int id, int x, int y, int color) {
        super(id, x, y, 50, 20, "");
        this.color = color;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        if (!this.visible)
            return;
        drawRect(x, y, x + 50, y + 20, 0xFF000000 + color);
    }

}

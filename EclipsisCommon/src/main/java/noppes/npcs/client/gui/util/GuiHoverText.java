package noppes.npcs.client.gui.util;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class GuiHoverText extends GuiScreen {
    private int x, y;
    public int id;

    protected static final ResourceLocation buttonTextures = new ResourceLocation("customnpcs:textures/gui/info.png");
    private String text;

    public GuiHoverText(int id, String text, int x, int y) {
        this.text = text;
        this.id = id;
        this.x = x;
        this.y = y;
    }

    @Override
    public void drawScreen(int par1, int par2, float par3) {
        GlStateManager.color(1, 1, 1, 1);
        mc.getTextureManager().bindTexture(buttonTextures);
        this.drawTexturedModalRect(this.x, this.y, 0, 0, 12, 12);

        if (inArea(x, y, 12, 12, par1, par2)) {
            List<String> lines = new ArrayList<String>();
            lines.add(text);
            this.drawHoveringText(lines, x + 8, y + 6, this.fontRenderer);
            GlStateManager.disableLighting();
        }
    }

    public boolean inArea(int x, int y, int width, int height, int mouseX, int mouseY) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }
}

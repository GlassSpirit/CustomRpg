package noppes.npcs.client.gui.util;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import noppes.npcs.common.entity.EntityNPCInterface;

public abstract class GuiNPCInterface2 extends GuiNPCInterface {
    private ResourceLocation background = new ResourceLocation("customnpcs:textures/gui/menubg.png");
    private GuiNpcMenu menu;

    public GuiNPCInterface2(EntityNPCInterface npc) {
        this(npc, -1);
    }

    public GuiNPCInterface2(EntityNPCInterface npc, int activeMenu) {
        super(npc);
        xSize = 420;
        ySize = 200;
        menu = new GuiNpcMenu(this, activeMenu, npc);

    }

    @Override
    public void initGui() {
        super.initGui();
        menu.initGui(guiLeft, guiTop, xSize);
    }


    @Override
    public void mouseClicked(int i, int j, int k) {
        if (!hasSubGui())
            menu.mouseClicked(i, j, k);
        super.mouseClicked(i, j, k);
    }

    public abstract void save();

    @Override
    public void drawScreen(int i, int j, float f) {
        if (drawDefaultBackground)
            drawDefaultBackground(); //drawDefaultBackground
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        mc.renderEngine.bindTexture(background);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, 200, 220);
        drawTexturedModalRect(guiLeft + xSize - 230, guiTop, 26, 0, 230, 220);
        int x = i;
        int y = j;
        if (hasSubGui())
            x = y = 0;

        menu.drawElements(getFontRenderer(), x, y, mc, f);

        boolean bo = drawDefaultBackground;
        drawDefaultBackground = false;
        super.drawScreen(i, j, f);
        drawDefaultBackground = bo;
    }
}

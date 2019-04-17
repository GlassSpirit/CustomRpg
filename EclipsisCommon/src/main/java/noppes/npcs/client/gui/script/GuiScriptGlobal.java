package noppes.npcs.client.gui.script;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import noppes.npcs.client.gui.util.GuiNPCInterface;
import noppes.npcs.client.gui.util.GuiNpcButton;

public class GuiScriptGlobal extends GuiNPCInterface {

    private final ResourceLocation resource = new ResourceLocation("customnpcs", "textures/gui/smallbg.png");

    public GuiScriptGlobal() {
        super();
        xSize = 176;
        ySize = 222;
        this.drawDefaultBackground = false;
        title = "";
    }

    @Override
    public void initGui() {
        super.initGui();

        this.addButton(new GuiNpcButton(0, guiLeft + 38, guiTop + 20, 100, 20, "Players"));
        this.addButton(new GuiNpcButton(1, guiLeft + 38, guiTop + 50, 100, 20, "Forge"));
    }

    @Override
    public void drawScreen(int i, int j, float f) {
        drawDefaultBackground();

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        mc.renderEngine.bindTexture(resource);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);

        super.drawScreen(i, j, f);
    }

    @Override
    protected void actionPerformed(GuiButton guibutton) {
        if (guibutton.id == 0) {
            displayGuiScreen(new GuiScriptPlayers());
        }
        if (guibutton.id == 1) {
            displayGuiScreen(new GuiScriptForge());
        }
    }

    @Override
    public void keyTyped(char c, int i) {
        if (i == 1 || isInventoryKey(i)) {
            close();
        }
    }

    @Override
    public void save() {

    }

}

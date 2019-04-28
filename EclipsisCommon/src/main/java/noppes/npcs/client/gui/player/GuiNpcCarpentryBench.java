package noppes.npcs.client.gui.player;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import noppes.npcs.client.CustomNpcResourceListener;
import noppes.npcs.client.gui.util.GuiContainerNPCInterface;
import noppes.npcs.client.gui.util.GuiNpcButton;
import noppes.npcs.containers.ContainerCarpentryBench;
import noppes.npcs.controllers.RecipeController;

public class GuiNpcCarpentryBench extends GuiContainerNPCInterface {
    private final ResourceLocation resource = new ResourceLocation("customnpcs", "textures/gui/carpentry.png");
    private ContainerCarpentryBench container;
    private GuiNpcButton button;

    public GuiNpcCarpentryBench(ContainerCarpentryBench container) {
        super(null, container);
        this.container = container;
        this.title = "";
        allowUserInput = false;//allowUserInput
        closeOnEsc = true;
        ySize = 180;
    }

    @Override
    public void initGui() {
        super.initGui();
        addButton(button = new GuiNpcButton(0, guiLeft + 158, guiTop + 4, 12, 20, "..."));
    }

    @Override
    public void buttonEvent(GuiButton guibutton) {
        displayGuiScreen(new GuiRecipes());
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
        button.enabled = RecipeController.instance != null && !RecipeController.instance.anvilRecipes.isEmpty();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        mc.renderEngine.bindTexture(resource);
        int l = (width - xSize) / 2;
        int i1 = (height - ySize) / 2;
        String title = I18n.format("tile.npccarpentybench.name");
        drawTexturedModalRect(l, i1, 0, 0, xSize, ySize);
        super.drawGuiContainerBackgroundLayer(f, i, j);
        fontRenderer.drawString(title, guiLeft + 4, guiTop + 4, CustomNpcResourceListener.DefaultTextColor);
        fontRenderer.drawString(I18n.format("container.inventory"), guiLeft + 4, guiTop + 87, CustomNpcResourceListener.DefaultTextColor);
    }

    @Override
    public void save() {
        return;
    }
}

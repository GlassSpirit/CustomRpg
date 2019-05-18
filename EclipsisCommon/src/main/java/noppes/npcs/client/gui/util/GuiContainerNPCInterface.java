package noppes.npcs.client.gui.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import noppes.npcs.containers.ContainerEmpty;
import noppes.npcs.entity.EntityNPCInterface;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public abstract class GuiContainerNPCInterface extends GuiContainer {
    public boolean drawDefaultBackground = false;
    public int guiLeft, guiTop;
    public EntityPlayerSP player;
    public EntityNPCInterface npc;
    private HashMap<Integer, GuiNpcButton> buttons = new HashMap<>();
    private HashMap<Integer, GuiMenuTopButton> topbuttons = new HashMap<>();
    private HashMap<Integer, GuiNpcTextField> textfields = new HashMap<>();
    private HashMap<Integer, GuiNpcLabel> labels = new HashMap<>();
    private HashMap<Integer, GuiCustomScroll> scrolls = new HashMap<>();
    private HashMap<Integer, GuiNpcSlider> sliders = new HashMap<>();
    public String title;
    public boolean closeOnEsc = false;
    private SubGuiInterface subgui;
    public int mouseX, mouseY;

    public GuiContainerNPCInterface(EntityNPCInterface npc, Container cont) {
        super(cont);
        this.player = Minecraft.getMinecraft().player;
        this.npc = npc;
        title = "Npc Mainmenu";

        this.mc = Minecraft.getMinecraft();
        this.itemRender = mc.getRenderItem();
        this.fontRenderer = mc.fontRenderer;
    }

    @Override
    public void setWorldAndResolution(Minecraft mc, int width, int height) {
        super.setWorldAndResolution(mc, width, height);
        initPacket();
    }

    public void initPacket() {
    }

    @Override
    public void initGui() {
        super.initGui();
        GuiNpcTextField.unfocus();
        buttonList.clear();
        buttons.clear();
        topbuttons.clear();
        scrolls.clear();
        sliders.clear();
        labels.clear();
        textfields.clear();
        Keyboard.enableRepeatEvents(true);

        if (subgui != null) {
            subgui.setWorldAndResolution(mc, width, height);
            subgui.initGui();
        }

        buttonList.clear();

        guiLeft = (width - xSize) / 2;
        guiTop = (height - ySize) / 2;
    }

    public ResourceLocation getResource(String texture) {
        return new ResourceLocation("customnpcs", "textures/gui/" + texture);
    }

    @Override
    public void updateScreen() {
        for (GuiNpcTextField tf : new ArrayList<>(textfields.values()))
            if (tf.enabled)
                tf.updateCursorCounter();
        super.updateScreen();
    }

    @Override
    protected void mouseClicked(int i, int j, int k) throws IOException {
        if (subgui != null)
            subgui.mouseClicked(i, j, k);
        else {
            for (GuiNpcTextField tf : new ArrayList<>(textfields.values()))
                if (tf.enabled)
                    tf.mouseClicked(i, j, k);
            if (k == 0) {
                for (GuiCustomScroll scroll : new ArrayList<>(scrolls.values())) {
                    scroll.mouseClicked(i, j, k);
                }
            }
            mouseEvent(i, j, k);
            super.mouseClicked(i, j, k);
        }
    }

    public void mouseEvent(int i, int j, int k) {
    }

    @Override
    protected void keyTyped(char c, int i) {
        if (subgui != null)
            subgui.keyTyped(c, i);
        else {
            for (GuiNpcTextField tf : new ArrayList<>(textfields.values()))
                tf.textboxKeyTyped(c, i);

            if (closeOnEsc && (i == 1 || i == mc.gameSettings.keyBindInventory.getKeyCode() && !GuiNpcTextField.isActive()))
                close();
        }
    }


    @Override
    protected void actionPerformed(GuiButton guibutton) {
        if (subgui != null)
            subgui.buttonEvent(guibutton);
        else
            buttonEvent(guibutton);
    }

    public void buttonEvent(GuiButton guibutton) {
    }

    public void close() {
        GuiNpcTextField.unfocus();
        save();
        player.closeScreen();
        displayGuiScreen(null);
        mc.setIngameFocus();
    }

    public void addButton(GuiNpcButton button) {
        buttons.put(button.id, button);
        buttonList.add(button);
    }

    public void addTopButton(GuiMenuTopButton button) {
        topbuttons.put(button.id, button);
        buttonList.add(button);
    }

    public GuiNpcButton getButton(int i) {
        return buttons.get(i);
    }

    public void addTextField(GuiNpcTextField tf) {
        textfields.put(tf.id, tf);
    }

    public GuiNpcTextField getTextField(int i) {
        return textfields.get(i);
    }

    public void addLabel(GuiNpcLabel label) {
        labels.put(label.id, label);
    }

    public GuiNpcLabel getLabel(int i) {
        return labels.get(i);
    }

    public GuiMenuTopButton getTopButton(int i) {
        return topbuttons.get(i);
    }

    public void addSlider(GuiNpcSlider slider) {
        sliders.put(slider.id, slider);
        buttonList.add(slider);
    }

    public GuiNpcSlider getSlider(int i) {
        return sliders.get(i);
    }

    public void addScroll(GuiCustomScroll scroll) {
        scroll.setWorldAndResolution(mc, 350, 250);
        scrolls.put(scroll.id, scroll);
    }

    public GuiCustomScroll getScroll(int id) {
        return scrolls.get(id);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2) {

    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
        drawCenteredString(fontRenderer, I18n.translateToLocal(title), width / 2, guiTop - 8, 0xffffff);
        for (GuiNpcLabel label : new ArrayList<>(labels.values()))
            label.drawLabel(this, fontRenderer);
        for (GuiNpcTextField tf : new ArrayList<>(textfields.values()))
            tf.drawTextBox(i, j);
        for (GuiCustomScroll scroll : new ArrayList<>(scrolls.values()))
            scroll.drawScreen(i, j, f, hasSubGui() ? 0 : Mouse.getDWheel());
    }

    public abstract void save();

    @Override
    public void drawScreen(int i, int j, float f) {
        mouseX = i;
        mouseY = j;

        Container container = this.inventorySlots;
        if (subgui != null) {
            this.inventorySlots = new ContainerEmpty();
        }
        super.drawScreen(i, j, f);
        zLevel = 0;
        GlStateManager.color(1, 1, 1, 1);
        if (subgui != null) {
            this.inventorySlots = container;
            RenderHelper.disableStandardItemLighting();
            subgui.drawScreen(i, j, f);
        } else {
            this.renderHoveredToolTip(mouseX, mouseY);
        }
    }

    @Override
    public void drawDefaultBackground() {
        if (drawDefaultBackground && subgui == null)
            super.drawDefaultBackground();
    }

    public FontRenderer getFontRenderer() {
        return this.fontRenderer;
    }

    public void closeSubGui(SubGuiInterface gui) {
        subgui = null;
    }

    public boolean hasSubGui() {
        return subgui != null;
    }

    public SubGuiInterface getSubGui() {
        if (hasSubGui() && subgui.hasSubGui())
            return subgui.getSubGui();
        return subgui;
    }

    public void displayGuiScreen(GuiScreen gui) {
        this.mc.displayGuiScreen(gui);
    }

    public void setSubGui(SubGuiInterface gui) {
        subgui = gui;
        subgui.setWorldAndResolution(mc, width, height);
        subgui.parent = this;
        initGui();
    }

    public void drawNpc(int x, int y) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        GlStateManager.enableColorMaterial();
        GlStateManager.pushMatrix();
        GlStateManager.translate(guiLeft + x, guiTop + y, 50F);
        float scale = 1;
        if (npc.height > 2.4)
            scale = 2 / npc.height;
        GlStateManager.scale(-30 * scale, 30 * scale, 30 * scale);
        GlStateManager.rotate(180F, 0.0F, 0.0F, 1.0F);


        float f2 = npc.renderYawOffset;
        float f3 = npc.rotationYaw;
        float f4 = npc.rotationPitch;
        float f7 = npc.rotationYawHead;
        float f5 = (float) (guiLeft + x) - mouseX;
        float f6 = (float) ((guiTop + y) - 50) - mouseY;
        int orientation = 0;
        if (npc != null) {
            orientation = npc.ais.orientation;
            npc.ais.orientation = 0;
        }
        GlStateManager.rotate(135F, 0.0F, 1.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        GlStateManager.rotate(-135F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-(float) Math.atan(f6 / 40F) * 20F, 1.0F, 0.0F, 0.0F);
        npc.renderYawOffset = (float) Math.atan(f5 / 40F) * 20F;
        npc.rotationYaw = (float) Math.atan(f5 / 40F) * 40F;
        npc.rotationPitch = -(float) Math.atan(f6 / 40F) * 20F;
        npc.rotationYawHead = npc.rotationYaw;
        mc.getRenderManager().playerViewY = 180F;
        mc.getRenderManager().renderEntity(npc, 0, 0, 0, 0, 1, false);
        npc.renderYawOffset = f2;
        npc.rotationYaw = f3;
        npc.rotationPitch = f4;
        npc.rotationYawHead = f7;
        if (npc != null) {
            npc.ais.orientation = orientation;
        }
        GlStateManager.popMatrix();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();

        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

}

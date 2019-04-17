package noppes.npcs.client.gui.util;

import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import noppes.npcs.entity.EntityNPCInterface;
import org.lwjgl.input.Mouse;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class GuiNPCInterface extends GuiScreen {
    public EntityPlayerSP player;
    public boolean drawDefaultBackground = true;
    public EntityNPCInterface npc;
    private Map<Integer, GuiNpcButton> buttons = new ConcurrentHashMap<>();
    private Map<Integer, GuiMenuTopButton> topbuttons = new ConcurrentHashMap<>();
    private Map<Integer, GuiMenuSideButton> sidebuttons = new ConcurrentHashMap<>();
    private Map<Integer, GuiNpcTextField> textfields = new ConcurrentHashMap<>();
    private Map<Integer, GuiNpcLabel> labels = new ConcurrentHashMap<>();
    private Map<Integer, GuiCustomScroll> scrolls = new ConcurrentHashMap<>();
    private Map<Integer, GuiNpcSlider> sliders = new ConcurrentHashMap<>();
    private Map<Integer, GuiScreen> extra = new ConcurrentHashMap<>();
    private List<IGui> components = new ArrayList<>();
    public String title;
    public ResourceLocation background = null;
    public boolean closeOnEsc = false;
    public int guiLeft, guiTop, xSize, ySize;
    private SubGuiInterface subgui;
    public int mouseX, mouseY;

    public float bgScale = 1;

    private GuiButton selectedButton;

    public GuiNPCInterface(EntityNPCInterface npc) {
        this.player = Minecraft.getMinecraft().player;
        this.npc = npc;
        title = "";
        xSize = 200;
        ySize = 222;
        this.drawDefaultBackground = false;
        this.mc = Minecraft.getMinecraft();
        this.itemRender = mc.getRenderItem();
        this.fontRenderer = mc.fontRenderer;
    }

    public GuiNPCInterface() {
        this(null);
    }

    public void setBackground(String texture) {
        background = new ResourceLocation("customnpcs", "textures/gui/" + texture);
    }

    public ResourceLocation getResource(String texture) {
        return new ResourceLocation("customnpcs", "textures/gui/" + texture);
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
        if (subgui != null) {
            subgui.setWorldAndResolution(mc, width, height);
            subgui.initGui();
        }
        guiLeft = (width - xSize) / 2;
        guiTop = (height - ySize) / 2;
        buttonList = Lists.newArrayList();
        buttons = new ConcurrentHashMap<>();
        topbuttons = new ConcurrentHashMap<>();
        sidebuttons = new ConcurrentHashMap<>();
        textfields = new ConcurrentHashMap<>();
        labels = new ConcurrentHashMap<>();
        scrolls = new ConcurrentHashMap<>();
        sliders = new ConcurrentHashMap<>();
        extra = new ConcurrentHashMap<>();
        components = new ArrayList<>();
    }

    @Override
    public void updateScreen() {
        if (subgui != null)
            subgui.updateScreen();
        else {
            for (GuiNpcTextField tf : new ArrayList<>(textfields.values())) {
                if (tf.enabled)
                    tf.updateCursorCounter();
            }

            for (IGui comp : new ArrayList<>(components)) {
                comp.updateScreen();
            }

            super.updateScreen();
        }
    }


    public void addExtra(GuiHoverText gui) {
        gui.setWorldAndResolution(mc, 350, 250);
        extra.put(gui.id, gui);
    }

    @Override
    public void mouseClicked(int i, int j, int k) {
        if (subgui != null)
            subgui.mouseClicked(i, j, k);
        else {
            for (GuiNpcTextField tf : new ArrayList<>(textfields.values())) {
                if (tf.enabled)
                    tf.mouseClicked(i, j, k);
            }

            for (IGui comp : new ArrayList<>(components)) {
                if (comp instanceof IMouseListener) {
                    ((IMouseListener) comp).mouseClicked(i, j, k);
                }
            }

            mouseEvent(i, j, k);
            if (k == 0) {
                for (GuiCustomScroll scroll : new ArrayList<>(scrolls.values())) {
                    scroll.mouseClicked(i, j, k);
                }
                for (GuiButton guibutton : this.buttonList) {
                    if (guibutton.mousePressed(this.mc, mouseX, mouseY)) {
                        net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent.Pre event = new net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent.Pre(this, guibutton, this.buttonList);
                        if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event))
                            break;
                        guibutton = event.getButton();
                        this.selectedButton = guibutton;
                        guibutton.playPressSound(this.mc.getSoundHandler());
                        this.actionPerformed(guibutton);
                        if (this.equals(this.mc.currentScreen))
                            net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent.Post(this, event.getButton(), this.buttonList));
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        if (this.selectedButton != null && state == 0) {
            this.selectedButton.mouseReleased(mouseX, mouseY);
            this.selectedButton = null;
        }
    }

    public void mouseEvent(int i, int j, int k) {
    }

    @Override
    protected void actionPerformed(GuiButton guibutton) {
        if (subgui != null)
            subgui.buttonEvent(guibutton);
        else {
            buttonEvent(guibutton);
        }
    }

    public void buttonEvent(GuiButton guibutton) {
    }

    @Override
    public void keyTyped(char c, int i) {
        if (subgui != null) {
            subgui.keyTyped(c, i);
            return;
        }

        boolean active = false;
        for (IGui gui : components) {
            if (gui.isActive()) {
                active = true;
                break;
            }
        }

        active = active || GuiNpcTextField.isActive();

        if (closeOnEsc && (i == 1 || !active && isInventoryKey(i))) {
            close();
            return;
        }
        for (GuiNpcTextField tf : new ArrayList<>(textfields.values())) {
            tf.textboxKeyTyped(c, i);
        }

        for (IGui comp : new ArrayList<>(components)) {
            if (comp instanceof IKeyListener) {
                ((IKeyListener) comp).keyTyped(c, i);
            }
        }
    }

    @Override
    public void onGuiClosed() {
        GuiNpcTextField.unfocus();
    }

    public void close() {
        displayGuiScreen(null);
        mc.setIngameFocus();
        save();
    }

    public void addButton(GuiNpcButton button) {
        buttons.put(button.id, button);
        buttonList.add(button);
    }

    public void addTopButton(GuiMenuTopButton button) {
        topbuttons.put(button.id, button);
        buttonList.add(button);
    }

    public void addSideButton(GuiMenuSideButton button) {
        sidebuttons.put(button.id, button);
        buttonList.add(button);
    }

    public GuiNpcButton getButton(int i) {
        return buttons.get(i);
    }

    public GuiMenuSideButton getSideButton(int i) {
        return sidebuttons.get(i);
    }

    public GuiMenuTopButton getTopButton(int i) {
        return topbuttons.get(i);
    }

    public void addTextField(GuiNpcTextField tf) {
        textfields.put(tf.id, tf);
    }

    public GuiNpcTextField getTextField(int i) {
        return textfields.get(i);
    }

    public void add(IGui gui) {
        components.add(gui);
    }

    public IGui get(int id) {
        for (IGui comp : components) {
            if (comp.getID() == id)
                return comp;
        }
        return null;
    }

    public void addLabel(GuiNpcLabel label) {
        labels.put(label.id, label);
    }

    public GuiNpcLabel getLabel(int i) {
        return labels.get(i);
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

    public abstract void save();

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        int x = mouseX;
        int y = mouseY;
        if (subgui != null) {
            x = y = 0;
        }
        if (drawDefaultBackground && subgui == null)
            drawDefaultBackground();

        if (background != null && mc.renderEngine != null) {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.pushMatrix();
            GlStateManager.translate(guiLeft, guiTop, 0);
            GlStateManager.scale(bgScale, bgScale, bgScale);
            mc.renderEngine.bindTexture(background);
            if (xSize > 256) {
                drawTexturedModalRect(0, 0, 0, 0, 250, ySize);
                drawTexturedModalRect(250, 0, 256 - (xSize - 250), 0, xSize - 250, ySize);
            } else
                drawTexturedModalRect(0, 0, 0, 0, xSize, ySize);
            GlStateManager.popMatrix();
        }

        drawCenteredString(fontRenderer, title, width / 2, 8, 0xffffff);
        for (GuiNpcLabel label : new ArrayList<>(labels.values()))
            label.drawLabel(this, fontRenderer);
        for (GuiNpcTextField tf : new ArrayList<>(textfields.values())) {
            tf.drawTextBox(x, y);
        }
        for (IGui comp : new ArrayList<>(components)) {
            comp.drawScreen(x, y);
        }
        for (GuiCustomScroll scroll : new ArrayList<>(scrolls.values()))
            scroll.drawScreen(x, y, partialTicks, !hasSubGui() && scroll.isMouseOver(x, y) ? Mouse.getDWheel() : 0);
        for (GuiScreen gui : new ArrayList<>(extra.values()))
            gui.drawScreen(x, y, partialTicks);
        super.drawScreen(x, y, partialTicks);
        if (subgui != null)
            subgui.drawScreen(mouseX, mouseY, partialTicks);
    }

    public FontRenderer getFontRenderer() {
        return this.fontRenderer;
    }

    public void elementClicked() {
        if (subgui != null)
            subgui.elementClicked();
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    public void doubleClicked() {
    }

    public boolean isInventoryKey(int i) {
        return i == mc.gameSettings.keyBindInventory.getKeyCode(); //inventory key
    }

    @Override
    public void drawDefaultBackground() {
        super.drawDefaultBackground();
    }

    public void displayGuiScreen(GuiScreen gui) {
        mc.displayGuiScreen(gui);
    }

    public void setSubGui(SubGuiInterface gui) {
        subgui = gui;
        subgui.npc = npc;
        subgui.setWorldAndResolution(mc, width, height);
        subgui.parent = this;
        initGui();
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

    public void drawNpc(int x, int y) {
        drawNpc(npc, x, y, 1, 0);
    }

    public void drawNpc(EntityLivingBase entity, int x, int y, float zoomed, int rotation) {
        EntityNPCInterface npc = null;
        if (entity instanceof EntityNPCInterface)
            npc = (EntityNPCInterface) entity;
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        GlStateManager.enableColorMaterial();
        GlStateManager.pushMatrix();
        GlStateManager.translate(guiLeft + x, guiTop + y, 50F);
        float scale = 1;
        if (entity.height > 2.4)
            scale = 2 / entity.height;

        GlStateManager.scale(-30 * scale * zoomed, 30 * scale * zoomed, 30 * scale * zoomed);
        GlStateManager.rotate(180F, 0.0F, 0.0F, 1.0F);
        //GlStateManager.disableLighting();
        RenderHelper.enableStandardItemLighting();

        float f2 = entity.renderYawOffset;
        float f3 = entity.rotationYaw;
        float f4 = entity.rotationPitch;
        float f7 = entity.rotationYawHead;
        float f5 = (float) (guiLeft + x) - mouseX;
        float f6 = (guiTop + y) - 50 * scale * zoomed - mouseY;
        int orientation = 0;
        if (npc != null) {
            orientation = npc.ais.orientation;
            npc.ais.orientation = rotation;
        }
        GlStateManager.rotate(135F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-135F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-(float) Math.atan(f6 / 40F) * 20F, 1.0F, 0.0F, 0.0F);
        entity.renderYawOffset = rotation;
        entity.rotationYaw = (float) Math.atan(f5 / 80F) * 40F + rotation;
        entity.rotationPitch = -(float) Math.atan(f6 / 40F) * 20F;
        entity.rotationYawHead = entity.rotationYaw;
        mc.getRenderManager().playerViewY = 180F;
        mc.getRenderManager().renderEntity(entity, 0, 0, 0, 0, 1, false);
        entity.prevRenderYawOffset = entity.renderYawOffset = f2;
        entity.prevRotationYaw = entity.rotationYaw = f3;
        entity.prevRotationPitch = entity.rotationPitch = f4;
        entity.prevRotationYawHead = entity.rotationYawHead = f7;
        if (npc != null) {
            npc.ais.orientation = orientation;
        }
        GlStateManager.popMatrix();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
    }

    public void openLink(String link) {
        try {
            Class oclass = Class.forName("java.awt.Desktop");
            Object object = oclass.getMethod("getDesktop", new Class[0]).invoke(null);
            oclass.getMethod("browse", new Class[]{URI.class}).invoke(object, new URI(link));
        } catch (Throwable throwable) {
        }
    }
}

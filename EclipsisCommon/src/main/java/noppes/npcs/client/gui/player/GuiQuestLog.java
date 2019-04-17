package noppes.npcs.client.gui.player;

import micdoodle8.mods.galacticraft.api.client.tabs.InventoryTabQuests;
import micdoodle8.mods.galacticraft.api.client.tabs.TabRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.translation.I18n;
import noppes.npcs.api.handler.data.IQuestObjective;
import noppes.npcs.client.CustomNpcResourceListener;
import noppes.npcs.client.NoppesUtil;
import noppes.npcs.client.TextBlockClient;
import noppes.npcs.client.gui.util.*;
import noppes.npcs.controllers.PlayerQuestController;
import noppes.npcs.controllers.data.Quest;
import noppes.npcs.util.NaturalOrderComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class GuiQuestLog extends GuiNPCInterface implements ITopButtonListener, ICustomScrollListener {

    private final ResourceLocation resource = new ResourceLocation("customnpcs", "textures/gui/standardbg.png");

    public HashMap<String, List<Quest>> activeQuests = new HashMap<String, List<Quest>>();
    private HashMap<String, Quest> categoryQuests = new HashMap<String, Quest>();
    public Quest selectedQuest = null;
    public String selectedCategory = "";
    private EntityPlayer player;
    private GuiCustomScroll scroll;
    private HashMap<Integer, GuiMenuSideButton> sideButtons = new HashMap<Integer, GuiMenuSideButton>();
    private boolean noQuests = false;

    private final int maxLines = 10;
    private int currentPage = 0;
    private int maxPages = 1;

    TextBlockClient textblock = null;

    private Minecraft mc = Minecraft.getMinecraft();

    public GuiQuestLog(EntityPlayer player) {
        super();
        this.player = player;
        xSize = 280;
        ySize = 180;
        drawDefaultBackground = false;
    }

    @Override
    public void initGui() {
        super.initGui();
        for (Quest quest : PlayerQuestController.getActiveQuests(player)) {
            String category = quest.category.title;
            if (!activeQuests.containsKey(category))
                activeQuests.put(category, new ArrayList<Quest>());
            List<Quest> list = activeQuests.get(category);
            list.add(quest);
        }

        sideButtons.clear();
        guiTop += 10;

        TabRegistry.updateTabValues(guiLeft, guiTop, InventoryTabQuests.class);
        TabRegistry.addTabsToList(buttonList);

        noQuests = false;

        if (activeQuests.isEmpty()) {
            noQuests = true;
            return;
        }
        List<String> categories = new ArrayList<String>();
        categories.addAll(activeQuests.keySet());
        Collections.sort(categories, new NaturalOrderComparator());
        int i = 0;
        for (String category : categories) {
            if (selectedCategory.isEmpty())
                selectedCategory = category;
            sideButtons.put(i, new GuiMenuSideButton(i, guiLeft - 69, this.guiTop + 2 + i * 21, 70, 22, category));
            i++;
        }
        sideButtons.get(categories.indexOf(selectedCategory)).active = true;

        if (scroll == null)
            scroll = new GuiCustomScroll(this, 0);

        HashMap<String, Quest> categoryQuests = new HashMap<String, Quest>();
        for (Quest q : activeQuests.get(selectedCategory)) {
            categoryQuests.put(q.title, q);
        }
        this.categoryQuests = categoryQuests;

        scroll.setList(new ArrayList<String>(categoryQuests.keySet()));
        scroll.setSize(134, 174);
        scroll.guiLeft = guiLeft + 5;
        scroll.guiTop = guiTop + 15;
        addScroll(scroll);

        addButton(new GuiButtonNextPage(1, guiLeft + 286, guiTop + 114, true));
        addButton(new GuiButtonNextPage(2, guiLeft + 144, guiTop + 114, false));

        getButton(1).visible = selectedQuest != null && currentPage < (maxPages - 1);
        getButton(2).visible = selectedQuest != null && currentPage > 0;
    }

    @Override
    protected void actionPerformed(GuiButton guibutton) {
        if (!(guibutton instanceof GuiButtonNextPage))
            return;
        if (guibutton.id == 1) {
            currentPage++;
            initGui();
        }
        if (guibutton.id == 2) {
            currentPage--;
            initGui();
        }
    }

    @Override
    public void drawScreen(int i, int j, float f) {
        if (scroll != null)
            scroll.visible = !noQuests;
        drawDefaultBackground();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        mc.renderEngine.bindTexture(resource);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, 252, 195);
        drawTexturedModalRect(guiLeft + 252, guiTop, 188, 0, 67, 195);
        super.drawScreen(i, j, f);

        if (noQuests) {
            mc.fontRenderer.drawString(I18n.translateToLocal("quest.noquests"), guiLeft + 84, guiTop + 80, CustomNpcResourceListener.DefaultTextColor);
            return;
        }
        for (GuiMenuSideButton button : sideButtons.values().toArray(new GuiMenuSideButton[sideButtons.size()])) {
            button.drawButton(mc, i, j, f);
        }
        mc.fontRenderer.drawString(selectedCategory, guiLeft + 5, guiTop + 5, CustomNpcResourceListener.DefaultTextColor);

        if (selectedQuest == null)
            return;

        drawProgress();

        drawQuestText();

        GlStateManager.pushMatrix();
        GlStateManager.translate(guiLeft + 148, guiTop, 0);
        GlStateManager.scale(1.24f, 1.24f, 1.24f);
        String title = I18n.translateToLocal(selectedQuest.title);
        fontRenderer.drawString(title, (130 - fontRenderer.getStringWidth(title)) / 2, 4, CustomNpcResourceListener.DefaultTextColor);
        GlStateManager.popMatrix();
        drawHorizontalLine(guiLeft + 142, guiLeft + 312, guiTop + 17, +0xFF000000 + CustomNpcResourceListener.DefaultTextColor);
    }

    private void drawQuestText() {
        if (textblock == null)
            return;
        int yoffset = guiTop + 5;
        for (int i = 0; i < maxLines; i++) {
            int index = i + currentPage * maxLines;
            if (index >= textblock.lines.size())
                continue;
            String text = textblock.lines.get(index).getFormattedText();
            fontRenderer.drawString(text, guiLeft + 142, guiTop + 20 + (i * fontRenderer.FONT_HEIGHT), CustomNpcResourceListener.DefaultTextColor);
        }
    }

    private void drawProgress() {
        String title = I18n.translateToLocal("quest.objectives") + ":";
        mc.fontRenderer.drawString(title, guiLeft + 142, guiTop + 130, CustomNpcResourceListener.DefaultTextColor);
        drawHorizontalLine(guiLeft + 142, guiLeft + 312, guiTop + 140, +0xFF000000 + CustomNpcResourceListener.DefaultTextColor);

        int yoffset = guiTop + 144;
        for (IQuestObjective objective : selectedQuest.questInterface.getObjectives(player)) {
            mc.fontRenderer.drawString("- " + objective.getText(), guiLeft + 142, yoffset, CustomNpcResourceListener.DefaultTextColor);
            yoffset += 10;
        }

        drawHorizontalLine(guiLeft + 142, guiLeft + 312, guiTop + 178, +0xFF000000 + CustomNpcResourceListener.DefaultTextColor);
        String complete = selectedQuest.getNpcName();
        if (complete != null && !complete.isEmpty()) {
            mc.fontRenderer.drawString(I18n.translateToLocalFormatted("quest.completewith", complete), guiLeft + 142, guiTop + 182, CustomNpcResourceListener.DefaultTextColor);
        }
    }

    @Override
    public void mouseClicked(int i, int j, int k) {
        super.mouseClicked(i, j, k);
        if (k == 0) {
            if (scroll != null)
                scroll.mouseClicked(i, j, k);
            for (GuiMenuSideButton button : new ArrayList<GuiMenuSideButton>(sideButtons.values())) {
                if (button.mousePressed(mc, i, j)) {
                    sideButtonPressed(button);
                }
            }
        }
    }

    private void sideButtonPressed(GuiMenuSideButton button) {
        if (button.active)
            return;
        NoppesUtil.clickSound();
        selectedCategory = button.displayString;
        selectedQuest = null;
        this.initGui();
    }

    @Override
    public void scrollClicked(int i, int j, int k, GuiCustomScroll scroll) {
        if (!scroll.hasSelected())
            return;
        selectedQuest = categoryQuests.get(scroll.getSelected());
        textblock = new TextBlockClient(selectedQuest.getLogText(), 172, true, player);
        if (textblock.lines.size() > maxLines) {
            maxPages = MathHelper.ceil(1f * textblock.lines.size() / maxLines);
        }
        currentPage = 0;
        initGui();
    }

    @Override
    public void keyTyped(char c, int i) {
        if (i == 1 || i == mc.gameSettings.keyBindInventory.getKeyCode()) {
            mc.displayGuiScreen(null);
            mc.setIngameFocus();
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void save() {

    }

    @Override
    public void scrollDoubleClicked(String selection, GuiCustomScroll scroll) {
    }

}

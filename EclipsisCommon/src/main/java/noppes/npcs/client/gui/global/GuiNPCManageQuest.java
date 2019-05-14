package noppes.npcs.client.gui.global;

import com.google.common.collect.Lists;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.translation.I18n;
import noppes.npcs.client.Client;
import noppes.npcs.client.NoppesUtil;
import noppes.npcs.client.gui.SubGuiEditText;
import noppes.npcs.client.gui.util.*;
import noppes.npcs.constants.EnumPacketServer;
import noppes.npcs.controllers.QuestController;
import noppes.npcs.controllers.data.Quest;
import noppes.npcs.controllers.data.QuestCategory;
import noppes.npcs.common.entity.EntityNPCInterface;

import java.util.HashMap;

public class GuiNPCManageQuest extends GuiNPCInterface2 implements ISubGuiListener, ICustomScrollListener, GuiYesNoCallback {
    private HashMap<String, QuestCategory> categoryData = new HashMap<String, QuestCategory>();
    private HashMap<String, Quest> questData = new HashMap<String, Quest>();

    private GuiCustomScroll scrollCategories;
    private GuiCustomScroll scrollQuests;

    public static GuiScreen Instance;

    private QuestCategory selectedCategory;
    private Quest selectedQuest;

    public GuiNPCManageQuest(EntityNPCInterface npc) {
        super(npc);
        Instance = this;
    }

    @Override
    public void initGui() {
        super.initGui();
        this.addLabel(new GuiNpcLabel(0, "gui.categories", guiLeft + 8, guiTop + 4));
        this.addLabel(new GuiNpcLabel(1, "quest.quests", guiLeft + 175, guiTop + 4));

        this.addLabel(new GuiNpcLabel(3, "quest.quests", guiLeft + 356, guiTop + 8));
        this.addButton(new GuiNpcButton(13, guiLeft + 356, guiTop + 18, 58, 20, "selectServer.edit", selectedQuest != null));
        this.addButton(new GuiNpcButton(12, guiLeft + 356, guiTop + 41, 58, 20, "gui.remove", selectedQuest != null));
        this.addButton(new GuiNpcButton(11, guiLeft + 356, guiTop + 64, 58, 20, "gui.add", selectedCategory != null));

        this.addLabel(new GuiNpcLabel(2, "gui.categories", guiLeft + 356, guiTop + 110));
        this.addButton(new GuiNpcButton(3, guiLeft + 356, guiTop + 120, 58, 20, "selectServer.edit", selectedCategory != null));
        this.addButton(new GuiNpcButton(2, guiLeft + 356, guiTop + 143, 58, 20, "gui.remove", selectedCategory != null));
        this.addButton(new GuiNpcButton(1, guiLeft + 356, guiTop + 166, 58, 20, "gui.add"));

        HashMap<String, QuestCategory> categoryData = new HashMap<String, QuestCategory>();
        HashMap<String, Quest> questData = new HashMap<String, Quest>();

        for (QuestCategory category : QuestController.instance.categories.values()) {
            categoryData.put(category.title, category);
        }
        this.categoryData = categoryData;

        if (selectedCategory != null) {
            for (Quest quest : selectedCategory.quests.values()) {
                questData.put(quest.title, quest);
            }
        }
        this.questData = questData;

        if (scrollCategories == null) {
            scrollCategories = new GuiCustomScroll(this, 0);
            scrollCategories.setSize(170, 200);
        }
        scrollCategories.setList(Lists.newArrayList(categoryData.keySet()));

        scrollCategories.guiLeft = guiLeft + 4;
        scrollCategories.guiTop = guiTop + 14;
        this.addScroll(scrollCategories);

        if (scrollQuests == null) {
            scrollQuests = new GuiCustomScroll(this, 1);
            scrollQuests.setSize(170, 200);
        }
        scrollQuests.setList(Lists.newArrayList(questData.keySet()));
        scrollQuests.guiLeft = guiLeft + 175;
        scrollQuests.guiTop = guiTop + 14;
        this.addScroll(scrollQuests);

    }

    @Override
    public void buttonEvent(GuiButton guibutton) {
        GuiNpcButton button = (GuiNpcButton) guibutton;

        if (button.id == 1) {
            setSubGui(new SubGuiEditText(1, I18n.translateToLocal("gui.new")));
        }
        if (button.id == 2) {
            GuiYesNo guiyesno = new GuiYesNo(this, selectedCategory.title, I18n.translateToLocal("gui.deleteMessage"), 2);
            displayGuiScreen(guiyesno);
        }
        if (button.id == 3) {
            setSubGui(new SubGuiEditText(3, selectedCategory.title));
        }
        if (button.id == 11) {
            setSubGui(new SubGuiEditText(11, I18n.translateToLocal("gui.new")));
        }
        if (button.id == 12) {
            GuiYesNo guiyesno = new GuiYesNo(this, selectedQuest.title, I18n.translateToLocal("gui.deleteMessage"), 12);
            displayGuiScreen(guiyesno);
        }
        if (button.id == 13) {
            setSubGui(new GuiQuestEdit(selectedQuest));
        }
    }


    @Override
    public void subGuiClosed(SubGuiInterface subgui) {
        if (subgui instanceof SubGuiEditText && ((SubGuiEditText) subgui).cancelled) {
            return;
        }
        if (subgui.id == 1) {
            QuestCategory category = new QuestCategory();
            category.title = ((SubGuiEditText) subgui).text;
            while (QuestController.instance.containsCategoryName(category)) {
                category.title += "_";
            }
            Client.sendData(EnumPacketServer.QuestCategorySave, category.writeNBT(new NBTTagCompound()));
        }
        if (subgui.id == 3) {
            selectedCategory.title = ((SubGuiEditText) subgui).text;
            while (QuestController.instance.containsCategoryName(selectedCategory)) {
                selectedCategory.title += "_";
            }
            Client.sendData(EnumPacketServer.QuestCategorySave, selectedCategory.writeNBT(new NBTTagCompound()));
        }
        if (subgui.id == 11) {
            Quest quest = new Quest(selectedCategory);
            quest.title = ((SubGuiEditText) subgui).text;
            while (QuestController.instance.containsQuestName(selectedCategory, quest)) {
                quest.title += "_";
            }
            Client.sendData(EnumPacketServer.QuestSave, selectedCategory.id, quest.writeToNBT(new NBTTagCompound()));
        }
        if (subgui instanceof GuiQuestEdit) {
            initGui();
        }
    }

    @Override
    public void scrollClicked(int i, int j, int k, GuiCustomScroll guiCustomScroll) {
        if (guiCustomScroll.id == 0) {
            selectedCategory = categoryData.get(scrollCategories.getSelected());
            selectedQuest = null;
            scrollQuests.selected = -1;
        }
        if (guiCustomScroll.id == 1) {
            selectedQuest = questData.get(scrollQuests.getSelected());
        }
        initGui();
    }

    @Override
    public void scrollDoubleClicked(String selection, GuiCustomScroll scroll) {
        if (selectedQuest != null && scroll.id == 1) {
            setSubGui(new GuiQuestEdit(selectedQuest));
        }
    }

    @Override
    public void close() {
        super.close();
    }

    @Override
    public void save() {
        GuiNpcTextField.unfocus();
    }

    public void confirmClicked(boolean result, int id) {
        NoppesUtil.openGUI(player, this);
        if (!result)
            return;
        if (id == 2) {
            Client.sendData(EnumPacketServer.QuestCategoryRemove, selectedCategory.id);
        }
        if (id == 12) {
            Client.sendData(EnumPacketServer.QuestRemove, selectedQuest.id);
        }
    }

}

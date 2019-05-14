package noppes.npcs.client.gui.global;

import com.google.common.collect.Lists;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.client.Client;
import noppes.npcs.client.NoppesUtil;
import noppes.npcs.client.gui.SubGuiEditText;
import noppes.npcs.client.gui.util.*;
import noppes.npcs.constants.EnumPacketServer;
import noppes.npcs.controllers.DialogController;
import noppes.npcs.controllers.data.Dialog;
import noppes.npcs.controllers.data.DialogCategory;
import noppes.npcs.common.entity.EntityNPCInterface;

import java.util.HashMap;

public class GuiNPCManageDialogs extends GuiNPCInterface2 implements ISubGuiListener, ICustomScrollListener, GuiYesNoCallback {
    public static GuiScreen Instance;
    private HashMap<String, DialogCategory> categoryData = new HashMap<>();
    private HashMap<String, Dialog> dialogData = new HashMap<>();
    private GuiCustomScroll scrollCategories;
    private GuiCustomScroll scrollDialogs;
    private DialogCategory selectedCategory;
    private Dialog selectedDialog;

    public GuiNPCManageDialogs(EntityNPCInterface npc) {
        super(npc);
        Instance = this;
    }

    @Override
    public void initGui() {
        super.initGui();
        this.addLabel(new GuiNpcLabel(0, "gui.categories", guiLeft + 8, guiTop + 4));
        this.addLabel(new GuiNpcLabel(1, "dialog.dialogs", guiLeft + 175, guiTop + 4));

        this.addLabel(new GuiNpcLabel(3, "dialog.dialogs", guiLeft + 356, guiTop + 8));
        this.addButton(new GuiNpcButton(13, guiLeft + 356, guiTop + 18, 58, 20, "selectServer.edit", selectedDialog != null));
        this.addButton(new GuiNpcButton(12, guiLeft + 356, guiTop + 41, 58, 20, "gui.remove", selectedDialog != null));
        this.addButton(new GuiNpcButton(11, guiLeft + 356, guiTop + 64, 58, 20, "gui.add", selectedCategory != null));

        this.addLabel(new GuiNpcLabel(2, "gui.categories", guiLeft + 356, guiTop + 110));
        this.addButton(new GuiNpcButton(3, guiLeft + 356, guiTop + 120, 58, 20, "selectServer.edit", selectedCategory != null));
        this.addButton(new GuiNpcButton(2, guiLeft + 356, guiTop + 143, 58, 20, "gui.remove", selectedCategory != null));
        this.addButton(new GuiNpcButton(1, guiLeft + 356, guiTop + 166, 58, 20, "gui.add"));

        HashMap<String, DialogCategory> categoryData = new HashMap<>();
        HashMap<String, Dialog> dialogData = new HashMap<>();

        for (DialogCategory category : DialogController.instance.categories.values()) {
            categoryData.put(category.title, category);
        }
        this.categoryData = categoryData;

        if (selectedCategory != null) {
            for (Dialog dialog : selectedCategory.dialogs.values()) {
                dialogData.put(dialog.title, dialog);
            }
        }
        this.dialogData = dialogData;

        if (scrollCategories == null) {
            scrollCategories = new GuiCustomScroll(this, 0);
            scrollCategories.setSize(170, 200);
        }
        scrollCategories.setList(Lists.newArrayList(categoryData.keySet()));

        scrollCategories.guiLeft = guiLeft + 4;
        scrollCategories.guiTop = guiTop + 14;
        this.addScroll(scrollCategories);

        if (scrollDialogs == null) {
            scrollDialogs = new GuiCustomScroll(this, 1);
            scrollDialogs.setSize(170, 200);
        }
        scrollDialogs.setList(Lists.newArrayList(dialogData.keySet()));
        scrollDialogs.guiLeft = guiLeft + 175;
        scrollDialogs.guiTop = guiTop + 14;
        this.addScroll(scrollDialogs);

    }

    @Override
    public void buttonEvent(GuiButton guibutton) {
        GuiNpcButton button = (GuiNpcButton) guibutton;

        if (button.id == 1) {
            setSubGui(new SubGuiEditText(1, I18n.format("gui.new")));
        }
        if (button.id == 2) {
            GuiYesNo guiyesno = new GuiYesNo(this, selectedCategory.title, I18n.format("gui.deleteMessage"), 2);
            displayGuiScreen(guiyesno);
        }
        if (button.id == 3) {
            setSubGui(new SubGuiEditText(3, selectedCategory.title));
        }
        if (button.id == 11) {
            setSubGui(new SubGuiEditText(11, I18n.format("gui.new")));
        }
        if (button.id == 12) {
            GuiYesNo guiyesno = new GuiYesNo(this, selectedDialog.title, I18n.format("gui.deleteMessage"), 12);
            displayGuiScreen(guiyesno);
        }
        if (button.id == 13) {
            setSubGui(new GuiDialogEdit(selectedDialog));
        }
    }


    @Override
    public void subGuiClosed(SubGuiInterface subgui) {
        if (subgui instanceof SubGuiEditText && ((SubGuiEditText) subgui).cancelled) {
            return;
        }
        if (subgui.id == 1) {
            DialogCategory category = new DialogCategory();
            category.title = ((SubGuiEditText) subgui).text;
            while (DialogController.instance.containsCategoryName(category)) {
                category.title += "_";
            }
            Client.sendData(EnumPacketServer.DialogCategorySave, category.writeNBT(new NBTTagCompound()));
        }
        if (subgui.id == 3) {
            selectedCategory.title = ((SubGuiEditText) subgui).text;
            while (DialogController.instance.containsCategoryName(selectedCategory)) {
                selectedCategory.title += "_";
            }
            Client.sendData(EnumPacketServer.DialogCategorySave, selectedCategory.writeNBT(new NBTTagCompound()));
        }
        if (subgui.id == 11) {
            Dialog dialog = new Dialog(selectedCategory);
            dialog.title = ((SubGuiEditText) subgui).text;
            while (DialogController.instance.containsDialogName(selectedCategory, dialog)) {
                dialog.title += "_";
            }
            Client.sendData(EnumPacketServer.DialogSave, selectedCategory.id, dialog.writeToNBT(new NBTTagCompound()));
        }
        if (subgui instanceof GuiDialogEdit) {
            initGui();
        }
    }

    @Override
    public void scrollClicked(int i, int j, int k, GuiCustomScroll guiCustomScroll) {
        if (guiCustomScroll.id == 0) {
            selectedCategory = categoryData.get(scrollCategories.getSelected());
            selectedDialog = null;
            scrollDialogs.selected = -1;
        }
        if (guiCustomScroll.id == 1) {
            selectedDialog = dialogData.get(scrollDialogs.getSelected());
        }
        initGui();
    }

    @Override
    public void scrollDoubleClicked(String selection, GuiCustomScroll scroll) {
        if (selectedDialog != null && scroll.id == 1) {
            setSubGui(new GuiDialogEdit(selectedDialog));
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

    @Override
    public void confirmClicked(boolean result, int id) {
        NoppesUtil.openGUI(player, this);
        if (!result)
            return;
        if (id == 2) {
            Client.sendData(EnumPacketServer.DialogCategoryRemove, selectedCategory.id);
        }
        if (id == 12) {
            Client.sendData(EnumPacketServer.DialogRemove, selectedDialog.id);
        }
    }

}

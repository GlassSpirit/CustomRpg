package noppes.npcs.client.gui.global;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.client.Client;
import noppes.npcs.client.gui.*;
import noppes.npcs.client.gui.select.GuiQuestSelection;
import noppes.npcs.client.gui.select.GuiSoundSelection;
import noppes.npcs.client.gui.util.*;
import noppes.npcs.constants.EnumPacketServer;
import noppes.npcs.controllers.DialogController;
import noppes.npcs.controllers.data.Dialog;
import noppes.npcs.controllers.data.PlayerMail;

public class GuiDialogEdit extends SubGuiInterface implements ISubGuiListener, ITextfieldListener {
    private Dialog dialog;

    public GuiDialogEdit(Dialog dialog) {
        this.dialog = dialog;
        setBackground("menubg.png");
        xSize = 386;
        ySize = 226;
        closeOnEsc = true;
    }

    @Override
    public void initGui() {
        super.initGui();

        addLabel(new GuiNpcLabel(1, "gui.title", guiLeft + 4, guiTop + 8));
        addTextField(new GuiNpcTextField(1, this, this.fontRenderer, guiLeft + 46, guiTop + 3, 220, 20, dialog.title));

        addLabel(new GuiNpcLabel(0, "ID", guiLeft + 268, guiTop + 4));
        addLabel(new GuiNpcLabel(2, dialog.id + "", guiLeft + 268, guiTop + 14));

        addLabel(new GuiNpcLabel(3, "dialog.dialogtext", guiLeft + 4, guiTop + 30));
        addButton(new GuiNpcButton(3, guiLeft + 120, guiTop + 25, 50, 20, "selectServer.edit"));

        addLabel(new GuiNpcLabel(4, "availability.options", guiLeft + 4, guiTop + 51));
        addButton(new GuiNpcButton(4, guiLeft + 120, guiTop + 46, 50, 20, "selectServer.edit"));

        addLabel(new GuiNpcLabel(5, "faction.options", guiLeft + 4, guiTop + 72));
        addButton(new GuiNpcButton(5, guiLeft + 120, guiTop + 67, 50, 20, "selectServer.edit"));

        addLabel(new GuiNpcLabel(6, "dialog.options", guiLeft + 4, guiTop + 93));
        addButton(new GuiNpcButton(6, guiLeft + 120, guiTop + 89, 50, 20, "selectServer.edit"));

        addButton(new GuiNpcButton(7, guiLeft + 4, guiTop + 114, 144, 20, "availability.selectquest"));
        addButton(new GuiNpcButton(8, guiLeft + 150, guiTop + 114, 20, 20, "X"));
        if (dialog.hasQuest()) {
            getButton(7).setDisplayText(dialog.getQuest().title);
        }

        addLabel(new GuiNpcLabel(9, "gui.selectSound", guiLeft + 4, guiTop + 138));
        addTextField(new GuiNpcTextField(2, this, fontRenderer, guiLeft + 4, guiTop + 148, 264, 20, dialog.sound));
        addButton(new GuiNpcButton(9, guiLeft + 270, guiTop + 148, 60, 20, "mco.template.button.select"));

        addButton(new GuiNpcButton(13, guiLeft + 4, guiTop + 172, 164, 20, "mailbox.setup"));
        addButton(new GuiNpcButton(14, guiLeft + 170, guiTop + 172, 20, 20, "X"));
        if (!dialog.mail.subject.isEmpty())
            getButton(13).setDisplayText(dialog.mail.subject);

        int y = guiTop + 4;
        addButton(new GuiNpcButton(10, guiLeft + 330, y += 22, 50, 20, "selectServer.edit"));
        addLabel(new GuiNpcLabel(10, "advMode.command", guiLeft + 214, y + 5));

        addButton(new GuiNpcButtonYesNo(11, guiLeft + 330, y += 22, dialog.hideNPC));
        addLabel(new GuiNpcLabel(11, "dialog.hideNPC", guiLeft + 214, y + 5));

        addButton(new GuiNpcButtonYesNo(12, guiLeft + 330, y += 22, dialog.showWheel));
        addLabel(new GuiNpcLabel(12, "dialog.showWheel", guiLeft + 214, y + 5));

        addButton(new GuiNpcButtonYesNo(15, guiLeft + 330, y += 22, dialog.disableEsc));
        addLabel(new GuiNpcLabel(15, "dialog.disableEsc", guiLeft + 214, y + 5));

        addButton(new GuiNpcButton(66, guiLeft + 362, guiTop + 4, 20, 20, "X"));
    }

    @Override
    public void buttonEvent(GuiButton guibutton) {
        int id = guibutton.id;
        GuiNpcButton button = (GuiNpcButton) guibutton;
        if (id == 3) {
            setSubGui(new SubGuiNpcTextArea(dialog.text));
        }
        if (id == 4) {
            setSubGui(new SubGuiNpcAvailability(dialog.availability));
        }
        if (id == 5) {
            setSubGui(new SubGuiNpcFactionOptions(dialog.factionOptions));
        }
        if (id == 6) {
            setSubGui(new SubGuiNpcDialogOptions(dialog));
        }
        if (id == 7) {
            setSubGui(new GuiQuestSelection(dialog.quest));
        }
        if (id == 8) {
            dialog.quest = -1;
            initGui();
        }
        if (id == 9) {
            setSubGui(new GuiSoundSelection(getTextField(2).getText()));
        }
        if (id == 10) {
            setSubGui(new SubGuiNpcCommand(dialog.command));
        }
        if (id == 11) {
            dialog.hideNPC = button.getValue() == 1;
        }
        if (id == 12) {
            dialog.showWheel = button.getValue() == 1;
        }
        if (id == 15) {
            dialog.disableEsc = button.getValue() == 1;
        }
        if (id == 13) {
            setSubGui(new SubGuiMailmanSendSetup(dialog.mail));
        }
        if (id == 14) {
            dialog.mail = new PlayerMail();
            initGui();
        }
        if (id == 66) {
            close();
        }
    }

    @Override
    public void unFocused(GuiNpcTextField guiNpcTextField) {
        if (guiNpcTextField.id == 1) {
            dialog.title = guiNpcTextField.getText();
            while (DialogController.instance.containsDialogName(dialog.category, dialog)) {
                dialog.title += "_";
            }
        }
        if (guiNpcTextField.id == 2) {
            dialog.sound = guiNpcTextField.getText();
        }

    }

    @Override
    public void subGuiClosed(SubGuiInterface subgui) {
        if (subgui instanceof SubGuiNpcTextArea) {
            SubGuiNpcTextArea gui = (SubGuiNpcTextArea) subgui;
            dialog.text = gui.text;
        }
        if (subgui instanceof SubGuiNpcDialogOption) {
            setSubGui(new SubGuiNpcDialogOptions(dialog));
        }
        if (subgui instanceof SubGuiNpcCommand) {
            dialog.command = ((SubGuiNpcCommand) subgui).command;
        }
        if (subgui instanceof GuiQuestSelection) {
            GuiQuestSelection gqs = (GuiQuestSelection) subgui;
            if (gqs.selectedQuest != null) {
                dialog.quest = gqs.selectedQuest.id;
                initGui();
            }
        }
        if (subgui instanceof GuiSoundSelection) {
            GuiSoundSelection gss = (GuiSoundSelection) subgui;
            if (gss.selectedResource != null) {
                getTextField(2).setText(gss.selectedResource.toString());
                unFocused(getTextField(2));
            }
        }
    }

    @Override
    public void save() {
        GuiNpcTextField.unfocus();
        Client.sendData(EnumPacketServer.DialogSave, dialog.category.id, dialog.writeToNBT(new NBTTagCompound()));
    }

}

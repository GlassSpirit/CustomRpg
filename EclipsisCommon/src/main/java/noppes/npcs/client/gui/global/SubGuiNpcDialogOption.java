package noppes.npcs.client.gui.global;

import net.minecraft.client.gui.GuiButton;
import noppes.npcs.api.constants.OptionType;
import noppes.npcs.client.gui.SubGuiColorSelector;
import noppes.npcs.client.gui.select.GuiDialogSelection;
import noppes.npcs.client.gui.util.*;
import noppes.npcs.controllers.DialogController;
import noppes.npcs.controllers.data.Dialog;
import noppes.npcs.controllers.data.DialogOption;

public class SubGuiNpcDialogOption extends SubGuiInterface implements ITextfieldListener, ISubGuiListener {
    private DialogOption option;
    public static int LastColor = 0xe0e0e0;

    public SubGuiNpcDialogOption(DialogOption option) {
        this.option = option;
        setBackground("menubg.png");
        xSize = 256;
        ySize = 216;
        closeOnEsc = true;
    }

    @Override
    public void initGui() {
        super.initGui();
        this.addLabel(new GuiNpcLabel(66, "dialog.editoption", guiLeft, guiTop + 4));
        this.getLabel(66).center(xSize);

        this.addLabel(new GuiNpcLabel(0, "gui.title", guiLeft + 4, guiTop + 20));
        this.addTextField(new GuiNpcTextField(0, this, this.fontRenderer, guiLeft + 40, guiTop + 15, 196, 20, option.title));

        String color = Integer.toHexString(option.optionColor);
        while (color.length() < 6)
            color = 0 + color;
        addLabel(new GuiNpcLabel(2, "gui.color", guiLeft + 4, guiTop + 45));
        this.addButton(new GuiNpcButton(2, guiLeft + 62, guiTop + 40, 92, 20, color));
        this.getButton(2).setTextColor(option.optionColor);

        this.addLabel(new GuiNpcLabel(1, "dialog.optiontype", guiLeft + 4, guiTop + 67));
        this.addButton(new GuiNpcButton(1, guiLeft + 62, guiTop + 62, 92, 20, new String[]{"gui.close", "dialog.dialog", "gui.disabled", "menu.role", "tile.commandBlock.name"}, option.optionType));

        if (option.optionType == OptionType.DIALOG_OPTION) {
            this.addButton(new GuiNpcButton(3, guiLeft + 4, guiTop + 84, "availability.selectdialog"));
            if (option.dialogId >= 0) {
                Dialog dialog = DialogController.instance.dialogs.get(option.dialogId);
                if (dialog != null) {
                    getButton(3).setDisplayText(dialog.title);
                }
            }
        }
        if (option.optionType == OptionType.COMMAND_BLOCK) {
            this.addTextField(new GuiNpcTextField(4, this, this.fontRenderer, guiLeft + 4, guiTop + 84, 248, 20, option.command));
            this.getTextField(4).setMaxStringLength(32767);

            this.addLabel(new GuiNpcLabel(4, "advMode.command", guiLeft + 4, guiTop + 110));
            this.addLabel(new GuiNpcLabel(5, "advMode.nearestPlayer", guiLeft + 4, guiTop + 125));
            this.addLabel(new GuiNpcLabel(6, "advMode.randomPlayer", guiLeft + 4, guiTop + 140));
            this.addLabel(new GuiNpcLabel(7, "advMode.allPlayers", guiLeft + 4, guiTop + 155));
            this.addLabel(new GuiNpcLabel(8, "dialog.commandoptionplayer", guiLeft + 4, guiTop + 170));
        }

        this.addButton(new GuiNpcButton(66, guiLeft + 82, guiTop + 190, 98, 20, "gui.done"));

    }

    @Override
    protected void actionPerformed(GuiButton guibutton) {
        GuiNpcButton button = (GuiNpcButton) guibutton;

        if (button.id == 1) {
            option.optionType = button.getValue();
            initGui();
        }
        if (button.id == 2) {
            setSubGui(new SubGuiColorSelector(option.optionColor));
        }
        if (button.id == 3) {
            setSubGui(new GuiDialogSelection(option.dialogId));
        }
        if (button.id == 66) {
            close();
        }
    }

    @Override
    public void unFocused(GuiNpcTextField textfield) {
        if (textfield.id == 0) {
            if (textfield.isEmpty())
                textfield.setText(option.title);
            else {
                option.title = textfield.getText();
            }
        }
        if (textfield.id == 4) {
            option.command = textfield.getText();
        }
    }

    @Override
    public void subGuiClosed(SubGuiInterface subgui) {
        if (subgui instanceof SubGuiColorSelector) {
            LastColor = option.optionColor = ((SubGuiColorSelector) subgui).color;
        }
        if (subgui instanceof GuiDialogSelection) {
            Dialog dialog = ((GuiDialogSelection) subgui).selectedDialog;
            if (dialog != null) {
                option.dialogId = dialog.id;
            }
        }

        initGui();
    }

}

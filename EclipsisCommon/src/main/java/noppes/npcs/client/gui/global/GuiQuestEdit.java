package noppes.npcs.client.gui.global;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.api.constants.QuestType;
import noppes.npcs.client.Client;
import noppes.npcs.client.gui.SubGuiMailmanSendSetup;
import noppes.npcs.client.gui.SubGuiNpcCommand;
import noppes.npcs.client.gui.SubGuiNpcFactionOptions;
import noppes.npcs.client.gui.SubGuiNpcTextArea;
import noppes.npcs.client.gui.questtypes.GuiNpcQuestTypeDialog;
import noppes.npcs.client.gui.questtypes.GuiNpcQuestTypeKill;
import noppes.npcs.client.gui.questtypes.GuiNpcQuestTypeLocation;
import noppes.npcs.client.gui.questtypes.GuiNpcQuestTypeManual;
import noppes.npcs.client.gui.select.GuiQuestSelection;
import noppes.npcs.client.gui.util.*;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.constants.EnumPacketServer;
import noppes.npcs.constants.EnumQuestCompletion;
import noppes.npcs.constants.EnumQuestRepeat;
import noppes.npcs.controllers.QuestController;
import noppes.npcs.controllers.data.PlayerMail;
import noppes.npcs.controllers.data.Quest;

public class GuiQuestEdit extends SubGuiInterface implements ISubGuiListener, GuiSelectionListener, ITextfieldListener {
    private Quest quest;
    private boolean questlogTA = false;

    public GuiQuestEdit(Quest quest) {
        this.quest = quest;
        setBackground("menubg.png");
        xSize = 386;
        ySize = 226;
        NoppesUtilServer.setEditingQuest(player, quest);
        closeOnEsc = true;
    }

    @Override
    public void initGui() {
        super.initGui();

        addLabel(new GuiNpcLabel(1, "gui.title", guiLeft + 4, guiTop + 8));
        addTextField(new GuiNpcTextField(1, this, this.fontRenderer, guiLeft + 46, guiTop + 3, 220, 20, quest.title));

        addLabel(new GuiNpcLabel(0, "ID", guiLeft + 268, guiTop + 4));
        addLabel(new GuiNpcLabel(2, quest.id + "", guiLeft + 268, guiTop + 14));

        addLabel(new GuiNpcLabel(3, "quest.completedtext", guiLeft + 4, guiTop + 30));
        addButton(new GuiNpcButton(3, guiLeft + 120, guiTop + 25, 50, 20, "selectServer.edit"));

        addLabel(new GuiNpcLabel(4, "quest.questlogtext", guiLeft + 4, guiTop + 51));
        addButton(new GuiNpcButton(4, guiLeft + 120, guiTop + 46, 50, 20, "selectServer.edit"));

        addLabel(new GuiNpcLabel(5, "quest.reward", guiLeft + 4, guiTop + 72));
        addButton(new GuiNpcButton(5, guiLeft + 120, guiTop + 67, 50, 20, "selectServer.edit"));

        addLabel(new GuiNpcLabel(6, "gui.type", guiLeft + 4, guiTop + 93));
        addButton(new GuiButtonBiDirectional(6, guiLeft + 70, guiTop + 88, 90, 20, new String[]{"quest.item", "quest.dialog", "quest.kill", "quest.location", "quest.areakill", "quest.manual"}, quest.type));
        addButton(new GuiNpcButton(7, guiLeft + 162, guiTop + 88, 50, 20, "selectServer.edit"));

        addLabel(new GuiNpcLabel(8, "quest.repeatable", guiLeft + 4, guiTop + 114));
        this.addButton(new GuiButtonBiDirectional(8, guiLeft + 70, guiTop + 109, 140, 20, new String[]{"gui.no", "gui.yes", "quest.mcdaily", "quest.mcweekly", "quest.rldaily", "quest.rlweekly"}, quest.repeat.ordinal()));

        this.addButton(new GuiNpcButton(9, guiLeft + 4, guiTop + 131, 90, 20, new String[]{"quest.npc", "quest.instant"}, quest.completion.ordinal()));

        if (quest.completerNpc.isEmpty())
            quest.completerNpc = npc.display.getName();

        this.addTextField(new GuiNpcTextField(2, this, this.fontRenderer, guiLeft + 96, guiTop + 131, 114, 20, quest.completerNpc));
        this.getTextField(2).enabled = quest.completion == EnumQuestCompletion.Npc;

        addLabel(new GuiNpcLabel(10, "faction.options", guiLeft + 214, guiTop + 30));
        addButton(new GuiNpcButton(10, guiLeft + 330, guiTop + 25, 50, 20, "selectServer.edit"));

        addLabel(new GuiNpcLabel(15, "advMode.command", guiLeft + 214, guiTop + 52));
        addButton(new GuiNpcButton(15, guiLeft + 330, guiTop + 47, 50, 20, "selectServer.edit"));

        addButton(new GuiNpcButton(13, guiLeft + 4, guiTop + 153, 164, 20, "mailbox.setup"));
        addButton(new GuiNpcButton(14, guiLeft + 170, guiTop + 153, 20, 20, "X"));
        if (!quest.mail.subject.isEmpty())
            getButton(13).setDisplayText(quest.mail.subject);

        addButton(new GuiNpcButton(11, guiLeft + 4, guiTop + 175, 164, 20, "quest.next"));
        addButton(new GuiNpcButton(12, guiLeft + 170, guiTop + 175, 20, 20, "X"));
        if (!quest.nextQuestTitle.isEmpty())
            getButton(11).setDisplayText(quest.nextQuestTitle);

        addButton(new GuiNpcButton(66, guiLeft + 362, guiTop + 4, 20, 20, "X"));
    }

    @Override
    public void buttonEvent(GuiButton guibutton) {
        GuiNpcButton button = (GuiNpcButton) guibutton;

        if (button.id == 3) {
            questlogTA = false;
            setSubGui(new SubGuiNpcTextArea(quest.completeText));
        }
        if (button.id == 4) {
            questlogTA = true;
            setSubGui(new SubGuiNpcTextArea(quest.logText));
        }
        if (button.id == 5) {
            Client.sendData(EnumPacketServer.QuestOpenGui, EnumGuiType.QuestReward, quest.writeToNBT(new NBTTagCompound()));
        }
        if (button.id == 6) {
            quest.setType(button.getValue());
        }
        if (button.id == 7) {
            if (quest.type == QuestType.ITEM)
                Client.sendData(EnumPacketServer.QuestOpenGui, EnumGuiType.QuestItem, quest.writeToNBT(new NBTTagCompound()));

            if (quest.type == QuestType.DIALOG)
                setSubGui(new GuiNpcQuestTypeDialog(npc, quest, parent));

            if (quest.type == QuestType.KILL)
                setSubGui(new GuiNpcQuestTypeKill(npc, quest, parent));

            if (quest.type == QuestType.LOCATION)
                setSubGui(new GuiNpcQuestTypeLocation(npc, quest, parent));

            if (quest.type == QuestType.AREA_KILL)
                setSubGui(new GuiNpcQuestTypeKill(npc, quest, parent));

            if (quest.type == QuestType.MANUAL)
                setSubGui(new GuiNpcQuestTypeManual(npc, quest, parent));
        }
        if (button.id == 8) {
            quest.repeat = EnumQuestRepeat.values()[button.getValue()];
        }
        if (button.id == 9) {
            quest.completion = EnumQuestCompletion.values()[button.getValue()];
            this.getTextField(2).enabled = quest.completion == EnumQuestCompletion.Npc;
        }
        if (button.id == 15) {
            setSubGui(new SubGuiNpcCommand(quest.command));
        }
        if (button.id == 10) {
            setSubGui(new SubGuiNpcFactionOptions(quest.factionOptions));
        }

        if (button.id == 11) {
            setSubGui(new GuiQuestSelection(quest.nextQuestid));
        }

        if (button.id == 12) {
            quest.nextQuestid = -1;
            initGui();
        }
        if (button.id == 13) {
            setSubGui(new SubGuiMailmanSendSetup(quest.mail));
        }
        if (button.id == 14) {
            quest.mail = new PlayerMail();
            initGui();
        }
        if (button.id == 66) {
            close();
        }
    }

    @Override
    public void unFocused(GuiNpcTextField guiNpcTextField) {
        if (guiNpcTextField.id == 1) {
            quest.title = guiNpcTextField.getText();
            while (QuestController.instance.containsQuestName(quest.category, quest)) {
                quest.title += "_";
            }
        }
        if (guiNpcTextField.id == 2) {
            quest.completerNpc = guiNpcTextField.getText();
        }
    }

    @Override
    public void subGuiClosed(SubGuiInterface subgui) {
        if (subgui instanceof SubGuiNpcTextArea) {
            SubGuiNpcTextArea gui = (SubGuiNpcTextArea) subgui;
            if (questlogTA)
                quest.logText = gui.text;
            else
                quest.completeText = gui.text;
        } else if (subgui instanceof SubGuiNpcCommand) {
            SubGuiNpcCommand sub = (SubGuiNpcCommand) subgui;
            quest.command = sub.command;
        } else {
            initGui();
        }
    }

    @Override
    public void selected(int id, String name) {
        quest.nextQuestid = id;
        quest.nextQuestTitle = name;
    }

    @Override
    public void close() {
        super.close();
    }

    @Override
    public void save() {
        GuiNpcTextField.unfocus();
        Client.sendData(EnumPacketServer.QuestSave, quest.category.id, quest.writeToNBT(new NBTTagCompound()));
    }
}

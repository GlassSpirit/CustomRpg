package noppes.npcs.client.gui.advanced;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.client.Client;
import noppes.npcs.client.gui.select.GuiSoundSelection;
import noppes.npcs.client.gui.util.*;
import noppes.npcs.constants.EnumPacketServer;
import noppes.npcs.entity.EntityNPCInterface;

public class GuiNPCSoundsMenu extends GuiNPCInterface2 implements ITextfieldListener, ISubGuiListener {
    private GuiSoundSelection gui;
    private GuiNpcTextField selectedField;

    public GuiNPCSoundsMenu(EntityNPCInterface npc) {
        super(npc);
    }

    @Override
    public void initGui() {
        super.initGui();
        addLabel(new GuiNpcLabel(0, "advanced.idlesound", guiLeft + 5, guiTop + 20));
        addTextField(new GuiNpcTextField(0, this, fontRenderer, guiLeft + 80, guiTop + 15, 200, 20, npc.advanced.getSound(0)));
        addButton(new GuiNpcButton(0, guiLeft + 290, guiTop + 15, 80, 20, "gui.selectSound"));

        addLabel(new GuiNpcLabel(2, "advanced.angersound", guiLeft + 5, guiTop + 45));
        addTextField(new GuiNpcTextField(2, this, fontRenderer, guiLeft + 80, guiTop + 40, 200, 20, npc.advanced.getSound(1)));
        addButton(new GuiNpcButton(2, guiLeft + 290, guiTop + 40, 80, 20, "gui.selectSound"));

        addLabel(new GuiNpcLabel(3, "advanced.hurtsound", guiLeft + 5, guiTop + 70));
        addTextField(new GuiNpcTextField(3, this, fontRenderer, guiLeft + 80, guiTop + 65, 200, 20, npc.advanced.getSound(2)));
        addButton(new GuiNpcButton(3, guiLeft + 290, guiTop + 65, 80, 20, "gui.selectSound"));

        addLabel(new GuiNpcLabel(4, "advanced.deathsound", guiLeft + 5, guiTop + 95));
        addTextField(new GuiNpcTextField(4, this, fontRenderer, guiLeft + 80, guiTop + 90, 200, 20, npc.advanced.getSound(3)));
        addButton(new GuiNpcButton(4, guiLeft + 290, guiTop + 90, 80, 20, "gui.selectSound"));

        addLabel(new GuiNpcLabel(5, "advanced.stepsound", guiLeft + 5, guiTop + 120));
        addTextField(new GuiNpcTextField(5, this, fontRenderer, guiLeft + 80, guiTop + 115, 200, 20, npc.advanced.getSound(4)));
        addButton(new GuiNpcButton(5, guiLeft + 290, guiTop + 115, 80, 20, "gui.selectSound"));

        addLabel(new GuiNpcLabel(6, "advanced.haspitch", guiLeft + 5, guiTop + 150));
        addButton(new GuiNpcButton(6, guiLeft + 120, guiTop + 145, 80, 20, new String[]{"gui.no", "gui.yes"}, npc.advanced.disablePitch ? 0 : 1));

    }

    @Override
    public void buttonEvent(GuiButton button) {
        if (button.id == 6)
            npc.advanced.disablePitch = ((GuiNpcButton) button).getValue() == 0;
        else {
            selectedField = getTextField(button.id);
            setSubGui(new GuiSoundSelection(selectedField.getText()));
        }

    }

    @Override
    public void unFocused(GuiNpcTextField textfield) {
        if (textfield.id == 0)
            npc.advanced.setSound(0, textfield.getText());
        if (textfield.id == 2)
            npc.advanced.setSound(1, textfield.getText());
        if (textfield.id == 3)
            npc.advanced.setSound(2, textfield.getText());
        if (textfield.id == 4)
            npc.advanced.setSound(3, textfield.getText());
        if (textfield.id == 5)
            npc.advanced.setSound(4, textfield.getText());
    }

    @Override
    public void save() {
        Client.sendData(EnumPacketServer.MainmenuAdvancedSave, npc.advanced.writeToNBT(new NBTTagCompound()));
    }

    @Override
    public void subGuiClosed(SubGuiInterface subgui) {
        GuiSoundSelection gss = (GuiSoundSelection) subgui;
        if (gss.selectedResource != null) {
            selectedField.setText(gss.selectedResource.toString());
            unFocused(selectedField);
        }
    }

}
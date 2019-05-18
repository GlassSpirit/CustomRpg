package noppes.npcs.client.gui.roles;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.client.Client;
import noppes.npcs.client.gui.SubGuiNpcTextArea;
import noppes.npcs.client.gui.util.*;
import noppes.npcs.constants.EnumPacketServer;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.roles.RoleDialog;

import java.util.HashMap;


public class GuiRoleDialog extends GuiNPCInterface2 implements ISubGuiListener {
    private RoleDialog role;

    private int slot = 0;

    public GuiRoleDialog(EntityNPCInterface npc) {
        super(npc);
        role = (RoleDialog) npc.roleInterface;
    }

    @Override
    public void initGui() {
        super.initGui();

        addLabel(new GuiNpcLabel(0, "dialog.starttext", guiLeft + 4, guiTop + 10));
        addButton(new GuiNpcButton(0, guiLeft + 60, guiTop + 5, 50, 20, "selectServer.edit"));

        addLabel(new GuiNpcLabel(100, "dialog.options", guiLeft + 4, guiTop + 34));

        for (int i = 1; i <= 6; i++) {
            int y = guiTop + 24 + i * 23;
            addLabel(new GuiNpcLabel(i, i + ":", guiLeft + 4, y + 5));
            String text = role.options.get(i);
            if (text == null)
                text = "";
            addTextField(new GuiNpcTextField(i, this, guiLeft + 16, y, 280, 20, text));
            addButton(new GuiNpcButton(i, guiLeft + 310, y, 50, 20, "selectServer.edit"));

        }
    }

    @Override
    protected void actionPerformed(GuiButton guibutton) {
        if (guibutton.id <= 6) {
            save();
            slot = guibutton.id;
            String text = role.dialog;
            if (slot >= 1) {
                text = role.optionsTexts.get(slot);
            }
            if (text == null)
                text = "";
            setSubGui(new SubGuiNpcTextArea(text));
        }
    }

    public void save() {
        HashMap<Integer, String> map = new HashMap<Integer, String>();
        for (int i = 1; i <= 6; i++) {
            String text = getTextField(i).getText();
            if (!text.isEmpty())
                map.put(i, text);
        }
        role.options = map;
        Client.sendData(EnumPacketServer.RoleSave, role.writeToNBT(new NBTTagCompound()));
    }

    @Override
    public void subGuiClosed(SubGuiInterface subgui) {
        if (subgui instanceof SubGuiNpcTextArea) {
            SubGuiNpcTextArea text = (SubGuiNpcTextArea) subgui;
            if (slot == 0)
                role.dialog = text.text;
            else {
                if (text.text.isEmpty())
                    role.optionsTexts.remove(slot);
                else
                    role.optionsTexts.put(slot, text.text);
            }
        }
    }
}

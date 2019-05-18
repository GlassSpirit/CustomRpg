package noppes.npcs.client.gui.mainmenu;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.client.Client;
import noppes.npcs.client.NoppesUtil;
import noppes.npcs.client.gui.GuiNPCTextures;
import noppes.npcs.client.gui.GuiNpcTextureCloaks;
import noppes.npcs.client.gui.GuiNpcTextureOverlays;
import noppes.npcs.client.gui.model.GuiCreationParts;
import noppes.npcs.client.gui.util.*;
import noppes.npcs.constants.EnumPacketServer;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.entity.data.DataDisplay;

public class GuiNpcDisplay extends GuiNPCInterface2 implements ITextfieldListener, IGuiData {

    private DataDisplay display;

    public GuiNpcDisplay(EntityNPCInterface npc) {
        super(npc, 1);
        display = npc.display;
        Client.sendData(EnumPacketServer.MainmenuDisplayGet);
    }

    @Override
    public void initGui() {
        super.initGui();
        int y = guiTop + 4;
        addLabel(new GuiNpcLabel(0, "gui.name", guiLeft + 5, y + 5));
        addTextField(new GuiNpcTextField(0, this, fontRenderer, guiLeft + 50, y, 200, 20, display.getName()));
        this.addButton(new GuiNpcButton(0, guiLeft + 253, y, 110, 20, new String[]{"display.show", "display.hide", "display.showAttacking"}, display.getShowName()));

        y += 23;
        addLabel(new GuiNpcLabel(11, "gui.title", guiLeft + 5, y + 5));
        addTextField(new GuiNpcTextField(11, this, fontRenderer, guiLeft + 50, y, 200, 20, display.getTitle()));

        y += 23;
        addLabel(new GuiNpcLabel(1, "display.model", guiLeft + 5, y + 5));
        this.addButton(new GuiNpcButton(1, guiLeft + 50, y, 110, 20, "selectServer.edit"));
        addLabel(new GuiNpcLabel(2, "display.size", guiLeft + 175, y + 5));
        addTextField(new GuiNpcTextField(2, this, fontRenderer, guiLeft + 203, y, 40, 20, display.getSize() + ""));
        getTextField(2).numbersOnly = true;
        getTextField(2).setMinMaxDefault(1, 30, 5);
        addLabel(new GuiNpcLabel(3, "(1-30)", guiLeft + 246, y + 5));

        y += 23;
        addLabel(new GuiNpcLabel(4, "display.texture", guiLeft + 5, y + 5));
        addTextField(new GuiNpcTextField(3, this, fontRenderer, guiLeft + 80, y, 200, 20, display.skinType == 0 ? display.getSkinTexture() : display.getSkinUrl()));
        this.addButton(new GuiNpcButton(3, guiLeft + 325, y, 38, 20, "mco.template.button.select"));
        this.addButton(new GuiNpcButton(2, guiLeft + 283, y, 40, 20, new String[]{"display.texture", "display.player", "display.url"}, display.skinType));
        getButton(3).setEnabled(display.skinType == 0);
        if (display.skinType == 1 && !display.getSkinPlayer().isEmpty())
            getTextField(3).setText(display.getSkinPlayer());

        y += 23;
        addLabel(new GuiNpcLabel(8, "display.cape", guiLeft + 5, y + 5));
        addTextField(new GuiNpcTextField(8, this, fontRenderer, guiLeft + 80, y, 200, 20, display.getCapeTexture()));
        this.addButton(new GuiNpcButton(8, guiLeft + 283, y, 80, 20, "display.selectTexture"));

        y += 23;
        addLabel(new GuiNpcLabel(9, "display.overlay", guiLeft + 5, y + 5));
        addTextField(new GuiNpcTextField(9, this, fontRenderer, guiLeft + 80, y, 200, 20, display.getOverlayTexture()));
        this.addButton(new GuiNpcButton(9, guiLeft + 283, y, 80, 20, "display.selectTexture"));

        y += 23;
        addLabel(new GuiNpcLabel(5, "display.livingAnimation", guiLeft + 5, y + 5));
        this.addButton(new GuiNpcButton(5, guiLeft + 120, y, 50, 20, new String[]{"gui.yes", "gui.no"}, display.getHasLivingAnimation() ? 0 : 1));

        addLabel(new GuiNpcLabel(6, "display.tint", guiLeft + 180, y + 5));
        String color = Integer.toHexString(display.getTint());
        while (color.length() < 6)
            color = "0" + color;
        this.addTextField(new GuiNpcTextField(6, this, guiLeft + 220, y, 60, 20, color));
        getTextField(6).setTextColor(display.getTint());

        y += 23;
        addLabel(new GuiNpcLabel(7, "display.visible", guiLeft + 5, y + 5));
        this.addButton(new GuiNpcButton(7, guiLeft + 120, y, 50, 20, new String[]{"gui.yes", "gui.no", "gui.partly"}, display.getVisible()));
        addLabel(new GuiNpcLabel(13, "display.interactable", guiLeft + 180, y + 5));
        this.addButton(new GuiNpcButtonYesNo(13, guiLeft + 280, y, display.getHasHitbox()));

        y += 23;
        addLabel(new GuiNpcLabel(10, "display.bossbar", guiLeft + 5, y + 5));
        this.addButton(new GuiNpcButton(10, guiLeft + 60, y, 110, 20, new String[]{"display.hide", "display.show", "display.showAttacking"}, display.getBossbar()));
        addLabel(new GuiNpcLabel(12, "gui.color", guiLeft + 180, y + 5));
        this.addButton(new GuiNpcButton(12, guiLeft + 220, y, 110, 20, display.getBossColor(), "color.pink", "color.blue", "color.red", "color.green", "color.yellow", "color.purple", "color.white"));

        //addExtra(new GuiHoverText(0, "testing", guiLeft, guiTop));
    }

    @Override
    public void unFocused(GuiNpcTextField textfield) {
        if (textfield.id == 0) {
            if (!textfield.isEmpty())
                display.setName(textfield.getText());
            else
                textfield.setText(display.getName());
        } else if (textfield.id == 2) {
            display.setSize(textfield.getInteger());
        } else if (textfield.id == 3) {
            if (display.skinType == 2)
                display.setSkinUrl(textfield.getText());
            else if (display.skinType == 1)
                display.setSkinPlayer(textfield.getText());
            else
                display.setSkinTexture(textfield.getText());
        } else if (textfield.id == 6) {
            int color = 0;
            try {
                color = Integer.parseInt(textfield.getText(), 16);
            } catch (NumberFormatException e) {
                color = 0xFFFFFF;
            }
            display.setTint(color);
            textfield.setTextColor(display.getTint());
        } else if (textfield.id == 8) {
            display.setCapeTexture(textfield.getText());
        } else if (textfield.id == 9) {
            display.setOverlayTexture(textfield.getText());
        } else if (textfield.id == 11) {
            display.setTitle(textfield.getText());
        }
    }

    @Override
    protected void actionPerformed(GuiButton guibutton) {
        GuiNpcButton button = (GuiNpcButton) guibutton;
        if (button.id == 0) {
            display.setShowName(button.getValue());
        }
        if (button.id == 1) {
            NoppesUtil.openGUI(player, new GuiCreationParts(npc));
            //NoppesUtil.openGUI(player, new GuiNpcModelSelection(npc,this));
        }
        if (button.id == 2) {
            display.setSkinUrl("");
            display.setSkinPlayer(null);
            display.skinType = (byte) button.getValue();
            initGui();
        } else if (button.id == 3) {
            NoppesUtil.openGUI(player, new GuiNPCTextures(npc, this));
        } else if (button.id == 5) {
            display.setHasLivingAnimation(button.getValue() == 0);
        } else if (button.id == 7) {
            display.setVisible(button.getValue());
        } else if (button.id == 8) {
            NoppesUtil.openGUI(player, new GuiNpcTextureCloaks(npc, this));
        } else if (button.id == 9) {
            NoppesUtil.openGUI(player, new GuiNpcTextureOverlays(npc, this));
        } else if (button.id == 10) {
            display.setBossbar(button.getValue());
        } else if (button.id == 12) {
            display.setBossColor(button.getValue());
        } else if (button.id == 13) {
            display.setHasHitbox(((GuiNpcButtonYesNo) button).getBoolean());
        }
    }

    @Override
    public void save() {
        if (display.skinType == 1)
            display.loadProfile();
        npc.textureLocation = null;
        mc.renderGlobal.onEntityRemoved(npc);
        mc.renderGlobal.onEntityAdded(npc);
        Client.sendData(EnumPacketServer.MainmenuDisplaySave, display.writeToNBT(new NBTTagCompound()));

    }

    @Override
    public void setGuiData(NBTTagCompound compound) {
        display.readToNBT(compound);
        initGui();
    }

}

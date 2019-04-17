package noppes.npcs.client.gui;

import net.minecraft.client.gui.GuiButton;
import noppes.npcs.client.gui.select.GuiSoundSelection;
import noppes.npcs.client.gui.util.*;
import noppes.npcs.entity.data.DataRanged;
import noppes.npcs.entity.data.DataStats;

public class SubGuiNpcRangeProperties extends SubGuiInterface implements ITextfieldListener, ISubGuiListener {
    private DataRanged ranged;
    private DataStats stats;
    private GuiSoundSelection gui;
    private GuiNpcTextField soundSelected = null;

    public SubGuiNpcRangeProperties(DataStats stats) {
        this.ranged = stats.ranged;
        this.stats = stats;
        setBackground("menubg.png");
        xSize = 256;
        ySize = 216;
        closeOnEsc = true;
    }

    @Override
    public void initGui() {
        super.initGui();
        int y = guiTop + 4;
        addTextField(new GuiNpcTextField(1, this, fontRenderer, guiLeft + 80, y, 50, 18, ranged.getAccuracy() + ""));
        addLabel(new GuiNpcLabel(1, "stats.accuracy", guiLeft + 5, y + 5));
        getTextField(1).numbersOnly = true;
        getTextField(1).setMinMaxDefault(0, 100, 90);
        addTextField(new GuiNpcTextField(8, this, fontRenderer, guiLeft + 200, y, 50, 18, ranged.getShotCount() + ""));
        addLabel(new GuiNpcLabel(8, "stats.shotcount", guiLeft + 135, y + 5));
        getTextField(8).numbersOnly = true;
        getTextField(8).setMinMaxDefault(1, 10, 1);

        addTextField(new GuiNpcTextField(2, this, fontRenderer, guiLeft + 80, y += 22, 50, 18, ranged.getRange() + ""));
        addLabel(new GuiNpcLabel(2, "gui.range", guiLeft + 5, y + 5));
        getTextField(2).numbersOnly = true;
        getTextField(2).setMinMaxDefault(1, 64, 2);

        addTextField(new GuiNpcTextField(9, this, fontRenderer, guiLeft + 200, y, 30, 20, ranged.getMeleeRange() + ""));
        addLabel(new GuiNpcLabel(16, "stats.meleerange", guiLeft + 135, y + 5));
        getTextField(9).numbersOnly = true;
        getTextField(9).setMinMaxDefault(0, stats.aggroRange, 5);

        addTextField(new GuiNpcTextField(3, this, fontRenderer, guiLeft + 80, y += 22, 50, 18, ranged.getDelayMin() + ""));
        addLabel(new GuiNpcLabel(3, "stats.mindelay", guiLeft + 5, y + 5));
        getTextField(3).numbersOnly = true;
        getTextField(3).setMinMaxDefault(1, 9999, 20);
        addTextField(new GuiNpcTextField(4, this, fontRenderer, guiLeft + 200, y, 50, 18, ranged.getDelayMax() + ""));
        addLabel(new GuiNpcLabel(4, "stats.maxdelay", guiLeft + 135, y + 5));
        getTextField(4).numbersOnly = true;
        getTextField(4).setMinMaxDefault(1, 9999, 20);

        addTextField(new GuiNpcTextField(6, this, fontRenderer, guiLeft + 80, y += 22, 50, 18, ranged.getBurst() + ""));
        addLabel(new GuiNpcLabel(6, "stats.burstcount", guiLeft + 5, y + 5));
        getTextField(6).numbersOnly = true;
        getTextField(6).setMinMaxDefault(1, 100, 20);
        addTextField(new GuiNpcTextField(5, this, fontRenderer, guiLeft + 200, y, 50, 18, ranged.getBurstDelay() + ""));
        addLabel(new GuiNpcLabel(5, "stats.burstspeed", guiLeft + 135, y + 5));
        getTextField(5).numbersOnly = true;
        getTextField(5).setMinMaxDefault(0, 30, 0);

        addTextField(new GuiNpcTextField(7, this, fontRenderer, guiLeft + 80, y += 22, 100, 20, ranged.getSound(0)));
        addLabel(new GuiNpcLabel(7, "stats.firesound", guiLeft + 5, y + 5));
        addButton(new GuiNpcButton(7, guiLeft + 187, y, 60, 20, "mco.template.button.select"));

        addTextField(new GuiNpcTextField(11, this, fontRenderer, guiLeft + 80, y += 22, 100, 20, ranged.getSound(1)));
        addLabel(new GuiNpcLabel(11, "stats.hitsound", guiLeft + 5, y + 5));
        addButton(new GuiNpcButton(11, guiLeft + 187, y, 60, 20, "mco.template.button.select"));

        addTextField(new GuiNpcTextField(10, this, fontRenderer, guiLeft + 80, y += 22, 100, 20, ranged.getSound(2)));
        addLabel(new GuiNpcLabel(10, "stats.groundsound", guiLeft + 5, y + 5));
        addButton(new GuiNpcButton(10, guiLeft + 187, y, 60, 20, "mco.template.button.select"));

        addButton(new GuiNpcButtonYesNo(9, guiLeft + 100, y += 22, ranged.getHasAimAnimation()));
        addLabel(new GuiNpcLabel(9, "stats.aimWhileShooting", guiLeft + 5, y + 5));

        addButton(new GuiNpcButton(13, guiLeft + 100, y += 22, 80, 20, new String[]{"gui.no", "gui.whendistant", "gui.whenhidden"}, ranged.getFireType()));
        addLabel(new GuiNpcLabel(13, "stats.indirect", guiLeft + 5, y + 5));

        addButton(new GuiNpcButton(66, guiLeft + 190, guiTop + 190, 60, 20, "gui.done"));
    }

    @Override
    public void unFocused(GuiNpcTextField textfield) {
        if (textfield.id == 1) {
            ranged.setAccuracy(textfield.getInteger());
        } else if (textfield.id == 2) {
            ranged.setRange(textfield.getInteger());
        } else if (textfield.id == 3) {
            ranged.setDelay(textfield.getInteger(), ranged.getDelayMax());
            initGui();
        } else if (textfield.id == 4) {
            ranged.setDelay(ranged.getDelayMin(), textfield.getInteger());
            initGui();
        } else if (textfield.id == 5) {
            ranged.setBurstDelay(textfield.getInteger());
        } else if (textfield.id == 6) {
            ranged.setBurst(textfield.getInteger());
        } else if (textfield.id == 7) {
            ranged.setSound(0, textfield.getText());
        } else if (textfield.id == 8) {
            ranged.setShotCount(textfield.getInteger());
        } else if (textfield.id == 9) {
            ranged.setMeleeRange(textfield.getInteger());
        } else if (textfield.id == 10) {
            ranged.setSound(2, textfield.getText());
        } else if (textfield.id == 11) {
            ranged.setSound(1, textfield.getText());
        }
    }

    @Override
    protected void actionPerformed(GuiButton guibutton) {
        int id = guibutton.id;
        if (id == 7) {
            soundSelected = getTextField(7);
            setSubGui(new GuiSoundSelection(soundSelected.getText()));
        }
        if (id == 11) {
            soundSelected = getTextField(11);
            setSubGui(new GuiSoundSelection(soundSelected.getText()));
        }
        if (id == 10) {
            soundSelected = getTextField(10);
            setSubGui(new GuiSoundSelection(soundSelected.getText()));
        } else if (id == 66) {
            close();
        } else if (id == 9) {
            ranged.setHasAimAnimation(((GuiNpcButtonYesNo) guibutton).getBoolean());
        } else if (id == 13) {
            ranged.setFireType(((GuiNpcButton) guibutton).getValue());
        }
    }

    @Override
    public void subGuiClosed(SubGuiInterface subgui) {
        GuiSoundSelection gss = (GuiSoundSelection) subgui;
        if (gss.selectedResource != null) {
            soundSelected.setText(gss.selectedResource.toString());
            unFocused(soundSelected);
        }
    }
}

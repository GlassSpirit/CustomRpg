package noppes.npcs.client.gui;

import net.minecraft.client.gui.GuiButton;
import noppes.npcs.util.NoppesStringUtils;
import noppes.npcs.client.gui.util.*;
import noppes.npcs.constants.EnumAvailabilityScoreboard;
import noppes.npcs.controllers.data.Availability;

public class SubGuiNpcAvailabilityScoreboard extends SubGuiInterface implements ITextfieldListener {
    private Availability availabitily;
    private boolean selectFaction = false;
    private int slot = 0;

    public SubGuiNpcAvailabilityScoreboard(Availability availabitily) {
        this.availabitily = availabitily;
        setBackground("menubg.png");
        xSize = 316;
        ySize = 216;
        closeOnEsc = true;
    }

    @Override
    public void initGui() {
        super.initGui();
        addLabel(new GuiNpcLabel(1, "availability.available", guiLeft, guiTop + 4));
        getLabel(1).center(xSize);

        int y = guiTop + 12;
        this.addTextField(new GuiNpcTextField(10, this, guiLeft + 4, y, 140, 20, availabitily.scoreboardObjective));
        this.addButton(new GuiNpcButton(0, guiLeft + 148, y, 90, 20, new String[]{"availability.smaller", "availability.equals", "availability.bigger"}, availabitily.scoreboardType.ordinal()));
        this.addTextField(new GuiNpcTextField(20, this, guiLeft + 244, y, 60, 20, availabitily.scoreboardValue + ""));
        this.getTextField(20).numbersOnly = true;

        y += 23;
        this.addTextField(new GuiNpcTextField(11, this, guiLeft + 4, y, 140, 20, availabitily.scoreboard2Objective));
        this.addButton(new GuiNpcButton(1, guiLeft + 148, y, 90, 20, new String[]{"availability.smaller", "availability.equals", "availability.bigger"}, availabitily.scoreboard2Type.ordinal()));
        this.addTextField(new GuiNpcTextField(21, this, guiLeft + 244, y, 60, 20, availabitily.scoreboard2Value + ""));
        this.getTextField(21).numbersOnly = true;

        this.addButton(new GuiNpcButton(66, guiLeft + 82, guiTop + 192, 98, 20, "gui.done"));
    }

    @Override
    protected void actionPerformed(GuiButton guibutton) {
        GuiNpcButton button = (GuiNpcButton) guibutton;
        if (guibutton.id == 0) {
            availabitily.scoreboardType = EnumAvailabilityScoreboard.values()[button.getValue()];
        }
        if (guibutton.id == 1) {
            availabitily.scoreboard2Type = EnumAvailabilityScoreboard.values()[button.getValue()];
        }
        if (guibutton.id == 66) {
            close();
        }
    }

    @Override
    public void unFocused(GuiNpcTextField textfield) {
        if (textfield.id == 10) {
            availabitily.scoreboardObjective = textfield.text;
        }
        if (textfield.id == 11) {
            availabitily.scoreboard2Objective = textfield.text;
        }
        if (textfield.id == 20) {
            availabitily.scoreboardValue = NoppesStringUtils.parseInt(textfield.text, 0);
        }
        if (textfield.id == 21) {
            availabitily.scoreboard2Value = NoppesStringUtils.parseInt(textfield.text, 0);
        }
    }

}

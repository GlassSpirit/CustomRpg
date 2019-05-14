package noppes.npcs.client.gui;

import net.minecraft.client.gui.GuiButton;
import noppes.npcs.api.constants.PotionEffectType;
import noppes.npcs.client.gui.util.*;
import noppes.npcs.common.entity.data.DataMelee;

public class SubGuiNpcMeleeProperties extends SubGuiInterface implements ITextfieldListener {
    private DataMelee stats;
    private String[] potionNames = new String[]{"gui.none", "tile.fire.name", "effect.poison", "effect.hunger", "effect.weakness", "effect.moveSlowdown", "effect.confusion", "effect.blindness", "effect.wither"};

    public SubGuiNpcMeleeProperties(DataMelee stats) {
        this.stats = stats;
        setBackground("menubg.png");
        xSize = 256;
        ySize = 216;
        closeOnEsc = true;
    }

    @Override
    public void initGui() {
        super.initGui();
        addLabel(new GuiNpcLabel(1, "stats.meleestrength", guiLeft + 5, guiTop + 15));
        addTextField(new GuiNpcTextField(1, this, fontRenderer, guiLeft + 85, guiTop + 10, 50, 18, stats.getStrength() + ""));
        getTextField(1).numbersOnly = true;
        getTextField(1).setMinMaxDefault(0, Integer.MAX_VALUE, 5);
        addLabel(new GuiNpcLabel(2, "stats.meleerange", guiLeft + 5, guiTop + 45));
        addTextField(new GuiNpcTextField(2, this, fontRenderer, guiLeft + 85, guiTop + 40, 50, 18, stats.getRange() + ""));
        getTextField(2).numbersOnly = true;
        getTextField(2).setMinMaxDefault(1, 30, 2);
        addLabel(new GuiNpcLabel(3, "stats.meleespeed", guiLeft + 5, guiTop + 75));
        addTextField(new GuiNpcTextField(3, this, fontRenderer, guiLeft + 85, guiTop + 70, 50, 18, stats.getDelay() + ""));
        getTextField(3).numbersOnly = true;
        getTextField(3).setMinMaxDefault(1, 1000, 20);


        addLabel(new GuiNpcLabel(4, "enchantment.knockback", guiLeft + 5, guiTop + 105));
        addTextField(new GuiNpcTextField(4, this, fontRenderer, guiLeft + 85, guiTop + 100, 50, 18, stats.getKnockback() + ""));
        getTextField(4).numbersOnly = true;
        getTextField(4).setMinMaxDefault(0, 4, 0);
        addLabel(new GuiNpcLabel(5, "stats.meleeeffect", guiLeft + 5, guiTop + 135));
        addButton(new GuiButtonBiDirectional(5, guiLeft + 85, guiTop + 130, 100, 20, potionNames, stats.getEffectType()));
        if (stats.getEffectType() != PotionEffectType.NONE) {
            addLabel(new GuiNpcLabel(6, "gui.time", guiLeft + 5, guiTop + 165));
            addTextField(new GuiNpcTextField(6, this, fontRenderer, guiLeft + 85, guiTop + 160, 50, 18, stats.getEffectTime() + ""));
            getTextField(6).numbersOnly = true;
            getTextField(6).setMinMaxDefault(1, 99999, 5);
            if (stats.getEffectType() != PotionEffectType.FIRE) {
                addLabel(new GuiNpcLabel(7, "stats.amplify", guiLeft + 5, guiTop + 195));
                addButton(new GuiButtonBiDirectional(7, guiLeft + 85, guiTop + 190, 52, 20, new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"}, stats.getEffectStrength()));
            }
        }
        addButton(new GuiNpcButton(66, guiLeft + 164, guiTop + 192, 90, 20, "gui.done"));
    }

    @Override
    public void unFocused(GuiNpcTextField textfield) {
        if (textfield.id == 1) {
            stats.setStrength(textfield.getInteger());
        } else if (textfield.id == 2) {
            stats.setRange(textfield.getInteger());
        } else if (textfield.id == 3) {
            stats.setDelay(textfield.getInteger());
        } else if (textfield.id == 4) {
            stats.setKnockback(textfield.getInteger());
        } else if (textfield.id == 6) {
            stats.setEffect(stats.getEffectType(), stats.getEffectStrength(), textfield.getInteger());
        }
    }

    @Override
    protected void actionPerformed(GuiButton guibutton) {
        GuiNpcButton button = (GuiNpcButton) guibutton;
        if (button.id == 5) {
            stats.setEffect(button.getValue(), stats.getEffectStrength(), stats.getEffectTime());
            initGui();
        }
        if (button.id == 7) {
            stats.setEffect(stats.getEffectType(), button.getValue(), stats.getEffectTime());
        }
        if (button.id == 66) {
            close();
        }
    }

}

package noppes.npcs.client.gui;

import net.minecraft.client.gui.GuiButton;
import noppes.npcs.ai.EntityAIAnimation;
import noppes.npcs.api.constants.AnimationType;
import noppes.npcs.client.gui.util.*;
import noppes.npcs.entity.data.DataAI;

public class SubGuiNpcMovement extends SubGuiInterface implements ITextfieldListener {
    private DataAI ai;

    public SubGuiNpcMovement(DataAI ai) {
        this.ai = ai;
        setBackground("menubg.png");
        xSize = 256;
        ySize = 216;
        closeOnEsc = true;
    }

    public void initGui() {
        super.initGui();
        int y = guiTop + 4;
        this.addLabel(new GuiNpcLabel(0, "movement.type", guiLeft + 4, y + 5));
        this.addButton(new GuiNpcButton(0, guiLeft + 80, y, 100, 20, new String[]{"ai.standing", "ai.wandering", "ai.movingpath"}, ai.getMovingType()));

        addButton(new GuiNpcButton(15, guiLeft + 80, y += 22, 100, 20, new String[]{"movement.ground", "movement.flying", "movement.swimming"}, ai.movementType));
        this.addLabel(new GuiNpcLabel(15, "movement.navigation", guiLeft + 4, y + 5));

        if (ai.getMovingType() == 1) {
            addTextField(new GuiNpcTextField(4, this, guiLeft + 100, y += 22, 40, 20, ai.walkingRange + ""));
            getTextField(4).numbersOnly = true;
            getTextField(4).setMinMaxDefault(0, 1000, 10);
            addLabel(new GuiNpcLabel(4, "gui.range", guiLeft + 4, y + 5));

            addButton(new GuiNpcButton(5, guiLeft + 100, y += 22, 50, 20, new String[]{"gui.no", "gui.yes"}, ai.npcInteracting ? 1 : 0));
            addLabel(new GuiNpcLabel(5, "movement.wanderinteract", guiLeft + 4, y + 5));

            this.addButton(new GuiNpcButton(9, guiLeft + 80, y += 22, 80, 20, new String[]{"gui.no", "gui.yes"}, ai.movingPause ? 1 : 0));
            this.addLabel(new GuiNpcLabel(9, "movement.pauses", guiLeft + 4, y + 5));
        } else if (ai.getMovingType() == 0) {
            addTextField(new GuiNpcTextField(7, this, guiLeft + 99, y += 22, 24, 20, (int) ai.bodyOffsetX + ""));
            addLabel(new GuiNpcLabel(17, "spawner.posoffset", guiLeft + 4, y + 5));
            addLabel(new GuiNpcLabel(7, "X:", guiLeft + 115, y + 5));
            getTextField(7).numbersOnly = true;
            getTextField(7).setMinMaxDefault(0, 10, 5);
            addLabel(new GuiNpcLabel(8, "Y:", guiLeft + 125, y + 5));
            addTextField(new GuiNpcTextField(8, this, guiLeft + 135, y, 24, 20, (int) ai.bodyOffsetY + ""));
            getTextField(8).numbersOnly = true;
            getTextField(8).setMinMaxDefault(0, 10, 5);
            addLabel(new GuiNpcLabel(9, "Z:", guiLeft + 161, y + 5));
            addTextField(new GuiNpcTextField(9, this, guiLeft + 171, y, 24, 20, (int) ai.bodyOffsetZ + ""));
            getTextField(9).numbersOnly = true;
            getTextField(9).setMinMaxDefault(0, 10, 5);

            this.addButton(new GuiNpcButton(3, guiLeft + 80, y += 22, 100, 20, new String[]{"stats.normal", "movement.sitting", "movement.lying", "movement.hug", "movement.sneaking", "movement.dancing", "movement.aiming", "movement.crawling"}, ai.animationType));
            this.addLabel(new GuiNpcLabel(3, "movement.animation", guiLeft + 4, y + 5));

            if (ai.animationType != AnimationType.SLEEP) {
                this.addButton(new GuiNpcButton(4, guiLeft + 80, y += 22, 80, 20, new String[]{"movement.body", "movement.manual", "movement.stalking", "movement.head"}, ai.getStandingType()));
                this.addLabel(new GuiNpcLabel(1, "movement.rotation", guiLeft + 4, y + 5));
            } else {
                addTextField(new GuiNpcTextField(5, this, guiLeft + 99, y += 22, 40, 20, ai.orientation + ""));
                getTextField(5).numbersOnly = true;
                getTextField(5).setMinMaxDefault(0, 359, 0);
                this.addLabel(new GuiNpcLabel(6, "movement.rotation", guiLeft + 4, y + 5));
                addLabel(new GuiNpcLabel(5, "(0-359)", guiLeft + 142, y + 5));
            }
            if (ai.getStandingType() == 1 || ai.getStandingType() == 3) {
                addTextField(new GuiNpcTextField(5, this, guiLeft + 165, y, 40, 20, ai.orientation + ""));
                getTextField(5).numbersOnly = true;
                getTextField(5).setMinMaxDefault(0, 359, 0);
                addLabel(new GuiNpcLabel(5, "(0-359)", guiLeft + 207, y + 5));
            }
        }
        if (ai.getMovingType() != 0) {
            this.addButton(new GuiNpcButton(12, guiLeft + 80, y += 22, 100, 20, new String[]{"stats.normal", "movement.sneaking", "movement.aiming", "movement.dancing", "movement.crawling", "movement.hug"}, EntityAIAnimation.getWalkingAnimationGuiIndex(ai.animationType)));
            this.addLabel(new GuiNpcLabel(12, "movement.animation", guiLeft + 4, y + 5));
        }

        if (ai.getMovingType() == 2) {
            this.addButton(new GuiNpcButton(8, guiLeft + 80, y += 22, 80, 20, new String[]{"ai.looping", "ai.backtracking"}, ai.movingPattern));
            this.addLabel(new GuiNpcLabel(8, "movement.name", guiLeft + 4, y + 5));
            this.addButton(new GuiNpcButton(9, guiLeft + 80, y += 22, 80, 20, new String[]{"gui.no", "gui.yes"}, ai.movingPause ? 1 : 0));
            this.addLabel(new GuiNpcLabel(9, "movement.pauses", guiLeft + 4, y + 5));
        }
        addButton(new GuiNpcButton(13, guiLeft + 100, y += 22, 50, 20, new String[]{"gui.no", "gui.yes"}, ai.stopAndInteract ? 1 : 0));
        addLabel(new GuiNpcLabel(13, "movement.stopinteract", guiLeft + 4, y + 5));

        addTextField(new GuiNpcTextField(14, this, guiLeft + 80, y += 22, 50, 18, ai.getWalkingSpeed() + ""));
        getTextField(14).numbersOnly = true;
        getTextField(14).setMinMaxDefault(0, 10, 4);
        addLabel(new GuiNpcLabel(14, "stats.movespeed", guiLeft + 5, y + 5));

        addButton(new GuiNpcButton(66, guiLeft + 190, guiTop + 190, 60, 20, "gui.done"));
    }

    protected void actionPerformed(GuiButton guibutton) {
        GuiNpcButton button = (GuiNpcButton) guibutton;
        if (button.id == 0) {
            ai.setMovingType(button.getValue());
            if (ai.getMovingType() != 0) {
                ai.animationType = AnimationType.NORMAL;
                ai.setStandingType(0);
                ai.bodyOffsetX = ai.bodyOffsetY = ai.bodyOffsetZ = 5;
            }
            initGui();
        } else if (button.id == 3) {
            ai.animationType = button.getValue();
            initGui();
        } else if (button.id == 4) {
            ai.setStandingType(button.getValue());
            initGui();
        } else if (button.id == 5) {
            ai.npcInteracting = button.getValue() == 1;
        } else if (button.id == 8) {
            ai.movingPattern = button.getValue();
        } else if (button.id == 9) {
            ai.movingPause = button.getValue() == 1;
        } else if (button.id == 12) {
            if (button.getValue() == 0)
                ai.animationType = AnimationType.NORMAL;
            if (button.getValue() == 1)
                ai.animationType = AnimationType.SNEAK;
            if (button.getValue() == 2)
                ai.animationType = AnimationType.AIM;
            if (button.getValue() == 3)
                ai.animationType = AnimationType.DANCE;
            if (button.getValue() == 4)
                ai.animationType = AnimationType.CRAWL;
            if (button.getValue() == 5)
                ai.animationType = AnimationType.HUG;
        } else if (button.id == 13) {
            ai.stopAndInteract = button.getValue() == 1;
        } else if (button.id == 15) {
            ai.movementType = button.getValue();
        } else if (button.id == 66) {
            close();
        }
    }

    @Override
    public void unFocused(GuiNpcTextField textfield) {
        if (textfield.id == 7) {
            ai.bodyOffsetX = textfield.getInteger();
        } else if (textfield.id == 8) {
            ai.bodyOffsetY = textfield.getInteger();
        } else if (textfield.id == 9) {
            ai.bodyOffsetZ = textfield.getInteger();
        } else if (textfield.id == 5) {
            ai.orientation = textfield.getInteger();
        } else if (textfield.id == 4) {
            ai.walkingRange = textfield.getInteger();
        } else if (textfield.id == 14) {
            ai.setWalkingSpeed(textfield.getInteger());
        }
    }

}

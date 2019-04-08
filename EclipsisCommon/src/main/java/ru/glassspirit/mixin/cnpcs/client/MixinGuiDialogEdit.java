package ru.glassspirit.mixin.cnpcs.client;

import net.minecraft.client.gui.GuiButton;
import noppes.npcs.client.NoppesUtil;
import noppes.npcs.client.gui.global.GuiDialogEdit;
import noppes.npcs.client.gui.util.*;
import noppes.npcs.controllers.data.Dialog;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.glassspirit.cnpcs.client.gui.GuiScriptAvailability;

@Mixin(GuiDialogEdit.class)
public abstract class MixinGuiDialogEdit extends SubGuiInterface {

    @Shadow(remap = false)
    private Dialog dialog;

    @Override
    public void initGui() {
        super.initGui();
        this.addLabel(new GuiNpcLabel(1, "gui.title", this.guiLeft + 4, this.guiTop + 8));
        this.addTextField(new GuiNpcTextField(1, this, this.fontRenderer, this.guiLeft + 46, this.guiTop + 3, 220, 20, this.dialog.title));
        this.addLabel(new GuiNpcLabel(0, "ID", this.guiLeft + 268, this.guiTop + 4));
        this.addLabel(new GuiNpcLabel(2, this.dialog.id + "", this.guiLeft + 268, this.guiTop + 14));
        this.addLabel(new GuiNpcLabel(3, "dialog.dialogtext", this.guiLeft + 4, this.guiTop + 30));
        this.addButton(new GuiNpcButton(3, this.guiLeft + 120, this.guiTop + 25, 50, 20, "selectServer.edit"));
        this.addLabel(new GuiNpcLabel(4, "availability.options", this.guiLeft + 4, this.guiTop + 51));
        this.addButton(new GuiNpcButton(4, this.guiLeft + 120, this.guiTop + 46, 50, 20, "selectServer.edit"));
        this.addLabel(new GuiNpcLabel(5, "faction.options", this.guiLeft + 4, this.guiTop + 72));
        this.addButton(new GuiNpcButton(5, this.guiLeft + 120, this.guiTop + 67, 50, 20, "selectServer.edit"));
        this.addLabel(new GuiNpcLabel(6, "dialog.options", this.guiLeft + 4, this.guiTop + 93));
        this.addButton(new GuiNpcButton(6, this.guiLeft + 120, this.guiTop + 89, 50, 20, "selectServer.edit"));
        this.addButton(new GuiNpcButton(7, this.guiLeft + 4, this.guiTop + 114, 144, 20, "availability.selectquest"));
        this.addButton(new GuiNpcButton(8, this.guiLeft + 150, this.guiTop + 114, 20, 20, "X"));
        if (this.dialog.hasQuest()) {
            this.getButton(7).setDisplayText(this.dialog.getQuest().title);
        }

        this.addLabel(new GuiNpcLabel(9, "gui.selectSound", this.guiLeft + 4, this.guiTop + 138));
        this.addTextField(new GuiNpcTextField(2, this, this.fontRenderer, this.guiLeft + 4, this.guiTop + 148, 264, 20, this.dialog.sound));
        this.addButton(new GuiNpcButton(9, this.guiLeft + 270, this.guiTop + 148, 60, 20, "mco.template.button.select"));
        this.addButton(new GuiNpcButton(13, this.guiLeft + 4, this.guiTop + 172, 164, 20, "mailbox.setup"));
        this.addButton(new GuiNpcButton(14, this.guiLeft + 170, this.guiTop + 172, 20, 20, "X"));
        if (!this.dialog.mail.subject.isEmpty()) {
            this.getButton(13).setDisplayText(this.dialog.mail.subject);
        }

        int y = this.guiTop + 4;
        y += 22;
        this.addButton(new GuiNpcButton(10, this.guiLeft + 330, y, 50, 20, "selectServer.edit"));
        this.addLabel(new GuiNpcLabel(10, "advMode.command", this.guiLeft + 214, y + 5));
        y += 22;
        this.addButton(new GuiNpcButtonYesNo(11, this.guiLeft + 330, y, this.dialog.hideNPC));
        this.addLabel(new GuiNpcLabel(11, "dialog.hideNPC", this.guiLeft + 214, y + 5));
        y += 22;
        this.addButton(new GuiNpcButtonYesNo(12, this.guiLeft + 330, y, this.dialog.showWheel));
        this.addLabel(new GuiNpcLabel(12, "dialog.showWheel", this.guiLeft + 214, y + 5));
        y += 22;
        this.addButton(new GuiNpcButtonYesNo(15, this.guiLeft + 330, y, this.dialog.disableEsc));
        this.addLabel(new GuiNpcLabel(15, "dialog.disableEsc", this.guiLeft + 214, y + 5));
        this.addButton(new GuiNpcButton(66, this.guiLeft + 362, this.guiTop + 4, 20, 20, "X"));
        this.addButton(new GuiNpcButton(100, +214, this.guiTop + 114, 100, 20, "Script"));
    }

    @Inject(method = "buttonEvent", at = @At("RETURN"), remap = false)
    private void buttonEventInject(GuiButton guibutton, CallbackInfo ci) {
        GuiNpcButton button = (GuiNpcButton) guibutton;
        if (button.id == 100) {
            GuiScriptAvailability gui = new GuiScriptAvailability(dialog);
            NoppesUtil.openGUI(this.player, gui);
        }
    }

}

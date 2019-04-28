package noppes.npcs.client.gui.player;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.ITextComponent;
import noppes.npcs.NoppesStringUtils;
import noppes.npcs.NoppesUtilPlayer;
import noppes.npcs.api.constants.OptionType;
import noppes.npcs.client.ClientProxy;
import noppes.npcs.client.NoppesUtil;
import noppes.npcs.client.TextBlockClient;
import noppes.npcs.client.controllers.MusicController;
import noppes.npcs.client.gui.util.GuiNPCInterface;
import noppes.npcs.client.gui.util.IGuiClose;
import noppes.npcs.constants.EnumPlayerPacket;
import noppes.npcs.controllers.data.Dialog;
import noppes.npcs.controllers.data.DialogOption;
import noppes.npcs.entity.EntityNPCInterface;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.util.ArrayList;
import java.util.List;

public class GuiDialogInteract extends GuiNPCInterface implements IGuiClose {
    private Dialog dialog;
    private int selected = 0;
    private List<TextBlockClient> lines = new ArrayList<>();
    private List<Integer> options = new ArrayList<>();
    private int rowStart = 0;
    private int rowTotal = 0;
    private int dialogHeight = 180;

    private ResourceLocation wheel;
    private ResourceLocation[] wheelparts;
    private ResourceLocation indicator;

    private boolean isGrabbed = false;

    public GuiDialogInteract(EntityNPCInterface npc, Dialog dialog) {
        super(npc);
        this.dialog = dialog;
        appendDialog(dialog);
        ySize = 238;

        wheel = this.getResource("wheel.png");
        indicator = this.getResource("indicator.png");
        wheelparts = new ResourceLocation[]{getResource("wheel1.png"), getResource("wheel2.png"), getResource("wheel3.png"),
                getResource("wheel4.png"), getResource("wheel5.png"), getResource("wheel6.png")};
    }

    @Override
    public void initGui() {
        super.initGui();
        isGrabbed = false;
        grabMouse(dialog.showWheel);
        guiTop = (height - ySize);
        calculateRowHeight();
    }

    public void grabMouse(boolean grab) {
        if (grab && !isGrabbed) {
            Minecraft.getMinecraft().mouseHelper.grabMouseCursor();
            isGrabbed = true;
        } else if (!grab && isGrabbed) {
            Minecraft.getMinecraft().mouseHelper.ungrabMouseCursor();
            isGrabbed = false;
        }
    }

    @Override
    public void drawScreen(int i, int j, float f) {
        GlStateManager.color(1, 1, 1, 1);
        //this.drawDefaultBackground();
        this.drawGradientRect(0, 0, this.width, this.height, 0xDD000000, 0xDD000000);

        if (!dialog.hideNPC) {
            int l = (-70);
            int i1 = (ySize);
            drawNpc(npc, l, i1, 1.4f, 0);
        }
        super.drawScreen(i, j, f);

        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.enableAlpha();
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0F, 0.5f, 100.065F);
        int count = 0;
        for (TextBlockClient block : new ArrayList<>(lines)) {
            int size = ClientProxy.Companion.getFont().width(block.getName() + ": ");
            drawString(block.getName() + ": ", -4 - size, block.color, count);
            for (ITextComponent line : block.lines) {
                drawString(line.getFormattedText(), 0, block.color, count);
                count++;
            }
            count++;
        }

        if (!options.isEmpty()) {
            if (!dialog.showWheel)
                drawLinedOptions(j);
            else
                drawWheel();
        }
        GlStateManager.popMatrix();
    }

    private int selectedX = 0;
    private int selectedY = 0;

    private void drawWheel() {
        int yoffset = guiTop + dialogHeight + 14;
        GlStateManager.color(1, 1, 1, 1);
        mc.renderEngine.bindTexture(wheel);
        drawTexturedModalRect((width / 2) - 31, yoffset, 0, 0, 63, 40);

        selectedX += Mouse.getDX();
        selectedY += Mouse.getDY();
        int limit = 80;
        if (selectedX > limit)
            selectedX = limit;
        if (selectedX < -limit)
            selectedX = -limit;

        if (selectedY > limit)
            selectedY = limit;
        if (selectedY < -limit)
            selectedY = -limit;

        selected = 1;
        if (selectedY < -20)
            selected++;
        if (selectedY > 54)
            selected--;

        if (selectedX < 0)
            selected += 3;
        mc.renderEngine.bindTexture(wheelparts[selected]);
        drawTexturedModalRect((width / 2) - 31, yoffset, 0, 0, 85, 55);
        for (int slot : dialog.options.keySet()) {
            DialogOption option = dialog.options.get(slot);
            if (option == null || option.optionType == OptionType.DISABLED || option.hasDialog() && !option.getDialog().availability.isAvailable(player))
                continue;
            int color = option.optionColor;
            if (slot == (selected))
                color = 0x838FD8;
            //drawString(fontRenderer, option.title, width/2 -50 ,yoffset+ 162 + slot * 13 , color);
            int height = ClientProxy.Companion.getFont().height(option.title);
            if (slot == 0)
                drawString(fontRenderer, option.title, width / 2 + 13, yoffset - height, color);
            if (slot == 1)
                drawString(fontRenderer, option.title, width / 2 + 33, yoffset - height / 2 + 14, color);
            if (slot == 2)
                drawString(fontRenderer, option.title, width / 2 + 27, yoffset + 27, color);
            if (slot == 3)
                drawString(fontRenderer, option.title, width / 2 - 13 - ClientProxy.Companion.getFont().width(option.title), yoffset - height, color);
            if (slot == 4)
                drawString(fontRenderer, option.title, width / 2 - 33 - ClientProxy.Companion.getFont().width(option.title), yoffset - height / 2 + 14, color);
            if (slot == 5)
                drawString(fontRenderer, option.title, width / 2 - 27 - ClientProxy.Companion.getFont().width(option.title), yoffset + 27, color);

        }
        mc.renderEngine.bindTexture(indicator);
        drawTexturedModalRect(width / 2 + selectedX / 4 - 2, yoffset + 16 - selectedY / 6, 0, 0, 8, 8);
    }

    private void drawLinedOptions(int j) {
        drawHorizontalLine(guiLeft - 60, guiLeft + xSize + 120, guiTop + dialogHeight - ClientProxy.Companion.getFont().height(null) / 3, 0xFFFFFFFF);
        int offset = dialogHeight;
        if (j >= (guiTop + offset)) {
            int selected = ((j - (guiTop + offset)) / (ClientProxy.Companion.getFont().height(null)));
            if (selected < options.size())
                this.selected = selected;
        }
        if (selected >= options.size())
            selected = 0;
        if (selected < 0)
            selected = 0;

        for (int k = 0; k < options.size(); k++) {
            int id = options.get(k);
            DialogOption option = dialog.options.get(id);
            int y = ((guiTop + offset + (k * ClientProxy.Companion.getFont().height(null))));
            if (selected == k) {
                drawString(fontRenderer, ">", guiLeft - 60, y, 0xe0e0e0);
            }
            drawString(fontRenderer, NoppesStringUtils.formatText(option.title, player, npc), guiLeft - 30, y, option.optionColor);
        }
    }

    private void drawString(String text, int left, int color, int count) {
        int height = count - rowStart;
        drawString(fontRenderer, text, guiLeft + left, guiTop + (height * ClientProxy.Companion.getFont().height(null)), color);
    }

    @Override
    public void drawString(FontRenderer fontRendererIn, String text, int x, int y, int color) {
        ClientProxy.Companion.getFont().drawString(text, x, y, color);
        //super.drawString(fontRendererIn, text, x, y, color);
    }


    private int getSelected() {
        if (selected <= 0)
            return 0;

        if (selected < options.size())
            return selected;

        return options.size() - 1;
    }

    @Override
    public void keyTyped(char c, int i) {
        if (i == mc.gameSettings.keyBindForward.getKeyCode() || i == Keyboard.KEY_UP) {
            selected--;
        }
        if (i == mc.gameSettings.keyBindBack.getKeyCode() || i == Keyboard.KEY_DOWN) {
            selected++;
        }
        if (i == 28) {
            handleDialogSelection();
        }
        if (closeOnEsc && (i == 1 || isInventoryKey(i))) {
            NoppesUtilPlayer.sendData(EnumPlayerPacket.Dialog, dialog.id, -1);
            closed();
            close();
        }
        super.keyTyped(c, i);
    }

    @Override
    public void mouseClicked(int i, int j, int k) {
        if ((selected == -1 && options.isEmpty() || selected >= 0) && k == 0) {
            handleDialogSelection();
        }
    }

    private void handleDialogSelection() {
        int optionId = -1;
        if (dialog.showWheel)
            optionId = selected;
        else if (!options.isEmpty())
            optionId = options.get(selected);
        NoppesUtilPlayer.sendData(EnumPlayerPacket.Dialog, dialog.id, optionId);
        if (dialog == null || !dialog.hasOtherOptions() || options.isEmpty()) {
            closed();
            close();
            return;
        }
        DialogOption option = dialog.options.get(optionId);
        if (option == null || option.optionType != OptionType.DIALOG_OPTION) {
            closed();
            close();
            return;
        }

        lines.add(new TextBlockClient(player.getDisplayNameString(), option.title, 280, option.optionColor, player, npc));
        calculateRowHeight();

        NoppesUtil.clickSound();

    }

    private void closed() {
        grabMouse(false);
        NoppesUtilPlayer.sendData(EnumPlayerPacket.CheckQuestCompletion);
    }

    public void appendDialog(Dialog dialog) {
        closeOnEsc = !dialog.disableEsc;
        this.dialog = dialog;
        this.options = new ArrayList<>();

        if (dialog.sound != null && !dialog.sound.isEmpty()) {
            MusicController.Instance.stopMusic();
            MusicController.Instance.playSound(SoundCategory.VOICE, dialog.sound, (float) npc.posX, (float) npc.posY, (float) npc.posZ);
        }

        lines.add(new TextBlockClient(npc, dialog.text, 280, 0xe0e0e0, player, npc));

        for (int slot : dialog.options.keySet()) {
            DialogOption option = dialog.options.get(slot);
            if (option == null || !option.isAvailable(player))
                continue;
            options.add(slot);
        }
        calculateRowHeight();

        grabMouse(dialog.showWheel);
    }

    private void calculateRowHeight() {
        if (dialog.showWheel) {
            dialogHeight = ySize - 58;
        } else {
            dialogHeight = ySize - 3 * ClientProxy.Companion.getFont().height(null) - 4;
            if (dialog.options.size() > 3) {
                dialogHeight -= (dialog.options.size() - 3) * ClientProxy.Companion.getFont().height(null);
            }
        }
        rowTotal = 0;
        for (TextBlockClient block : lines) {
            rowTotal += block.lines.size() + 1;
        }
        int max = dialogHeight / ClientProxy.Companion.getFont().height(null);

        rowStart = rowTotal - max;
        if (rowStart < 0)
            rowStart = 0;
    }

    @Override
    public void setClose(int i, NBTTagCompound data) {
        grabMouse(false);
    }

    @Override
    public void save() {

    }
}


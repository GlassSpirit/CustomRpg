package noppes.npcs.client.gui.player;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import noppes.npcs.NoppesUtilPlayer;
import noppes.npcs.api.handler.data.IQuest;
import noppes.npcs.client.CustomNpcResourceListener;
import noppes.npcs.client.TextBlockClient;
import noppes.npcs.client.gui.util.GuiNPCInterface;
import noppes.npcs.client.gui.util.GuiNpcButton;
import noppes.npcs.client.gui.util.GuiNpcLabel;
import noppes.npcs.client.gui.util.ITopButtonListener;
import noppes.npcs.constants.EnumPlayerPacket;

public class GuiQuestCompletion extends GuiNPCInterface implements ITopButtonListener {

    private IQuest quest;
    private final ResourceLocation resource = new ResourceLocation("customnpcs", "textures/gui/smallbg.png");

    public GuiQuestCompletion(IQuest quest) {
        super();
        xSize = 176;
        ySize = 222;
        this.quest = quest;
        this.drawDefaultBackground = false;
        title = "";
    }

    @Override
    public void initGui() {
        super.initGui();

        String questTitle = I18n.translateToLocal(quest.getName());
        int left = (xSize - this.fontRenderer.getStringWidth(questTitle)) / 2;
        this.addLabel(new GuiNpcLabel(0, questTitle, guiLeft + left, guiTop + 4));

        this.addButton(new GuiNpcButton(0, guiLeft + 38, guiTop + ySize - 24, 100, 20, I18n.translateToLocal("quest.complete")));
    }

    @Override
    public void drawScreen(int i, int j, float f) {
        drawDefaultBackground();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        mc.renderEngine.bindTexture(resource);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
        drawHorizontalLine(guiLeft + 4, guiLeft + 170, guiTop + 13, +0xFF000000 + CustomNpcResourceListener.DefaultTextColor);


        drawQuestText();
        super.drawScreen(i, j, f);
    }

    private void drawQuestText() {
        int xoffset = guiLeft + 4;
        TextBlockClient block = new TextBlockClient(quest.getCompleteText(), 172, true, player);
        int yoffset = guiTop + 20;
        for (int i = 0; i < block.lines.size(); i++) {
            String text = block.lines.get(i).getFormattedText();
            fontRenderer.drawString(text, guiLeft + 4, guiTop + 16 + (i * fontRenderer.FONT_HEIGHT), CustomNpcResourceListener.DefaultTextColor);
        }
    }

    @Override
    protected void actionPerformed(GuiButton guibutton) {
        if (guibutton.id == 0) {
            NoppesUtilPlayer.sendData(EnumPlayerPacket.QuestCompletion, quest.getId());
            close();
        }
    }

    @Override
    public void keyTyped(char c, int i) {
        if (i == 1 || isInventoryKey(i)) {
            close();
        }
    }

    @Override
    public void save() {
        NoppesUtilPlayer.sendData(EnumPlayerPacket.QuestCompletion, quest.getId());
    }

}

package noppes.npcs.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.text.ITextComponent;
import noppes.npcs.CustomNpcs;
import noppes.npcs.IChatMessages;
import noppes.npcs.entity.EntityNPCInterface;
import org.lwjgl.opengl.GL11;

import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class RenderChatMessages implements IChatMessages {
    private Map<Long, TextBlockClient> messages = new TreeMap<Long, TextBlockClient>();

    private int boxLength = 46;
    private float scale = 0.5f;

    private String lastMessage = "";
    private long lastMessageTime = 0;

    @Override
    public void addMessage(String message, EntityNPCInterface npc) {
        if (!CustomNpcs.EnableChatBubbles)
            return;
        long time = System.currentTimeMillis();
        if (message.equals(lastMessage) && lastMessageTime + 5000 > time) {
            return;
        }
        Map<Long, TextBlockClient> messages = new TreeMap<Long, TextBlockClient>(this.messages);
        messages.put(time, new TextBlockClient(message, (boxLength * 4), true, Minecraft.getMinecraft().player, npc));

        if (messages.size() > 3) {
            messages.remove(messages.keySet().iterator().next());
        }
        this.messages = messages;
        lastMessage = message;
        lastMessageTime = time;
    }

    @Override
    public void renderMessages(double par3, double par5, double par7, float textscale, boolean inRange) {
        Map<Long, TextBlockClient> messages = getMessages();
        if (messages.isEmpty())
            return;
        if (inRange)
            render(par3, par5, par7, textscale, false);
        render(par3, par5, par7, textscale, true);
    }

    private void render(double par3, double par5, double par7, float textscale, boolean depth) {
        FontRenderer font = Minecraft.getMinecraft().fontRenderer;
        float var13 = 1.6F;
        float var14 = 0.016666668F * var13;
        GlStateManager.pushMatrix();
        int size = 0;
        for (TextBlockClient block : messages.values())
            size += block.lines.size();
        Minecraft mc = Minecraft.getMinecraft();
        int textYSize = (int) (size * font.FONT_HEIGHT * scale);
        GlStateManager.translate((float) par3 + 0.0F, (float) par5 + textYSize * textscale * var14, (float) par7);
        GlStateManager.scale(textscale, textscale, textscale);
        GL11.glNormal3f(0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-mc.getRenderManager().playerViewY, 0.0F, 1F, 0.0F);
        GlStateManager.rotate(mc.getRenderManager().playerViewX, 1F, 0.0F, 0.0F);
        GlStateManager.scale(-var14, -var14, var14);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        GlStateManager.depthMask(true);
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        if (depth)
            GlStateManager.enableDepth();
        else
            GlStateManager.disableDepth();
        int black = depth ? 0xFF000000 : 0x55000000;
        int white = depth ? 0xBBFFFFFF : 0x44FFFFFF;
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.disableTexture2D();
        GlStateManager.enableCull();

        drawRect(-boxLength - 2, -2, boxLength + 2, textYSize + 1, white, 0.11);

        drawRect(-boxLength - 1, -3, boxLength + 1, -2, black, 0.1); //top
        drawRect(-boxLength - 1, textYSize + 2, -1, textYSize + 1, black, 0.1); //bottom1
        drawRect(3, textYSize + 2, boxLength + 1, textYSize + 1, black, 0.1); //bottom2
        drawRect(-boxLength - 3, -1, -boxLength - 2, textYSize, black, 0.1); //left
        drawRect(boxLength + 3, -1, boxLength + 2, textYSize, black, 0.1); //right

        drawRect(-boxLength - 2, -2, -boxLength - 1, -1, black, 0.1);
        drawRect(boxLength + 2, -2, boxLength + 1, -1, black, 0.1);
        drawRect(-boxLength - 2, textYSize + 1, -boxLength - 1, textYSize, black, 0.1);
        drawRect(boxLength + 2, textYSize + 1, boxLength + 1, textYSize, black, 0.1);

        drawRect(0, textYSize + 1, 3, textYSize + 4, white, 0.11);
        drawRect(-1, textYSize + 4, 1, textYSize + 5, white, 0.11);

        drawRect(-1, textYSize + 1, 0, textYSize + 4, black, 0.1);
        drawRect(3, textYSize + 1, 4, textYSize + 3, black, 0.1);
        drawRect(2, textYSize + 3, 3, textYSize + 4, black, 0.1);
        drawRect(1, textYSize + 4, 2, textYSize + 5, black, 0.1);
        drawRect(-2, textYSize + 4, -1, textYSize + 5, black, 0.1);

        drawRect(-2, textYSize + 5, 1, textYSize + 6, black, 0.1);
        GlStateManager.enableTexture2D();
        GlStateManager.depthMask(true);

        GlStateManager.scale(scale, scale, scale);
        int index = 0;
        for (TextBlockClient block : messages.values()) {
            for (ITextComponent chat : block.lines) {
                String message = chat.getFormattedText();
                font.drawString(message, -font.getStringWidth(message) / 2, index * font.FONT_HEIGHT, black);
                index++;
            }
        }
        GlStateManager.disableCull();
        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.enableDepth();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();
    }

    private void drawRect(int par0, int par1, int par2, int par3, int par4, double par5) {
        int j1;

        if (par0 < par2) {
            j1 = par0;
            par0 = par2;
            par2 = j1;
        }

        if (par1 < par3) {
            j1 = par1;
            par1 = par3;
            par3 = j1;
        }

        float f = (float) (par4 >> 24 & 255) / 255.0F;
        float f1 = (float) (par4 >> 16 & 255) / 255.0F;
        float f2 = (float) (par4 >> 8 & 255) / 255.0F;
        float f3 = (float) (par4 & 255) / 255.0F;
        BufferBuilder tessellator = Tessellator.getInstance().getBuffer();
        GlStateManager.color(f1, f2, f3, f);
        tessellator.begin(7, DefaultVertexFormats.POSITION);
        tessellator.pos((double) par0, (double) par3, par5).endVertex();
        tessellator.pos((double) par2, (double) par3, par5).endVertex();
        tessellator.pos((double) par2, (double) par1, par5).endVertex();
        tessellator.pos((double) par0, (double) par1, par5).endVertex();
        Tessellator.getInstance().draw();
    }

    private Map<Long, TextBlockClient> getMessages() {
        Map<Long, TextBlockClient> messages = new TreeMap<Long, TextBlockClient>();
        long time = System.currentTimeMillis();
        for (Entry<Long, TextBlockClient> entry : this.messages.entrySet()) {
            if (time > entry.getKey() + 10000)
                continue;
            messages.put(entry.getKey(), entry.getValue());
        }
        return this.messages = messages;
    }
}

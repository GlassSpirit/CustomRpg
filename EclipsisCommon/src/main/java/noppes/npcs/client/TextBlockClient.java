package noppes.npcs.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import noppes.npcs.NoppesStringUtils;
import noppes.npcs.TextBlock;

public class TextBlockClient extends TextBlock {
    private Style style;
    public int color = 0xe0e0e0;
    private String name;
    private ICommandSender sender;

    public TextBlockClient(String name, String text, int lineWidth, int color, Object... obs) {
        this(text, lineWidth, false, obs);
        this.color = color;
        this.name = name;
    }

    public TextBlockClient(ICommandSender sender, String text, int lineWidth, int color, Object... obs) {
        this(text, lineWidth, false, obs);
        this.color = color;
        this.sender = sender;
    }

    public String getName() {
        if (sender != null)
            return sender.getName();
        return name;
    }

    public TextBlockClient(String text, int lineWidth, boolean mcFont, Object... obs) {
        style = new Style();
        text = NoppesStringUtils.formatText(text, obs);

        String line = "";
        text = text.replace("\n", " \n ");
        text = text.replace("\r", " \r ");
        String[] words = text.split(" ");

        FontRenderer font = Minecraft.getMinecraft().fontRenderer;
        for (String word : words) {
            if (word.isEmpty())
                continue;
            if (word.length() == 1) {
                char c = word.charAt(0);
                if (c == '\r' || c == '\n') {
                    addLine(line);
                    line = "";
                    continue;
                }
            }
            String newLine;
            if (line.isEmpty())
                newLine = word;
            else
                newLine = line + " " + word;

            if ((mcFont ? font.getStringWidth(newLine) : ClientProxy.Companion.getFont().width(newLine)) > lineWidth) {
                addLine(line);
                line = word.trim();
            } else {
                line = newLine;
            }
        }
        if (!line.isEmpty())
            addLine(line);
    }

    private void addLine(String text) {
        TextComponentString line = new TextComponentString(text);
        line.setStyle(style);
        lines.add(line);
    }
}

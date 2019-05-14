package noppes.npcs.client.gui.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ChatAllowedCharacters;
import noppes.npcs.common.CustomNpcsConfig;
import noppes.npcs.util.NoppesStringUtils;
import noppes.npcs.client.gui.util.TextContainer.LineData;
import noppes.npcs.common.config.TrueTypeFont;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

public class GuiTextArea extends Gui implements IGui, IKeyListener, IMouseListener {
    public int id;
    public int x, y, width, height;
    private int cursorCounter;

    private ITextChangeListener listener;

    private static TrueTypeFont font = new TrueTypeFont(new Font("Arial Unicode MS", Font.PLAIN, CustomNpcsConfig.FontSize), 1);

    public String text = null;

    private TextContainer container = null;

    public boolean active = false, enabled = true, visible = true, clicked = false, doubleClicked = false, clickScrolling = false;

    private int startSelection, endSelection, cursorPosition;

    private int scrolledLine = 0;

    private boolean enableCodeHighlighting = false;
    private static final char colorChar = '\uFFFF';

    public List<UndoData> undoList = new ArrayList<>();
    public List<UndoData> redoList = new ArrayList<>();
    public boolean undoing = false;

    private long lastClicked = 0;

    public GuiTextArea(int id, int x, int y, int width, int height, String text) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        undoing = true;
        setText(text);
        undoing = false;
        font.setSpecial(colorChar);

    }

    @Override
    public void drawScreen(int xMouse, int yMouse) {
        if (!visible)
            return;
        drawRect(x - 1, y - 1, x + width + 1, y + height + 1, 0xffa0a0a0);
        drawRect(x, y, x + width, y + height, 0xff000000);

        container.visibleLines = height / container.lineHeight;

        if (clicked) {
            clicked = Mouse.isButtonDown(0);
            int i = getSelectionPos(xMouse, yMouse);
            if (i != cursorPosition) {
                if (doubleClicked) {
                    startSelection = endSelection = cursorPosition;
                    doubleClicked = false;
                }
                setCursor(i, true);
            }
        } else if (doubleClicked) {
            doubleClicked = false;
        }

        if (clickScrolling) {
            clickScrolling = Mouse.isButtonDown(0);
            int diff = container.linesCount - container.visibleLines;
            scrolledLine = Math.min(Math.max((int) (1f * diff * (yMouse - y) / height), 0), diff);
        }
        int startBracket = 0, endBracket = 0;
        if (endSelection - startSelection == 1 || startSelection == endSelection && startSelection < text.length()) {
            char c = text.charAt(startSelection);
            int found = 0;
            if (c == '{') {
                found = findClosingBracket(text.substring(startSelection), '{', '}');
            } else if (c == '[') {
                found = findClosingBracket(text.substring(startSelection), '[', ']');
            } else if (c == '(') {
                found = findClosingBracket(text.substring(startSelection), '(', ')');
            } else if (c == '}') {
                found = findOpeningBracket(text.substring(0, startSelection + 1), '{', '}');
            } else if (c == ']') {
                found = findOpeningBracket(text.substring(0, startSelection + 1), '[', ']');
            } else if (c == ')') {
                found = findOpeningBracket(text.substring(0, startSelection + 1), '(', ')');
            }
            if (found != 0) {
                startBracket = startSelection;
                endBracket = startSelection + found;
            }
        }

        List<LineData> list = new ArrayList<>(container.lines);

        String wordHightLight = null;
        if (startSelection != endSelection) {
            Matcher m = container.regexWord.matcher(text);
            while (m.find()) {
                if (m.start() == startSelection && m.end() == endSelection) {
                    wordHightLight = text.substring(startSelection, endSelection);
                }
            }
        }
        for (int i = 0; i < list.size(); i++) {
            LineData data = list.get(i);
            String line = data.text;
            int w = line.length();
            if (startBracket != endBracket) {
                if (startBracket >= data.start && startBracket < data.end) {
                    int s = font.width(line.substring(0, startBracket - data.start));
                    int e = font.width(line.substring(0, startBracket - data.start + 1)) + 1;
                    int posY = y + 1 + (i - scrolledLine) * container.lineHeight;
                    drawRect(x + 1 + s, posY, x + 1 + e, posY + container.lineHeight + 1, 0x9900cc00);
                }
                if (endBracket >= data.start && endBracket < data.end) {
                    int s = font.width(line.substring(0, endBracket - data.start));
                    int e = font.width(line.substring(0, endBracket - data.start + 1)) + 1;
                    int posY = y + 1 + (i - scrolledLine) * container.lineHeight;
                    drawRect(x + 1 + s, posY, x + 1 + e, posY + container.lineHeight + 1, 0x9900cc00);
                }
            }
            if (i >= scrolledLine && i < scrolledLine + container.visibleLines) {
                if (wordHightLight != null) {
                    Matcher m = container.regexWord.matcher(line);
                    while (m.find()) {
                        if (line.substring(m.start(), m.end()).equals(wordHightLight)) {
                            int s = font.width(line.substring(0, m.start()));
                            int e = font.width(line.substring(0, m.end())) + 1;
                            int posY = y + 1 + (i - scrolledLine) * container.lineHeight;
                            drawRect(x + 1 + s, posY, x + 1 + e, posY + container.lineHeight + 1, 0x99004c00);
                        }
                    }
                }
                if (startSelection != endSelection && endSelection > data.start && startSelection <= data.end) {
                    if (startSelection < data.end) {
                        int s = font.width(line.substring(0, Math.max(startSelection - data.start, 0)));
                        int e = font.width(line.substring(0, Math.min(endSelection - data.start, w))) + 1;
                        int posY = y + 1 + (i - scrolledLine) * container.lineHeight;
                        drawRect(x + 1 + s, posY, x + 1 + e, posY + container.lineHeight + 1, 0x990000ff);
                    }
                }
                int yPos = y + (i - scrolledLine) * container.lineHeight + 1;
                font.draw(data.getFormattedString(), x + 1, yPos, 0xFFe0e0e0);

                if (active && isEnabled() && (cursorCounter / 6) % 2 == 0 && cursorPosition >= data.start && cursorPosition < data.end) {
                    int posX = x + font.width(line.substring(0, cursorPosition - data.start));
                    drawRect(posX + 1, yPos, posX + 2, yPos + 1 + container.lineHeight, -3092272);
                }
            }
        }

        if (hasVerticalScrollbar()) {
            Minecraft.getMinecraft().renderEngine.bindTexture(GuiCustomScroll.resource);
            int sbSize = Math.max((int) (1f * container.visibleLines / container.linesCount * height), 2);

            int posX = x + width - 6;
            int posY = (int) (y + 1f * scrolledLine / container.linesCount * (height - 4)) + 1;

            drawRect(posX, posY, posX + 5, posY + sbSize, 0xFFe0e0e0);
        }
    }

    private int findClosingBracket(String str, char s, char e) {
        int found = 0;
        char[] chars = str.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if (c == s) {
                found++;
            } else if (c == e) {
                found--;
                if (found == 0)
                    return i;
            }
        }
        return 0;
    }

    private int findOpeningBracket(String str, char s, char e) {
        int found = 0;
        char[] chars = str.toCharArray();
        for (int i = chars.length - 1; i >= 0; i--) {
            char c = chars[i];
            if (c == e) {
                found++;
            } else if (c == s) {
                found--;
                if (found == 0)
                    return i - chars.length + 1;
            }
        }
        return 0;
    }

    private int getSelectionPos(int xMouse, int yMouse) {
        xMouse -= x + 1;
        yMouse -= y + 1;

        List<LineData> list = new ArrayList<>(container.lines);
        for (int i = 0; i < list.size(); i++) {
            LineData data = list.get(i);
            if (i >= scrolledLine && i < scrolledLine + container.visibleLines) {
                int yPos = (i - scrolledLine) * container.lineHeight;
                if (yMouse >= yPos && yMouse < yPos + container.lineHeight) {
                    int lineWidth = 0;
                    char[] chars = data.text.toCharArray();
                    for (int j = 1; j <= chars.length; j++) {
                        int w = font.width(data.text.substring(0, j));
                        if (xMouse < lineWidth + (w - lineWidth) / 2) {
                            return data.start + j - 1;
                        }
                        lineWidth = w;
                    }
                    return data.end - 1;
                }
            }
        }
        return container.text.length();
    }

    @Override
    public int getID() {
        return id;
    }

    @Override
    public void keyTyped(char c, int i) {
        if (!active)
            return;

        if (GuiScreen.isKeyComboCtrlA(i)) {
            startSelection = cursorPosition = 0;
            endSelection = text.length();
            return;
        }

        if (!isEnabled())
            return;

        String original = text;
        if (i == Keyboard.KEY_LEFT) {
            int j = 1;
            if (GuiScreen.isCtrlKeyDown()) {
                Matcher m = container.regexWord.matcher(text.substring(0, cursorPosition));
                while (m.find()) {
                    if (m.start() == m.end())
                        continue;
                    j = cursorPosition - m.start();
                }
            }
            setCursor(cursorPosition - j, GuiScreen.isShiftKeyDown());
            return;
        }
        if (i == Keyboard.KEY_RIGHT) {
            int j = 1;
            if (GuiScreen.isCtrlKeyDown()) {
                Matcher m = container.regexWord.matcher(text.substring(cursorPosition));
                if (m.find() && m.start() > 0 || m.find()) {
                    j = m.start();
                }
            }
            setCursor(cursorPosition + j, GuiScreen.isShiftKeyDown());
            return;
        }
        if (i == Keyboard.KEY_UP) {
            setCursor(cursorUp(), GuiScreen.isShiftKeyDown());
            return;
        }
        if (i == Keyboard.KEY_DOWN) {
            setCursor(cursorDown(), GuiScreen.isShiftKeyDown());
            return;
        }
        if (i == Keyboard.KEY_DELETE) {
            String s = getSelectionAfterText();
            if (!s.isEmpty() && startSelection == endSelection)
                s = s.substring(1);
            setText(getSelectionBeforeText() + s);
            endSelection = cursorPosition = startSelection;
            return;
        }
        if (i == Keyboard.KEY_BACK) {
            String s = getSelectionBeforeText();
            if (startSelection > 0 && startSelection == endSelection) {
                s = s.substring(0, s.length() - 1);
                startSelection--;
            }
            setText(s + getSelectionAfterText());
            endSelection = cursorPosition = startSelection;
            return;
        }
        if (GuiScreen.isKeyComboCtrlX(i)) {
            if (startSelection != endSelection) {
                NoppesStringUtils.setClipboardContents(text.substring(startSelection, endSelection));
                String s = getSelectionBeforeText();
                setText(s + getSelectionAfterText());
                endSelection = startSelection = cursorPosition = s.length();

            }
            return;
        }
        if (GuiScreen.isKeyComboCtrlC(i)) {
            if (startSelection != endSelection) {
                NoppesStringUtils.setClipboardContents(text.substring(startSelection, endSelection));
            }
            return;
        }
        if (GuiScreen.isKeyComboCtrlV(i)) {
            addText(NoppesStringUtils.getClipboardContents());
            return;
        }
        if (i == Keyboard.KEY_Z && GuiScreen.isCtrlKeyDown()) {
            if (undoList.isEmpty())
                return;
            undoing = true;
            redoList.add(new UndoData(this.text, this.cursorPosition));
            UndoData data = undoList.remove(undoList.size() - 1);
            setText(data.text);
            endSelection = startSelection = cursorPosition = data.cursorPosition;
            undoing = false;
            return;
        }
        if (i == Keyboard.KEY_Y && GuiScreen.isCtrlKeyDown()) {
            if (redoList.isEmpty())
                return;
            undoing = true;
            undoList.add(new UndoData(this.text, this.cursorPosition));
            UndoData data = redoList.remove(redoList.size() - 1);
            setText(data.text);
            endSelection = startSelection = cursorPosition = data.cursorPosition;
            undoing = false;
            return;
        }
        if (i == Keyboard.KEY_TAB) {
            addText("    ");
        }
        if (i == Keyboard.KEY_RETURN) {
            addText('\n' + getIndentCurrentLine());
        }
        if (ChatAllowedCharacters.isAllowedCharacter(c)) {
            addText(Character.toString(c));
        }
    }

    private String getIndentCurrentLine() {
        for (LineData data : container.lines) {
            if (cursorPosition > data.start && cursorPosition <= data.end) {
                int i = 0;
                while (i < data.text.length() && data.text.charAt(i) == ' ') {
                    i++;
                }
                return data.text.substring(0, i);
            }
        }
        return "";
    }

    private void setCursor(int i, boolean select) {
        i = Math.min(Math.max(i, 0), text.length());
        if (i == cursorPosition) {
            return;
        }
        if (!select) {
            endSelection = startSelection = cursorPosition = i;
            return;
        }
        int diff = cursorPosition - i;
        if (cursorPosition == startSelection) {
            startSelection -= diff;
        } else if (cursorPosition == endSelection) {
            endSelection -= diff;
        }
        if (startSelection > endSelection) {
            int j = endSelection;
            endSelection = startSelection;
            startSelection = j;
        }

        cursorPosition = i;
    }

    private void addText(String s) {
        setText(getSelectionBeforeText() + s + getSelectionAfterText());
        endSelection = startSelection = cursorPosition = startSelection + s.length();
    }

    private int cursorUp() {
        for (int i = 0; i < container.lines.size(); i++) {
            LineData data = container.lines.get(i);
            if (cursorPosition >= data.start && cursorPosition < data.end) {
                if (i == 0) {
                    return 0;
                }
                int linePos = cursorPosition - data.start;
                return getSelectionPos(x + 1 + font.width(data.text.substring(0, cursorPosition - data.start)),
                        y + 1 + (i - 1 - scrolledLine) * container.lineHeight);
            }
        }
        return 0;
    }

    private int cursorDown() {
        for (int i = 0; i < container.lines.size(); i++) {
            LineData data = container.lines.get(i);
            if (cursorPosition >= data.start && cursorPosition < data.end) {
                int linePos = cursorPosition - data.start;
                return getSelectionPos(x + 1 + font.width(data.text.substring(0, cursorPosition - data.start)),
                        y + 1 + (i + 1 - scrolledLine) * container.lineHeight);
            }
        }
        return text.length();
    }

    public String getSelectionBeforeText() {
        if (startSelection == 0)
            return "";
        return text.substring(0, startSelection);
    }

    public String getSelectionAfterText() {
        return text.substring(endSelection);
    }

    @Override
    public boolean mouseClicked(int xMouse, int yMouse, int mouseButton) {
        active = xMouse >= this.x && xMouse < this.x + this.width && yMouse >= this.y && yMouse < this.y + this.height;
        if (active) {
            startSelection = endSelection = cursorPosition = getSelectionPos(xMouse, yMouse);
            clicked = mouseButton == 0;
            doubleClicked = false;
            long time = System.currentTimeMillis();
            if (clicked && container.linesCount * container.lineHeight > height && xMouse > x + width - 8) {
                clicked = false;
                clickScrolling = true;
            } else if (time - lastClicked < 500) {
                doubleClicked = true;
                Matcher m = container.regexWord.matcher(text);
                while (m.find()) {
                    if (cursorPosition > m.start() && cursorPosition < m.end()) {
                        startSelection = m.start();
                        endSelection = m.end();
                        break;
                    }
                }
            }
            lastClicked = time;
        }
        return active;
    }

    @Override
    public void updateScreen() {
        cursorCounter++;

        int k2 = Mouse.getDWheel();
        if (k2 != 0) {
            scrolledLine += k2 > 0 ? -1 : 1;
            scrolledLine = Math.max(Math.min(scrolledLine, container.linesCount - height / container.lineHeight), 0);
        }
    }

    public void setText(String text) {
        text = text.replace("\r", "");
        if (this.text != null && this.text.equals(text))
            return;

        if (listener != null) {
            listener.textUpdate(text);
        }

        if (!undoing) {
            undoList.add(new UndoData(this.text, this.cursorPosition));
            redoList.clear();
        }

        this.text = text;
        this.container = new TextContainer(text);
        container.init(font, width, height);
        if (enableCodeHighlighting) {
            container.formatCodeText();
        }

        if (scrolledLine > container.linesCount - container.visibleLines) {
            scrolledLine = Math.max(0, container.linesCount - container.visibleLines);
        }
    }

    public String getText() {
        return text;
    }

    public boolean isEnabled() {
        return enabled && visible;
    }

    public boolean hasVerticalScrollbar() {
        return container.visibleLines < container.linesCount;
    }

    public void enableCodeHighlighting() {
        enableCodeHighlighting = true;
        container.formatCodeText();
    }

    public void setListener(ITextChangeListener listener) {
        this.listener = listener;
    }

    class UndoData {
        public String text;
        public int cursorPosition;

        public UndoData(String text, int cursorPosition) {
            this.text = text;
            this.cursorPosition = cursorPosition;
        }
    }

    @Override
    public boolean isActive() {
        return active;
    }
}

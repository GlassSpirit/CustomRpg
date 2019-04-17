package noppes.npcs;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.translation.I18n;
import noppes.npcs.entity.EntityNPCInterface;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.util.Arrays;

public class NoppesStringUtils {

    final static int[] illegalChars = {34, 60, 62, 124, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 58, 42, 63, 92, 47};

    static {
        Arrays.sort(illegalChars);
    }

    public static String cleanFileName(String badFileName) {
        StringBuilder cleanName = new StringBuilder();
        for (int i = 0; i < badFileName.length(); i++) {
            int c = (int) badFileName.charAt(i);
            if (Arrays.binarySearch(illegalChars, c) < 0) {
                cleanName.append((char) c);
            }
        }
        return cleanName.toString();
    }

    public static String removeHidden(String text) {
        StringBuilder newString = new StringBuilder(text.length());
        for (int offset = 0; offset < text.length(); ) {
            int codePoint = text.codePointAt(offset);
            offset += Character.charCount(codePoint);

            // Replace invisible control characters and unused code points
            switch (Character.getType(codePoint)) {
                case Character.FORMAT:      // \p{Cf}
                case Character.PRIVATE_USE: // \p{Co}
                case Character.SURROGATE:   // \p{Cs}
                case Character.UNASSIGNED:  // \p{Cn}
                    break;
                default:
                    newString.append(Character.toChars(codePoint));
                    break;
            }
        }
        return newString.toString();
    }

    public static String formatText(String text, Object... obs) {
        if (text == null || text.isEmpty())
            return "";
        text = translate(text);
        for (Object ob : obs) {
            if (ob instanceof EntityPlayer) {
                String username = ((EntityPlayer) ob).getDisplayNameString();
                text = text.replace("{player}", username);
                text = text.replace("@p", username);
            } else if (ob instanceof EntityNPCInterface)
                text = text.replace("@npc", ((EntityNPCInterface) ob).getName());

        }
        text = text.replace("&", Character.toChars(167)[0] + "");
        return text;
    }

    public static void setClipboardContents(String aString) {
        StringSelection stringSelection = new StringSelection(aString);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, (arg0, arg1) -> {

        });
    }

    public static String getClipboardContents() {
        String result = "";
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable contents = clipboard.getContents(null);
        boolean hasTransferableText = contents != null && contents.isDataFlavorSupported(DataFlavor.stringFlavor);
        if (hasTransferableText) {
            try {
                result = (String) contents.getTransferData(DataFlavor.stringFlavor);
            } catch (Exception ex) {
                LogWriter.except(ex);
            }
        }
        return removeHidden(result);
    }

    public static String translate(Object... arr) {
        String s = "";
        for (Object str : arr) {
            s += I18n.translateToLocal(str.toString());
        }
        return s;
    }

    public static String[] splitLines(String s) {
        return s.split("\r\n|\r|\n");
    }

    public static String newLine() {
        return System.getProperty("line.separator");
    }

    public static int parseInt(String s, int i) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
        }
        return i;
    }
}

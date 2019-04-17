package noppes.npcs.client;

import net.minecraft.stats.StatBasic;
import net.minecraft.util.text.TextComponentTranslation;

public class QuestAchievement extends StatBasic {

    public String description;
    public String message;

    public QuestAchievement(String message, String description) {
        super("", new TextComponentTranslation(message));
        this.description = description;
        this.message = message;
    }
}

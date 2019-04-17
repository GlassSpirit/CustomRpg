package noppes.npcs.client;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.event.ClickEvent;

public class VersionChecker extends Thread {

    public void run() {
        String name = '\u00A7' + "2CustomNpcs" + '\u00A7' + "f";
        String link = '\u00A7' + "9" + '\u00A7' + "nClick here";
        String text = name + " installed. For more info " + link;

        EntityPlayer player;
        try {
            player = Minecraft.getMinecraft().player;
        } catch (NoSuchMethodError e) {
            return;
        }
        while ((player = Minecraft.getMinecraft().player) == null) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        TextComponentTranslation message = new TextComponentTranslation(text);
        message.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "http://www.kodevelopment.nl/minecraft/customnpcs/"));
        player.sendMessage(message);
    }
}

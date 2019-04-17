package noppes.npcs.client.gui.util;

public interface IGui {

    int getID();

    void drawScreen(int xMouse, int yMouse);

    void updateScreen();

    boolean isActive();
}

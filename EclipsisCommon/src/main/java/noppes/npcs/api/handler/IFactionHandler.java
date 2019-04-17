package noppes.npcs.api.handler;

import noppes.npcs.api.handler.data.IFaction;

import java.util.List;

public interface IFactionHandler {

    List<IFaction> list();

    IFaction delete(int id);

    /**
     * Example: create("Bandits", 0xFF0000)
     */
    IFaction create(String name, int color);

    IFaction get(int id);
}

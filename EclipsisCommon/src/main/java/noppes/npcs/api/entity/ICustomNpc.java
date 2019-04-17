package noppes.npcs.api.entity;

import net.minecraft.entity.EntityCreature;
import noppes.npcs.api.ITimers;
import noppes.npcs.api.entity.data.*;
import noppes.npcs.api.handler.data.IDialog;
import noppes.npcs.api.handler.data.IFaction;
import noppes.npcs.api.item.IItemStack;

public interface ICustomNpc<T extends EntityCreature> extends IEntityLiving<T> {

    INPCDisplay getDisplay();

    INPCInventory getInventory();

    INPCStats getStats();

    INPCAi getAi();

    INPCAdvanced getAdvanced();

    IFaction getFaction();

    void setFaction(int id);

    INPCRole getRole();

    INPCJob getJob();

    ITimers getTimers();

    int getHomeX();

    int getHomeY();

    int getHomeZ();

    /**
     * @return Incase the npc is a Follower or Companion it will return the one who its following. Also works for scene followers
     */
    IEntityLivingBase getOwner();

    void setHome(int x, int y, int z);

    /**
     * Basically completely resets the npc. This will also call the Init script
     */
    void reset();

    void say(String message);

    void sayTo(IPlayer player, String message);

    /**
     * @param item     The item you want to shoot
     * @param accuracy Accuracy of the shot (1-100)
     */
    IEntityProjectile shootItem(IEntityLivingBase target, IItemStack item, int accuracy);

    /**
     * @param item     The item you want to shoot
     * @param accuracy Accuracy of the shot (1-100)
     * @return
     */
    IEntityProjectile shootItem(double x, double y, double z, IItemStack item, int accuracy);

    /**
     * If the player can't carry the item it will fall on the ground. (unless the player is in creative)
     */
    void giveItem(IPlayer player, IItemStack item);

    /**
     * @param slot (0-11)
     */
    void setDialog(int slot, IDialog dialog);

    /**
     * @param slot (0-11)
     */
    IDialog getDialog(int slot);

    /**
     * Force update client. Normally it updates client once every 10 ticks
     */
    void updateClient();

    /**
     * On servers the enable-command-block option in the server.properties needs to be set to true <br>
     * Use /gamerule commandBlockOutput false/true to turn off/on command block feedback <br>
     * Setting NpcUseOpCommands to true in the CustomNPCs.cfg should allow the npc to run op commands, be warned this could be a major security risk, use at own risk <br>
     * For permission plugins the commands are run under uuid:c9c843f8-4cb1-4c82-aa61-e264291b7bd6 and name:[customnpcs]
     *
     * @param command The command to be executed
     * @return Returns the commands output
     */
    String executeCommand(String command);

}

package noppes.npcs.api.entity;

import net.minecraft.entity.player.EntityPlayerMP;
import noppes.npcs.api.IContainer;
import noppes.npcs.api.ITimers;
import noppes.npcs.api.block.IBlock;
import noppes.npcs.api.handler.data.IQuest;
import noppes.npcs.api.item.IItemStack;

public interface IPlayer<T extends EntityPlayerMP> extends IEntityLivingBase<T> {

    String getDisplayName();

    boolean hasFinishedQuest(int id);

    boolean hasActiveQuest(int id);

    void startQuest(int id);

    /**
     * @return Returns -1:Unfriendly, 0:Neutral, 1:Friendly
     */
    int factionStatus(int factionId);

    /**
     * Add the quest from finished quest list
     *
     * @param id The Quest ID
     */
    void finishQuest(int id);

    /**
     * Removes the quest from active quest list
     *
     * @param id The Quest ID
     */
    void stopQuest(int id);

    /**
     * Removes the quest from active and finished quest list
     *
     * @param id The Quest ID
     */
    void removeQuest(int id);

    boolean hasReadDialog(int id);

    /**
     * @param name Name of the person talking in the dialog
     */
    void showDialog(int id, String name);

    /**
     * @param id Removes the given id from the read dialogs list
     */
    void removeDialog(int id);

    /**
     * @param id Adds the given id to the read dialogs
     */
    void addDialog(int id);

    /**
     * @param faction The faction id
     * @param points  The points to increase. Use negative values to decrease
     */
    void addFactionPoints(int faction, int points);

    /**
     * @param faction The faction id
     * @return points
     */
    int getFactionPoints(int faction);

    void message(String message);

    int getGamemode();

    void setGamemode(int mode);

    /**
     * @param item The item to be checked
     * @return How many of this item the player has
     */
    int inventoryItemCount(IItemStack item);

    /**
     * @param id     The items name
     * @param damage The damage value (give -1 for any damage value)
     * @return How many of this item the player has
     */
    int inventoryItemCount(String id, int damage);

    /**
     * @return Returns a IItemStack array size 36
     */
    IContainer getInventory();

    /**
     * @param item   The Item type to be removed
     * @param amount How many will be removed
     * @return Returns true if the items were removed succesfully. Returns false incase a bigger amount than what the player has was given
     */
    boolean removeItem(IItemStack item, int amount);

    /**
     * @param id     The items name
     * @param damage The damage value (give -1 for any damage value)
     * @param amount How many will be removed
     * @return Returns true if the items were removed succesfully. Returns false incase a bigger amount than what the player has was given or item doesnt exist
     */
    boolean removeItem(String id, int damage, int amount);

    void removeAllItems(IItemStack item);

    /**
     * @param item Item to be added
     * @return Returns whether or not it gave the item succesfully
     */
    boolean giveItem(IItemStack item);


    /**
     * @param id     The items name
     * @param damage The damage value
     * @param amount The amount of the item to be added
     * @return Returns whether or not it gave the item succesfully
     */
    boolean giveItem(String id, int damage, int amount);


    /**
     * Same as the /spawnpoint command
     *
     * @param x The x position
     * @param y The y position
     * @param z The z position
     */
    void setSpawnpoint(int x, int y, int z);

    void resetSpawnpoint();

    /**
     * @param achievement The achievement id. For a complete list see <a href="http://minecraft.gamepedia.com/Achievements>Achievements</a>
     * @return Returns whether or not the player has this achievement
     */
    boolean hasAchievement(String achievement);

    int getExpLevel();

    void setExpLevel(int level);

    boolean hasPermission(String permission);

    Object getPixelmonData();

    ITimers getTimers();

    void closeGui();

    @Override
    T getMCEntity();

    IBlock getSpawnPoint();

    void setSpawnPoint(IBlock block);

    int getHunger();

    void setHunger(int level);

    /**
     * @param message The message the player gets when kicked
     */
    void kick(String message);

    /**
     * @param title Title of the notification
     * @param msg   Message of the notification
     * @param type  (0-3) notification background type
     */
    void sendNotification(String title, String msg, int type);

    /**
     * WANRING, REMOVES ALL PLAYER DATA (data only from CustomNPCs, does not clear inventory etc)
     */
    void clearData();

    IQuest[] getActiveQuests();

    IQuest[] getFinishedQuests();

    /**
     * Syncs inventory changes to the client side. Also checks Item Quests for completion
     */
    void updatePlayerInventory();

}

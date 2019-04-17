package noppes.npcs.api;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import noppes.npcs.api.block.IBlock;
import noppes.npcs.api.entity.IEntity;
import noppes.npcs.api.entity.IPlayer;
import noppes.npcs.api.entity.data.IData;
import noppes.npcs.api.item.IItemStack;

public interface IWorld {

    /**
     * @deprecated
     */
    IEntity[] getNearbyEntities(int x, int y, int z, int range, int type);

    IEntity[] getNearbyEntities(IPos pos, int range, int type);

    /**
     * @deprecated
     */
    IEntity getClosestEntity(int x, int y, int z, int range, int type);

    IEntity getClosestEntity(IPos pos, int range, int type);

    /**
     * This gets all currently loaded entities in a world
     *
     * @param type {@link noppes.npcs.api.constants.EntityType}}
     * @return An array of all entities
     */
    IEntity[] getAllEntities(int type);

    /**
     * @return The world time
     */
    long getTime();

    void setTime(long time);

    /**
     * @return The total world time (doesn't change with the /time set command
     */
    long getTotalTime();

    /**
     * @return The block at the given position. Returns null if there isn't a block
     */
    IBlock getBlock(int x, int y, int z);

    void setBlock(int x, int y, int z, String name, int meta);

    void removeBlock(int x, int y, int z);

    /**
     * @return Returns a value between 0 and 1
     */
    float getLightValue(int x, int y, int z);

    /**
     * @param name The name of the player to be returned
     * @return The Player with name. Null is returned when the player isnt found
     */
    IPlayer getPlayer(String name);

    boolean isDay();

    boolean isRaining();

    IDimension getDimension();

    void setRaining(boolean bo);

    void thunderStrike(double x, double y, double z);

    /**
     * Sends a packet from the server to the client everytime its called. Probably should not use this too much.
     *
     * @param particle Particle name. Particle name list: http://minecraft.gamepedia.com/Particles
     * @param x        The x position
     * @param y        The y position
     * @param z        The z position
     * @param dx       Usually used for the x motion
     * @param dy       Usually used for the y motion
     * @param dz       Usually used for the z motion
     * @param speed    Speed of the particles, usually between 0 and 1
     * @param count    Particle count
     */
    void spawnParticle(String particle, double x, double y, double z, double dx, double dy, double dz, double speed, int count);

    void broadcast(String message);

    IScoreboard getScoreboard();

    /**
     * Stores any type of data, but will be gone on restart
     * Temp data is the same cross dimension
     */
    IData getTempdata();

    /**
     * Stored data persists through world restart. Unlike tempdata only Strings and Numbers can be saved.
     * Stored data is the same cross dimension
     */
    IData getStoreddata();

    IItemStack createItem(String name, int damage, int size);

    IItemStack createItemFromNbt(INbt nbt);


    /**
     * @param x     Position x
     * @param y     Position y
     * @param z     Position z
     * @param range Range of the explosion
     * @param fire  Whether or not the explosion does fire damage
     * @param grief Whether or not the explosion does damage to blocks
     */
    void explode(double x, double y, double z, float range, boolean fire, boolean grief);

    IPlayer[] getAllPlayers();

    String getBiomeName(int x, int z);

    void spawnEntity(IEntity entity);

    /**
     * Depricated, use the API.clones.spawn instead
     */
    @Deprecated
    IEntity spawnClone(double x, double y, double z, int tab, String name);

    /**
     * Depricated, use the API.clones.get instead
     */
    @Deprecated
    IEntity getClone(int tab, String name);

    /**
     * @return value between 0 and 16
     */
    int getRedstonePower(int x, int y, int z);

    /**
     * Expert users only
     *
     * @return Returns minecrafts world
     */
    WorldServer getMCWorld();

    /**
     * Expert users only
     *
     * @return Returns minecraft BlockPos object
     */
    BlockPos getMCBlockPos(int x, int y, int z);

    /**
     * @param uuid entity uuid
     * @return Returns entity based on uuid
     */
    IEntity getEntity(String uuid);

    IEntity createEntityFromNBT(INbt nbt);

    IEntity createEntity(String id);

    IBlock getSpawnPoint();

    void setSpawnPoint(IBlock block);

    String getName();

}

package noppes.npcs.api;

import net.minecraft.entity.Entity;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import noppes.npcs.api.block.IBlock;
import noppes.npcs.api.entity.ICustomNpc;
import noppes.npcs.api.entity.IEntity;
import noppes.npcs.api.handler.*;
import noppes.npcs.api.item.IItemStack;

import java.io.File;

/**
 * Note this API should only be used Server side not on the client
 */
public abstract class NpcAPI {
    private static NpcAPI instance = null;

    /**
     * Doesnt spawn the npc in the world
     */
    public abstract ICustomNpc createNPC(World world);

    /**
     * Creates and spawns an npc
     */
    public abstract ICustomNpc spawnNPC(World world, int x, int y, int z);

    public abstract IEntity getIEntity(Entity entity);

    public abstract IBlock getIBlock(World world, BlockPos pos);

    public abstract IContainer getIContainer(IInventory inventory);

    public abstract IContainer getIContainer(Container container);

    public abstract IItemStack getIItemStack(ItemStack itemstack);

    public abstract IWorld getIWorld(WorldServer world);

    public abstract IWorld getIWorld(int dimensionId);

    public abstract IWorld[] getIWorlds();

    public abstract INbt getINbt(NBTTagCompound compound);

    public abstract IPos getIPos(double x, double y, double z);

    public abstract IFactionHandler getFactions();

    public abstract IRecipeHandler getRecipes();

    public abstract IQuestHandler getQuests();

    public abstract IDialogHandler getDialogs();

    public abstract ICloneHandler getClones();

    public abstract IDamageSource getIDamageSource(DamageSource damagesource);

    public abstract INbt stringToNbt(String str);

    /**
     * Get player data even if they are offline
     *
     * @param uuid
     * @return
     */
    public abstract INbt getRawPlayerData(String uuid);

    /**
     * Used by modders
     *
     * @return The event bus where you register CustomNPCEvents
     */
    public abstract EventBus events();


    /**
     * Use to register your own /noppes subcommand
     */
    public abstract void registerCommand(CommandNoppesBase command);

    /**
     * @return Returns the .minecraft/customnpcs folder or [yourserverfolder]/customnpcs
     */
    public abstract File getGlobalDir();

    /**
     * @return Returns the .minecraft/saves/[yourworld]/customnpcs folder or [yourserverfolder]/[yourworld]/customnpcs
     */
    public abstract File getWorldDir();

    public static boolean IsAvailable() {
        return Loader.isModLoaded("customnpcs");
    }

    public static NpcAPI Instance() {
        if (instance != null)
            return instance;

        if (!IsAvailable())
            return null;

        try {
            Class c = Class.forName("noppes.npcs.api.wrapper.WrapperNpcAPI");

            instance = (NpcAPI) c.getMethod("Instance").invoke(null);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return instance;
    }

    /**
     * @param permission  Permission node, best if it's lowercase and contains '.' (e.g. <code>"modid.subgroup.permission_id"</code>)
     * @param defaultType 0:ALL, 1:OP, 2:NONE. This determines who can use the permission by default everybody, only ops or nobody
     */
    public abstract void registerPermissionNode(String permission, int defaultType);

    public abstract boolean hasPermissionNode(String permission);

    /**
     * @param world   The world in which the command is executed
     * @param command The Command to execute
     * @return
     */
    public abstract String executeCommand(IWorld world, String command);
}

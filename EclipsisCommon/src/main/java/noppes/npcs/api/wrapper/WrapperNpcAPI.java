package noppes.npcs.api.wrapper;

import net.minecraft.entity.Entity;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import net.minecraftforge.server.permission.PermissionAPI;
import noppes.npcs.CustomNpcs;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.api.*;
import noppes.npcs.api.block.IBlock;
import noppes.npcs.api.entity.ICustomNpc;
import noppes.npcs.api.entity.IEntity;
import noppes.npcs.api.handler.*;
import noppes.npcs.api.item.IItemStack;
import noppes.npcs.controllers.*;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.util.LRUHashMap;
import noppes.npcs.util.NBTJsonUtil;
import noppes.npcs.util.NBTJsonUtil.JsonException;

import java.io.File;
import java.util.Map;

public class WrapperNpcAPI extends NpcAPI {
    private static final Map<Integer, WorldWrapper> worldCache = new LRUHashMap<>(10);
    public static final EventBus EVENT_BUS = new EventBus();

    private static NpcAPI instance = null;

    public static void clearCache() {
        worldCache.clear();
        BlockWrapper.clearCache();
    }

    @Override
    public IEntity getIEntity(Entity entity) {
        if (entity == null || entity.world.isRemote)
            return null;
        if (entity instanceof EntityNPCInterface)
            return ((EntityNPCInterface) entity).wrappedNPC;
        else {
            return WrapperEntityData.get(entity);
        }
    }

    @Override
    public ICustomNpc createNPC(World world) {
        if (world.isRemote)
            return null;
        EntityCustomNpc npc = new EntityCustomNpc(world);
        return npc.wrappedNPC;
    }


    @Override
    public void registerPermissionNode(String permission, int defaultType) {
        if (defaultType < 0 || defaultType > 2) {
            throw new CustomNPCsException("Default type cant be smaller than 0 or larger than 2");
        }
        if (hasPermissionNode(permission)) {
            throw new CustomNPCsException("Permission already exists");
        }
        DefaultPermissionLevel level = DefaultPermissionLevel.values()[defaultType];
        PermissionAPI.registerNode(permission, level, permission);
    }

    @Override
    public boolean hasPermissionNode(String permission) {
        return PermissionAPI.getPermissionHandler().getRegisteredNodes().contains(permission);
    }

    @Override
    public ICustomNpc spawnNPC(World world, int x, int y, int z) {
        if (world.isRemote)
            return null;
        EntityCustomNpc npc = new EntityCustomNpc(world);
        npc.setPositionAndRotation(x + 0.5, y, z + 0.5, 0, 0);
        npc.ais.setStartPos(new BlockPos(x, y, z));
        npc.setHealth(npc.getMaxHealth());
        world.spawnEntity(npc);
        return npc.wrappedNPC;
    }


    public static NpcAPI Instance() {
        if (instance == null)
            instance = new WrapperNpcAPI();
        return instance;
    }

    @Override
    public EventBus events() {
        return EVENT_BUS;
    }

    @Override
    public IBlock getIBlock(World world, BlockPos pos) {
        return BlockWrapper.createNew(world, pos, world.getBlockState(pos));
    }

    @Override
    public IItemStack getIItemStack(ItemStack itemstack) {
        if (itemstack == null || itemstack.isEmpty())
            return ItemStackWrapper.AIR;
        return itemstack.getCapability(ItemStackWrapper.ITEMSCRIPTEDDATA_CAPABILITY, null);
    }

    @Override
    public IWorld getIWorld(WorldServer world) {
        WorldWrapper w = worldCache.get(world.provider.getDimension());
        if (w != null) {
            w.world = world;
            return w;
        }
        worldCache.put(world.provider.getDimension(), w = WorldWrapper.createNew(world));
        return w;
    }

    @Override
    public IWorld getIWorld(int dimensionId) {
        for (WorldServer world : CustomNpcs.INSTANCE.getServer().worlds) {
            if (world.provider.getDimension() == dimensionId)
                return getIWorld(world);
        }
        throw new CustomNPCsException("Unknown dimension id: " + dimensionId);
    }

    @Override
    public IContainer getIContainer(IInventory inventory) {
        return new ContainerWrapper(inventory);
    }

    @Override
    public IContainer getIContainer(Container container) {
        return new ContainerWrapper(container);
    }

    @Override
    public IFactionHandler getFactions() {
        checkWorld();
        return FactionController.instance;
    }

    private void checkWorld() {
        if (CustomNpcs.INSTANCE.getServer() == null || CustomNpcs.INSTANCE.getServer().isServerStopped())
            throw new CustomNPCsException("No world is loaded right now");
    }

    @Override
    public IRecipeHandler getRecipes() {
        checkWorld();
        return RecipeController.instance;
    }

    @Override
    public IQuestHandler getQuests() {
        checkWorld();
        return QuestController.instance;
    }

    @Override
    public IWorld[] getIWorlds() {
        checkWorld();
        IWorld[] worlds = new IWorld[CustomNpcs.INSTANCE.getServer().worlds.length];
        for (int i = 0; i < CustomNpcs.INSTANCE.getServer().worlds.length; i++) {
            worlds[i] = getIWorld(CustomNpcs.INSTANCE.getServer().worlds[i]);
        }
        return worlds;
    }

    @Override
    public IPos getIPos(double x, double y, double z) {
        return new BlockPosWrapper(new BlockPos(x, y, z));
    }

    @Override
    public File getGlobalDir() {
        return CustomNpcs.INSTANCE.getDir();
    }

    @Override
    public File getWorldDir() {
        return CustomNpcs.INSTANCE.getWorldSaveDirectory();
    }

    @Override
    public void registerCommand(CommandNoppesBase command) {
        CustomNpcs.NoppesCommand.registerCommand(command);
    }

    @Override
    public INbt getINbt(NBTTagCompound compound) {
        if (compound == null)
            return new NBTWrapper(new NBTTagCompound());
        return new NBTWrapper(compound);
    }

    @Override
    public INbt stringToNbt(String str) {
        if (str == null || str.isEmpty())
            throw new CustomNPCsException("Cant cast empty string to nbt");
        try {
            return getINbt(NBTJsonUtil.Convert(str));
        } catch (JsonException e) {
            throw new CustomNPCsException(e, "Failed converting " + str);
        }
    }

    @Override
    public IDamageSource getIDamageSource(DamageSource damagesource) {
        return new DamageSourceWrapper(damagesource);
    }

    @Override
    public IDialogHandler getDialogs() {
        return DialogController.instance;
    }

    @Override
    public ICloneHandler getClones() {
        return ServerCloneController.Instance;
    }

    @Override
    public String executeCommand(IWorld world, String command) {
        FakePlayer player = EntityNPCInterface.CommandPlayer;
        player.setWorld(world.getMCWorld());
        player.setPosition(0, 0, 0);
        return NoppesUtilServer.runCommand(world.getMCWorld(), BlockPos.ORIGIN, "API", command, null, player);
    }

    @Override
    public INbt getRawPlayerData(String uuid) {
        return getINbt(PlayerData.loadPlayerData(uuid));
    }
}

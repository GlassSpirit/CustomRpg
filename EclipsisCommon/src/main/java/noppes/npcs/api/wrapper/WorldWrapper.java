package noppes.npcs.api.wrapper;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTPrimitive;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import noppes.npcs.api.*;
import noppes.npcs.api.block.IBlock;
import noppes.npcs.api.constants.EntityType;
import noppes.npcs.api.entity.IEntity;
import noppes.npcs.api.entity.IPlayer;
import noppes.npcs.api.entity.data.IData;
import noppes.npcs.api.item.IItemStack;
import noppes.npcs.controllers.ScriptController;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.entity.EntityProjectile;

import java.util.*;

public class WorldWrapper implements IWorld {
    public static Map<String, Object> tempData = new HashMap<String, Object>();

    public WorldServer world;

    public IDimension dimension;

    private IData tempdata = new IData() {

        @Override
        public void put(String key, Object value) {
            tempData.put(key, value);
        }

        @Override
        public Object get(String key) {
            return tempData.get(key);
        }

        @Override
        public void remove(String key) {
            tempData.remove(key);
        }

        @Override
        public boolean has(String key) {
            return tempData.containsKey(key);
        }

        @Override
        public void clear() {
            tempData.clear();
        }

        @Override
        public String[] getKeys() {
            return tempData.keySet().toArray(new String[tempData.size()]);
        }

    };

    private IData storeddata = new IData() {

        @Override
        public void put(String key, Object value) {
            NBTTagCompound compound = ScriptController.Instance.compound;
            if (value instanceof Number)
                compound.setDouble(key, ((Number) value).doubleValue());
            else if (value instanceof String)
                compound.setString(key, (String) value);
            ScriptController.Instance.shouldSave = true;
        }

        @Override
        public Object get(String key) {
            NBTTagCompound compound = ScriptController.Instance.compound;
            if (!compound.hasKey(key))
                return null;
            NBTBase base = compound.getTag(key);
            if (base instanceof NBTPrimitive)
                return ((NBTPrimitive) base).getDouble();
            return ((NBTTagString) base).getString();
        }

        @Override
        public void remove(String key) {
            ScriptController.Instance.compound.removeTag(key);
            ScriptController.Instance.shouldSave = true;
        }

        @Override
        public boolean has(String key) {
            return ScriptController.Instance.compound.hasKey(key);
        }

        @Override
        public void clear() {
            ScriptController.Instance.compound = new NBTTagCompound();
            ScriptController.Instance.shouldSave = true;
        }

        @Override
        public String[] getKeys() {
            return ScriptController.Instance.compound.getKeySet().toArray(new String[ScriptController.Instance.compound.getKeySet().size()]);
        }

    };

    private WorldWrapper(World world) {
        this.world = (WorldServer) world;
        this.dimension = new DimensionWrapper(world.provider.getDimension(), world.provider.getDimensionType());
    }

    @Override
    public WorldServer getMCWorld() {
        return world;
    }

    @Override
    public IEntity[] getNearbyEntities(int x, int y, int z, int range, int type) {
        return getNearbyEntities(new BlockPosWrapper(new BlockPos(x, y, z)), range, type);
    }

    @Override
    public IEntity[] getNearbyEntities(IPos pos, int range, int type) {
        AxisAlignedBB bb = new AxisAlignedBB(0, 0, 0, 1, 1, 1).offset(pos.getMCBlockPos()).grow(range, range, range);
        List<Entity> entities = world.getEntitiesWithinAABB(getClassForType(type), bb);
        List<IEntity> list = new ArrayList<IEntity>();
        for (Entity living : entities) {
            list.add(NpcAPI.Instance().getIEntity(living));
        }
        return list.toArray(new IEntity[list.size()]);
    }

    @Override
    public IEntity[] getAllEntities(int type) {
        List<Entity> entities = world.getEntities(getClassForType(type), EntitySelectors.NOT_SPECTATING);
        List<IEntity> list = new ArrayList<IEntity>();
        for (Entity living : entities) {
            list.add(NpcAPI.Instance().getIEntity(living));
        }
        return list.toArray(new IEntity[list.size()]);
    }

    @Override
    public IEntity getClosestEntity(int x, int y, int z, int range, int type) {
        return getClosestEntity(new BlockPosWrapper(new BlockPos(x, y, z)), range, type);
    }

    @Override
    public IEntity getClosestEntity(IPos pos, int range, int type) {
        AxisAlignedBB bb = new AxisAlignedBB(0, 0, 0, 1, 1, 1).offset(pos.getMCBlockPos()).grow(range, range, range);
        List<Entity> entities = world.getEntitiesWithinAABB(getClassForType(type), bb);
        double distance = range * range * range;
        Entity entity = null;
        for (Entity e : entities) {
            double r = pos.getMCBlockPos().distanceSq(e.getPosition());
            if (entity == null) {
                distance = r;
                entity = e;
            } else if (r < distance) {
                distance = r;
                entity = e;
            }
        }
        return NpcAPI.Instance().getIEntity(entity);
    }

    @Override
    public IEntity getEntity(String uuid) {
        try {
            UUID id = UUID.fromString(uuid);
            Entity e = world.getEntityFromUuid(id);
            if (e == null)
                e = world.getPlayerEntityByUUID(id);
            if (e == null)
                return null;
            return NpcAPI.Instance().getIEntity(e);
        } catch (Exception e) {
            throw new CustomNPCsException("Given uuid was invalid " + uuid);
        }
    }

    @Override
    public IEntity createEntityFromNBT(INbt nbt) {
        Entity entity = EntityList.createEntityFromNBT(nbt.getMCNBT(), world);
        if (entity == null)
            throw new CustomNPCsException("Failed to create an entity from given NBT");
        return NpcAPI.Instance().getIEntity(entity);
    }

    @Override
    public IEntity createEntity(String id) {
        ResourceLocation resource = new ResourceLocation(id);
        Entity entity = EntityList.createEntityByIDFromName(resource, world);
        if (entity == null)
            throw new CustomNPCsException("Failed to create an entity from given id: " + id);
        return NpcAPI.Instance().getIEntity(entity);
    }

    @Override
    public IPlayer getPlayer(String name) {
        EntityPlayer player = world.getPlayerEntityByName(name);
        if (player == null)
            return null;
        return (IPlayer) NpcAPI.Instance().getIEntity(player);
    }


    private Class getClassForType(int type) {
        if (type == EntityType.ANY)
            return Entity.class;
        if (type == EntityType.LIVING)
            return EntityLivingBase.class;
        if (type == EntityType.PLAYER)
            return EntityPlayer.class;
        if (type == EntityType.ANIMAL)
            return EntityAnimal.class;
        if (type == EntityType.MONSTER)
            return EntityMob.class;
        if (type == EntityType.NPC)
            return EntityNPCInterface.class;
        if (type == EntityType.ITEM)
            return EntityItem.class;
        if (type == EntityType.PROJECTILE)
            return EntityProjectile.class;
        if (type == EntityType.MONSTER)
            return EntityMob.class;
        if (type == EntityType.VILLAGER)
            return EntityVillager.class;
        return Entity.class;
    }

    @Override
    public long getTime() {
        return world.getWorldTime();
    }

    @Override
    public void setTime(long time) {
        world.setWorldTime(time);
    }

    @Override
    public long getTotalTime() {
        return world.getTotalWorldTime();
    }

    @Override
    public IBlock getBlock(int x, int y, int z) {
        return NpcAPI.Instance().getIBlock(world, new BlockPos(x, y, z));
    }

    public boolean isChunkLoaded(int x, int z) {
        return world.getChunkProvider().chunkExists(x >> 4, z >> 4);
    }

    @Override
    public void setBlock(int x, int y, int z, String name, int meta) {
        Block block = Block.getBlockFromName(name);
        if (block == null) {
            throw new CustomNPCsException("There is no such block: %s");
        }

        world.setBlockState(new BlockPos(x, y, z), block.getStateFromMeta(meta));
    }

    @Override
    public void removeBlock(int x, int y, int z) {
        world.setBlockToAir(new BlockPos(x, y, z));
    }

    @Override
    public float getLightValue(int x, int y, int z) {
        return world.getLight(new BlockPos(x, y, z)) / 16f;
    }

    @Override
    public IBlock getSpawnPoint() {
        BlockPos pos = world.getSpawnCoordinate();
        if (pos == null)
            pos = world.getSpawnPoint();
        return NpcAPI.Instance().getIBlock(world, pos);
    }

    @Override
    public void setSpawnPoint(IBlock block) {
        world.setSpawnPoint(new BlockPos(block.getX(), block.getY(), block.getZ()));
    }

    @Override
    public boolean isDay() {
        return world.getWorldTime() % 24000 < 12000;
    }

    @Override
    public boolean isRaining() {
        return world.getWorldInfo().isRaining();
    }

    @Override
    public void setRaining(boolean bo) {
        world.getWorldInfo().setRaining(bo);
    }

    @Override
    public void thunderStrike(double x, double y, double z) {
        world.addWeatherEffect(new EntityLightningBolt(world, x, y, z, false));
    }

    @Override
    public void spawnParticle(String particle, double x, double y, double z, double dx, double dy, double dz, double speed, int count) {
        EnumParticleTypes particleType = null;
        for (EnumParticleTypes enumParticle : EnumParticleTypes.values()) {
            if (enumParticle.getArgumentCount() > 0) {
                if (particle.startsWith(enumParticle.getParticleName())) {
                    particleType = enumParticle;
                    break;
                }
            } else if (particle.equals(enumParticle.getParticleName())) {
                particleType = enumParticle;
                break;
            }
        }
        if (particleType != null)
            world.spawnParticle(particleType, x, y, z, count, dx, dy, dz, speed);
    }

    @Override
    public IData getTempdata() {
        return tempdata;
    }

    @Override
    public IData getStoreddata() {
        return storeddata;
    }

    @Override
    public IItemStack createItem(String name, int damage, int size) {
        Item item = Item.REGISTRY.getObject(new ResourceLocation(name));
        if (item == null)
            throw new CustomNPCsException("Unknown item id: " + name);
        return NpcAPI.Instance().getIItemStack(new ItemStack(item, size, damage));
    }

    @Override
    public IItemStack createItemFromNbt(INbt nbt) {
        ItemStack item = new ItemStack(nbt.getMCNBT());
        if (item.isEmpty())
            throw new CustomNPCsException("Failed to create an item from given NBT");
        return NpcAPI.Instance().getIItemStack(item);
    }

    @Override
    public void explode(double x, double y, double z, float range, boolean fire, boolean grief) {
        world.newExplosion(null, x, y, z, range, fire, grief);
    }

    @Override
    public IPlayer[] getAllPlayers() {
        List<EntityPlayerMP> list = world.getMinecraftServer().getPlayerList().getPlayers();
        IPlayer[] arr = new IPlayer[list.size()];
        for (int i = 0; i < list.size(); i++) {
            arr[i] = (IPlayer) NpcAPI.Instance().getIEntity(list.get(i));
        }

        return arr;
    }

    @Override
    public String getBiomeName(int x, int z) {
        return world.getBiomeForCoordsBody(new BlockPos(x, 0, z)).biomeName;
    }

    @Override
    public IEntity spawnClone(double x, double y, double z, int tab, String name) {
        return NpcAPI.Instance().getClones().spawn(x, y, z, tab, name, this);
    }

    @Override
    public void spawnEntity(IEntity entity) {
        Entity e = entity.getMCEntity();
        if (world.getEntityFromUuid(e.getUniqueID()) != null)
            throw new CustomNPCsException("Entity with this UUID already exists");
        e.setPosition(e.posX, e.posY, e.posZ);
        world.spawnEntity(e);
    }

    @Override
    public IEntity getClone(int tab, String name) {
        return NpcAPI.Instance().getClones().get(tab, name, this);
    }

    @Override
    public IScoreboard getScoreboard() {
        return new ScoreboardWrapper(world.getMinecraftServer());
    }

    @Override
    public void broadcast(String message) {
        world.getMinecraftServer().getPlayerList().sendMessage(new TextComponentString(message));
    }

    @Override
    public int getRedstonePower(int x, int y, int z) {
        return world.getStrongPower(new BlockPos(x, y, z));
    }

    @Deprecated
    public static WorldWrapper createNew(WorldServer world) {
        return new WorldWrapper(world);
    }

    @Override
    public IDimension getDimension() {
        return dimension;
    }

    @Override
    public String getName() {
        return world.getWorldInfo().getWorldName();
    }

    @Override
    public BlockPos getMCBlockPos(int x, int y, int z) {
        return new BlockPos(x, y, z);
    }
}

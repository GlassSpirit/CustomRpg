package noppes.npcs.api.wrapper;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTPrimitive;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.play.server.SPacketAnimation;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.WorldServer;
import noppes.npcs.api.*;
import noppes.npcs.api.constants.EntityType;
import noppes.npcs.api.entity.IEntity;
import noppes.npcs.api.entity.data.IData;
import noppes.npcs.api.item.IItemStack;
import noppes.npcs.controllers.ServerCloneController;

import java.util.*;

public class EntityWrapper<T extends Entity> implements IEntity {
    protected T entity;
    private final IData storeddata = new IData() {

        @Override
        public void put(String key, Object value) {
            NBTTagCompound compound = getStoredCompound();
            if (value instanceof Number) {
                compound.setDouble(key, ((Number) value).doubleValue());
            } else if (value instanceof String)
                compound.setString(key, (String) value);
            saveStoredCompound(compound);
        }

        @Override
        public Object get(String key) {
            NBTTagCompound compound = getStoredCompound();
            if (!compound.hasKey(key))
                return null;
            NBTBase base = compound.getTag(key);
            if (base instanceof NBTPrimitive)
                return ((NBTPrimitive) base).getDouble();
            return ((NBTTagString) base).getString();
        }

        @Override
        public void remove(String key) {
            NBTTagCompound compound = getStoredCompound();
            compound.removeTag(key);
            saveStoredCompound(compound);
        }

        @Override
        public boolean has(String key) {
            return getStoredCompound().hasKey(key);
        }

        @Override
        public void clear() {
            entity.getEntityData().removeTag("CNPCStoredData");
        }

        private NBTTagCompound getStoredCompound() {
            NBTTagCompound compound = entity.getEntityData().getCompoundTag("CNPCStoredData");
            if (compound == null)
                entity.getEntityData().setTag("CNPCStoredData", compound = new NBTTagCompound());
            return compound;
        }

        private void saveStoredCompound(NBTTagCompound compound) {
            entity.getEntityData().setTag("CNPCStoredData", compound);
        }

        @Override
        public String[] getKeys() {
            NBTTagCompound compound = getStoredCompound();
            return compound.getKeySet().toArray(new String[compound.getKeySet().size()]);
        }
    };
    private Map<String, Object> tempData = new HashMap<>();
    private final IData tempdata = new IData() {

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
    private IWorld worldWrapper;

    public EntityWrapper(T entity) {
        this.entity = entity;
        this.worldWrapper = NpcAPI.instance().getIWorld((WorldServer) entity.world);
    }

    @Override
    public double getX() {
        return entity.posX;
    }

    @Override
    public void setX(double x) {
        entity.posX = x;
    }

    @Override
    public double getY() {
        return entity.posY;
    }

    @Override
    public void setY(double y) {
        entity.posY = y;
    }

    @Override
    public double getZ() {
        return entity.posZ;
    }

    @Override
    public void setZ(double z) {
        entity.posZ = z;
    }

    @Override
    public int getBlockX() {
        return MathHelper.floor(entity.posX);
    }

    @Override
    public int getBlockY() {
        return MathHelper.floor(entity.posY);
    }

    @Override
    public int getBlockZ() {
        return MathHelper.floor(entity.posZ);
    }

    @Override
    public String getEntityName() {
        return entity.getName();
    }

    @Override
    public String getName() {
        return entity.getName();
    }

    @Override
    public void setName(String name) {
        entity.setCustomNameTag(name);
    }

    @Override
    public boolean hasCustomName() {
        return entity.hasCustomName();
    }

    @Override
    public void setPosition(double x, double y, double z) {
        entity.setPosition(x, y, z);
    }

    @Override
    public IWorld getWorld() {
        if (entity.world != worldWrapper.getMCWorld())
            this.worldWrapper = NpcAPI.instance().getIWorld((WorldServer) entity.world);
        return worldWrapper;
    }

    @Override
    public boolean isAlive() {
        return entity.isEntityAlive();
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
    public long getAge() {
        return entity.ticksExisted;
    }

    @Override
    public void damage(float amount) {
        entity.attackEntityFrom(DamageSource.GENERIC, amount);
    }

    @Override
    public void despawn() {
        entity.isDead = true;
    }

    @Override
    public void spawn() {
        if (worldWrapper.getMCWorld().getEntityFromUuid(entity.getUniqueID()) != null)
            throw new CustomNPCsException("Entity is already spawned");
        entity.isDead = false;
        worldWrapper.getMCWorld().spawnEntity(entity);
    }

    @Override
    public void kill() {
        entity.setDead();
    }

    @Override
    public boolean inWater() {
        return entity.isInsideOfMaterial(Material.WATER);
    }

    @Override
    public boolean inLava() {
        return entity.isInsideOfMaterial(Material.LAVA);
    }

    @Override
    public boolean inFire() {
        return entity.isInsideOfMaterial(Material.FIRE);
    }

    @Override
    public boolean isBurning() {
        return entity.isBurning();
    }

    @Override
    public void setBurning(int ticks) {
        entity.setFire(ticks);
    }

    @Override
    public void extinguish() {
        entity.extinguish();
    }

    @Override
    public String getTypeName() {
        return EntityList.getEntityString(entity);
    }

    @Override
    public void dropItem(IItemStack item) {
        entity.entityDropItem(item.getMCItemStack(), 0);
    }

    @Override
    public IEntity[] getRiders() {
        List<Entity> list = entity.getPassengers();
        IEntity[] riders = new IEntity[list.size()];
        for (int i = 0; i < list.size(); i++) {
            riders[i] = NpcAPI.instance().getIEntity(list.get(i));
        }
        return riders;
    }

    @Override
    public IRayTrace rayTraceBlock(double distance, boolean stopOnLiquid, boolean ignoreBlockWithoutBoundingBox) {
        Vec3d vec3d = entity.getPositionEyes(1);
        Vec3d vec3d1 = entity.getLook(1);
        Vec3d vec3d2 = vec3d.add(vec3d1.x * distance, vec3d1.y * distance, vec3d1.z * distance);
        RayTraceResult result = entity.world.rayTraceBlocks(vec3d, vec3d2, stopOnLiquid, ignoreBlockWithoutBoundingBox, true);
        if (result == null)
            return null;
        return new RayTraceWrapper(NpcAPI.instance().getIBlock(entity.world, result.getBlockPos()), result.sideHit.getIndex());
    }

    @Override
    public IEntity[] rayTraceEntities(double distance, boolean stopOnLiquid, boolean ignoreBlockWithoutBoundingBox) {
        Vec3d vec3d = entity.getPositionEyes(1);
        Vec3d vec3d1 = entity.getLook(1);
        Vec3d vec3d2 = vec3d.add(vec3d1.x * distance, vec3d1.y * distance, vec3d1.z * distance);
        RayTraceResult result = entity.world.rayTraceBlocks(vec3d, vec3d2, stopOnLiquid, ignoreBlockWithoutBoundingBox, false);
        if (result != null) {
            vec3d2 = new Vec3d(result.hitVec.x, result.hitVec.y, result.hitVec.z);
        }
        return this.findEntityOnPath(distance, vec3d, vec3d2);
    }

    private IEntity[] findEntityOnPath(double distance, Vec3d vec3d, Vec3d vec3d1) {

        List<Entity> list = entity.world.getEntitiesWithinAABBExcludingEntity(entity, entity.getEntityBoundingBox().grow(distance));

        List<IEntity> result = new ArrayList<>();
        for (Entity entity1 : list) {
            if (entity1.canBeCollidedWith() && entity1 != this.entity) {
                AxisAlignedBB axisalignedbb = entity1.getEntityBoundingBox().grow(entity1.getCollisionBorderSize());
                RayTraceResult raytraceresult1 = axisalignedbb.calculateIntercept(vec3d, vec3d1);

                if (raytraceresult1 != null) {
                    result.add(NpcAPI.instance().getIEntity(entity1));
                }

            }
        }
        result.sort((o1, o2) -> {
            double d1 = EntityWrapper.this.entity.getDistanceSq(o1.getMCEntity());
            double d2 = EntityWrapper.this.entity.getDistanceSq(o2.getMCEntity());
            if (d1 == d2)
                return 0;
            return d1 > d2 ? 1 : -1;
        });
        return result.toArray(new IEntity[result.size()]);
    }

    @Override
    public IEntity[] getAllRiders() {
        List<Entity> list = new ArrayList<>(entity.getRecursivePassengers());
        IEntity[] riders = new IEntity[list.size()];
        for (int i = 0; i < list.size(); i++) {
            riders[i] = NpcAPI.instance().getIEntity(list.get(i));
        }
        return riders;
    }

    @Override
    public void addRider(IEntity entity) {
        if (entity != null) {
            entity.getMCEntity().startRiding(this.entity, true);
        }
    }

    @Override
    public void clearRiders() {
        entity.removePassengers();
    }

    @Override
    public IEntity getMount() {
        return NpcAPI.instance().getIEntity(entity.getRidingEntity());
    }

    @Override
    public void setMount(IEntity entity) {
        if (entity == null)
            this.entity.dismountRidingEntity();
        else {
            this.entity.startRiding(entity.getMCEntity(), true);
        }
    }

    @Override
    public float getRotation() {
        return entity.rotationYaw;
    }

    @Override
    public void setRotation(float rotation) {
        entity.rotationYaw = rotation;
    }

    @Override
    public float getPitch() {
        return entity.rotationPitch;
    }

    @Override
    public void setPitch(float rotation) {
        entity.rotationPitch = rotation;
    }

    @Override
    public void knockback(int power, float direction) {
        float v = direction * (float) Math.PI / 180.0F;
        entity.addVelocity(-MathHelper.sin(v) * (float) power, 0.1D + power * 0.04f, MathHelper.cos(v) * (float) power);
        entity.motionX *= 0.6D;
        entity.motionZ *= 0.6D;
        entity.velocityChanged = true;
    }

    @Override
    public boolean isSneaking() {
        return entity.isSneaking();
    }

    @Override
    public boolean isSprinting() {
        return entity.isSprinting();
    }

    @Override
    public T getMCEntity() {
        return entity;
    }

    @Override
    public int getType() {
        return EntityType.UNKNOWN;
    }

    @Override
    public boolean typeOf(int type) {
        return type == EntityType.UNKNOWN;
    }

    @Override
    public String getUUID() {
        return entity.getUniqueID().toString();
    }

    @Override
    public String generateNewUUID() {
        UUID id = UUID.randomUUID();
        entity.setUniqueId(id);
        return id.toString();
    }

    @Override
    public INbt getNbt() {
        return NpcAPI.instance().getINbt(entity.getEntityData());
    }

    @Override
    public void storeAsClone(int tab, String name) {
        NBTTagCompound compound = new NBTTagCompound();
        if (!entity.writeToNBTAtomically(compound))
            throw new CustomNPCsException("Cannot store dead entities");
        ServerCloneController.Instance.addClone(compound, name, tab);
    }

    @Override
    public INbt getEntityNbt() {
        NBTTagCompound compound = new NBTTagCompound();
        entity.writeToNBT(compound);
        ResourceLocation resourcelocation = EntityList.getKey(entity);
        if (getType() == EntityType.PLAYER) {
            resourcelocation = new ResourceLocation("player");
        }
        if (resourcelocation != null) {
            compound.setString("id", resourcelocation.toString());
        }
        return NpcAPI.instance().getINbt(compound);
    }

    @Override
    public void setEntityNbt(INbt nbt) {
        entity.readFromNBT(nbt.getMCNBT());
    }

    @Override
    public void playAnimation(int type) {
        worldWrapper.getMCWorld().getEntityTracker().sendToTrackingAndSelf(entity, new SPacketAnimation(entity, type));
    }

    @Override
    public float getHeight() {
        return entity.height;
    }

    @Override
    public float getEyeHeight() {
        return entity.getEyeHeight();
    }

    @Override
    public float getWidth() {
        return entity.width;
    }

    @Override
    public IPos getPos() {
        return new BlockPosWrapper(entity.getPosition());
    }

    @Override
    public void setPos(IPos pos) {
        entity.setPosition(pos.getX() + 0.5f, pos.getY(), pos.getZ() + 0.5f);
    }

    @Override
    public String[] getTags() {
        return entity.getTags().toArray(new String[entity.getTags().size()]);
    }

    @Override
    public void addTag(String tag) {
        entity.addTag(tag);
    }

    @Override
    public boolean hasTag(String tag) {
        return entity.getTags().contains(tag);
    }

    @Override
    public void removeTag(String tag) {
        entity.removeTag(tag);
    }

    @Override
    public double getMotionX() {
        return entity.motionX;
    }

    @Override
    public void setMotionX(double motion) {
        if (entity.motionX == motion)
            return;
        entity.motionX = motion;
        entity.velocityChanged = true;
    }

    @Override
    public double getMotionY() {
        return entity.motionY;
    }

    @Override
    public void setMotionY(double motion) {
        if (entity.motionY == motion)
            return;
        entity.motionY = motion;
        entity.velocityChanged = true;
    }

    @Override
    public double getMotionZ() {
        return entity.motionZ;
    }

    @Override
    public void setMotionZ(double motion) {
        if (entity.motionZ == motion)
            return;
        entity.motionZ = motion;
        entity.velocityChanged = true;
    }
}

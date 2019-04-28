package noppes.npcs.api.wrapper;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import noppes.npcs.LogWriter;
import noppes.npcs.api.entity.IEntity;
import noppes.npcs.controllers.PixelmonHelper;
import noppes.npcs.entity.EntityProjectile;

public class WrapperEntityData implements ICapabilityProvider {

    private static final ResourceLocation key = new ResourceLocation("customnpcs", "entitydata");
    @CapabilityInject(WrapperEntityData.class)
    public static Capability<WrapperEntityData> ENTITYDATA_CAPABILITY = null;
    public IEntity base;

    public WrapperEntityData(IEntity base) {
        this.base = base;
    }

    public static IEntity get(Entity entity) {
        if (entity == null)
            return null;
        WrapperEntityData data = entity.getCapability(ENTITYDATA_CAPABILITY, null);
        if (data == null) {
            LogWriter.warn("Unable to get EntityData for " + entity);
            return getData(entity).base;//shouldnt happen, only happens if other mods are bad
        }
        return data.base;
    }

    public static void register(net.minecraftforge.event.AttachCapabilitiesEvent<Entity> event) {
        event.addCapability(key, getData(event.getObject()));
    }

    private static WrapperEntityData getData(Entity entity) {
        if (entity == null || entity.world == null || entity.world.isRemote)
            return null;

        if (entity instanceof EntityPlayerMP)
            return new WrapperEntityData(new PlayerWrapper((EntityPlayerMP) entity));
        else if (PixelmonHelper.isPixelmon(entity))
            return new WrapperEntityData(new PixelmonWrapper((EntityTameable) entity));
        else if (entity instanceof EntityAnimal)
            return new WrapperEntityData(new AnimalWrapper((EntityAnimal) entity));
        else if (entity instanceof EntityMob)
            return new WrapperEntityData(new MonsterWrapper((EntityMob) entity));
        else if (entity instanceof EntityLiving)
            return new WrapperEntityData(new EntityLivingWrapper((EntityLiving) entity));
        else if (entity instanceof EntityLivingBase)
            return new WrapperEntityData(new EntityLivingBaseWrapper((EntityLivingBase) entity));
        else if (entity instanceof EntityVillager)
            return new WrapperEntityData(new EntityVillagerWrapper((EntityVillager) entity));
        else if (entity instanceof EntityItem)
            return new WrapperEntityData(new EntityItemWrapper((EntityItem) entity));
        else if (entity instanceof EntityProjectile)
            return new WrapperEntityData(new EntityProjectileWrapper((EntityProjectile) entity));
        return new WrapperEntityData(new EntityWrapper(entity));

    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        return capability == ENTITYDATA_CAPABILITY;
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (hasCapability(capability, facing))
            return (T) this;
        return null;
    }
}

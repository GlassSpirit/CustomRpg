package noppes.npcs.api.wrapper;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumHand;
import noppes.npcs.api.CustomNPCsException;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.constants.EntityType;
import noppes.npcs.api.entity.IEntity;
import noppes.npcs.api.entity.IEntityLivingBase;
import noppes.npcs.api.entity.data.IMark;
import noppes.npcs.api.item.IItemStack;
import noppes.npcs.controllers.data.MarkData;

public class EntityLivingBaseWrapper<T extends EntityLivingBase> extends EntityWrapper<T> implements IEntityLivingBase {

    public EntityLivingBaseWrapper(T entity) {
        super(entity);
    }

    @Override
    public float getHealth() {
        return entity.getHealth();
    }

    @Override
    public void setHealth(float health) {
        entity.setHealth(health);
    }

    @Override
    public float getMaxHealth() {
        return entity.getMaxHealth();
    }

    @Override
    public void setMaxHealth(float health) {
        if (health < 0)
            return;
        entity.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(health);
    }

    @Override
    public boolean isAttacking() {
        return entity.getRevengeTarget() != null;
    }

    @Override
    public void setAttackTarget(IEntityLivingBase living) {
        if (living == null)
            entity.setRevengeTarget(null);
        else
            entity.setRevengeTarget(living.getMCEntity());
    }

    @Override
    public IEntityLivingBase getAttackTarget() {
        return (IEntityLivingBase) NpcAPI.instance().getIEntity(entity.getRevengeTarget());
    }

    @Override
    public IEntityLivingBase getLastAttacked() {
        return (IEntityLivingBase) NpcAPI.instance().getIEntity(entity.getLastAttackedEntity());
    }

    @Override
    public int getLastAttackedTime() {
        return entity.getLastAttackedEntityTime();
    }

    @Override
    public boolean canSeeEntity(IEntity entity) {
        return this.entity.canEntityBeSeen(entity.getMCEntity());
    }

    @Override
    public void swingMainhand() {
        entity.swingArm(EnumHand.MAIN_HAND);
    }

    @Override
    public void swingOffhand() {
        entity.swingArm(EnumHand.OFF_HAND);
    }

    @Override
    public void addPotionEffect(int effect, int duration, int strength, boolean hideParticles) {
        Potion p = Potion.getPotionById(effect);
        if (p == null)
            return;

        if (strength < 0)
            strength = 0;
        else if (strength > 255)
            strength = 255;

        if (duration < 0)
            duration = 0;
        else if (duration > 1000000)
            duration = 1000000;

        if (!p.isInstant())
            duration *= 20;

        if (duration == 0)
            entity.removePotionEffect(p);
        else
            entity.addPotionEffect(new PotionEffect(p, duration, strength, false, hideParticles));
    }

    @Override
    public void clearPotionEffects() {
        entity.clearActivePotions();
    }

    @Override
    public int getPotionEffect(int effect) {
        PotionEffect pf = entity.getActivePotionEffect(Potion.getPotionById(effect));
        if (pf == null)
            return -1;
        return pf.getAmplifier();
    }

    @Override
    public IItemStack getMainhandItem() {
        return NpcAPI.instance().getIItemStack(entity.getHeldItemMainhand());
    }

    @Override
    public void setMainhandItem(IItemStack item) {
        entity.setHeldItem(EnumHand.MAIN_HAND, item == null ? ItemStack.EMPTY : item.getMCItemStack());
    }

    @Override
    public IItemStack getOffhandItem() {
        return NpcAPI.instance().getIItemStack(entity.getHeldItemOffhand());
    }

    @Override
    public void setOffhandItem(IItemStack item) {
        entity.setHeldItem(EnumHand.OFF_HAND, item == null ? ItemStack.EMPTY : item.getMCItemStack());
    }

    @Override
    public IItemStack getArmor(int slot) {
        if (slot < 0 || slot > 3)
            throw new CustomNPCsException("Wrong slot id:" + slot);
        return NpcAPI.instance().getIItemStack(entity.getItemStackFromSlot(getSlot(slot)));
    }

    @Override
    public void setArmor(int slot, IItemStack item) {
        if (slot < 0 || slot > 3)
            throw new CustomNPCsException("Wrong slot id:" + slot);
        entity.setItemStackToSlot(getSlot(slot), item == null ? ItemStack.EMPTY : item.getMCItemStack());
    }

    private EntityEquipmentSlot getSlot(int slot) {
        if (slot == 3)
            return EntityEquipmentSlot.HEAD;
        if (slot == 2)
            return EntityEquipmentSlot.CHEST;
        if (slot == 1)
            return EntityEquipmentSlot.LEGS;
        if (slot == 0)
            return EntityEquipmentSlot.FEET;
        return null;
    }

    @Override
    public float getRotation() {
        return entity.renderYawOffset;
    }

    @Override
    public void setRotation(float rotation) {
        entity.renderYawOffset = rotation;
    }

    @Override
    public int getType() {
        return EntityType.LIVING;
    }

    @Override
    public boolean typeOf(int type) {
        return type == EntityType.LIVING || super.typeOf(type);
    }

    @Override
    public boolean isChild() {
        return entity.isChild();
    }

    @Override
    public IMark addMark(int type) {
        MarkData data = MarkData.get(entity);
        return data.addMark(type);
    }

    @Override
    public void removeMark(IMark mark) {
        MarkData data = MarkData.get(entity);
        data.marks.remove(mark);
        data.syncClients();
    }

    @Override
    public IMark[] getMarks() {
        MarkData data = MarkData.get(entity);
        return data.marks.toArray(new IMark[data.marks.size()]);
    }

    @Override
    public float getMoveForward() {
        return entity.moveForward;
    }

    @Override
    public void setMoveForward(float move) {
        entity.moveForward = move;
    }

    @Override
    public float getMoveStrafing() {
        return entity.moveStrafing;
    }

    @Override
    public void setMoveStrafing(float move) {
        entity.moveStrafing = move;
    }

    @Override
    public float getMoveVertical() {
        return entity.moveVertical;
    }

    @Override
    public void setMoveVertical(float move) {
        entity.moveVertical = move;
    }
}

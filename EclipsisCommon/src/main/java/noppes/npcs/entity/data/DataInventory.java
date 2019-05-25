package noppes.npcs.entity.data;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import noppes.npcs.NBTTags;
import noppes.npcs.api.CustomNPCsException;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.entity.data.INPCInventory;
import noppes.npcs.api.item.IItemStack;
import noppes.npcs.api.wrapper.ItemStackWrapper;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.util.ValueUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DataInventory implements IInventory, INPCInventory {
    public Map<Integer, IItemStack> weapons = new HashMap<>();
    public Map<Integer, IItemStack> armor = new HashMap<>();
    public Map<Integer, IItemStack> drops = new HashMap<>();
    public Map<Integer, Float> dropChance = new HashMap<>();
    public int lootMode = 0;
    private int minExp = 0;
    private int maxExp = 0;
    private EntityNPCInterface npc;

    public DataInventory(EntityNPCInterface npc) {
        this.npc = npc;
    }

    public NBTTagCompound writeEntityToNBT(NBTTagCompound nbttagcompound) {
        nbttagcompound.setInteger("MinExp", minExp);
        nbttagcompound.setInteger("MaxExp", maxExp);
        nbttagcompound.setTag("NpcInv", NBTTags.nbtIItemStackMap(drops));
        nbttagcompound.setTag("Armor", NBTTags.nbtIItemStackMap(armor));
        nbttagcompound.setTag("Weapons", NBTTags.nbtIItemStackMap(weapons));
        nbttagcompound.setTag("DropChance", NBTTags.nbtIntegerFloatMap(dropChance));
        nbttagcompound.setInteger("LootMode", lootMode);
        return nbttagcompound;
    }

    public void readEntityFromNBT(NBTTagCompound nbttagcompound) {
        minExp = nbttagcompound.getInteger("MinExp");
        maxExp = nbttagcompound.getInteger("MaxExp");
        drops = NBTTags.getIItemStackMap(nbttagcompound.getTagList("NpcInv", 10));
        armor = NBTTags.getIItemStackMap(nbttagcompound.getTagList("Armor", 10));
        weapons = NBTTags.getIItemStackMap(nbttagcompound.getTagList("Weapons", 10));
        dropChance = NBTTags.getIntegerFloatMap(nbttagcompound.getTagList("DropChance", 10));
        lootMode = nbttagcompound.getInteger("LootMode");
    }

    @Override
    public IItemStack getArmor(int slot) {
        return armor.get(slot);
    }

    @Override
    public void setArmor(int slot, IItemStack item) {
        armor.put(slot, item);
        npc.updateClient = true;
    }

    @Override
    public IItemStack getRightHand() {
        return weapons.get(0);
    }

    @Override
    public void setRightHand(IItemStack item) {
        weapons.put(0, item);
        npc.updateClient = true;
    }

    @Override
    public IItemStack getProjectile() {
        return weapons.get(1);
    }

    @Override
    public void setProjectile(IItemStack item) {
        weapons.put(1, item);
        npc.updateAI = true;
    }

    @Override
    public IItemStack getLeftHand() {
        return weapons.get(2);
    }

    @Override
    public void setLeftHand(IItemStack item) {
        weapons.put(2, item);
        npc.updateClient = true;
    }

    @Override
    public IItemStack getDropItem(int slot) {
        if (slot < 0 || slot > 26) throw new CustomNPCsException("Bad slot number: " + slot);

        IItemStack item = npc.inventory.drops.get(slot);
        if (item == null) return null;

        return NpcAPI.Instance().getIItemStack(item.getMCItemStack());
    }

    @Override
    public void setDropItem(int slot, IItemStack item, float chance) {
        if (slot < 0 || slot > 26) throw new CustomNPCsException("Bad slot number: " + slot);
        chance = ValueUtil.correctFloat(chance, 0, 100);

        if (item == null || item.isEmpty()) {
            dropChance.remove(slot);
            drops.remove(slot);
        } else {
            dropChance.put(slot, chance);
            drops.put(slot, item);
        }
    }


    public void dropStuff(Entity entity, DamageSource damagesource) {
        ArrayList<EntityItem> list = new ArrayList<>();
        for (int i : drops.keySet()) {
            IItemStack item = drops.get(i);
            if (item == null)
                continue;
            float dchance = 100;
            if (dropChance.containsKey(i))
                dchance = dropChance.get(i);
            float chance = npc.world.rand.nextFloat() * 100 + dchance;
            if (chance >= 100) {
                EntityItem e = getEntityItem(item.getMCItemStack().copy());
                if (e != null) list.add(e);
            }
        }

        int enchant = 0;
        if (damagesource.getTrueSource() instanceof EntityPlayer) {
            enchant = EnchantmentHelper.getLootingModifier((EntityLivingBase) damagesource.getTrueSource());
        }

        if (!net.minecraftforge.common.ForgeHooks.onLivingDrops(npc, damagesource, list, enchant, true)) {
            for (EntityItem item : list) {
                if (lootMode == 1 && entity instanceof EntityPlayer) {
                    EntityPlayer player = (EntityPlayer) entity;
                    item.setPickupDelay(2);
                    npc.world.spawnEntity(item);
                    ItemStack stack = item.getItem();
                    int i = stack.getCount();

                    if (player.inventory.addItemStackToInventory(stack)) {
                        entity.world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_ITEM_PICKUP,
                                SoundCategory.PLAYERS, 0.2F, ((player.getRNG().nextFloat() - player.getRNG().nextFloat()) * 0.7F + 1.0F) * 2.0F);
                        player.onItemPickup(item, i);

                        if (stack.getCount() <= 0) {
                            item.setDead();
                        }
                    }
                } else npc.world.spawnEntity(item);
            }
        }
        int exp = getExpRNG();
        while (exp > 0) {
            int var2 = EntityXPOrb.getXPSplit(exp);
            exp -= var2;

            if (lootMode == 1 && entity instanceof EntityPlayer) {
                npc.world.spawnEntity(new EntityXPOrb(entity.world, entity.posX, entity.posY, entity.posZ, var2));
            } else {
                npc.world.spawnEntity(new EntityXPOrb(npc.world, npc.posX, npc.posY, npc.posZ, var2));
            }
        }

    }

    public EntityItem getEntityItem(ItemStack itemstack) {
        if (itemstack == null || itemstack.isEmpty()) {
            return null;
        }
        EntityItem entityitem = new EntityItem(npc.world, npc.posX,
                (npc.posY - 0.3) + (double) npc.getEyeHeight(), npc.posZ,
                itemstack);
        entityitem.setPickupDelay(40);

        float f2 = npc.getRNG().nextFloat() * 0.5F;
        float f4 = npc.getRNG().nextFloat() * 3.141593F * 2.0F;
        entityitem.motionX = -MathHelper.sin(f4) * f2;
        entityitem.motionZ = MathHelper.cos(f4) * f2;
        entityitem.motionY = 0.2;

        return entityitem;
    }

    @Override
    public int getSizeInventory() {
        return 15;
    }

    @Override
    public ItemStack getStackInSlot(int i) {
        if (i < 4)
            return ItemStackWrapper.MCItem(getArmor(i));
        else if (i < 7)
            return ItemStackWrapper.MCItem(weapons.get(i - 4));
        else
            return ItemStackWrapper.MCItem(drops.get(i - 7));
    }

    @Override
    public ItemStack decrStackSize(int par1, int par2) {
        int i = 0;
        Map<Integer, IItemStack> var3;

        if (par1 >= 7) {
            var3 = drops;
            par1 -= 7;
        } else if (par1 >= 4) {
            var3 = weapons;
            par1 -= 4;
            i = 1;
        } else {
            var3 = armor;
            i = 2;
        }

        ItemStack var4 = null;
        if (var3.get(par1) != null) {

            if (var3.get(par1).getMCItemStack().getCount() <= par2) {
                var4 = var3.get(par1).getMCItemStack();
                var3.put(par1, null);
            } else {
                var4 = var3.get(par1).getMCItemStack().splitStack(par2);

                if (var3.get(par1).getMCItemStack().getCount() == 0) {
                    var3.put(par1, null);
                }
            }
        }
        if (i == 1)
            weapons = var3;
        if (i == 2)
            armor = var3;
        if (var4 == null)
            return ItemStack.EMPTY;
        return var4;
    }

    @Override
    public ItemStack removeStackFromSlot(int par1) {
        int i = 0;
        Map<Integer, IItemStack> var2;

        if (par1 >= 7) {
            var2 = drops;
            par1 -= 7;
        } else if (par1 >= 4) {
            var2 = weapons;
            par1 -= 4;
            i = 1;
        } else {
            var2 = armor;
            i = 2;
        }

        if (var2.get(par1) != null) {
            ItemStack var3 = var2.get(par1).getMCItemStack();
            var2.put(par1, null);
            if (i == 1)
                weapons = var2;
            if (i == 2)
                armor = var2;
            return var3;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void setInventorySlotContents(int par1, ItemStack par2ItemStack) {
        int i = 0;
        Map<Integer, IItemStack> var3;

        if (par1 >= 7) {
            var3 = drops;
            par1 -= 7;
        } else if (par1 >= 4) {
            var3 = weapons;
            par1 -= 4;
            i = 1;
        } else {
            var3 = armor;
            i = 2;
        }
        var3.put(par1, NpcAPI.Instance().getIItemStack(par2ItemStack));

        if (i == 1)
            weapons = var3;
        if (i == 2)
            armor = var3;
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public boolean isUsableByPlayer(EntityPlayer var1) {
        return true;
    }

    @Override
    public boolean isItemValidForSlot(int i, ItemStack itemstack) {
        return true;
    }

    @Override
    public String getName() {
        return "NPC Inventory";
    }

    @Override
    public void markDirty() {
    }

    @Override
    public boolean hasCustomName() {
        return true;
    }

    @Override
    public ITextComponent getDisplayName() {
        return null;
    }

    @Override
    public void openInventory(EntityPlayer player) {
    }

    @Override
    public void closeInventory(EntityPlayer player) {
    }

    @Override
    public int getField(int id) {
        return 0;
    }

    @Override
    public void setField(int id, int value) {
    }

    @Override
    public int getFieldCount() {
        return 0;
    }

    @Override
    public void clear() {
    }

    @Override
    public int getExpMin() {
        return npc.inventory.minExp;
    }

    @Override
    public int getExpMax() {
        return npc.inventory.maxExp;
    }

    @Override
    public int getExpRNG() {
        int exp = minExp;
        if (maxExp - minExp > 0)
            exp += npc.world.rand.nextInt(maxExp - minExp);
        return exp;
    }

    @Override
    public void setExp(int min, int max) {
        min = Math.min(min, max);

        npc.inventory.minExp = min;
        npc.inventory.maxExp = max;
    }

    @Override
    public boolean isEmpty() {
        for (int slot = 0; slot < this.getSizeInventory(); slot++) {
            ItemStack item = getStackInSlot(slot);
            if (!item.isEmpty()) {
                return false;
            }
        }
        return true;
    }
}

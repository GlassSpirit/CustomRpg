package noppes.npcs.api.wrapper;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.*;
import net.minecraft.nbt.*;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import noppes.npcs.ItemStackEmptyWrapper;
import noppes.npcs.api.CustomNPCsException;
import noppes.npcs.api.INbt;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.constants.ItemType;
import noppes.npcs.api.entity.IEntityLiving;
import noppes.npcs.api.entity.data.IData;
import noppes.npcs.api.item.IItemStack;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.objects.items.ItemScripted;

import java.util.*;
import java.util.Map.Entry;

public class ItemStackWrapper implements IItemStack, ICapabilityProvider, ICapabilitySerializable {
    private Map<String, Object> tempData = new HashMap<>();

    @CapabilityInject(ItemStackWrapper.class)
    public static Capability<ItemStackWrapper> ITEMSCRIPTEDDATA_CAPABILITY = null;

    private static final EntityEquipmentSlot[] VALID_EQUIPMENT_SLOTS = new EntityEquipmentSlot[]{EntityEquipmentSlot.HEAD, EntityEquipmentSlot.CHEST, EntityEquipmentSlot.LEGS, EntityEquipmentSlot.FEET};

    public ItemStack item;

    private NBTTagCompound storedData = new NBTTagCompound();

    public static ItemStackWrapper AIR = new ItemStackEmptyWrapper();

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


    private final IData storeddata = new IData() {

        @Override
        public void put(String key, Object value) {
            if (value instanceof Number) {
                storedData.setDouble(key, ((Number) value).doubleValue());
            } else if (value instanceof String)
                storedData.setString(key, (String) value);
        }

        @Override
        public Object get(String key) {
            if (!storedData.hasKey(key))
                return null;
            NBTBase base = storedData.getTag(key);
            if (base instanceof NBTPrimitive)
                return ((NBTPrimitive) base).getDouble();
            return ((NBTTagString) base).getString();
        }

        @Override
        public void remove(String key) {
            storedData.removeTag(key);
        }

        @Override
        public boolean has(String key) {
            return storedData.hasKey(key);
        }

        @Override
        public void clear() {
            storedData = new NBTTagCompound();
        }

        @Override
        public String[] getKeys() {
            return storedData.getKeySet().toArray(new String[storedData.getKeySet().size()]);
        }
    };

    protected ItemStackWrapper(ItemStack item) {
        this.item = item;
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
    public int getStackSize() {
        return item.getCount();
    }

    @Override
    public void setStackSize(int size) {
        if (size > getMaxStackSize())
            throw new CustomNPCsException("Can't set the stacksize bigger than MaxStacksize");
        item.setCount(size);
    }

    @Override
    public void setAttribute(String name, double value) {
        setAttribute(name, value, -1);
    }

    @Override
    public void setAttribute(String name, double value, int slot) {
        if (slot < -1 || slot > 5) {
            throw new CustomNPCsException("Slot has to be between -1 and 5, given was: " + slot);
        }

        NBTTagCompound compound = item.getTagCompound();
        if (compound == null)
            item.setTagCompound(compound = new NBTTagCompound());


        NBTTagList nbttaglist = compound.getTagList("AttributeModifiers", 10);
        NBTTagList newList = new NBTTagList();
        for (int i = 0; i < nbttaglist.tagCount(); i++) {
            NBTTagCompound c = nbttaglist.getCompoundTagAt(i);
            if (!c.getString("AttributeName").equals(name)) {
                newList.appendTag(c);
            }
        }
        if (value != 0) {
            NBTTagCompound nbttagcompound = SharedMonsterAttributes.writeAttributeModifierToNBT(new AttributeModifier(name, value, 0));
            nbttagcompound.setString("AttributeName", name);
            if (slot >= 0) {
                nbttagcompound.setString("Slot", EntityEquipmentSlot.values()[slot].getName());
            }
            newList.appendTag(nbttagcompound);
        }
        compound.setTag("AttributeModifiers", newList);
    }

    @Override
    public double getAttribute(String name) {
        NBTTagCompound compound = item.getTagCompound();
        if (compound == null)
            return 0;
        Multimap<String, AttributeModifier> map = item.getAttributeModifiers(EntityEquipmentSlot.MAINHAND);
        for (Entry<String, AttributeModifier> entry : map.entries()) {
            if (entry.getKey().equals(name)) {
                AttributeModifier mod = entry.getValue();
                return mod.getAmount();
            }
        }
        return 0;
    }

    @Override
    public boolean hasAttribute(String name) {
        NBTTagCompound compound = item.getTagCompound();
        if (compound == null)
            return false;
        NBTTagList nbttaglist = compound.getTagList("AttributeModifiers", 10);
        for (int i = 0; i < nbttaglist.tagCount(); i++) {
            NBTTagCompound c = nbttaglist.getCompoundTagAt(i);
            if (c.getString("AttributeName").equals(name)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int getItemDamage() {
        return item.getItemDamage();
    }

    @Override
    public void setItemDamage(int value) {
        item.setItemDamage(value);
    }

    @Override
    public void addEnchantment(String id, int strenght) {
        Enchantment ench = Enchantment.getEnchantmentByLocation(id);
        if (ench == null)
            throw new CustomNPCsException("Unknown enchant id:" + id);
        item.addEnchantment(ench, strenght);
    }

    @Override
    public boolean isEnchanted() {
        return item.isItemEnchanted();
    }

    @Override
    public boolean hasEnchant(String id) {
        Enchantment ench = Enchantment.getEnchantmentByLocation(id);
        if (ench == null)
            throw new CustomNPCsException("Unknown enchant id:" + id);
        if (!isEnchanted())
            return false;
        int enchId = Enchantment.getEnchantmentID(ench);
        NBTTagList list = item.getEnchantmentTagList();
        for (int i = 0; i < list.tagCount(); i++) {
            NBTTagCompound compound = list.getCompoundTagAt(i);
            if (compound.getShort("id") == enchId)
                return true;
        }
        return false;
    }

    @Override
    public boolean removeEnchant(String id) {
        Enchantment ench = Enchantment.getEnchantmentByLocation(id);
        if (ench == null)
            throw new CustomNPCsException("Unknown enchant id:" + id);
        if (!isEnchanted())
            return false;
        int enchId = Enchantment.getEnchantmentID(ench);
        NBTTagList list = item.getEnchantmentTagList();
        NBTTagList newList = new NBTTagList();
        for (int i = 0; i < list.tagCount(); i++) {
            NBTTagCompound compound = list.getCompoundTagAt(i);
            if (compound.getShort("id") != enchId)
                newList.appendTag(compound);
        }
        if (list.tagCount() == newList.tagCount()) {
            return false;
        }
        item.getTagCompound().setTag("ench", newList);
        return true;
    }

    @Override
    public boolean isBlock() {
        Block block = Block.getBlockFromItem(item.getItem());
        return block != null && block != Blocks.AIR;
    }

    @Override
    public boolean hasCustomName() {
        return item.hasDisplayName();
    }

    @Override
    public void setCustomName(String name) {
        item.setStackDisplayName(name);
    }

    @Override
    public String getDisplayName() {
        return item.getDisplayName();
    }

    @Override
    public String getItemName() {
        return item.getItem().getItemStackDisplayName(item);
    }

    @Override
    public String getName() {
        return Item.REGISTRY.getNameForObject(item.getItem()) + "";
    }

    @Override
    public INbt getNbt() {
        NBTTagCompound compound = item.getTagCompound();
        if (compound == null)
            item.setTagCompound(compound = new NBTTagCompound());
        return NpcAPI.Instance().getINbt(compound);
    }

    @Override
    public boolean hasNbt() {
        NBTTagCompound compound = item.getTagCompound();
        return compound != null && !compound.isEmpty();
    }

    @Override
    public ItemStack getMCItemStack() {
        return item;
    }

    public static ItemStack MCItem(IItemStack item) {
        if (item == null)
            return ItemStack.EMPTY;
        return item.getMCItemStack();
    }

    @Override
    public void damageItem(int damage, IEntityLiving living) {
        item.damageItem(damage, living == null ? null : living.getMCEntity());
    }

    @Override
    public boolean isBook() {
        return false;
    }

    @Override
    public int getFoodLevel() {
        if (item.getItem() instanceof ItemFood) {
            return ((ItemFood) item.getItem()).getHealAmount(item);
        }
        return 0;
    }

    @Override
    public IItemStack copy() {
        return createNew(item.copy());
    }

    @Override
    public int getMaxStackSize() {
        return item.getMaxStackSize();
    }

    @Override
    public int getMaxItemDamage() {
        return item.getMaxDamage();
    }

    @Override
    public INbt getItemNbt() {
        NBTTagCompound compound = new NBTTagCompound();
        item.writeToNBT(compound);
        return NpcAPI.Instance().getINbt(compound);
    }

    @Override
    public double getAttackDamage() {
        HashMultimap map = (HashMultimap) item.getAttributeModifiers(EntityEquipmentSlot.MAINHAND);
        Iterator iterator = map.entries().iterator();
        double damage = 0;
        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();
            if (entry.getKey().equals(SharedMonsterAttributes.ATTACK_DAMAGE.getName())) {
                AttributeModifier mod = (AttributeModifier) entry.getValue();
                damage = mod.getAmount();
            }
        }
        damage += EnchantmentHelper.getModifierForCreature(item, EnumCreatureAttribute.UNDEFINED);
        return damage;
    }

    @Override
    public boolean isEmpty() {
        return item.isEmpty();
    }

    @Override
    public int getType() {
        if (item.getItem() instanceof IPlantable)
            return ItemType.SEEDS;
        if (item.getItem() instanceof ItemSword)
            return ItemType.SWORD;
        return ItemType.NORMAL;
    }

    @Override
    public boolean isWearable() {
        for (EntityEquipmentSlot slot : VALID_EQUIPMENT_SLOTS) {
            if (item.getItem().isValidArmor(item, slot, EntityNPCInterface.CommandPlayer))
                return true;
        }
        return false;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        return capability == ITEMSCRIPTEDDATA_CAPABILITY;
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (hasCapability(capability, facing))
            return (T) this;
        return null;
    }

    private static final ResourceLocation key = new ResourceLocation("customnpcs", "itemscripteddata");

    public static void register(AttachCapabilitiesEvent<ItemStack> event) {
        ItemStackWrapper wrapper = createNew(event.getObject());
        event.addCapability(key, wrapper);
    }

    private static ItemStackWrapper createNew(ItemStack item) {
        if (item == null || item.isEmpty())
            return AIR;

        if (item.getItem() instanceof ItemScripted) {
            return new ItemScriptedWrapper(item);
        }
        if (item.getItem() == Items.WRITTEN_BOOK || item.getItem() == Items.WRITABLE_BOOK || item.getItem() instanceof ItemWritableBook || item.getItem() instanceof ItemWrittenBook)
            return new ItemBookWrapper(item);
        if (item.getItem() instanceof ItemArmor)
            return new ItemArmorWrapper(item);

        Block block = Block.getBlockFromItem(item.getItem());
        if (block != Blocks.AIR)
            return new ItemBlockWrapper(item);

        return new ItemStackWrapper(item);
    }

    @Override
    public String[] getLore() {
        NBTTagCompound compound = item.getSubCompound("display");
        if (compound == null || compound.getTagId("Lore") != 9)
            return new String[0];

        NBTTagList nbttaglist = compound.getTagList("Lore", 8);
        if (nbttaglist.isEmpty())
            return new String[0];

        List<String> lore = new ArrayList<>();
        for (int i = 0; i < nbttaglist.tagCount(); ++i) {
            lore.add(nbttaglist.getStringTagAt(i));
        }

        return lore.toArray(new String[lore.size()]);
    }

    @Override
    public void setLore(String[] lore) {
        NBTTagCompound compound = item.getOrCreateSubCompound("display");
        if (lore == null || lore.length == 0) {
            compound.removeTag("Lore");
            return;
        }

        NBTTagList nbtlist = new NBTTagList();
        for (String s : lore) {
            nbtlist.appendTag(new NBTTagString(s));
        }
        compound.setTag("Lore", nbtlist);
    }

    @Override
    public NBTBase serializeNBT() {
        return getMCNbt();
    }

    @Override
    public void deserializeNBT(NBTBase nbt) {
        setMCNbt((NBTTagCompound) nbt);
    }

    public NBTTagCompound getMCNbt() {
        NBTTagCompound compound = new NBTTagCompound();
        if (!storedData.isEmpty()) {
            compound.setTag("StoredData", storedData);
        }
        return compound;
    }

    public void setMCNbt(NBTTagCompound compound) {
        storedData = compound.getCompoundTag("StoredData");
    }


    @Override
    public void removeNbt() {
        this.item.setTagCompound(null);
    }
}

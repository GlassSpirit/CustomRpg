package noppes.npcs.api.item;

import net.minecraft.item.ItemStack;
import noppes.npcs.api.INbt;
import noppes.npcs.api.entity.IEntityLiving;
import noppes.npcs.api.entity.data.IData;

public interface IItemStack {

    int getStackSize();

    /**
     * @param size The size of the itemstack. A number between 1 and 64
     */
    void setStackSize(int size);

    int getMaxStackSize();

    /**
     * @return Returns the item damage of this item. For tools this is the durability for other items the color and more.
     */
    int getItemDamage();

    /**
     * @param value The value to be set as item damage. For tools this is the durability for other items the color and more.
     */
    void setItemDamage(int value);

    int getMaxItemDamage();

    double getAttackDamage();

    void damageItem(int damage, IEntityLiving living);

    /**
     * @param id       The enchantment id
     * @param strenght The strenght of the enchantment
     */
    void addEnchantment(String id, int strenght);

    boolean isEnchanted();

    /**
     * @param id The enchantment id
     */
    boolean hasEnchant(String id);

    /**
     * @param id The enchantment id
     * @return Returns whether something was removed or not
     */
    boolean removeEnchant(String id);

    /**
     * @return Returns whether or not this item is a block
     * @deprecated
     */
    boolean isBlock();

    boolean isWearable();

    /**
     * @return Return whether or not the item has a custom name
     */
    boolean hasCustomName();

    /**
     * @param name The custom name this item will get
     */
    void setCustomName(String name);

    /**
     * @return Return the ingame displayed name. This is either the item name or the custom name if it has one.
     */
    String getDisplayName();

    /**
     * @return Get the items ingame name. Use this incase the item ingame has custom name and you want the original name.
     */
    String getItemName();

    /**
     * @return The minecraft name for this item
     */
    String getName();

    /**
     * @return Whether this is a writable book item. If it is check IItemBook for more info
     * @deprecated
     */
    boolean isBook();

    /**
     * @return A copy of the ItemStack
     */
    IItemStack copy();

    /**
     * No support is given for this method. Dont use if you dont know what you are doing.
     *
     * @return Minecraft ItemStack
     */
    ItemStack getMCItemStack();

    /**
     * @return Used to get the extra NBT, which is used by enchantments and customname
     */
    INbt getNbt();

    /**
     * @return Returns false if the nbt of this itemstack is null or empty
     */
    boolean hasNbt();

    /**
     * Removes the nbt from the itemstack
     */
    void removeNbt();

    /**
     * @return The entire item as nbt
     */
    INbt getItemNbt();

    /**
     * @return Returns true if this itemstack is air or the stacksize is 0
     */
    boolean isEmpty();

    int getType();

    String[] getLore();

    void setLore(String[] lore);

    /**
     * @param name  Attribute name see (https://minecraft.gamepedia.com/Attribute)
     * @param value
     * @deprecated Replaced by setAttribute(String name, double value, int slot)
     */
    void setAttribute(String name, double value);

    /**
     * @param name  Attribute name see (https://minecraft.gamepedia.com/Attribute)
     * @param value
     * @param slot  Slot in which the attribute is active -1:ALL, 0:MAINHAND, 1:OFFHAND, 2:FEET, 3:LEGS, 4:CHEST, 5:HEAD
     */
    void setAttribute(String name, double value, int slot);

    /**
     * @param name Attribute name see (https://minecraft.gamepedia.com/Attribute)
     * @return Returns the value of this attribute
     */
    double getAttribute(String name);

    /**
     * @param name Attribute name see (https://minecraft.gamepedia.com/Attribute)
     * @return Whether or not this item has the attribute
     */
    boolean hasAttribute(String name);

    /**
     * Temp data stores anything but only untill it's reloaded
     */
    IData getTempdata();

    /**
     * Stored data persists through world restart. Unlike tempdata only Strings and Numbers can be saved
     */
    IData getStoreddata();

    /**
     * @return Returns 0 if the item isnt food and otherwise the amount it restores hunger
     */
    int getFoodLevel();

}

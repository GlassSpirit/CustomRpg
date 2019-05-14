package noppes.npcs;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import noppes.npcs.util.NBTTags;

public class NpcMiscInventory implements IInventory {
    public final NonNullList<ItemStack> items;
    public int stackLimit = 64;

    private int size;

    public NpcMiscInventory(int size) {
        this.size = size;
        items = NonNullList.withSize(size, ItemStack.EMPTY);
    }

    public NBTTagCompound getToNBT() {
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        nbttagcompound.setTag("NpcMiscInv", NBTTags.nbtItemStackList(items));
        return nbttagcompound;
    }

    public void setFromNBT(NBTTagCompound nbttagcompound) {
        NBTTags.getItemStackList(nbttagcompound.getTagList("NpcMiscInv", 10), items);
    }

    @Override
    public int getSizeInventory() {
        return size;
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        return items.get(index);
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        return ItemStackHelper.getAndSplit(items, index, count);
    }


    public boolean decrStackSize(ItemStack eating, int decrease) {
        for (int slot = 0; slot < items.size(); slot++) {
            ItemStack item = items.get(slot);
            if (!item.isEmpty() && eating == item && item.getCount() >= decrease) {
                item.splitStack(decrease);
                if (item.getCount() <= 0)
                    items.set(slot, ItemStack.EMPTY);
                return true;
            }
        }
        return false;
    }

    @Override
    public ItemStack removeStackFromSlot(int var1) {
        return items.set(var1, ItemStack.EMPTY);
    }

    @Override
    public void setInventorySlotContents(int var1, ItemStack var2) {
        if (var1 >= getSizeInventory())
            return;
        items.set(var1, var2);
    }

    @Override
    public int getInventoryStackLimit() {
        return stackLimit;
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
    public void markDirty() {

    }

    public boolean addItemStack(ItemStack item) {
        ItemStack mergable;
        boolean merged = false;
        while (!(mergable = getMergableItem(item)).isEmpty() && mergable.getCount() > 0) {
            int size = mergable.getMaxStackSize() - mergable.getCount();
            if (size > item.getCount()) {
                mergable.setCount(mergable.getMaxStackSize());
                item.setCount(item.getCount() - size);
                merged = true;
            } else {
                mergable.setCount(mergable.getCount() + item.getCount());
                item.setCount(0);
            }
        }
        if (item.getCount() <= 0)
            return true;
        int slot = firstFreeSlot();
        if (slot >= 0) {
            items.set(slot, item.copy());
            item.setCount(0);
            return true;
        }
        return merged;
    }

    public ItemStack getMergableItem(ItemStack item) {
        for (ItemStack is : items) {
            if (NoppesUtilPlayer.compareItems(item, is, false, false) && is.getCount() < is.getMaxStackSize()) {
                return is;
            }
        }
        return ItemStack.EMPTY;
    }

    public int firstFreeSlot() {
        for (int i = 0; i < getSizeInventory(); i++) {
            if (items.get(i).isEmpty())
                return i;
        }
        return -1;
    }

    public void setSize(int i) {
        size = i;
    }

    @Override
    public String getName() {
        return "Npc Misc Inventory";
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
    public boolean isEmpty() {
        for (int slot = 0; slot < this.getSizeInventory(); slot++) {
            ItemStack item = getStackInSlot(slot);
            if (!NoppesUtilServer.IsItemStackNull(item) && !item.isEmpty()) {
                return false;
            }
        }
        return true;
    }
}

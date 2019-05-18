package noppes.npcs.api.wrapper;

import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import noppes.npcs.NoppesUtilPlayer;
import noppes.npcs.api.CustomNPCsException;
import noppes.npcs.api.IContainer;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.item.IItemStack;

public class ContainerWrapper implements IContainer {
    private IInventory inventory;
    private Container container;

    public ContainerWrapper(IInventory inventory) {
        this.inventory = inventory;
    }

    public ContainerWrapper(Container container) {
        this.container = container;
    }

    @Override
    public int getSize() {
        if (inventory != null)
            return inventory.getSizeInventory();
        return container.inventorySlots.size();
    }

    @Override
    public IItemStack getSlot(int slot) {
        if (slot < 0 || slot >= getSize())
            throw new CustomNPCsException("Slot is out of range " + slot);
        if (inventory != null)
            return NpcAPI.Instance().getIItemStack(inventory.getStackInSlot(slot));
        return NpcAPI.Instance().getIItemStack(container.getSlot(slot).getStack());
    }

    @Override
    public void setSlot(int slot, IItemStack item) {
        if (slot < 0 || slot >= getSize())
            throw new CustomNPCsException("Slot is out of range " + slot);
        ItemStack itemstack = item == null ? ItemStack.EMPTY : item.getMCItemStack();
        if (inventory != null)
            inventory.setInventorySlotContents(slot, itemstack);
        else
            container.putStackInSlot(slot, itemstack);
    }

    @Override
    public int count(IItemStack item, boolean ignoreDamage, boolean ignoreNBT) {
        int count = 0;
        for (int i = 0; i < getSize(); i++) {
            IItemStack toCompare = getSlot(i);
            if (NoppesUtilPlayer.compareItems(item.getMCItemStack(), toCompare.getMCItemStack(), ignoreDamage, ignoreNBT)) {
                count += toCompare.getStackSize();
            }
        }
        return count;
    }

    @Override
    public IInventory getMCInventory() {
        return inventory;
    }

    @Override
    public Container getMCContainer() {
        return container;
    }

    @Override
    public IItemStack[] getItems() {
        IItemStack[] items = new IItemStack[getSize()];
        for (int i = 0; i < getSize(); i++) {
            items[i] = getSlot(i);
        }
        return items;
    }

}

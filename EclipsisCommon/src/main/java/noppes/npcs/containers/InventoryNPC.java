package noppes.npcs.containers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import noppes.npcs.NoppesUtilServer;

public class InventoryNPC implements IInventory {
    private String inventoryTitle;
    private int slotsCount;
    public final NonNullList<ItemStack> inventoryContents;
    private Container con;

    public InventoryNPC(String s, int i, Container con) {
        this.con = con;
        inventoryTitle = s;
        slotsCount = i;
        inventoryContents = NonNullList.withSize(i, ItemStack.EMPTY);
    }

    @Override
    public ItemStack getStackInSlot(int i) {
        return inventoryContents.get(i);
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        return ItemStackHelper.getAndSplit(inventoryContents, index, count);
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        this.inventoryContents.set(index, stack);

        if (!stack.isEmpty() && stack.getCount() > this.getInventoryStackLimit()) {
            stack.setCount(this.getInventoryStackLimit());
        }
    }

    @Override
    public int getSizeInventory() {
        return slotsCount;
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public boolean isUsableByPlayer(EntityPlayer entityplayer) {
        return false;
    }


    @Override
    public ItemStack removeStackFromSlot(int i) {
        return ItemStackHelper.getAndRemove(inventoryContents, i);
    }

    @Override
    public boolean isItemValidForSlot(int i, ItemStack itemstack) {
        return true;
    }

    @Override
    public ITextComponent getDisplayName() {
        return new TextComponentString(inventoryTitle);
    }

    @Override
    public boolean hasCustomName() {
        return true;
    }

    @Override
    public void markDirty() {
        con.onCraftMatrixChanged(this);
    }

    @Override
    public void openInventory(EntityPlayer player) {

    }

    @Override
    public void closeInventory(EntityPlayer player) {

    }

    @Override
    public String getName() {
        return null;
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

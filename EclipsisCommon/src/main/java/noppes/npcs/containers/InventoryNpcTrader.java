package noppes.npcs.containers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import noppes.npcs.NoppesUtilServer;

public class InventoryNpcTrader implements IInventory {

    private String inventoryTitle;
    private int slotsCount;
    public final NonNullList<ItemStack> inventoryContents;
    private ContainerNPCTrader con;

    public InventoryNpcTrader(String s, int i, ContainerNPCTrader con) {
        this.con = con;
        inventoryTitle = s;
        slotsCount = i;
        inventoryContents = NonNullList.withSize(i, ItemStack.EMPTY);
    }

    @Override
    public ItemStack getStackInSlot(int i) {
        ItemStack toBuy = inventoryContents.get(i);
        if (NoppesUtilServer.IsItemStackNull(toBuy))
            return ItemStack.EMPTY;

        return toBuy.copy();
    }

    @Override
    public ItemStack decrStackSize(int i, int j) {
        ItemStack stack = inventoryContents.get(i);
        if (!NoppesUtilServer.IsItemStackNull(stack)) {
            return stack.copy();
        }
        return ItemStack.EMPTY;

    }

    @Override
    public void setInventorySlotContents(int i, ItemStack itemstack) {
        if (!itemstack.isEmpty())
            inventoryContents.set(i, itemstack.copy());
        markDirty();
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
        return true;
    }


    @Override
    public ItemStack removeStackFromSlot(int i) {
        return inventoryContents.set(i, ItemStack.EMPTY);
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

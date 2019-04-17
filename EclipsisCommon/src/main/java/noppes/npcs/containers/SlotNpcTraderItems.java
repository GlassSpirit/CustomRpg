package noppes.npcs.containers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import noppes.npcs.NoppesUtilServer;

class SlotNpcTraderItems extends Slot {

    public SlotNpcTraderItems(IInventory iinventory, int i, int j, int k) {
        super(iinventory, i, j, k);
    }

    @Override
    public ItemStack onTake(EntityPlayer player, ItemStack itemstack) {
        if (NoppesUtilServer.IsItemStackNull(itemstack) || NoppesUtilServer.IsItemStackNull(getStack()))
            return itemstack;
        if (itemstack.getItem() != getStack().getItem())
            return itemstack;
        itemstack.shrink(1);
        //onSlotChanged();
        return itemstack;
    }

    @Override
    public int getSlotStackLimit() {
        return 64;
    }

    @Override
    public boolean isItemValid(ItemStack itemstack) {
        return false;
    }
}

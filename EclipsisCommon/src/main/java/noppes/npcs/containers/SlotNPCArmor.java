package noppes.npcs.containers;

import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

class SlotNPCArmor extends Slot {

    final EntityEquipmentSlot armorType; /* synthetic field */

    SlotNPCArmor(IInventory iinventory, int i, int j, int k, EntityEquipmentSlot l) {
        super(iinventory, i, j, k);
        armorType = l;
    }

    @Override
    public int getSlotStackLimit() {
        return 1;
    }

    @SideOnly(Side.CLIENT)
    public String getSlotTexture() {
        return ItemArmor.EMPTY_SLOT_NAMES[armorType.getIndex()];
    }

    @Override
    public boolean isItemValid(ItemStack itemstack) {
        if (itemstack.getItem() instanceof ItemArmor) {
            return ((ItemArmor) itemstack.getItem()).armorType == armorType;
        }
        if (itemstack.getItem() instanceof ItemBlock) {
            return armorType == EntityEquipmentSlot.HEAD;
        }
        return false;
    }
}

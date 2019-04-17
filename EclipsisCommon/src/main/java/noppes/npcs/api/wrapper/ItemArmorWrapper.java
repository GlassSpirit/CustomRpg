package noppes.npcs.api.wrapper;

import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import noppes.npcs.api.constants.ItemType;
import noppes.npcs.api.item.IItemArmor;

public class ItemArmorWrapper extends ItemStackWrapper implements IItemArmor {
    protected ItemArmor armor;

    protected ItemArmorWrapper(ItemStack item) {
        super(item);
        armor = (ItemArmor) item.getItem();
    }

    @Override
    public int getType() {
        return ItemType.ARMOR;
    }

    @Override
    public int getArmorSlot() {
        return armor.getEquipmentSlot().getSlotIndex();
    }

    @Override
    public String getArmorMaterial() {
        return armor.getArmorMaterial().getName();
    }
}

package noppes.npcs.api.wrapper;

import net.minecraft.item.ItemStack;
import noppes.npcs.api.entity.data.IData;

public class ItemStackEmptyWrapper extends ItemStackWrapper {

    public ItemStackEmptyWrapper() {
        super(ItemStack.EMPTY);
    }

    @Override
    public IData getTempdata() {
        return null;
    }

    @Override
    public IData getStoreddata() {
        return null;
    }
}

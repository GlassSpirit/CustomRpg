package noppes.npcs.api.wrapper;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import noppes.npcs.api.constants.ItemType;
import noppes.npcs.api.item.IItemBlock;

public class ItemBlockWrapper extends ItemStackWrapper implements IItemBlock {
    protected String blockName;

    protected ItemBlockWrapper(ItemStack item) {
        super(item);
        Block b = Block.getBlockFromItem(item.getItem());
        blockName = Block.REGISTRY.getNameForObject(b) + "";
    }

    @Override
    public int getType() {
        return ItemType.BLOCK;
    }

    @Override
    public String getBlockName() {
        return blockName;
    }
}

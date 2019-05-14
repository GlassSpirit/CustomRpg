package noppes.npcs.common.objects.items;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;

public class ItemNpcBlock extends ItemBlock {

    public ItemNpcBlock(Block block) {
        super(block);

        String name = block.getTranslationKey().substring(5);

        setRegistryName(name);
        setTranslationKey(name);
    }

}

package noppes.npcs.objects.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;

public abstract class BlockInterface extends BlockContainer {

    protected BlockInterface(Material materialIn) {
        super(materialIn);
    }

    @Override
    public Block setTranslationKey(String name) {
        setRegistryName("customnpcs", name);
        return super.setTranslationKey(name);
    }
}

package ru.glassspirit.mixin.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRail;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(BlockRail.class)
public abstract class MixinBlockRail extends Block {

    public MixinBlockRail(Material materialIn) {
        super(materialIn);
    }

    @Shadow
    protected abstract void updateState(IBlockState state, World worldIn, BlockPos pos, Block blockIn);

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        if (!worldIn.isRemote) {
            this.updateState(state, worldIn, pos, blockIn);
        }
    }

}

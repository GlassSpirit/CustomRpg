package ru.glassspirit.mixin.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockFence.class)
public abstract class MixinBlockFence extends Block {

    public MixinBlockFence(Material materialIn) {
        super(materialIn);
    }

    @Inject(method = "canFenceConnectTo", at = @At("HEAD"), cancellable = true, remap = false)
    private void canFenceConnectTo(IBlockAccess world, BlockPos pos, EnumFacing facing, CallbackInfoReturnable<Boolean> info) {
        if (this == Blocks.ACACIA_FENCE || this == Blocks.NETHER_BRICK_FENCE) {
            BlockPos other = pos.offset(facing);
            Block block = world.getBlockState(other).getBlock();
            info.setReturnValue(block instanceof BlockFence);
        }
    }

}

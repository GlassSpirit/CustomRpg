package ru.glassspirit.mixin.blocks;

import com.teamwizardry.librarianlib.features.base.block.BlockModStairs;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.state.IBlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockModStairs.class)
public abstract class MixinBlockModStairs extends BlockStairs {

    private MixinBlockModStairs(IBlockState modelState) {
        super(modelState);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void init(CallbackInfo ci) {
        setLightOpacity(0);
    }

}

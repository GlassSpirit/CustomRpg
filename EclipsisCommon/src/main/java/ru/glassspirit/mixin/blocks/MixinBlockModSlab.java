package ru.glassspirit.mixin.blocks;

import com.teamwizardry.librarianlib.features.base.block.BlockModSlab;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.material.Material;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockModSlab.class)
public abstract class MixinBlockModSlab extends BlockSlab {

    private MixinBlockModSlab(Material materialIn) {
        super(materialIn);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void init(CallbackInfo ci) {
        setLightOpacity(0);
    }
}

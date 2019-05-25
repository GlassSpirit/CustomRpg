package ru.glassspirit.mixin.common;

import net.minecraft.util.FoodStats;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Disables minecraft food regeneration and reduction
 */
@Mixin(FoodStats.class)
public abstract class MixinFoodStats {

    @Inject(method = "onUpdate", at = @At("HEAD"), cancellable = true)
    private void onUpdate(CallbackInfo ci) {
        ci.cancel();
    }

}

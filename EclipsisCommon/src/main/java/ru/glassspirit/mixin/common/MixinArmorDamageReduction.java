package ru.glassspirit.mixin.common;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Disables minecraft armor damage reduction
 */
@Mixin(EntityLivingBase.class)
public abstract class MixinArmorDamageReduction {

    @Inject(method = "applyArmorCalculations", at = @At("HEAD"), cancellable = true)
    private void applyArmorCalculations(DamageSource source, float damage, CallbackInfoReturnable<Float> ci) {
        ci.setReturnValue(damage);
    }

}

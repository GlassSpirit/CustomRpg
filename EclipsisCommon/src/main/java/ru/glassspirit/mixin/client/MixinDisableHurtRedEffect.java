package ru.glassspirit.mixin.client;

import net.minecraft.client.renderer.entity.RenderLivingBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RenderLivingBase.class)
public abstract class MixinDisableHurtRedEffect {

    @Inject(method = "setBrightness", at = @At("HEAD"), cancellable = true)
    private void setBrightness(CallbackInfoReturnable<Boolean> ci) {
        ci.setReturnValue(false);
    }

}

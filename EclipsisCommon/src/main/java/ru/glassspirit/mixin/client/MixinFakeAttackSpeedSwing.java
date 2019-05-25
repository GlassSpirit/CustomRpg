package ru.glassspirit.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.RayTraceResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MixinFakeAttackSpeedSwing {

    @Shadow
    public EntityPlayerSP player;

    @Shadow
    public RayTraceResult objectMouseOver;

    @Shadow
    protected abstract void clickMouse();

    @Inject(method = "clickMouse", at = @At(value = "HEAD"), cancellable = true)
    private void clickMouse(CallbackInfo ci) {
        if (this.player.ticksSinceLastSwing / this.player.getCooldownPeriod() < 1) {
            ci.cancel();
        }
    }

    @Inject(method = "processKeyBinds", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;sendClickBlockToController(Z)V"), cancellable = true)
    private void processKeybinds(CallbackInfo ci) {
        if (this.player.ticksSinceLastSwing / this.player.getCooldownPeriod() < 1) {
            ci.cancel();
        }
    }

    @Inject(method = "sendClickBlockToController", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/PlayerControllerMP;resetBlockRemoving()V"))
    private void sendClickBlockToController(boolean leftClick, CallbackInfo ci) {
        if (leftClick && this.objectMouseOver != null && this.objectMouseOver.typeOfHit == RayTraceResult.Type.ENTITY) {
            if (this.objectMouseOver.entityHit instanceof EntityLivingBase) clickMouse();
        }
    }

}

package ru.glassspirit.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SideOnly(Side.CLIENT)
@Mixin(Minecraft.class)
public abstract class MixinMinecraft {

    private static void checkFullscreen() {
        System.setProperty("org.lwjgl.opengl.Window.undecorated", String.valueOf(Minecraft.getMinecraft().gameSettings.fullScreen));
    }

    @Shadow
    protected abstract void updateDisplayMode() throws LWJGLException;

    @Inject(method = "createDisplay", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/Display;setTitle(Ljava/lang/String;)V", shift = At.Shift.AFTER, remap = false))
    private void setDisplayTitle(CallbackInfo ci) {
        Display.setTitle("CifrAzia RPG");
        try {
            if (Minecraft.getMinecraft().gameSettings.fullScreen) {
                checkFullscreen();
                this.updateDisplayMode();
            }
        } catch (LWJGLException e) {
            e.printStackTrace();
        }
    }

    @Redirect(method = "toggleFullscreen", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/Display;setFullscreen(Z)V", remap = false))
    private void redirectDisplayFullscreen(boolean isFullscreen) {
        //Do nothing
    }

    @Inject(method = "toggleFullscreen", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;fullscreen:Z", ordinal = 3))
    private void toggleFullscreen(CallbackInfo ci) {
        checkFullscreen();
    }

}

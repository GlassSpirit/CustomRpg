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
import ru.glassspirit.eclipsis.proxy.ClientProxy;

@SideOnly(Side.CLIENT)
@Mixin(Minecraft.class)
public abstract class MixinMinecraft {

    @Shadow
    protected abstract void updateDisplayMode() throws LWJGLException;

    @Inject(method = "createDisplay", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/Display;setTitle(Ljava/lang/String;)V", shift = At.Shift.AFTER, remap = false))
    private void setDisplayTitle(CallbackInfo ci) {
        Display.setTitle("эклыпсис");
        try {
            if (Minecraft.getMinecraft().gameSettings.fullScreen) {
                checkFullscreen();
                this.updateDisplayMode();
            }
        } catch (LWJGLException e) {
            e.printStackTrace();
        }
    }

    @Inject(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/fml/client/FMLClientHandler;beginMinecraftLoading(Lnet/minecraft/client/Minecraft;Ljava/util/List;Lnet/minecraft/client/resources/IReloadableResourceManager;Lnet/minecraft/client/resources/data/MetadataSerializer;)V"), remap = false)
    private void sendZeroLoadingStep(CallbackInfo ci) {
        ClientProxy.loadingStep = 0;
        ClientProxy.loadingPercent = 0;
    }

    @Inject(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/fml/client/FMLClientHandler;beginMinecraftLoading(Lnet/minecraft/client/Minecraft;Ljava/util/List;Lnet/minecraft/client/resources/IReloadableResourceManager;Lnet/minecraft/client/resources/data/MetadataSerializer;)V", shift = At.Shift.AFTER, remap = false))
    private void sendSecondLoadingStep(CallbackInfo ci) {
        ClientProxy.loadingStep = 2;
        ClientProxy.loadingPercent = 0;
    }

    @Inject(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resources/IReloadableResourceManager;registerReloadListener(Lnet/minecraft/client/resources/IResourceManagerReloadListener;)V", ordinal = 2, shift = At.Shift.AFTER))
    private void sendThirdLoadingStep(CallbackInfo ci) {
        ClientProxy.loadingStep = 3;
        ClientProxy.loadingPercent = 0;
    }

    @Inject(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resources/IReloadableResourceManager;registerReloadListener(Lnet/minecraft/client/resources/IResourceManagerReloadListener;)V", ordinal = 7, shift = At.Shift.AFTER))
    private void sendFourthLoadingStep(CallbackInfo ci) {
        ClientProxy.loadingStep = 4;
        ClientProxy.loadingPercent = 0;
    }

    @Redirect(method = "toggleFullscreen", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/Display;setFullscreen(Z)V", remap = false))
    private void redirectDisplayFullscreen(boolean isFullscreen) {
        //Do nothing
    }

    @Inject(method = "toggleFullscreen", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;fullscreen:Z", ordinal = 3))
    private void toggleFullscreen(CallbackInfo ci) {
        checkFullscreen();
    }

    private static void checkFullscreen() {
        System.setProperty("org.lwjgl.opengl.Window.undecorated", String.valueOf(Minecraft.getMinecraft().gameSettings.fullScreen));
    }

}

package ru.glassspirit.mixin.client;

import com.google.common.collect.BiMap;
import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.event.FMLEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.glassspirit.eclipsis.proxy.ClientProxy;

import java.util.List;

@SideOnly(Side.CLIENT)
@Mixin(LoadController.class)
public abstract class MixinLoadController {

    @Shadow(remap = false)
    private BiMap<ModContainer, Object> modObjectList;

    @Shadow(remap = false)
    private List<ModContainer> activeModList;

    @Inject(method = "propogateStateMessage", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/fml/common/LoadController;sendEventToModContainer(Lnet/minecraftforge/fml/common/event/FMLEvent;Lnet/minecraftforge/fml/common/ModContainer;)V", shift = At.Shift.AFTER), remap = false)
    private void onModEvent(FMLEvent event, CallbackInfo ci) {
        ClientProxy.onModEvent(this.activeModList, this.modObjectList, event);
    }
}

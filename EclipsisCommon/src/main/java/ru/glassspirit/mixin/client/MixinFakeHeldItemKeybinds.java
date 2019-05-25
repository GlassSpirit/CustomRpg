package ru.glassspirit.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.glassspirit.eclipsis.client.ClientRpgData;

@Mixin(Minecraft.class)
public class MixinFakeHeldItemKeybinds {

    @Shadow
    public EntityPlayerSP player;

    @Inject(method = "processKeyBinds", at = @At(value = "FIELD",
            target = "Lnet/minecraft/entity/player/InventoryPlayer;currentItem:I",
            shift = At.Shift.AFTER))
    private void processKeybindHotbar(CallbackInfo ci) {
        if (ClientRpgData.INSTANCE.getPlayerInventoryMode() == ClientRpgData.INVENTORY_RPG_MODE) {
            this.player.inventory.currentItem = 0;
        }
    }

    @Inject(method = "processKeyBinds", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/Minecraft;middleClickMouse()V",
            shift = At.Shift.AFTER))
    private void processKeybindPickBlock(CallbackInfo ci) {
        if (ClientRpgData.INSTANCE.getPlayerInventoryMode() == ClientRpgData.INVENTORY_RPG_MODE) {
            this.player.inventory.currentItem = 0;
        }
    }

    @Inject(method = "runTickMouse", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/entity/player/InventoryPlayer;changeCurrentItem(I)V",
            shift = At.Shift.AFTER))
    private void processKeybindMouseWheel(CallbackInfo ci) {
        if (ClientRpgData.INSTANCE.getPlayerInventoryMode() == ClientRpgData.INVENTORY_RPG_MODE) {
            this.player.inventory.currentItem = 0;
        }
    }

}

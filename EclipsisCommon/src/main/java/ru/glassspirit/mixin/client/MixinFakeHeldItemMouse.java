package ru.glassspirit.mixin.client;

import net.minecraft.entity.player.InventoryPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.glassspirit.eclipsis.client.ClientRpgData;

@Mixin(InventoryPlayer.class)
public abstract class MixinFakeHeldItemMouse {

	@Shadow
	public int currentItem;

	@Inject(method = "changeCurrentItem", at = @At("HEAD"), cancellable = true)
	private void injectGetCurrentItem(CallbackInfo ci) {
		if (ClientRpgData.INSTANCE.getPlayerInventoryMode() == ClientRpgData.INVENTORY_RPG_MODE) {
			this.currentItem = 0;
			ci.cancel();
		}
	}

}

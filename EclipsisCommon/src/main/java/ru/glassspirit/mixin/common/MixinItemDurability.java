package ru.glassspirit.mixin.common;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Adds custom max durability to items
 */
@Mixin(Item.class)
public abstract class MixinItemDurability {

    @Inject(method = "getMaxDamage(Lnet/minecraft/item/ItemStack;)I", at = @At("HEAD"), cancellable = true, remap = false)
    private void getMaxDamage(ItemStack stack, CallbackInfoReturnable<Integer> ci) {
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("eclipsis_damage")) {
            ci.setReturnValue(stack.getTagCompound().getInteger("eclipsis_damage"));
        }
    }

}

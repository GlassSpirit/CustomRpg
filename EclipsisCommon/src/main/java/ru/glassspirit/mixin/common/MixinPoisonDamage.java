package ru.glassspirit.mixin.common;

import net.minecraft.potion.Potion;
import net.minecraft.util.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import ru.glassspirit.eclipsis.damage.EclipsisDamageSource;

@Mixin(Potion.class)
public abstract class MixinPoisonDamage {

    @Redirect(method = "performEffect", at = @At(value = "FIELD",
            target = "Lnet/minecraft/util/DamageSource;MAGIC:Lnet/minecraft/util/DamageSource;", ordinal = 0))
    private DamageSource performEffect() {
        return EclipsisDamageSource.POISON;
    }

}

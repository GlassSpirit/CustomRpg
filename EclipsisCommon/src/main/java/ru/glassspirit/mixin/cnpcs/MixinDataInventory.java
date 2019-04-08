package ru.glassspirit.mixin.cnpcs;

import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;
import noppes.npcs.entity.data.DataInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.glassspirit.eclipsis.EclipsisPluginConfigurationReflection;


@Mixin(DataInventory.class)
public abstract class MixinDataInventory {

    @Inject(method = "dropStuff", at = @At(value = "INVOKE", target = "Lnoppes/npcs/entity/data/DataInventory;getExpRNG()I"),
            cancellable = true, remap = false)
    private void dropExp(Entity entity, DamageSource damagesource, CallbackInfo info) {
        if (!EclipsisPluginConfigurationReflection.NPC_KILLS_EXP_MINECRAFT) {
            info.cancel();
        }
    }

}

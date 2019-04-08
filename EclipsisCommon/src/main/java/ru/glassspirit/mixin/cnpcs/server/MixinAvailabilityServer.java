package ru.glassspirit.mixin.cnpcs.server;

import cz.neumimto.rpg.NtRpgPlugin;
import cz.neumimto.rpg.players.IActiveCharacter;
import net.minecraft.entity.player.EntityPlayer;
import noppes.npcs.controllers.data.Availability;
import org.spongepowered.asm.lib.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import ru.glassspirit.eclipsis.EclipsisPluginConfigurationReflection;

@Mixin(Availability.class)
public class MixinAvailabilityServer {

    @Redirect(method = "isAvailable(Lnet/minecraft/entity/player/EntityPlayer;)Z", remap = false,
            at = @At(value = "FIELD", target = "Lnet/minecraft/entity/player/EntityPlayer;experienceLevel:I", opcode = Opcodes.GETFIELD, remap = true))
    private int isAvailablePlayerLevelRedirect(EntityPlayer player) {
        if (EclipsisPluginConfigurationReflection.REPLACE_AVAILABILITY_LEVEL) {
            IActiveCharacter character = NtRpgPlugin.GlobalScope.characterService.getCharacter(player.getUniqueID());
            if (character == null || character.isStub()) {
                return 0;
            }
            return character.getLevel();
        } else return player.experienceLevel;
    }

}

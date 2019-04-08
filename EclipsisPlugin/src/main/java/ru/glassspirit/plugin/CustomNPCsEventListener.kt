package ru.glassspirit.plugin

import cz.neumimto.rpg.NtRpgPlugin
import cz.neumimto.rpg.common.logging.Log
import cz.neumimto.rpg.configuration.DebugLevel
import cz.neumimto.rpg.players.CharacterService
import cz.neumimto.rpg.players.ExperienceSources
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import noppes.npcs.api.NpcAPI
import noppes.npcs.api.constants.EntityType
import noppes.npcs.api.event.NpcEvent
import noppes.npcs.api.event.QuestEvent
import org.spongepowered.api.entity.living.player.Player

object CustomNPCsEventListener {

    private lateinit var characterService: CharacterService

    init {
        if (NpcAPI.IsAvailable()) {
            NpcAPI.Instance().events().register(this)
            characterService = NtRpgPlugin.GlobalScope.characterService
        }
    }

    @SubscribeEvent
    fun onNpcQuestCompletion(event: QuestEvent.QuestTurnedInEvent) {
        if (EclipsisPluginConfiguration.QUESTS_EXP_RPG) {
            val character = characterService.getCharacter(event.player.mcEntity as Player)
            if (character != null && !character.isStub) {
                characterService.addExperiences(character, event.expReward.toDouble(), ExperienceSources.QUESTING)
                Log.info(String.format("Adding %s experience to %s for completing quest \"%s\"",
                        event.expReward,
                        character.player.name,
                        event.quest.name),
                        DebugLevel.BALANCE)
            }
        }
        if (!EclipsisPluginConfiguration.QUESTS_EXP_MINECRAFT) {
            event.expReward = 0
        }
    }

    @SubscribeEvent
    fun onNpcDeath(event: NpcEvent.DiedEvent) {
        if (EclipsisPluginConfiguration.NPC_KILLS_EXP_RPG) {
            if (event.damageSource.trueSource.typeOf(EntityType.PLAYER)) {
                val character = characterService.getCharacter(event.damageSource.trueSource.mcEntity as Player)
                if (character != null && !character.isStub) {
                    val experience = event.npc.inventory.expRNG
                    characterService.addExperiences(character, experience.toDouble(), ExperienceSources.PVE)
                    Log.info(String.format("Adding %s experience to %s for killing npc \"%s\"",
                            experience,
                            character.player.name,
                            event.npc.name),
                            DebugLevel.BALANCE)
                }
            }
        }
    }


}

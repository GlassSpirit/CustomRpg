package ru.glassspirit.plugin

import com.google.inject.Inject
import ninja.leaping.configurate.commented.CommentedConfigurationNode
import ninja.leaping.configurate.loader.ConfigurationLoader
import ninja.leaping.configurate.objectmapping.ObjectMapper
import org.slf4j.Logger
import org.spongepowered.api.config.DefaultConfig
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.game.GameReloadEvent
import org.spongepowered.api.event.game.state.GameAboutToStartServerEvent
import org.spongepowered.api.event.game.state.GameInitializationEvent
import org.spongepowered.api.event.game.state.GamePostInitializationEvent
import org.spongepowered.api.plugin.Dependency
import org.spongepowered.api.plugin.Plugin
import ru.glassspirit.eclipsis.EclipsisPluginConfigurationReflection


@Plugin(id = "eclipsis_plugin", name = "EclipsisPlugin", authors = ["GlassSpirit"],
        dependencies = [Dependency(id = "customnpcs"), Dependency(id = "eclipsis")])
class EclipsisPlugin @Inject constructor() {

    @Inject
    private lateinit var logger: Logger

    @Inject
    @DefaultConfig(sharedRoot = true)
    private lateinit var config: ConfigurationLoader<CommentedConfigurationNode>

    @Listener
    fun onGameInitialization(event: GameInitializationEvent) {
        loadConfig()
    }

    @Listener
    fun onGamePostInitialization(event: GamePostInitializationEvent) {

    }

    @Listener
    fun onGameReload(event: GameReloadEvent) {
        loadConfig()
    }

    @Listener
    fun onGameAboutToStartServer(event: GameAboutToStartServerEvent) {
        try {
            Class.forName("noppes.npcs.api.NpcAPI")
            Class.forName("cz.neumimto.rpg.NtRpgPlugin")
            CustomNPCsEventListener
            logger.info("CustomNPCs found! Event listener registered.")
        } catch (e: ClassNotFoundException) {
            logger.error("CustomNPCs not found!", e)
        }

    }

    private fun loadConfig() {
        try {
            val configMapper = ObjectMapper.forObject(EclipsisPluginConfiguration)
            val node = config.load()
            configMapper.serialize(node)
            config.save(node)
            reloadReflectingConfig()
        } catch (e: Exception) {
            logger.error("Could not load config", e)
        }
    }

    private fun reloadReflectingConfig() {
        val o = EclipsisPluginConfiguration
        val r = EclipsisPluginConfigurationReflection
        r.NPC_KILLS_EXP_MINECRAFT = o.NPC_KILLS_EXP_MINECRAFT
        r.NPC_KILLS_EXP_RPG = o.NPC_KILLS_EXP_RPG
        r.QUESTS_EXP_MINECRAFT = o.QUESTS_EXP_MINECRAFT
        r.QUESTS_EXP_RPG = o.QUESTS_EXP_RPG
        r.REPLACE_AVAILABILITY_LEVEL = o.REPLACE_AVAILABILITY_LEVEL
    }

}

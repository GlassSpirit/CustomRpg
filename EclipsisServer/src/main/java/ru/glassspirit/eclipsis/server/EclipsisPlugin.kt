package ru.glassspirit.eclipsis.server

import com.google.inject.Inject
import cz.neumimto.rpg.NtRpgPlugin
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
import ru.glassspirit.eclipsis.server.listener.BasicMechanicServerListener
import ru.glassspirit.eclipsis.server.listener.CombatListener
import java.io.File
import java.io.IOException
import java.net.JarURLConnection


@Plugin(id = "eclipsis_plugin", name = "EclipsisPlugin", authors = ["GlassSpirit"],
        dependencies = [Dependency(id = "customnpcs"), Dependency(id = "eclipsis")])
class EclipsisPlugin @Inject constructor() {

    companion object {
        @JvmStatic
        lateinit var INSTANCE: EclipsisPlugin
    }

    @Inject
    private lateinit var logger: Logger

    @Inject
    @DefaultConfig(sharedRoot = true)
    private lateinit var config: ConfigurationLoader<CommentedConfigurationNode>

    @Listener
    fun onGameInitialization(event: GameInitializationEvent) {
        INSTANCE = this
        loadConfig()
    }

    @Listener
    fun onGamePostInitialization(event: GamePostInitializationEvent) {
        loadJar()
    }

    @Listener
    fun onGameReload(event: GameReloadEvent) {
        loadConfig()
        loadJar()
    }

    @Listener
    fun onGameAboutToStartServer(event: GameAboutToStartServerEvent) {
        BasicMechanicServerListener
        CombatListener
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
            logger.error("Could not init config", e)
        }
    }

    private fun loadJar() {
        var pluginJar = File("")
        val clsUrl = EclipsisPlugin::class.java.getResource(EclipsisPlugin::class.java.simpleName + ".class")
        if (clsUrl != null) {
            try {
                val conn = clsUrl.openConnection()
                if (conn is JarURLConnection) {
                    pluginJar = File(conn.jarFileURL.toURI())
                }
            } catch (e: IOException) {
                throw RuntimeException(e)
            }
        }
        NtRpgPlugin.GlobalScope.resourceLoader.loadJarFile(pluginJar, false)
    }

    private fun reloadReflectingConfig() {
        EclipsisPluginConfigurationReflection.NPC_KILLS_EXP_MINECRAFT = EclipsisPluginConfiguration.NPC_KILLS_EXP_MINECRAFT
        EclipsisPluginConfigurationReflection.NPC_KILLS_EXP_RPG = EclipsisPluginConfiguration.NPC_KILLS_EXP_RPG
        EclipsisPluginConfigurationReflection.QUESTS_EXP_MINECRAFT = EclipsisPluginConfiguration.QUESTS_EXP_MINECRAFT
        EclipsisPluginConfigurationReflection.QUESTS_EXP_RPG = EclipsisPluginConfiguration.QUESTS_EXP_RPG
        EclipsisPluginConfigurationReflection.REPLACE_AVAILABILITY_LEVEL = EclipsisPluginConfiguration.REPLACE_AVAILABILITY_LEVEL
    }

}

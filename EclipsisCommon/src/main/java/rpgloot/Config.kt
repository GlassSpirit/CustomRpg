package rpgloot

import net.minecraftforge.common.config.Configuration

import java.io.File

class Config(suggestedConfigurationFile: File) {

    var decayTime: Int = 0
        private set
    private val scatterDrops: Boolean

    val looting: Boolean
        get() = !scatterDrops

    init {
        configuration = Configuration(suggestedConfigurationFile)
        decayTime = configuration.getInt("corpseDecayTime", "general", 5, -1, Integer.MAX_VALUE,
                "Time in minutes a corpse will take to decay. Setting to -1 will require manual disposing of a corpse.")
        scatterDrops = configuration.getBoolean("scatterDrops", "general", false,
                "Should the Entities drops be scattered on the ground?")
        if (scatterDrops && decayTime == -1) {
            decayTime = 5
        }

        if (configuration.hasChanged()) {
            configuration.save()
        }

    }

    companion object {
        lateinit var configuration: Configuration
    }

}

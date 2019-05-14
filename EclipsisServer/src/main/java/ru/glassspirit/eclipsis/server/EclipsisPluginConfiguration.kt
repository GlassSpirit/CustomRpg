package ru.glassspirit.eclipsis.server

import ninja.leaping.configurate.objectmapping.Setting
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable

@ConfigSerializable
object EclipsisPluginConfiguration {

    @JvmField
    @Setting(value = "quests_exp_rpg", comment = "NT-RPG experience for completing quests")
    var QUESTS_EXP_RPG = true

    @JvmField
    @Setting(value = "quests_exp_minecraft", comment = "Minecraft experience for completing quests")
    var QUESTS_EXP_MINECRAFT = false

    @JvmField
    @Setting(value = "npc_kills_exp_rpg", comment = "NT-RPG experience for killing mobs")
    var NPC_KILLS_EXP_RPG = true

    @JvmField
    @Setting(value = "npc_kills_exp_minecraft", comment = "Minecraft experience for killing mobs")
    var NPC_KILLS_EXP_MINECRAFT = false

    @JvmField
    @Setting(value = "replace_availability_level", comment = "Replace player level check for character level check in Availability")
    var REPLACE_AVAILABILITY_LEVEL = true

}

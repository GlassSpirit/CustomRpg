package ru.glassspirit.eclipsis.client

import com.teamwizardry.librarianlib.features.kotlin.Minecraft
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

@SideOnly(Side.CLIENT)
object ClientRpgData {

    const val INVENTORY_BUILD_MODE = 0
    const val INVENTORY_RPG_MODE = 1

    var playerInventoryMode: Int = INVENTORY_RPG_MODE

    var characterName: String = Minecraft().player.name

    var characterClasses: List<RpgClass> = ArrayList()

    var characterPrimaryClass: RpgClass? = null
        get() = characterClasses.getOrNull(0)

    var characterHealth: Float = 20.0f
        get() = Minecraft().player.health

    var characterMaxHealth: Float = 20.0f
        get() = Minecraft().player.maxHealth

    var characterMana: Float = 0.0f

    var characterMaxMana: Float = 0.0f
}

interface RpgUsable {
    fun use()
}

class RpgHotbar {

}

class RpgClass(val name: String) {
    var level: Int = 0
    var experience: Int = 0
    var totalExperience: Int = 0
}

class RpgSkill(val name: String): RpgUsable {
    override fun use() {
        Minecraft().player.sendChatMessage("/skill $name")
    }
}
package ru.glassspirit.eclipsis.damage

import net.minecraft.util.DamageSource

object EclipsisDamageSource {
    @JvmField
    val POISON = DamageSource("poison").setDamageBypassesArmor()
}

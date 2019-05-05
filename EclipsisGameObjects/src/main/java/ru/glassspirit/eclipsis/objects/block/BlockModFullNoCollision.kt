package ru.glassspirit.eclipsis.objects.block

import net.minecraft.block.material.Material
import ru.glassspirit.eclipsis.objects.block.decorations.BlockDecor

open class BlockModFullNoCollision(name: String, materialIn: Material) : BlockDecor(name, materialIn, cutout = true, collide = false) {
    init {
        this.setLightOpacity(0)
    }
}

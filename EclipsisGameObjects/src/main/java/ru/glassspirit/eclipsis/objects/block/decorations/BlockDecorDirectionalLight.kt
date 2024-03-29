package ru.glassspirit.eclipsis.objects.block.decorations

import net.minecraft.block.material.Material

class BlockDecorDirectionalLight(name: String, material: Material, lightLevel: Float, horizontal: Boolean = true) : BlockDecorDirectionalFullNoCollision(name, material, horizontal) {
    init {
        setLightLevel(lightLevel)
    }
}

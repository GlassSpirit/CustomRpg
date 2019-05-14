package ru.glassspirit.eclipsis.objects.block.decorations

import net.minecraft.block.material.Material
import ru.glassspirit.eclipsis.objects.block.BlockDecorDirectional

open class BlockDecorDirectionalFullNoCollision(name: String, material: Material, horizontal: Boolean = true)
    : BlockDecorDirectional(name, material, horizontal, cutout = true, collide = false)
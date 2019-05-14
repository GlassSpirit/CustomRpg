package ru.glassspirit.eclipsis.objects.block.decorations

import net.minecraft.block.material.Material
import ru.glassspirit.eclipsis.objects.block.BlockDecor

open class BlockDecorFullNoCollision(name: String, materialIn: Material) : BlockDecor(name, materialIn, cutout = true, collide = false)

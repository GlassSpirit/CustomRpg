package ru.glassspirit.eclipsis.objects.block.decorations

import net.minecraft.block.material.Material
import ru.glassspirit.eclipsis.objects.block.BlockDecor
import ru.glassspirit.eclipsis.objects.block.BlockModel

class BlockDecorFlowerPot(name: String, material: Material) : BlockDecor(name, material, cutout = true, collide = false, model = BlockModel.FLOWER_POT)
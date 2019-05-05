package ru.glassspirit.eclipsis.objects.block.decorations

import net.minecraft.block.material.Material
import ru.glassspirit.eclipsis.objects.block.BlockModel

class BlockDecorWall(name: String, material: Material) : BlockDecor(name, material, cutout = true, model = BlockModel.WALL)
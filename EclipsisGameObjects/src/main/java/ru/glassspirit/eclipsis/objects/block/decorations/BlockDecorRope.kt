package ru.glassspirit.eclipsis.objects.block.decorations

import net.minecraft.block.material.Material
import ru.glassspirit.eclipsis.objects.block.BlockModel

class BlockDecorRope(name: String, materialIn: Material) : BlockDecor(name, materialIn, cutout = true, collide = false, model = BlockModel.ROPE)

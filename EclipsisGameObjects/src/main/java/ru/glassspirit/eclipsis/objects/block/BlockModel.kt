package ru.glassspirit.eclipsis.objects.block

import net.minecraft.block.Block
import net.minecraft.util.math.AxisAlignedBB

enum class BlockModel(val aabb: AxisAlignedBB) {
    FULL(Block.FULL_BLOCK_AABB),
    HALF(AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.5, 1.0)),
    PLATE(AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.375, 1.0)),
    CARPET(AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.0625, 1.0)),
    BUSH(AxisAlignedBB(0.3, 0.0, 0.3, 0.7, 0.6, 0.7)),
    FLOWER_POT(AxisAlignedBB(0.3125, 0.0, 0.3125, 0.6875, 0.375, 0.6875)),
    SAPLING(AxisAlignedBB(0.1, 0.0, 0.1, 0.9, 0.8, 0.9)),
    ROPE(AxisAlignedBB(0.375, 0.0, 0.375, 0.625, 1.0, 0.625)),
    WALL(AxisAlignedBB(0.25, 0.0, 0.25, 0.75, 1.0, 0.75))
}
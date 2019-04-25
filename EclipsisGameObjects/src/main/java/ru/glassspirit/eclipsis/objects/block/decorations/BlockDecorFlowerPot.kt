package ru.glassspirit.eclipsis.objects.block.decorations

import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import ru.glassspirit.eclipsis.objects.block.BlockModFullNoCollision

class BlockDecorFlowerPot(name: String, material: Material) : BlockModFullNoCollision(name, material) {
    companion object {
        private val FLOWER_POT_AABB = AxisAlignedBB(0.3125, 0.0, 0.3125, 0.6875, 0.375, 0.6875)
    }

    override fun getBoundingBox(state: IBlockState, source: IBlockAccess, pos: BlockPos): AxisAlignedBB {
        return FLOWER_POT_AABB
    }
}
package ru.glassspirit.eclipsis.objects.block.decorations

import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import ru.glassspirit.eclipsis.objects.block.BlockModFullNoCollision

class BlockDecorSapling(name: String, material: Material) : BlockModFullNoCollision(name, material) {
    companion object {
        private val SAPLING_AABB = AxisAlignedBB(0.1, 0.0, 0.1, 0.9, 0.8, 0.9)
    }

    override fun getBoundingBox(state: IBlockState, source: IBlockAccess, pos: BlockPos): AxisAlignedBB {
        return SAPLING_AABB
    }
}
package ru.glassspirit.eclipsis.objects.block.decorations

import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import ru.glassspirit.eclipsis.objects.block.BlockModFullNoCollision

class BlockDecorBush(name: String, material: Material) : BlockModFullNoCollision(name, material) {
    companion object {
        private val BUSH_AABB = AxisAlignedBB(0.3, 0.0, 0.3, 0.7, 0.6, 0.7)
    }

    override fun getBoundingBox(state: IBlockState, source: IBlockAccess, pos: BlockPos): AxisAlignedBB {
        return BUSH_AABB
    }
}
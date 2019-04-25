package ru.glassspirit.eclipsis.objects.block.decorations

import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import ru.glassspirit.eclipsis.objects.block.BlockModFullNoCollision

class BlockDecorPlate(name: String, material: Material) : BlockModFullNoCollision(name, material) {
    companion object {
        private val PLATE_AABB = AxisAlignedBB(0.0625, 0.0, 0.0625, 0.9375, 0.03125, 0.9375)
    }

    override fun getBoundingBox(state: IBlockState, source: IBlockAccess, pos: BlockPos): AxisAlignedBB {
        return PLATE_AABB
    }
}
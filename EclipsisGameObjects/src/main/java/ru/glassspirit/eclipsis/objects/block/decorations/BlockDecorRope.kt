package ru.glassspirit.eclipsis.objects.block.decorations

import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import ru.glassspirit.eclipsis.objects.block.BlockModFullNoCollision

class BlockDecorRope(name: String, materialIn: Material) : BlockModFullNoCollision(name, materialIn) {
    companion object {
        val ROPE_AABB = AxisAlignedBB(0.375, 0.0, 0.375, 0.625, 1.0, 0.625)
    }

    override fun getBoundingBox(state: IBlockState?, source: IBlockAccess?, pos: BlockPos?): AxisAlignedBB {
        return ROPE_AABB
    }
}

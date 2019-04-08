package ru.glassspirit.eclipsis.objects.block.decorations

import com.teamwizardry.librarianlib.features.base.block.BlockModDirectional
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess

class BlockDecorationLight(name: String, material: Material, lightLevel: Float) : BlockModDirectional(name, material, false) {

    init {
        setLightLevel(lightLevel)
    }

    override fun isFullCube(state: IBlockState): Boolean {
        return false
    }

    override fun isPassable(worldIn: IBlockAccess, pos: BlockPos): Boolean {
        return true
    }

    override fun isOpaqueCube(state: IBlockState): Boolean {
        return false
    }

    override fun getCollisionBoundingBox(blockState: IBlockState, worldIn: IBlockAccess, pos: BlockPos): AxisAlignedBB? {
        return Block.NULL_AABB
    }

    override fun getBoundingBox(state: IBlockState, source: IBlockAccess, pos: BlockPos): AxisAlignedBB {
        return FLOWER_POT_AABB
    }

    companion object {
        protected val FLOWER_POT_AABB = AxisAlignedBB(0.3125, 0.0, 0.3125, 0.6875, 0.375, 0.6875)
    }
}

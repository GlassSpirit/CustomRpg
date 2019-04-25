package ru.glassspirit.eclipsis.objects.block

import com.teamwizardry.librarianlib.features.base.block.BlockMod
import net.minecraft.block.material.Material
import net.minecraft.block.state.BlockFaceShape
import net.minecraft.block.state.IBlockState
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

open class BlockModCarpet(name: String, material: Material) : BlockMod(name, material) {
    companion object {
        private val CARPET_AABB = AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.0625, 1.0)
    }

    override fun getBoundingBox(state: IBlockState, source: IBlockAccess, pos: BlockPos): AxisAlignedBB {
        return CARPET_AABB
    }

    override fun isOpaqueCube(state: IBlockState): Boolean {
        return false
    }

    override fun isFullCube(state: IBlockState): Boolean {
        return false
    }

    @SideOnly(Side.CLIENT)
    override fun shouldSideBeRendered(blockState: IBlockState, blockAccess: IBlockAccess, pos: BlockPos, side: EnumFacing): Boolean {
        return if (side == EnumFacing.UP) {
            true
        } else {
            if (blockAccess.getBlockState(pos.offset(side)).block === this) true else super.shouldSideBeRendered(blockState, blockAccess, pos, side)
        }
    }

    override fun getBlockFaceShape(worldIn: IBlockAccess, state: IBlockState, pos: BlockPos, face: EnumFacing): BlockFaceShape {
        return if (face == EnumFacing.DOWN) BlockFaceShape.SOLID else BlockFaceShape.UNDEFINED
    }
}
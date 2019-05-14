package ru.glassspirit.eclipsis.objects.block.decorations

import net.minecraft.block.material.Material
import net.minecraft.block.state.BlockFaceShape
import net.minecraft.block.state.IBlockState
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import ru.glassspirit.eclipsis.objects.block.BlockDecor
import ru.glassspirit.eclipsis.objects.block.BlockModel

open class BlockDecorCarpet(name: String, material: Material) : BlockDecor(name, material, cutout = true, collide = false, model = BlockModel.CARPET) {
    @SideOnly(Side.CLIENT)
    override fun shouldSideBeRendered(blockState: IBlockState, blockAccess: IBlockAccess, pos: BlockPos, side: EnumFacing): Boolean {
        return if (side == EnumFacing.UP) {
            true
        } else {
            if (blockAccess.getBlockState(pos.offset(side)).block === this) true
            else super.shouldSideBeRendered(blockState, blockAccess, pos, side)
        }
    }

    override fun getBlockFaceShape(worldIn: IBlockAccess, state: IBlockState, pos: BlockPos, face: EnumFacing): BlockFaceShape {
        return if (face == EnumFacing.DOWN) BlockFaceShape.SOLID
        else BlockFaceShape.UNDEFINED
    }
}
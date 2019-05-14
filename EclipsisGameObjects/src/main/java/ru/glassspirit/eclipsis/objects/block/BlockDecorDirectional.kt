package ru.glassspirit.eclipsis.objects.block

import com.teamwizardry.librarianlib.features.base.block.BlockModDirectional
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.util.BlockRenderLayer
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

open class BlockDecorDirectional(name: String, material: Material,
                                 horizontal: Boolean = true,
                                 val cutout: Boolean = false,
                                 val collide: Boolean = true,
                                 val model: BlockModel = BlockModel.FULL) : BlockModDirectional(name, material, horizontal) {
    init {
        if (cutout) this.setLightOpacity(0)
    }

    override fun isOpaqueCube(state: IBlockState): Boolean {
        return !cutout
    }

    override fun isFullCube(state: IBlockState): Boolean {
        return collide
    }

    override fun isPassable(worldIn: IBlockAccess, pos: BlockPos): Boolean {
        return if (!collide) true
        else super.isPassable(worldIn, pos)
    }

    @SideOnly(Side.CLIENT)
    override fun getRenderLayer(): BlockRenderLayer {
        return if (cutout) BlockRenderLayer.CUTOUT_MIPPED
        else BlockRenderLayer.SOLID
    }

    override fun getBoundingBox(state: IBlockState, source: IBlockAccess, pos: BlockPos): AxisAlignedBB {
        return model.aabb
    }

    override fun getCollisionBoundingBox(blockState: IBlockState, worldIn: IBlockAccess, pos: BlockPos): AxisAlignedBB? {
        return if (!collide) NULL_AABB
        else model.aabb
    }
}
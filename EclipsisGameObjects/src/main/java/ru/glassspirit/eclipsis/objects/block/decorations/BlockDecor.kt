package ru.glassspirit.eclipsis.objects.block.decorations

import com.teamwizardry.librarianlib.features.base.block.BlockMod
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.util.BlockRenderLayer
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import ru.glassspirit.eclipsis.objects.block.BlockModel

open class BlockDecor(name: String, material: Material,
                      val cutout: Boolean = false,
                      val collide: Boolean = true,
                      val model: BlockModel = BlockModel.FULL) : BlockMod(name, material) {

    override fun isOpaqueCube(state: IBlockState): Boolean {
        return !cutout
    }

    override fun isFullCube(state: IBlockState): Boolean {
        return collide
    }

    override fun isPassable(worldIn: IBlockAccess, pos: BlockPos): Boolean {
        return collide
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
        return if (collide) NULL_AABB
        else model.aabb
    }

}
package ru.glassspirit.eclipsis.objects.block

import com.teamwizardry.librarianlib.features.base.block.BlockMod
import net.minecraft.block.material.Material
import net.minecraft.block.properties.PropertyInteger
import net.minecraft.block.state.BlockFaceShape
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.item.ItemBlock
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import ru.glassspirit.eclipsis.objects.item.ItemModBlockLayer
import java.util.*

open class BlockModLayer(name: String, material: Material) : BlockMod(name, material) {
    companion object {
        @JvmStatic
        val LAYERS = PropertyInteger.create("layers", 1, 8)

        @JvmStatic
        val LAYER_AABB = arrayOf(
                AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.0, 1.0),
                AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.125, 1.0),
                AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.25, 1.0),
                AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.375, 1.0),
                AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.5, 1.0),
                AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.625, 1.0),
                AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.75, 1.0),
                AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.875, 1.0),
                AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0))
    }

    init {
        this.defaultState = this.blockState.baseState.withProperty(LAYERS, 1)
    }

    override fun createItemForm(): ItemBlock? {
        return ItemModBlockLayer(this)
    }

    override fun getBoundingBox(state: IBlockState, source: IBlockAccess, pos: BlockPos): AxisAlignedBB {
        return LAYER_AABB[state.getValue(LAYERS)]
    }

    override fun isPassable(worldIn: IBlockAccess, pos: BlockPos): Boolean {
        return worldIn.getBlockState(pos).getValue(LAYERS) < 5
    }

    override fun isTopSolid(state: IBlockState): Boolean {
        return state.getValue(LAYERS) == 8
    }

    override fun getBlockFaceShape(worldIn: IBlockAccess, state: IBlockState, pos: BlockPos, face: EnumFacing): BlockFaceShape {
        return if (face == EnumFacing.DOWN) BlockFaceShape.SOLID else BlockFaceShape.UNDEFINED
    }

    override fun getCollisionBoundingBox(blockState: IBlockState, worldIn: IBlockAccess, pos: BlockPos): AxisAlignedBB? {
        val i = blockState.getValue(LAYERS) - 1
        val axisalignedbb = blockState.getBoundingBox(worldIn, pos)
        return AxisAlignedBB(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ, axisalignedbb.maxX, i * 0.125, axisalignedbb.maxZ)
    }

    override fun isOpaqueCube(state: IBlockState): Boolean {
        return false
    }

    override fun isFullCube(state: IBlockState): Boolean {
        return false
    }

    override fun canPlaceBlockAt(worldIn: World, pos: BlockPos): Boolean {
        val iblockstate = worldIn.getBlockState(pos.down())
        val block = iblockstate.block
        val blockfaceshape = iblockstate.getBlockFaceShape(worldIn, pos.down(), EnumFacing.UP)

        return blockfaceshape == BlockFaceShape.SOLID || iblockstate.block.isLeaves(iblockstate, worldIn, pos.down()) || block === this && iblockstate.getValue(LAYERS) == 8
    }

    @SideOnly(Side.CLIENT)
    override fun shouldSideBeRendered(blockState: IBlockState, blockAccess: IBlockAccess, pos: BlockPos, side: EnumFacing): Boolean {
        return if (side == EnumFacing.UP) {
            true
        } else {
            val iblockstate = blockAccess.getBlockState(pos.offset(side))
            if (iblockstate.block === this && iblockstate.getValue(LAYERS) >= blockState.getValue(LAYERS)) false
            else super.shouldSideBeRendered(blockState, blockAccess, pos, side)
        }
    }

    override fun isReplaceable(worldIn: IBlockAccess, pos: BlockPos): Boolean {
        return worldIn.getBlockState(pos).getValue(LAYERS) == 1
    }

    override fun quantityDropped(state: IBlockState, fortune: Int, random: Random): Int {
        return state.getValue(LAYERS) + 1
    }

    override fun getStateFromMeta(meta: Int): IBlockState {
        return this.defaultState.withProperty(LAYERS, Integer.valueOf((meta and 7) + 1))
    }

    override fun getMetaFromState(state: IBlockState): Int {
        return state.getValue(LAYERS) - 1
    }

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer(this, LAYERS)
    }

}
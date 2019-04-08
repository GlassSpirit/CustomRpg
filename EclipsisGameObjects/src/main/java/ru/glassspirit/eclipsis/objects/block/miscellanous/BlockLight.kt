package ru.glassspirit.eclipsis.objects.block.miscellanous

import com.teamwizardry.librarianlib.features.base.block.BlockMod
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.util.EnumBlockRenderType
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess

class BlockLight(name: String) : BlockMod(name, Material.ROCK) {
    init {
        setBlockUnbreakable()
        setLightLevel(1.0f)
        setLightOpacity(0)
    }

    override fun isOpaqueCube(state: IBlockState?): Boolean {
        return false
    }

    override fun isFullCube(state: IBlockState?): Boolean {
        return false
    }

    override fun isPassable(worldIn: IBlockAccess?, pos: BlockPos?): Boolean {
        return true
    }

    override fun getRenderType(state: IBlockState?): EnumBlockRenderType {
        return EnumBlockRenderType.INVISIBLE
    }

    override fun getCollisionBoundingBox(blockState: IBlockState, worldIn: IBlockAccess, pos: BlockPos): AxisAlignedBB? {
        return Block.NULL_AABB
    }

    override fun isSideSolid(base_state: IBlockState, world: IBlockAccess, pos: BlockPos, side: EnumFacing?): Boolean {
        return true
    }

    override fun isTopSolid(state: IBlockState): Boolean {
        return true
    }

}
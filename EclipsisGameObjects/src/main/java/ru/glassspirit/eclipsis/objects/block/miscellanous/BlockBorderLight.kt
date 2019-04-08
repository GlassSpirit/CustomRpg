package ru.glassspirit.eclipsis.objects.block.miscellanous

import com.teamwizardry.librarianlib.features.base.block.BlockMod
import net.minecraft.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.util.EnumBlockRenderType
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess

class BlockBorderLight(name: String) : BlockMod(name, Material.ROCK) {
    init {
        soundType = SoundType.STONE
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

    override fun getRenderType(state: IBlockState?): EnumBlockRenderType {
        return EnumBlockRenderType.INVISIBLE
    }

    override fun isSideSolid(base_state: IBlockState, world: IBlockAccess, pos: BlockPos, side: EnumFacing?): Boolean {
        return true
    }

    override fun isTopSolid(state: IBlockState): Boolean {
        return true
    }

}

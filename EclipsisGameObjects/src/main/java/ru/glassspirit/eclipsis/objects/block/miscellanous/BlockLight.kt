package ru.glassspirit.eclipsis.objects.block.miscellanous

import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.util.EnumBlockRenderType
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import ru.glassspirit.eclipsis.objects.block.BlockDecor

class BlockLight(name: String) : BlockDecor(name, Material.ROCK, cutout = true, collide = false) {
    init {
        setBlockUnbreakable()
        setLightLevel(1.0f)
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
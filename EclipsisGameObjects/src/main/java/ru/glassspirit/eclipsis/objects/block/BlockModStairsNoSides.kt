package ru.glassspirit.eclipsis.objects.block

import com.teamwizardry.librarianlib.features.base.block.BlockModStairs
import net.minecraft.block.state.IBlockState
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess

class BlockModStairsNoSides(name: String, parent: IBlockState) : BlockModStairs(name, parent) {
    init {
        setLightOpacity(0)
    }

    override fun doesSideBlockRendering(state: IBlockState, world: IBlockAccess, pos: BlockPos, face: EnumFacing): Boolean {
        return state.isOpaqueCube
    }
}
package ru.glassspirit.eclipsis.objects.block.decorations

import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import ru.glassspirit.eclipsis.objects.block.BlockModFullNoCollision

class BlockDecorLight(name: String, material: Material, lightLevel: Float) : BlockModFullNoCollision(name, material) {
    companion object {
        private val FLOWER_POT_AABB = AxisAlignedBB(0.3125, 0.0, 0.3125, 0.6875, 0.375, 0.6875)
    }

    init {
        setLightLevel(lightLevel)
    }

    override fun getBoundingBox(state: IBlockState, source: IBlockAccess, pos: BlockPos): AxisAlignedBB {
        return FLOWER_POT_AABB
    }
}

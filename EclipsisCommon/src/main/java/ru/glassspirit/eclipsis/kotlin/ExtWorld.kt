package ru.glassspirit.eclipsis.kotlin

import net.minecraft.block.state.IBlockState
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3i
import net.minecraft.world.World
import java.util.*

//net.minecraft.world.World
fun World.getBlocksWithinAABB(bb: AxisAlignedBB): Map<Vec3i, IBlockState> {
    val xMin = MathHelper.floor(bb.minX)
    val xMax = MathHelper.ceil(bb.maxX)
    val yMin = MathHelper.floor(bb.minY)
    val yMax = MathHelper.ceil(bb.maxY)
    val zMin = MathHelper.floor(bb.minZ)
    val zMax = MathHelper.ceil(bb.maxZ)
    val map = TreeMap<Vec3i, IBlockState>()

    val blockPos = BlockPos.PooledMutableBlockPos.retain()
    for (i in xMin until xMax) {
        for (j in yMin until yMax) {
            for (k in zMin until zMax) {
                val pos = blockPos.setPos(i, j, k)
                map[Vec3i(pos.x, pos.y, pos.z)] = this.getBlockState(pos)
            }
        }
    }
    blockPos.release()

    return map
}
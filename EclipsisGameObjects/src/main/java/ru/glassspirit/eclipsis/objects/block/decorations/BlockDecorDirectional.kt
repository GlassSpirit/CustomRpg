package ru.glassspirit.eclipsis.objects.block.decorations

import com.teamwizardry.librarianlib.features.base.block.BlockModDirectional
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.util.BlockRenderLayer
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

open class BlockDecorDirectional(name: String, material: Material) : BlockModDirectional(name, material, true) {
    init {
        this.setLightOpacity(0)
    }

    override fun isOpaqueCube(state: IBlockState): Boolean {
        return false
    }

    override fun isFullCube(state: IBlockState): Boolean {
        return false
    }

    @SideOnly(Side.CLIENT)
    override fun getRenderLayer(): BlockRenderLayer {
        return BlockRenderLayer.CUTOUT_MIPPED
    }
}
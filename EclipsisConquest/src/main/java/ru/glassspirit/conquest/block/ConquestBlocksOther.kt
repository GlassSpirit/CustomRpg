package ru.glassspirit.conquest.block

import com.teamwizardry.librarianlib.features.base.block.BlockModFenceGate
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.init.Blocks
import ru.glassspirit.eclipsis.kotlin.setModCreativeTab

object ConquestBlocksOther {
    init {
        BlockModFenceGate("stone_fencegate_1_alpha", Blocks.STONE.defaultState).setModCreativeTab(CreativeTabs.REDSTONE)
        BlockModFenceGate("stone_fencegate_1_bravo", Blocks.STONE.defaultState).setModCreativeTab(CreativeTabs.REDSTONE)

        BlockModFenceGate("wood_fencegate_1_bravo", Blocks.LOG.defaultState).setModCreativeTab(CreativeTabs.REDSTONE)
        BlockModFenceGate("wood_fencegate_11_alpha", Blocks.LOG.defaultState).setModCreativeTab(CreativeTabs.REDSTONE)
        BlockModFenceGate("wood_fencegate_11_bravo", Blocks.LOG.defaultState).setModCreativeTab(CreativeTabs.REDSTONE)
        BlockModFenceGate("wood_fencegate_12_alpha", Blocks.LOG.defaultState).setModCreativeTab(CreativeTabs.REDSTONE)
        BlockModFenceGate("wood_fencegate_12_bravo", Blocks.LOG.defaultState).setModCreativeTab(CreativeTabs.REDSTONE)
        BlockModFenceGate("wood_fencegate_13_alpha", Blocks.LOG.defaultState).setModCreativeTab(CreativeTabs.REDSTONE)
        BlockModFenceGate("wood_fencegate_13_bravo", Blocks.LOG.defaultState).setModCreativeTab(CreativeTabs.REDSTONE)
    }
}
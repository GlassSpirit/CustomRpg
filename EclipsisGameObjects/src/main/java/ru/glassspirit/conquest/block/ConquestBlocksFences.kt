package ru.glassspirit.conquest.block

import com.teamwizardry.librarianlib.features.base.block.BlockModFence
import com.teamwizardry.librarianlib.features.base.block.BlockModFenceGate
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.init.Blocks
import ru.glassspirit.conquest.ConquestTabs
import ru.glassspirit.eclipsis.kotlin.setModCreativeTab

object ConquestBlocksFences {
    init {
        BlockModFence("stone_fence_1_alpha", Blocks.STONE.defaultState).setModCreativeTab(ConquestTabs.ConquestBuildingStoneTab)
        BlockModFence("stone_fence_1_fox", Blocks.STONE.defaultState).setModCreativeTab(ConquestTabs.ConquestBuildingStoneTab)
        BlockModFence("stone_fence_1_golf", Blocks.STONE.defaultState).setModCreativeTab(ConquestTabs.ConquestBuildingStoneTab)
        BlockModFence("stone_fence_1_hotel", Blocks.STONE.defaultState).setModCreativeTab(ConquestTabs.ConquestBuildingStoneTab)
        BlockModFence("stone_fence_2_mike", Blocks.STONE.defaultState).setModCreativeTab(ConquestTabs.ConquestBuildingStoneTab)

        BlockModFenceGate("stone_fencegate_1_alpha", Blocks.STONE.defaultState).setModCreativeTab(CreativeTabs.REDSTONE)
        BlockModFenceGate("stone_fencegate_1_bravo", Blocks.STONE.defaultState).setModCreativeTab(CreativeTabs.REDSTONE)

        BlockModFence("wood_fence_1_alpha", Blocks.LOG.defaultState).setModCreativeTab(ConquestTabs.ConquestBuildingWoodTab)
        BlockModFence("wood_fence_1_hotel", Blocks.LOG.defaultState).setModCreativeTab(ConquestTabs.ConquestBuildingWoodTab)
        BlockModFence("wood_fence_1_india", Blocks.LOG.defaultState).setModCreativeTab(ConquestTabs.ConquestBuildingWoodTab)
        BlockModFence("wood_fence_1_juliet", Blocks.LOG.defaultState).setModCreativeTab(ConquestTabs.ConquestBuildingWoodTab)
        BlockModFence("wood_fence_2_alpha", Blocks.LOG.defaultState).setModCreativeTab(ConquestTabs.ConquestBuildingWoodTab)
        BlockModFence("wood_fence_2_bravo", Blocks.LOG.defaultState).setModCreativeTab(ConquestTabs.ConquestBuildingWoodTab)
        BlockModFence("wood_fence_2_charlie", Blocks.LOG.defaultState).setModCreativeTab(ConquestTabs.ConquestBuildingWoodTab)
        BlockModFence("wood_fence_2_delta", Blocks.LOG.defaultState).setModCreativeTab(ConquestTabs.ConquestBuildingWoodTab)
        BlockModFence("wood_fence_2_echo", Blocks.LOG.defaultState).setModCreativeTab(ConquestTabs.ConquestBuildingWoodTab)
        BlockModFence("wood_fence_2_golf", Blocks.LOG.defaultState).setModCreativeTab(ConquestTabs.ConquestBuildingWoodTab)
        BlockModFence("wood_fence_2_hotel", Blocks.LOG.defaultState).setModCreativeTab(ConquestTabs.ConquestBuildingWoodTab)
        BlockModFence("wood_fence_2_india", Blocks.LOG.defaultState).setModCreativeTab(ConquestTabs.ConquestBuildingWoodTab)
        BlockModFence("wood_fence_2_juliet", Blocks.LOG.defaultState).setModCreativeTab(ConquestTabs.ConquestBuildingWoodTab)
        BlockModFence("wood_fence_2_kilo", Blocks.LOG.defaultState).setModCreativeTab(ConquestTabs.ConquestBuildingWoodTab)
        BlockModFence("wood_fence_2_lima", Blocks.LOG.defaultState).setModCreativeTab(ConquestTabs.ConquestBuildingWoodTab)
        BlockModFence("wood_fence_2_mike", Blocks.LOG.defaultState).setModCreativeTab(ConquestTabs.ConquestBuildingWoodTab)
        BlockModFence("wood_fence_3_charlie", Blocks.LOG.defaultState).setModCreativeTab(ConquestTabs.ConquestBuildingWoodTab)
        BlockModFence("wood_fence_3_delta", Blocks.LOG.defaultState).setModCreativeTab(ConquestTabs.ConquestBuildingWoodTab)
        BlockModFence("wood_fence_3_echo", Blocks.LOG.defaultState).setModCreativeTab(ConquestTabs.ConquestBuildingWoodTab)
        BlockModFence("wood_fence_3_fox", Blocks.LOG.defaultState).setModCreativeTab(ConquestTabs.ConquestBuildingWoodTab)
        BlockModFence("wood_fence_3_golf", Blocks.LOG.defaultState).setModCreativeTab(ConquestTabs.ConquestBuildingWoodTab)
        BlockModFence("wood_fence_3_hotel", Blocks.LOG.defaultState).setModCreativeTab(ConquestTabs.ConquestBuildingWoodTab)
        BlockModFence("wood_fence_3_india", Blocks.LOG.defaultState).setModCreativeTab(ConquestTabs.ConquestBuildingWoodTab)
        BlockModFence("wood_fence_3_juliet", Blocks.LOG.defaultState).setModCreativeTab(ConquestTabs.ConquestBuildingWoodTab)
        BlockModFence("wood_fence_3_november", Blocks.LOG.defaultState).setModCreativeTab(ConquestTabs.ConquestBuildingWoodTab)
        BlockModFence("wood_fence_3_oscar", Blocks.LOG.defaultState).setModCreativeTab(ConquestTabs.ConquestBuildingWoodTab)
        BlockModFence("wood_fence_3_papa", Blocks.LOG.defaultState).setModCreativeTab(ConquestTabs.ConquestBuildingWoodTab)
        BlockModFence("wood_fence_4_alpha", Blocks.LOG.defaultState).setModCreativeTab(ConquestTabs.ConquestBuildingWoodTab)
        BlockModFence("wood_fence_4_charlie", Blocks.LOG.defaultState).setModCreativeTab(ConquestTabs.ConquestBuildingWoodTab)
        BlockModFence("wood_fence_4_delta", Blocks.LOG.defaultState).setModCreativeTab(ConquestTabs.ConquestBuildingWoodTab)
        BlockModFence("wood_fence_4_echo", Blocks.LOG.defaultState).setModCreativeTab(ConquestTabs.ConquestBuildingWoodTab)
        BlockModFence("wood_fence_4_fox", Blocks.LOG.defaultState).setModCreativeTab(ConquestTabs.ConquestBuildingWoodTab)
        BlockModFence("wood_fence_4_golf", Blocks.LOG.defaultState).setModCreativeTab(ConquestTabs.ConquestBuildingWoodTab)
        BlockModFence("wood_fence_4_hotel", Blocks.LOG.defaultState).setModCreativeTab(ConquestTabs.ConquestBuildingWoodTab)
        BlockModFence("wood_fence_4_india", Blocks.LOG.defaultState).setModCreativeTab(ConquestTabs.ConquestBuildingWoodTab)
        BlockModFence("wood_fence_4_juliet", Blocks.LOG.defaultState).setModCreativeTab(ConquestTabs.ConquestBuildingWoodTab)
        BlockModFence("wood_fence_4_kilo", Blocks.LOG.defaultState).setModCreativeTab(ConquestTabs.ConquestBuildingWoodTab)
        BlockModFence("wood_fence_4_lima", Blocks.LOG.defaultState).setModCreativeTab(ConquestTabs.ConquestBuildingWoodTab)
        BlockModFence("wood_fence_4_mike", Blocks.LOG.defaultState).setModCreativeTab(ConquestTabs.ConquestBuildingWoodTab)
        BlockModFence("wood_fence_4_november", Blocks.LOG.defaultState).setModCreativeTab(ConquestTabs.ConquestBuildingWoodTab)
        BlockModFence("wood_fence_4_oscar", Blocks.LOG.defaultState).setModCreativeTab(ConquestTabs.ConquestBuildingWoodTab)
        BlockModFence("wood_fence_4_papa", Blocks.LOG.defaultState).setModCreativeTab(ConquestTabs.ConquestBuildingWoodTab)
        BlockModFence("wood_fence_5_alpha", Blocks.LOG.defaultState).setModCreativeTab(ConquestTabs.ConquestBuildingWoodTab)
        BlockModFence("wood_fence_5_bravo", Blocks.LOG.defaultState).setModCreativeTab(ConquestTabs.ConquestBuildingWoodTab)
        BlockModFence("wood_fence_5_charlie", Blocks.LOG.defaultState).setModCreativeTab(ConquestTabs.ConquestBuildingWoodTab)
        BlockModFence("wood_fence_5_delta", Blocks.LOG.defaultState).setModCreativeTab(ConquestTabs.ConquestBuildingWoodTab)
        BlockModFence("wood_fence_5_echo", Blocks.LOG.defaultState).setModCreativeTab(ConquestTabs.ConquestBuildingWoodTab)
        BlockModFence("wood_fence_5_fox", Blocks.LOG.defaultState).setModCreativeTab(ConquestTabs.ConquestBuildingWoodTab)

        BlockModFenceGate("wood_fencegate_1_bravo", Blocks.LOG.defaultState).setModCreativeTab(CreativeTabs.REDSTONE)
        BlockModFenceGate("wood_fencegate_11_alpha", Blocks.LOG.defaultState).setModCreativeTab(CreativeTabs.REDSTONE)
        BlockModFenceGate("wood_fencegate_11_bravo", Blocks.LOG.defaultState).setModCreativeTab(CreativeTabs.REDSTONE)
        BlockModFenceGate("wood_fencegate_12_alpha", Blocks.LOG.defaultState).setModCreativeTab(CreativeTabs.REDSTONE)
        BlockModFenceGate("wood_fencegate_12_bravo", Blocks.LOG.defaultState).setModCreativeTab(CreativeTabs.REDSTONE)
        BlockModFenceGate("wood_fencegate_13_alpha", Blocks.LOG.defaultState).setModCreativeTab(CreativeTabs.REDSTONE)
        BlockModFenceGate("wood_fencegate_13_bravo", Blocks.LOG.defaultState).setModCreativeTab(CreativeTabs.REDSTONE)
    }
}
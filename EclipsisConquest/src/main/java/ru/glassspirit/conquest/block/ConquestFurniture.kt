package ru.glassspirit.conquest.block

import com.teamwizardry.librarianlib.features.base.block.BlockMod
import com.teamwizardry.librarianlib.features.base.block.BlockModPlanks
import net.minecraft.block.material.Material
import net.minecraft.init.Blocks
import ru.glassspirit.conquest.ConquestTabs
import ru.glassspirit.eclipsis.kotlin.setModCreativeTab
import ru.glassspirit.eclipsis.objects.block.BlockDecor
import ru.glassspirit.eclipsis.objects.block.BlockDecorDirectional
import ru.glassspirit.eclipsis.objects.block.BlockModHalf
import ru.glassspirit.eclipsis.objects.block.BlockModStairsNoSides
import ru.glassspirit.eclipsis.objects.block.decorations.BlockDecorDirectionalFullNoCollision
import ru.glassspirit.eclipsis.objects.block.decorations.BlockDecorPlate

object ConquestFurniture {
    init {
        BlockMod("wood_full_4_india", Material.WOOD).setModCreativeTab(ConquestTabs.ConquestFurnitureTab)
        BlockMod("wood_full_4_juliet", Material.WOOD).setModCreativeTab(ConquestTabs.ConquestFurnitureTab)
        BlockMod("wood_full_4_kilo", Material.WOOD).setModCreativeTab(ConquestTabs.ConquestFurnitureTab)
        BlockMod("wood_full_4_lima", Material.WOOD).setModCreativeTab(ConquestTabs.ConquestFurnitureTab)
        BlockMod("wood_full_4_mike", Material.WOOD).setModCreativeTab(ConquestTabs.ConquestFurnitureTab)
        BlockMod("wood_full_4_november", Material.WOOD).setModCreativeTab(ConquestTabs.ConquestFurnitureTab)
        BlockMod("wood_full_4_oscar", Material.WOOD).setModCreativeTab(ConquestTabs.ConquestFurnitureTab)
        BlockMod("wood_full_4_papa", Material.WOOD).setModCreativeTab(ConquestTabs.ConquestFurnitureTab)
        BlockMod("wood_full_5_alpha", Material.WOOD).setModCreativeTab(ConquestTabs.ConquestFurnitureTab)
        BlockMod("wood_full_5_bravo", Material.WOOD).setModCreativeTab(ConquestTabs.ConquestFurnitureTab)
        BlockMod("wood_full_5_charlie", Material.WOOD).setModCreativeTab(ConquestTabs.ConquestFurnitureTab)
        BlockMod("wood_full_5_delta", Material.WOOD).setModCreativeTab(ConquestTabs.ConquestFurnitureTab)
        BlockMod("wood_full_5_echo", Material.WOOD).setModCreativeTab(ConquestTabs.ConquestFurnitureTab)
        BlockMod("wood_full_5_fox", Material.WOOD).setModCreativeTab(ConquestTabs.ConquestFurnitureTab)
        BlockMod("wood_full_5_golf", Material.WOOD).setModCreativeTab(ConquestTabs.ConquestFurnitureTab)
        BlockMod("wood_full_5_hotel", Material.WOOD).setModCreativeTab(ConquestTabs.ConquestFurnitureTab)
        BlockMod("wood_full_5_india", Material.WOOD).setModCreativeTab(ConquestTabs.ConquestFurnitureTab)
        BlockMod("wood_full_5_juliet", Material.WOOD).setModCreativeTab(ConquestTabs.ConquestFurnitureTab)
        BlockMod("wood_full_5_lima", Material.WOOD).setModCreativeTab(ConquestTabs.ConquestFurnitureTab)
        BlockMod("wood_full_5_mike", Material.WOOD).setModCreativeTab(ConquestTabs.ConquestFurnitureTab)
        BlockMod("wood_full_5_november", Material.WOOD).setModCreativeTab(ConquestTabs.ConquestFurnitureTab)
        BlockMod("wood_full_5_oscar", Material.WOOD).setModCreativeTab(ConquestTabs.ConquestFurnitureTab)
        BlockMod("wood_full_5_papa", Material.WOOD).setModCreativeTab(ConquestTabs.ConquestFurnitureTab)
        BlockMod("wood_full_6_alpha", Material.WOOD).setModCreativeTab(ConquestTabs.ConquestFurnitureTab)
        BlockMod("wood_full_6_bravo", Material.WOOD).setModCreativeTab(ConquestTabs.ConquestFurnitureTab)
        BlockMod("wood_full_6_charlie", Material.WOOD).setModCreativeTab(ConquestTabs.ConquestFurnitureTab)
        BlockMod("wood_full_6_delta", Material.WOOD).setModCreativeTab(ConquestTabs.ConquestFurnitureTab)
        BlockMod("wood_full_6_echo", Material.WOOD).setModCreativeTab(ConquestTabs.ConquestFurnitureTab)
        BlockMod("wood_full_6_fox", Material.WOOD).setModCreativeTab(ConquestTabs.ConquestFurnitureTab)
        BlockMod("wood_full_6_golf", Material.WOOD).setModCreativeTab(ConquestTabs.ConquestFurnitureTab)
        BlockMod("wood_full_6_hotel", Material.WOOD).setModCreativeTab(ConquestTabs.ConquestFurnitureTab)

        BlockModPlanks("wood_full_6_november").setModCreativeTab(ConquestTabs.ConquestFurnitureTab)
        BlockModPlanks("wood_full_6_oscar").setModCreativeTab(ConquestTabs.ConquestFurnitureTab)

        BlockMod("wood_full_7_fox", Material.WOOD).setModCreativeTab(ConquestTabs.ConquestFurnitureTab)
        BlockMod("wood_full_7_golf", Material.WOOD).setModCreativeTab(ConquestTabs.ConquestFurnitureTab)
        BlockMod("wood_full_7_hotel", Material.WOOD).setModCreativeTab(ConquestTabs.ConquestFurnitureTab)
        BlockMod("wood_full_7_india", Material.WOOD).setModCreativeTab(ConquestTabs.ConquestFurnitureTab)
        BlockDecor("wood_fullpartial_2_alpha", Material.WOOD, cutout = true).setModCreativeTab(ConquestTabs.ConquestFurnitureTab)
        BlockDecor("wood_fullpartial_2_bravo", Material.WOOD, cutout = true).setModCreativeTab(ConquestTabs.ConquestFurnitureTab)

        BlockModHalf("wood_half_1_alpha", Material.WOOD).setModCreativeTab(ConquestTabs.ConquestFurnitureTab)
        BlockModHalf("wood_half_1_bravo", Material.WOOD).setModCreativeTab(ConquestTabs.ConquestFurnitureTab)
        BlockModHalf("wood_half_1_charlie", Material.WOOD).setModCreativeTab(ConquestTabs.ConquestFurnitureTab)
        BlockModHalf("wood_half_1_delta", Material.WOOD).setModCreativeTab(ConquestTabs.ConquestFurnitureTab)
        BlockModHalf("wood_half_1_echo", Material.WOOD).setModCreativeTab(ConquestTabs.ConquestFurnitureTab)
        BlockModHalf("wood_half_1_fox", Material.WOOD).setModCreativeTab(ConquestTabs.ConquestFurnitureTab)
        BlockModHalf("wood_half_1_golf", Material.WOOD).setModCreativeTab(ConquestTabs.ConquestFurnitureTab)
        BlockModHalf("wood_half_1_hotel", Material.WOOD).setModCreativeTab(ConquestTabs.ConquestFurnitureTab)
        BlockModHalf("wood_half_1_india", Material.WOOD).setModCreativeTab(ConquestTabs.ConquestFurnitureTab)
        BlockModHalf("wood_half_1_juliet", Material.WOOD).setModCreativeTab(ConquestTabs.ConquestFurnitureTab)

        BlockDecorPlate("wood_daylightdetector_1_alpha", Material.WOOD).setModCreativeTab(ConquestTabs.ConquestFurnitureTab)
        BlockDecorPlate("wood_daylightdetector_1_bravo", Material.WOOD).setModCreativeTab(ConquestTabs.ConquestFurnitureTab)
        BlockDecorPlate("wood_daylightdetector_1_charlie", Material.WOOD).setModCreativeTab(ConquestTabs.ConquestFurnitureTab)
        BlockDecorPlate("wood_daylightdetector_1_delta", Material.WOOD).setModCreativeTab(ConquestTabs.ConquestFurnitureTab)
        BlockDecorPlate("wood_daylightdetector_1_echo", Material.WOOD).setModCreativeTab(ConquestTabs.ConquestFurnitureTab)
        BlockDecorPlate("wood_daylightdetector_1_fox", Material.WOOD).setModCreativeTab(ConquestTabs.ConquestFurnitureTab)
        BlockDecorPlate("wood_daylightdetector_1_golf", Material.WOOD).setModCreativeTab(ConquestTabs.ConquestFurnitureTab)
        BlockDecorPlate("wood_daylightdetector_1_hotel", Material.WOOD).setModCreativeTab(ConquestTabs.ConquestFurnitureTab)
        BlockDecorPlate("wood_daylightdetector_1_india", Material.WOOD).setModCreativeTab(ConquestTabs.ConquestFurnitureTab)

        BlockDecorDirectional("wood_hopperdirectional_10_alpha", Material.WOOD).setModCreativeTab(ConquestTabs.ConquestFurnitureTab)
        BlockDecorDirectional("wood_hopperdirectional_8_alpha", Material.WOOD).setModCreativeTab(ConquestTabs.ConquestFurnitureTab)
        BlockDecorDirectionalFullNoCollision("wood_hopperdirectional_8_bravo", Material.WOOD).setModCreativeTab(ConquestTabs.ConquestFurnitureTab)
        BlockDecorDirectionalFullNoCollision("wood_hopperdirectional_8_charlie", Material.WOOD).setModCreativeTab(ConquestTabs.ConquestFurnitureTab)

        BlockModStairsNoSides("iron_stairs_2_alpha", Blocks.IRON_BLOCK.defaultState).setModCreativeTab(ConquestTabs.ConquestFurnitureTab)

        BlockModStairsNoSides("wood_stairs_4_bravo", Blocks.LOG.defaultState).setModCreativeTab(ConquestTabs.ConquestFurnitureTab)
        BlockModStairsNoSides("wood_stairs_5_alpha", Blocks.LOG.defaultState).setModCreativeTab(ConquestTabs.ConquestFurnitureTab)
        BlockModStairsNoSides("wood_stairs_5_bravo", Blocks.LOG.defaultState).setModCreativeTab(ConquestTabs.ConquestFurnitureTab)
        BlockModStairsNoSides("wood_stairs_6_alpha", Blocks.LOG.defaultState).setModCreativeTab(ConquestTabs.ConquestFurnitureTab)

        BlockModStairsNoSides("wood_stairs_7_alpha", Blocks.LOG.defaultState).setModCreativeTab(ConquestTabs.ConquestFurnitureTab)

        BlockModStairsNoSides("wood_stairs_41_alpha", Blocks.LOG.defaultState).setModCreativeTab(ConquestTabs.ConquestFurnitureTab)
        BlockModStairsNoSides("wood_stairs_41_bravo", Blocks.LOG.defaultState).setModCreativeTab(ConquestTabs.ConquestFurnitureTab)
    }
}
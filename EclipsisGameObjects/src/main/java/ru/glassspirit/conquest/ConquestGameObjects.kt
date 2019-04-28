package ru.glassspirit.conquest

import com.teamwizardry.librarianlib.features.base.ModCreativeTab
import net.minecraft.init.Blocks.*
import net.minecraft.init.Items
import net.minecraft.item.ItemStack
import ru.glassspirit.conquest.block.*
import ru.glassspirit.eclipsis.kotlin.set

object ConquestBlocks {
    init {
        ConquestBlocksFull
        ConquestBlocksPanes
        ConquestBlocksSlabs
        ConquestBlocksStairs
        ConquestBlocksWalls
        ConquestBlocksFences
        ConquestBlocksLayers
        ConquestBlocksCarpets
        ConquestBlocksNoCollision
        ConquestBlocksOther
    }
}

object ConquestItems {

}

object ConquestTabs {
    init {
        ConquestTopographyTab
        ConquestPlantsTab
        ConquestBuildingStoneTab
        ConquestBuildingWoodTab
        ConquestBuildingOtherTab
        ConquestFurnitureTab
        ConquestTab
    }

    object ConquestTab : ModCreativeTab() {
        override val iconStack: ItemStack
            get() = ItemStack.EMPTY

        init {
            MONSTER_EGG.creativeTab = SEARCH
            Items.END_CRYSTAL.creativeTab = SEARCH
        }
    }

    object ConquestTopographyTab : ModCreativeTab("topography") {
        override val iconStack: ItemStack
            get() = ItemStack(GRASS)
    }

    object ConquestPlantsTab : ModCreativeTab("plants") {
        override val iconStack: ItemStack
            get() = ItemStack(DEADBUSH)

        init {
            set(LEAVES)
            set(LEAVES2)
            set(SAPLING)
            set(TALLGRASS)
            set(DEADBUSH)
            set(CACTUS)
            set(YELLOW_FLOWER)
            set(RED_FLOWER)
            set(DOUBLE_PLANT)
            set(BROWN_MUSHROOM)
            set(RED_MUSHROOM)
            set(VINE)
            set(WATERLILY)
        }
    }

    object ConquestBuildingStoneTab : ModCreativeTab("buildingstone") {
        override val iconStack: ItemStack
            get() = ItemStack(STONEBRICK)

        init {
            set(COBBLESTONE_WALL)
            set(NETHER_BRICK_FENCE)
        }
    }

    object ConquestBuildingWoodTab : ModCreativeTab("buildingwood") {
        override val iconStack: ItemStack
            get() = ItemStack(PLANKS)

        init {
            set(ACACIA_FENCE)
            set(BIRCH_FENCE)
            set(DARK_OAK_FENCE)
            set(JUNGLE_FENCE)
            set(OAK_FENCE)
            set(SPRUCE_FENCE)
        }
    }

    object ConquestBuildingOtherTab : ModCreativeTab("buildingother") {
        override val iconStack: ItemStack
            get() = ItemStack(GOLD_BLOCK)

        init {
            set(GLASS_PANE)
            set(STAINED_GLASS_PANE)
            set(IRON_BARS)
            set(SNOW_LAYER)
            set(SLIME_BLOCK)
            set(BLACK_GLAZED_TERRACOTTA)
            set(BLUE_GLAZED_TERRACOTTA)
            set(BROWN_GLAZED_TERRACOTTA)
            set(CYAN_GLAZED_TERRACOTTA)
            set(GRAY_GLAZED_TERRACOTTA)
            set(GREEN_GLAZED_TERRACOTTA)
            set(LIGHT_BLUE_GLAZED_TERRACOTTA)
            set(LIME_GLAZED_TERRACOTTA)
            set(MAGENTA_GLAZED_TERRACOTTA)
            set(ORANGE_GLAZED_TERRACOTTA)
            set(PINK_GLAZED_TERRACOTTA)
            set(PURPLE_GLAZED_TERRACOTTA)
            set(RED_GLAZED_TERRACOTTA)
            set(SILVER_GLAZED_TERRACOTTA)
            set(WHITE_GLAZED_TERRACOTTA)
            set(YELLOW_GLAZED_TERRACOTTA)
        }
    }

    object ConquestFurnitureTab : ModCreativeTab("furniture") {
        override val iconStack: ItemStack
            get() = ItemStack(CRAFTING_TABLE)

        init {
            set(Items.SIGN)
            set(CHEST)
            set(JUKEBOX)
            set(LADDER)
            set(CRAFTING_TABLE)
            set(FURNACE)
            set(ENCHANTING_TABLE)
            set(END_PORTAL_FRAME)
            set(ANVIL)
            set(Items.FLOWER_POT)
            set(FLOWER_POT)
            set(ENDER_CHEST)
            set(Items.BED)
            set(BED)
            set(Items.PAINTING)
            set(Items.ITEM_FRAME)
            set(Items.ARMOR_STAND)
            set(Items.BANNER)
            set(END_ROD)
            set(BLACK_SHULKER_BOX)
            set(BLUE_SHULKER_BOX)
            set(BROWN_SHULKER_BOX)
            set(CYAN_SHULKER_BOX)
            set(GRAY_SHULKER_BOX)
            set(GREEN_SHULKER_BOX)
            set(LIGHT_BLUE_SHULKER_BOX)
            set(LIME_SHULKER_BOX)
            set(MAGENTA_SHULKER_BOX)
            set(ORANGE_SHULKER_BOX)
            set(PINK_SHULKER_BOX)
            set(PURPLE_SHULKER_BOX)
            set(RED_SHULKER_BOX)
            set(SILVER_SHULKER_BOX)
            set(WHITE_SHULKER_BOX)
            set(YELLOW_SHULKER_BOX)
        }
    }
}
package ru.glassspirit.eclipsis.kotlin

import com.teamwizardry.librarianlib.features.base.ModCreativeTab
import com.teamwizardry.librarianlib.features.base.block.IModBlock
import com.teamwizardry.librarianlib.features.base.block.IModBlockProvider
import com.teamwizardry.librarianlib.features.base.item.IModItemProvider
import com.teamwizardry.librarianlib.features.base.item.ItemMod
import net.minecraft.block.Block
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.init.Items
import net.minecraft.item.Item
import net.minecraft.item.ItemBlock

fun ModCreativeTab.set(block: Block) {
    this.set(object : IModBlockProvider {
        override val variants: Array<out String>
            get() = TODO("not implemented")
        override val providedBlock: Block
            get() = block
        override val itemForm: ItemBlock?
            get() {
                val item = Item.getItemFromBlock(block)
                return if (item == Items.AIR) null else item as ItemBlock
            }
    })
}

fun ModCreativeTab.set(item: Item) {
    this.set(object : IModItemProvider {
        override val variants: Array<out String>
            get() = TODO("not implemented")
        override val providedItem: Item
            get() = item
    })
}

fun IModBlock.setModCreativeTab(tab: ModCreativeTab): IModBlock {
    ModCreativeTab.blocksToTab[this] = fun(): ModCreativeTab { return tab }
    return this
}

fun IModBlock.setModCreativeTab(tab: CreativeTabs): IModBlock {
    try {
        this as Block
        Item.getItemFromBlock(this).creativeTab = tab
        this.setCreativeTab(tab)
        ModCreativeTab.blocksToTab.remove(this)
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return this
}

fun ItemMod.setModCreativeTab(tab: ModCreativeTab): ItemMod {
    ModCreativeTab.itemsToTab[this] = fun(): ModCreativeTab { return tab }
    return this
}

fun ItemMod.setModCreativeTab(tab: CreativeTabs): ItemMod {
    this.setCreativeTab(tab)
    ModCreativeTab.itemsToTab.remove(this)
    return this
}

package ru.glassspirit.eclipsis.objects.block

import com.teamwizardry.librarianlib.core.common.OreDictionaryRegistrar
import com.teamwizardry.librarianlib.features.base.ModCreativeTab
import com.teamwizardry.librarianlib.features.base.block.IModBlock
import com.teamwizardry.librarianlib.features.base.block.ItemModBlock
import com.teamwizardry.librarianlib.features.helpers.VariantHelper
import com.teamwizardry.librarianlib.features.helpers.currentModId
import net.minecraft.block.Block
import net.minecraft.block.BlockLeaves
import net.minecraft.block.BlockPlanks
import net.minecraft.block.properties.IProperty
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.RayTraceResult
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World

@Suppress("LeakingThis")
open class BlockModStandartLeaves(name: String, vararg variants: String) : BlockLeaves(), IModBlock {
    override val bareName: String = VariantHelper.toSnakeCase(name)
    override val variants: Array<out String> = VariantHelper.beginSetupBlock(bareName, variants)
    val modId = currentModId

    override val itemForm: ItemBlock? by lazy { createItemForm() }

    init {
        VariantHelper.finishSetupBlock(this, bareName, itemForm, this::creativeTab)
        val form = itemForm
        if (form != null)
            for (variant in this.variants.indices)
                OreDictionaryRegistrar.registerOre("treeLeaves") { ItemStack(form, 1, variant) }
    }

    override fun setTranslationKey(name: String): Block {
        super.setTranslationKey(name)
        VariantHelper.setTranslationKeyForBlock(this, modId, name, itemForm)
        return this
    }

    /**
     * Override this to have a custom ItemBlock implementation.
     */
    open fun createItemForm(): ItemBlock? {
        return ItemModBlock(this)
    }

    /**
     * Override this to have a custom creative tab. Leave blank to have a default tab (or none if no default tab is set).
     */
    override val creativeTab: ModCreativeTab?
        get() = ModCreativeTab.defaultTabs[modId]

    companion object {
        const val DECAY_BIT = 8
        const val CHECK_BIT = 4

        val thisLeavesFancy get() = !Blocks.LEAVES.isOpaqueCube(Blocks.LEAVES.defaultState)
    }

    override val ignoredProperties: Array<IProperty<*>>?
        get() = arrayOf(CHECK_DECAY, DECAYABLE)

    override fun getStateFromMeta(meta: Int): IBlockState {
        val check = (meta and CHECK_BIT) == 0
        val decayable = (meta and DECAY_BIT) == 0
        return defaultState.withProperty(CHECK_DECAY, check).withProperty(DECAYABLE, decayable)
    }

    override fun getMetaFromState(state: IBlockState): Int {
        var meta = 0
        if (state.getValue(CHECK_DECAY))
            meta = meta or CHECK_BIT

        if (state.getValue(DECAYABLE))
            meta = meta or DECAY_BIT

        return meta
    }

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer(this, DECAYABLE, CHECK_DECAY)
    }

    override fun onBlockPlacedBy(worldIn: World, pos: BlockPos, state: IBlockState, placer: EntityLivingBase, stack: ItemStack) {
        worldIn.setBlockState(pos, state.withProperty(DECAYABLE, false))
    }

    override fun onSheared(item: ItemStack, world: IBlockAccess, pos: BlockPos, fortune: Int): MutableList<ItemStack> {
        return mutableListOf(ItemStack(this, 1, getMetaFromState(world.getBlockState(pos).withProperty(DECAYABLE, false).withProperty(CHECK_DECAY, false))))
    }

    override fun getWoodType(meta: Int): BlockPlanks.EnumType {
        return BlockPlanks.EnumType.OAK
    }

    override fun getPickBlock(state: IBlockState, target: RayTraceResult, world: World, pos: BlockPos, player: EntityPlayer?): ItemStack {
        return ItemStack(this, 1, getMetaFromState(state.withProperty(DECAYABLE, false).withProperty(CHECK_DECAY, false)))
    }

    override fun isOpaqueCube(state: IBlockState): Boolean {
        this.leavesFancy = thisLeavesFancy
        return super.isOpaqueCube(state)
    }

}
package ru.glassspirit.eclipsis.objects.item

import com.teamwizardry.librarianlib.features.base.block.ItemModBlock
import net.minecraft.advancements.CriteriaTriggers
import net.minecraft.block.Block
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.SoundCategory
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import ru.glassspirit.eclipsis.objects.block.BlockModLayer

class ItemModBlockLayer(block: BlockModLayer) : ItemModBlock(block) {

    /**
     * Called when a Block is right-clicked with this Item
     */
    override fun onItemUse(player: EntityPlayer, worldIn: World, pos: BlockPos, hand: EnumHand, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): EnumActionResult {
        val itemstack = player.getHeldItem(hand)

        if (!itemstack.isEmpty && player.canPlayerEdit(pos, facing, itemstack)) {
            var iblockstate = worldIn.getBlockState(pos)
            var block = iblockstate.block
            var blockpos = pos

            if ((facing != EnumFacing.UP || block !== this.block) && !block.isReplaceable(worldIn, pos)) {
                blockpos = pos.offset(facing)
                iblockstate = worldIn.getBlockState(blockpos)
                block = iblockstate.block
            }

            if (block === this.block) {
                val i = iblockstate.getValue(BlockModLayer.LAYERS)

                if (i < 8) {
                    val iblockstate1 = iblockstate.withProperty(BlockModLayer.LAYERS, Integer.valueOf(i + 1)!!)
                    val axisalignedbb = iblockstate1.getCollisionBoundingBox(worldIn, blockpos)

                    if (axisalignedbb !== Block.NULL_AABB && worldIn.checkNoEntityCollision(axisalignedbb!!.offset(blockpos)) && worldIn.setBlockState(blockpos, iblockstate1, 10)) {
                        val soundtype = this.block.getSoundType(iblockstate1, worldIn, pos, player)
                        worldIn.playSound(player, blockpos, soundtype.placeSound, SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0f) / 2.0f, soundtype.getPitch() * 0.8f)

                        if (player is EntityPlayerMP) {
                            CriteriaTriggers.PLACED_BLOCK.trigger(player, pos, itemstack)
                        }

                        itemstack.shrink(1)
                        return EnumActionResult.SUCCESS
                    }
                }
            }

            return super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ)
        } else {
            return EnumActionResult.FAIL
        }
    }

    override fun canPlaceBlockOnSide(world: World, pos: BlockPos, side: EnumFacing, player: EntityPlayer, stack: ItemStack): Boolean {
        val block = this.block as BlockModLayer
        val state = world.getBlockState(pos)
        return if (state.block !== block || state.getValue(BlockModLayer.LAYERS) > 7) super.canPlaceBlockOnSide(world, pos, side, player, stack)
        else true
    }

}

package noppes.npcs.containers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketSetSlot;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import noppes.npcs.CustomItems;
import noppes.npcs.controllers.RecipeController;
import noppes.npcs.controllers.data.RecipeCarpentry;

public class ContainerCarpentryBench extends Container {
    public InventoryCrafting craftMatrix = new InventoryCrafting(this, 4, 4);
    public IInventory craftResult = new InventoryCraftResult();
    private EntityPlayer player;
    private World worldObj;
    private BlockPos pos;

    public ContainerCarpentryBench(InventoryPlayer par1InventoryPlayer, World par2World, BlockPos pos) {
        this.worldObj = par2World;
        this.pos = pos;
        this.player = par1InventoryPlayer.player;
        this.addSlotToContainer(new SlotNpcCrafting(par1InventoryPlayer.player, this.craftMatrix, this.craftResult, 0, 133, 41));
        int var6;
        int var7;

        for (var6 = 0; var6 < 4; ++var6) {
            for (var7 = 0; var7 < 4; ++var7) {
                this.addSlotToContainer(new Slot(this.craftMatrix, var7 + var6 * 4, 17 + var7 * 18, 14 + var6 * 18));
            }
        }

        for (var6 = 0; var6 < 3; ++var6) {
            for (var7 = 0; var7 < 9; ++var7) {
                this.addSlotToContainer(new Slot(par1InventoryPlayer, var7 + var6 * 9 + 9, 8 + var7 * 18, 98 + var6 * 18));
            }
        }

        for (var6 = 0; var6 < 9; ++var6) {
            this.addSlotToContainer(new Slot(par1InventoryPlayer, var6, 8 + var6 * 18, 156));
        }

        this.onCraftMatrixChanged(this.craftMatrix);
    }

    /**
     * Callback for when the crafting matrix is changed.
     */
    @Override
    public void onCraftMatrixChanged(IInventory par1IInventory) {
        if (!this.worldObj.isRemote) {
            RecipeCarpentry recipe = RecipeController.instance.findMatchingRecipe(this.craftMatrix);

            ItemStack item = ItemStack.EMPTY;
            if (recipe != null && recipe.availability.isAvailable(player)) {
                item = recipe.getCraftingResult(this.craftMatrix);
            }

            this.craftResult.setInventorySlotContents(0, item);
            EntityPlayerMP plmp = (EntityPlayerMP) player;
            plmp.connection.sendPacket(new SPacketSetSlot(this.windowId, 0, item));
        }
    }

    @Override
    public void onContainerClosed(EntityPlayer par1EntityPlayer) {
        super.onContainerClosed(par1EntityPlayer);

        if (!this.worldObj.isRemote) {
            for (int var2 = 0; var2 < 16; ++var2) {
                ItemStack var3 = this.craftMatrix.removeStackFromSlot(var2);

                if (var3 != null) {
                    par1EntityPlayer.dropItem(var3, false);
                }
            }
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer par1EntityPlayer) {
        return this.worldObj.getBlockState(pos).getBlock() == CustomItems.carpentyBench && par1EntityPlayer.getDistanceSq(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= 64.0D;
    }

    /**
     * Called to transfer a stack from one inventory to the other eg. when shift clicking.
     */
    @Override
    public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par1) {
        ItemStack var2 = ItemStack.EMPTY;
        Slot var3 = this.inventorySlots.get(par1);

        if (var3 != null && var3.getHasStack()) {
            ItemStack var4 = var3.getStack();
            var2 = var4.copy();

            if (par1 == 0) {
                if (!this.mergeItemStack(var4, 17, 53, true)) {
                    return ItemStack.EMPTY;
                }

                var3.onSlotChange(var4, var2);
            } else if (par1 >= 17 && par1 < 44) {
                if (!this.mergeItemStack(var4, 44, 53, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (par1 >= 44 && par1 < 53) {
                if (!this.mergeItemStack(var4, 17, 44, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.mergeItemStack(var4, 17, 53, false)) {
                return ItemStack.EMPTY;
            }

            if (var4.getCount() == 0) {
                var3.putStack(ItemStack.EMPTY);
            } else {
                var3.onSlotChanged();
            }

            if (var4.getCount() == var2.getCount()) {
                return ItemStack.EMPTY;
            }

            var3.onTake(par1EntityPlayer, var4);
        }

        return var2;
    }

    @Override
    public boolean canMergeSlot(ItemStack stack, Slot slotIn) {
        return slotIn.inventory != this.craftResult && super.canMergeSlot(stack, slotIn);
    }
}

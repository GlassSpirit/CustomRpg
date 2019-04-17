package noppes.npcs.containers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.controllers.data.PlayerMail;
import noppes.npcs.controllers.data.PlayerMailData;

import java.util.Iterator;

public class ContainerCustom extends ContainerNpcInterface {

    private InventoryNPC inventory;
    public final int columns;
    public final int rows;

    public ContainerCustom(EntityPlayer player, int columns, int rows) {
        super(player);
        this.columns = columns;
        this.rows = rows;
        inventory = new InventoryNPC("currency", columns * rows, this);

        for (int j = 0; j < rows; ++j) {
            for (int k = 0; k < columns; ++k) {
                this.addSlotToContainer(new SlotApi(inventory, k + j * 9, 179 + k * 24, 138));
            }
        }


        for (int j = 0; j < 3; ++j) {
            for (int k = 0; k < 9; ++k) {
                this.addSlotToContainer(new Slot(player.inventory, k + j * 9 + 9, 28 + k * 18, 175 + j * 18));
            }
        }

        for (int j = 0; j < 9; ++j) {
            this.addSlotToContainer(new Slot(player.inventory, j, 28 + j * 18, 230));
        }
    }

    /**
     * Called when a player shift-clicks on a slot. You must override this or you will crash when someone does that.
     */
    @Override
    public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par2) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(par2);

        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (par2 < 4) {
                if (!this.mergeItemStack(itemstack1, 4, this.inventorySlots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.mergeItemStack(itemstack1, 0, 4, false)) {
                return null;
            }

            if (itemstack1.getCount() == 0) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }
        }

        return itemstack;
    }

    /**
     * Called when the container is closed.
     */
    @Override
    public void onContainerClosed(EntityPlayer player) {
        super.onContainerClosed(player);
        if (!player.world.isRemote) {
            PlayerMailData data = PlayerData.get(player).mailData;
            Iterator<PlayerMail> it = data.playermail.iterator();
            while (it.hasNext()) {
                PlayerMail mail = it.next();
//				if(mail.time == this.mail.time && mail.sender.equals(this.mail.sender)){
//					mail.readNBT(this.mail.writeNBT());
//					break;
//				}
            }
        }
    }

}

package noppes.npcs.containers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;

public class ContainerCustomChest extends Container {

    private InventoryBasic craftingMatrix;

    public final int rows;

    public ContainerCustomChest(EntityPlayer player, int rows) {
        this.rows = rows;

        craftingMatrix = new InventoryBasic("crafting", false, rows * 9);

        for (int j1 = 0; j1 < 9; j1++) {
            addSlotToContainer(new Slot(player.inventory, j1, j1 * 18 + 8, 171));
        }

        for (int i1 = 0; i1 < 3; i1++) {
            for (int l1 = 0; l1 < 9; l1++) {
                addSlotToContainer(new Slot(player.inventory, l1 + i1 * 9 + 9, l1 * 18 + 8, 113 + i1 * 18));
            }
        }

        for (int j = 0; j < rows; ++j) {
            for (int k = 0; k < 9; ++k) {
                this.addSlotToContainer(new Slot(craftingMatrix, k + j * 9, 8 + k * 18, 18 + j * 18));
            }
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return false;
    }

}

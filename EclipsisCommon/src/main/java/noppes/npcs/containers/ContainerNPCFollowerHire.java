package noppes.npcs.containers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.roles.RoleFollower;

public class ContainerNPCFollowerHire extends ContainerNpcInterface {
    public InventoryBasic currencyMatrix;
    public RoleFollower role;

    public ContainerNPCFollowerHire(EntityNPCInterface npc, EntityPlayer player) {
        super(player);
        role = (RoleFollower) npc.roleInterface;

        currencyMatrix = new InventoryBasic("currency", false, 1);
        addSlotToContainer(new SlotNpcMercenaryCurrency(role, currencyMatrix, 0, 44, 35));

        for (int i1 = 0; i1 < 3; i1++) {
            for (int l1 = 0; l1 < 9; l1++) {
                addSlotToContainer(new Slot(player.inventory, l1 + i1 * 9 + 9, 8 + l1 * 18, 84 + i1 * 18));
            }

        }

        for (int j1 = 0; j1 < 9; j1++) {
            addSlotToContainer(new Slot(player.inventory, j1, 8 + j1 * 18, 142));
        }
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int i) {
        return ItemStack.EMPTY;
    }

    @Override
    public void onContainerClosed(EntityPlayer entityplayer) {
        super.onContainerClosed(entityplayer);

        if (!entityplayer.world.isRemote) {
            ItemStack itemstack = currencyMatrix.removeStackFromSlot(0);
            if (!NoppesUtilServer.IsItemStackNull(itemstack) && !entityplayer.world.isRemote) {
                entityplayer.entityDropItem(itemstack, 0f);
            }
        }
    }
}

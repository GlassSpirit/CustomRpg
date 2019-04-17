package noppes.npcs.containers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import noppes.npcs.EventHooks;
import noppes.npcs.NoppesUtilPlayer;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.api.event.RoleEvent;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.roles.RoleTrader;


public class ContainerNPCTrader extends ContainerNpcInterface {
    public RoleTrader role;
    private EntityNPCInterface npc;

    public ContainerNPCTrader(EntityNPCInterface npc, EntityPlayer player) {
        super(player);
        this.npc = npc;
        role = (RoleTrader) npc.roleInterface;

        for (int i = 0; i < 18; i++) {
            int x = 53;
            x += i % 3 * 72;
            int y = 7;
            y += i / 3 * 21;

            ItemStack item = role.inventoryCurrency.items.get(i);
            ItemStack item2 = role.inventoryCurrency.items.get(i + 18);
            if (item == null) {
                item = item2;
                item2 = null;
            }
            addSlotToContainer(new Slot(role.inventorySold, i, x, y));
        }

        for (int i1 = 0; i1 < 3; i1++) {
            for (int l1 = 0; l1 < 9; l1++) {
                addSlotToContainer(new Slot(player.inventory, l1 + i1 * 9 + 9, 32 + l1 * 18, 140 + i1 * 18));
            }

        }

        for (int j1 = 0; j1 < 9; j1++) {
            addSlotToContainer(new Slot(player.inventory, j1, 32 + j1 * 18, 198));
        }
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int i) {
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack slotClick(int i, int j, ClickType par3, EntityPlayer entityplayer) {
        if (par3 != ClickType.PICKUP)
            return ItemStack.EMPTY;
        if (i < 0 || i >= 18)
            return super.slotClick(i, j, par3, entityplayer);
        if (j == 1)
            return ItemStack.EMPTY;
        Slot slot = inventorySlots.get(i);
        if (slot == null || slot.getStack() == null)
            return ItemStack.EMPTY;

        ItemStack item = slot.getStack();
        if (!canGivePlayer(item, entityplayer))
            return ItemStack.EMPTY;

        ItemStack currency = role.inventoryCurrency.getStackInSlot(i);
        ItemStack currency2 = role.inventoryCurrency.getStackInSlot(i + 18);
        if (!canBuy(currency, currency2, entityplayer)) {
            RoleEvent.TradeFailedEvent event = new RoleEvent.TradeFailedEvent(entityplayer, npc.wrappedNPC, item, currency, currency2);
            EventHooks.onNPCRole(npc, event);
            return event.receiving == null ? ItemStack.EMPTY : event.receiving.getMCItemStack();
        }

        RoleEvent.TraderEvent event = new RoleEvent.TraderEvent(entityplayer, npc.wrappedNPC, item, currency, currency2);

        if (EventHooks.onNPCRole(npc, event))
            return ItemStack.EMPTY;

        if (event.currency1 != null)
            currency = event.currency1.getMCItemStack();
        if (event.currency2 != null)
            currency2 = event.currency2.getMCItemStack();

        if (!canBuy(currency, currency2, entityplayer))
            return ItemStack.EMPTY;

        NoppesUtilPlayer.consumeItem(entityplayer, currency, role.ignoreDamage, role.ignoreNBT);
        NoppesUtilPlayer.consumeItem(entityplayer, currency2, role.ignoreDamage, role.ignoreNBT);


        ItemStack soldItem = ItemStack.EMPTY;
        if (event.sold != null) {
            soldItem = event.sold.getMCItemStack();
            givePlayer(soldItem.copy(), entityplayer);
        }
        return soldItem;
    }

    public boolean canBuy(ItemStack currency, ItemStack currency2, EntityPlayer player) {
        if (NoppesUtilServer.IsItemStackNull(currency) && NoppesUtilServer.IsItemStackNull(currency2))
            return true;

        if (NoppesUtilServer.IsItemStackNull(currency)) {
            currency = currency2;
            currency2 = ItemStack.EMPTY;
        }
        if (NoppesUtilPlayer.compareItems(currency, currency2, role.ignoreDamage, role.ignoreNBT)) {
            currency = currency.copy();
            currency.grow(currency2.getCount());
            currency2 = ItemStack.EMPTY;
        }
        if (NoppesUtilServer.IsItemStackNull(currency2))
            return NoppesUtilPlayer.compareItems(player, currency, role.ignoreDamage, role.ignoreNBT);
        return NoppesUtilPlayer.compareItems(player, currency, role.ignoreDamage, role.ignoreNBT) && NoppesUtilPlayer.compareItems(player, currency2, role.ignoreDamage, role.ignoreNBT);

    }

    private boolean canGivePlayer(ItemStack item, EntityPlayer entityplayer) {
        ItemStack itemstack3 = entityplayer.inventory.getItemStack();
        if (NoppesUtilServer.IsItemStackNull(itemstack3)) {
            return true;
        } else if (NoppesUtilPlayer.compareItems(itemstack3, item, false, false)) {
            int k1 = item.getCount();
            return k1 > 0 && k1 + itemstack3.getCount() <= itemstack3.getMaxStackSize();
        }
        return false;
    }

    private void givePlayer(ItemStack item, EntityPlayer entityplayer) {
        ItemStack itemstack3 = entityplayer.inventory.getItemStack();
        if (NoppesUtilServer.IsItemStackNull(itemstack3)) {
            entityplayer.inventory.setItemStack(item);
        } else if (NoppesUtilPlayer.compareItems(itemstack3, item, false, false)) {

            int k1 = item.getCount();
            if (k1 > 0 && k1 + itemstack3.getCount() <= itemstack3.getMaxStackSize()) {
                itemstack3.grow(k1);
            }
        }
    }
}

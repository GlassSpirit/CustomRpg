package noppes.npcs.quests;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.NoppesUtilPlayer;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.NpcMiscInventory;
import noppes.npcs.api.CustomNPCsException;
import noppes.npcs.api.handler.data.IQuestObjective;
import noppes.npcs.util.ValueUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class QuestItem extends QuestInterface {
    public NpcMiscInventory items = new NpcMiscInventory(3);
    public boolean leaveItems = false;
    public boolean ignoreDamage = false;
    public boolean ignoreNBT = false;

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        items.setFromNBT(compound.getCompoundTag("Items"));
        leaveItems = compound.getBoolean("LeaveItems");
        ignoreDamage = compound.getBoolean("IgnoreDamage");
        ignoreNBT = compound.getBoolean("IgnoreNBT");
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        compound.setTag("Items", items.getToNBT());
        compound.setBoolean("LeaveItems", leaveItems);
        compound.setBoolean("IgnoreDamage", ignoreDamage);
        compound.setBoolean("IgnoreNBT", ignoreNBT);
    }

    @Override
    public boolean isCompleted(EntityPlayer player) {
        List<ItemStack> questItems = NoppesUtilPlayer.countStacks(items, ignoreDamage, ignoreNBT);
        for (ItemStack reqItem : questItems) {
            if (!NoppesUtilPlayer.compareItems(player, reqItem, ignoreDamage, ignoreNBT)) {
                return false;
            }
        }

        return true;
    }

    public Map<ItemStack, Integer> getProgressSet(EntityPlayer player) {
        HashMap<ItemStack, Integer> map = new HashMap<ItemStack, Integer>();
        List<ItemStack> questItems = NoppesUtilPlayer.countStacks(items, ignoreDamage, ignoreNBT);
        for (ItemStack item : questItems) {
            if (NoppesUtilServer.IsItemStackNull(item))
                continue;
            map.put(item, 0);
        }
        for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
            ItemStack item = player.inventory.getStackInSlot(i);
            if (NoppesUtilServer.IsItemStackNull(item))
                continue;
            for (Entry<ItemStack, Integer> questItem : map.entrySet()) {
                if (NoppesUtilPlayer.compareItems(questItem.getKey(), item, ignoreDamage, ignoreNBT)) {
                    map.put(questItem.getKey(), questItem.getValue() + item.getCount());
                }
            }
        }
        return map;
    }

    @Override
    public void handleComplete(EntityPlayer player) {
        if (leaveItems)
            return;
        for (ItemStack questitem : items.items) {
            if (questitem.isEmpty())
                continue;
            int stacksize = questitem.getCount();
            for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
                ItemStack item = player.inventory.getStackInSlot(i);
                if (NoppesUtilServer.IsItemStackNull(item))
                    continue;
                if (NoppesUtilPlayer.compareItems(item, questitem, ignoreDamage, ignoreNBT)) {
                    int size = item.getCount();
                    if (stacksize - size >= 0) {
                        player.inventory.setInventorySlotContents(i, ItemStack.EMPTY);
                        item.splitStack(size);
                    } else {
                        item.splitStack(stacksize);
                    }
                    stacksize -= size;
                    if (stacksize <= 0)
                        break;
                }
            }
        }
    }

    @Override
    public IQuestObjective[] getObjectives(EntityPlayer player) {
        List<IQuestObjective> list = new ArrayList<IQuestObjective>();
        List<ItemStack> questItems = NoppesUtilPlayer.countStacks(items, ignoreDamage, ignoreNBT);
        for (ItemStack stack : questItems) {
            if (!stack.isEmpty()) {
                list.add(new QuestItemObjective(player, stack));
            }
        }
        return list.toArray(new IQuestObjective[list.size()]);
    }


    class QuestItemObjective implements IQuestObjective {
        private final EntityPlayer player;
        private final ItemStack questItem;

        public QuestItemObjective(EntityPlayer player, ItemStack item) {
            this.player = player;
            this.questItem = item;
        }

        @Override
        public int getProgress() {
            int count = 0;
            for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
                ItemStack item = player.inventory.getStackInSlot(i);
                if (NoppesUtilServer.IsItemStackNull(item))
                    continue;
                if (NoppesUtilPlayer.compareItems(questItem, item, ignoreDamage, ignoreNBT)) {
                    count += item.getCount();
                }
            }
            return ValueUtil.CorrectInt(count, 0, questItem.getCount());
        }

        @Override
        public void setProgress(int progress) {
            throw new CustomNPCsException("Cant set the progress of ItemQuests");
        }

        @Override
        public int getMaxProgress() {
            return questItem.getCount();
        }

        @Override
        public boolean isCompleted() {
            return NoppesUtilPlayer.compareItems(player, questItem, ignoreDamage, ignoreNBT);
        }

        @Override
        public String getText() {
            return questItem.getDisplayName() + ": " + getProgress() + "/" + getMaxProgress();
        }
    }

}

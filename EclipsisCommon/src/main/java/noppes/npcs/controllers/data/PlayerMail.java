package noppes.npcs.controllers.data;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.api.entity.data.IPlayerMail;
import noppes.npcs.controllers.QuestController;

public class PlayerMail implements IInventory, IPlayerMail {
    public String subject = "";
    public String sender = "";
    public NBTTagCompound message = new NBTTagCompound();
    public long time = 0;
    public boolean beenRead = false;
    public int questId = -1;
    public String questTitle = "";
    public NonNullList<ItemStack> items = NonNullList.withSize(4, ItemStack.EMPTY);

    public long timePast;

    public void readNBT(NBTTagCompound compound) {
        subject = compound.getString("Subject");
        sender = compound.getString("Sender");
        time = compound.getLong("Time");
        beenRead = compound.getBoolean("BeenRead");
        message = compound.getCompoundTag("Message");
        timePast = compound.getLong("TimePast");
        if (compound.hasKey("MailQuest"))
            questId = compound.getInteger("MailQuest");
        questTitle = compound.getString("MailQuestTitle");

        items.clear();
        NBTTagList nbttaglist = compound.getTagList("MailItems", 10);
        for (int i = 0; i < nbttaglist.tagCount(); ++i) {
            NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
            int j = nbttagcompound1.getByte("Slot") & 255;

            if (j >= 0 && j < this.items.size()) {
                this.items.set(j, new ItemStack(nbttagcompound1));
            }
        }
    }

    public NBTTagCompound writeNBT() {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setString("Subject", subject);
        compound.setString("Sender", sender);
        compound.setLong("Time", time);
        compound.setBoolean("BeenRead", beenRead);
        compound.setTag("Message", message);
        compound.setLong("TimePast", System.currentTimeMillis() - time);
        compound.setInteger("MailQuest", questId);

        if (hasQuest())
            compound.setString("MailQuestTitle", getQuest().title);

        NBTTagList nbttaglist = new NBTTagList();

        for (int i = 0; i < this.items.size(); ++i) {
            if (!this.items.get(i).isEmpty()) {
                NBTTagCompound nbttagcompound1 = new NBTTagCompound();
                nbttagcompound1.setByte("Slot", (byte) i);
                this.items.get(i).writeToNBT(nbttagcompound1);
                nbttaglist.appendTag(nbttagcompound1);
            }
        }

        compound.setTag("MailItems", nbttaglist);
        return compound;
    }

    public boolean isValid() {
        return !subject.isEmpty() && !message.isEmpty() && !sender.isEmpty();
    }

    public boolean hasQuest() {
        return getQuest() != null;
    }

    public Quest getQuest() {
        return QuestController.instance != null ? QuestController.instance.quests.get(questId) : null;
    }

    @Override
    public int getSizeInventory() {
        return 4;
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public ItemStack getStackInSlot(int i) {
        return this.items.get(i);
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        ItemStack itemstack = ItemStackHelper.getAndSplit(items, index, count);

        if (!itemstack.isEmpty()) {
            this.markDirty();
        }

        return itemstack;
    }

    @Override
    public ItemStack removeStackFromSlot(int var1) {
        return items.set(var1, ItemStack.EMPTY);
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        this.items.set(index, stack);

        if (stack.getCount() > this.getInventoryStackLimit()) {
            stack.setCount(this.getInventoryStackLimit());
        }

        this.markDirty();
    }

    @Override
    public ITextComponent getDisplayName() {
        return null;
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    @Override
    public void markDirty() {

    }

    @Override
    public boolean isUsableByPlayer(EntityPlayer var1) {
        return true;
    }

    @Override
    public void openInventory(EntityPlayer player) {

    }

    @Override
    public void closeInventory(EntityPlayer player) {
    }

    @Override
    public boolean isItemValidForSlot(int var1, ItemStack var2) {
        return true;
    }

    public PlayerMail copy() {
        PlayerMail mail = new PlayerMail();
        mail.readNBT(writeNBT());
        return mail;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public int getField(int id) {
        return 0;
    }

    @Override
    public void setField(int id, int value) {
    }

    @Override
    public int getFieldCount() {
        return 0;
    }

    @Override
    public void clear() {
    }

    @Override
    public boolean isEmpty() {
        for (int slot = 0; slot < this.getSizeInventory(); slot++) {
            ItemStack item = getStackInSlot(slot);
            if (!NoppesUtilServer.IsItemStackNull(item) && !item.isEmpty()) {
                return false;
            }
        }
        return true;
    }
}

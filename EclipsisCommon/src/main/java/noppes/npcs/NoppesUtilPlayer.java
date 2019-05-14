package noppes.npcs;

import io.netty.buffer.Unpooled;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;
import net.minecraftforge.oredict.OreDictionary;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.constants.OptionType;
import noppes.npcs.api.constants.RoleType;
import noppes.npcs.api.event.QuestEvent;
import noppes.npcs.api.event.RoleEvent;
import noppes.npcs.api.item.IItemStack;
import noppes.npcs.common.CustomNpcs;
import noppes.npcs.common.entity.EntityNPCInterface;
import noppes.npcs.constants.EnumPacketClient;
import noppes.npcs.constants.EnumPlayerPacket;
import noppes.npcs.containers.ContainerNPCBankInterface;
import noppes.npcs.containers.ContainerNPCFollower;
import noppes.npcs.containers.ContainerNPCFollowerHire;
import noppes.npcs.controllers.BankController;
import noppes.npcs.controllers.DialogController;
import noppes.npcs.controllers.PlayerDataController;
import noppes.npcs.controllers.PlayerQuestController;
import noppes.npcs.controllers.data.*;
import noppes.npcs.roles.RoleCompanion;
import noppes.npcs.roles.RoleDialog;
import noppes.npcs.roles.RoleFollower;
import noppes.npcs.util.NoppesStringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NoppesUtilPlayer {

    public static void changeFollowerState(EntityPlayerMP player, EntityNPCInterface npc) {
        if (npc.advanced.role != RoleType.FOLLOWER)
            return;

        RoleFollower role = (RoleFollower) npc.roleInterface;
        EntityPlayer owner = role.owner;
        if (owner == null || !owner.getName().equals(player.getName()))
            return;

        role.isFollowing = !role.isFollowing;
    }

    public static void hireFollower(EntityPlayerMP player, EntityNPCInterface npc) {
        if (npc.advanced.role != RoleType.FOLLOWER)
            return;
        Container con = player.openContainer;
        if (con == null || !(con instanceof ContainerNPCFollowerHire))
            return;

        ContainerNPCFollowerHire container = (ContainerNPCFollowerHire) con;
        RoleFollower role = (RoleFollower) npc.roleInterface;
        followerBuy(role, container.currencyMatrix, player, npc);
    }

    public static void extendFollower(EntityPlayerMP player, EntityNPCInterface npc) {
        if (npc.advanced.role != RoleType.FOLLOWER)
            return;
        Container con = player.openContainer;
        if (con == null || !(con instanceof ContainerNPCFollower))
            return;

        ContainerNPCFollower container = (ContainerNPCFollower) con;
        RoleFollower role = (RoleFollower) npc.roleInterface;
        followerBuy(role, container.currencyMatrix, player, npc);
    }

    public static void teleportPlayer(EntityPlayerMP player, double x, double y, double z, int dimension) {
        if (player.dimension != dimension) {
            int dim = player.dimension;
            MinecraftServer server = player.getServer();
            WorldServer wor = server.getWorld(dimension);
            if (wor == null) {
                player.sendMessage(new TextComponentString("Broken transporter. Dimenion does not exist"));
                return;
            }
            player.setLocationAndAngles(x, y, z, player.rotationYaw, player.rotationPitch);
            server.getPlayerList().transferPlayerToDimension(player, dimension, new CustomTeleporter(wor));
            player.connection.setPlayerLocation(x, y, z, player.rotationYaw, player.rotationPitch);

            if (!wor.playerEntities.contains(player))
                wor.spawnEntity(player);
        } else {
            player.connection.setPlayerLocation(x, y, z, player.rotationYaw, player.rotationPitch);
        }
        player.world.updateEntityWithOptionalForce(player, false);
    }

    private static void followerBuy(RoleFollower role, IInventory currencyInv, EntityPlayerMP player, EntityNPCInterface npc) {
        ItemStack currency = currencyInv.getStackInSlot(0);
        if (currency == null || currency.isEmpty())
            return;
        HashMap<ItemStack, Integer> cd = new HashMap<>();
        for (int slot = 0; slot < role.inventory.items.size(); slot++) {
            ItemStack is = role.inventory.items.get(slot);
            if (is.isEmpty() || is.getItem() != currency.getItem() || is.getHasSubtypes() && is.getItemDamage() != currency.getItemDamage())
                continue;
            int days = 1;
            if (role.rates.containsKey(slot))
                days = role.rates.get(slot);

            cd.put(is, days);
        }
        if (cd.size() == 0)
            return;
        int stackSize = currency.getCount();
        int days = 0;

        int possibleDays = 0;
        int possibleSize = stackSize;
        while (true) {
            for (ItemStack item : cd.keySet()) {
                int rDays = cd.get(item);
                int rValue = item.getCount();
                if (rValue > stackSize)
                    continue;
                int newStackSize = stackSize % rValue;
                int size = stackSize - newStackSize;
                int posDays = (size / rValue) * rDays;
                if (possibleDays <= posDays) {
                    possibleDays = posDays;
                    possibleSize = newStackSize;
                }
            }
            if (stackSize == possibleSize)
                break;
            stackSize = possibleSize;
            days += possibleDays;
            possibleDays = 0;
        }

        RoleEvent.FollowerHireEvent event = new RoleEvent.FollowerHireEvent(player, npc.wrappedNPC, days);
        if (EventHooks.onNPCRole(npc, event))
            return;

        if (event.days == 0)
            return;

        if (stackSize <= 0)
            currencyInv.setInventorySlotContents(0, ItemStack.EMPTY);
        else
            currency = currency.splitStack(stackSize);


        npc.say(player, new Line(NoppesStringUtils.formatText(role.dialogHire.replace("{days}", days + ""), player, npc)));
        role.setOwner(player);
        role.addDays(days);
    }

    /*
         ScriptEventRoleBankUpgraded bankUpgradedEvent = new ScriptEventRoleBankUpgraded(player, slot);
        if(npc.roleInterface.onRoleEvent(bankUpgradedEvent, "player", player))
            return;
     */
    public static void bankUpgrade(EntityPlayerMP player, EntityNPCInterface npc) {


        if (npc.advanced.role != RoleType.BANK)
            return;
        Container con = player.openContainer;
        if (con == null || !(con instanceof ContainerNPCBankInterface))
            return;

        ContainerNPCBankInterface container = (ContainerNPCBankInterface) con;
        Bank bank = BankController.getInstance().getBank(container.bankid);
        ItemStack item = bank.upgradeInventory.getStackInSlot(container.slot);
        if (item == null || item.isEmpty())
            return;

        int price = item.getCount();
        ItemStack currency = container.currencyMatrix.getStackInSlot(0);
        if (currency == null || currency.isEmpty() || price > currency.getCount())
            return;
        if (currency.getCount() - price == 0)
            container.currencyMatrix.setInventorySlotContents(0, ItemStack.EMPTY);
        else
            currency = currency.splitStack(price);
        player.closeContainer();
        PlayerBankData data = PlayerDataController.instance.getBankData(player, bank.id);
        BankData bankData = data.getBank(bank.id);
        bankData.upgradedSlots.put(container.slot, true);

        RoleEvent.BankUpgradedEvent event = new RoleEvent.BankUpgradedEvent(player, npc.wrappedNPC, container.slot);
        EventHooks.onNPCRole(npc, event);

        bankData.openBankGui(player, npc, bank.id, container.slot);
    }

    public static void bankUnlock(EntityPlayerMP player, EntityNPCInterface npc) {

        if (npc.advanced.role != RoleType.BANK)
            return;
        Container con = player.openContainer;
        if (con == null || !(con instanceof ContainerNPCBankInterface))
            return;
        ContainerNPCBankInterface container = (ContainerNPCBankInterface) con;
        Bank bank = BankController.getInstance().getBank(container.bankid);

        ItemStack item = bank.currencyInventory.getStackInSlot(container.slot);
        if (item == null || item.isEmpty())
            return;

        int price = item.getCount();
        ItemStack currency = container.currencyMatrix.getStackInSlot(0);
        if (currency == null || currency.isEmpty() || price > currency.getCount())
            return;
        if (currency.getCount() - price == 0)
            container.currencyMatrix.setInventorySlotContents(0, ItemStack.EMPTY);
        else
            currency = currency.splitStack(price);
        player.closeContainer();
        PlayerBankData data = PlayerDataController.instance.getBankData(player, bank.id);
        BankData bankData = data.getBank(bank.id);
        if (bankData.unlockedSlots + 1 <= bank.maxSlots)
            bankData.unlockedSlots++;

        RoleEvent.BankUnlockedEvent event = new RoleEvent.BankUnlockedEvent(player, npc.wrappedNPC, container.slot);
        EventHooks.onNPCRole(npc, event);

        bankData.openBankGui(player, npc, bank.id, container.slot);
    }

    public static void sendData(EnumPlayerPacket enu, Object... obs) {
        PacketBuffer buffer = new PacketBuffer(Unpooled.buffer());
        try {
            if (!Server.fillBuffer(buffer, enu, obs))
                return;
            CustomNpcs.INSTANCE.getChannelPlayer().sendToServer(new FMLProxyPacket(buffer, "CustomNPCsPlayer"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void dialogSelected(int diaId, int optionId, EntityPlayerMP player, EntityNPCInterface npc) {
        PlayerData data = PlayerData.get(player);

        if (data.dialogId != diaId) {
            return;
        }
        if (data.dialogId < 0 && npc.advanced.role == RoleType.DIALOG) {
            String text = ((RoleDialog) npc.roleInterface).optionsTexts.get(optionId);
            if (text != null && !text.isEmpty()) {
                Dialog d = new Dialog(null);
                d.text = text;
                NoppesUtilServer.openDialog(player, npc, d);
            }
            return;
        }
        Dialog dialog = DialogController.instance.dialogs.get(data.dialogId);
        if (dialog == null)
            return;
        if (!dialog.hasDialogs(player) && !dialog.hasOtherOptions()) {
            closeDialog(player, npc, true);
            return;
        }

        DialogOption option = dialog.options.get(optionId);

        if (option == null || EventHooks.onNPCDialogOption(npc, player, dialog, option) ||
                option.optionType == OptionType.DIALOG_OPTION && (!option.isAvailable(player) || !option.hasDialog()) ||
                option.optionType == OptionType.DISABLED || option.optionType == OptionType.QUIT_OPTION) {
            closeDialog(player, npc, true);
            return;
        }

        if (option.optionType == OptionType.ROLE_OPTION) {
            closeDialog(player, npc, true);
            if (npc.roleInterface != null) {
                if (npc.advanced.role == RoleType.COMPANION)
                    ((RoleCompanion) npc.roleInterface).interact(player, true);
                else
                    npc.roleInterface.interact(player);
            }
        } else if (option.optionType == OptionType.DIALOG_OPTION) {
            closeDialog(player, npc, false);
            NoppesUtilServer.openDialog(player, npc, option.getDialog());
        } else if (option.optionType == OptionType.COMMAND_BLOCK) {
            closeDialog(player, npc, true);
            NoppesUtilServer.runCommand(npc, npc.getName(), option.command, player);
        } else {
            closeDialog(player, npc, true);
        }
    }

    public static void closeDialog(EntityPlayerMP player, EntityNPCInterface npc, boolean notifyClient) {
        PlayerData data = PlayerData.get(player);
        Dialog dialog = DialogController.instance.dialogs.get(data.dialogId);
        EventHooks.onNPCDialogClose(npc, player, dialog);
        if (notifyClient) {
            Server.sendData(player, EnumPacketClient.GUI_CLOSE, -1, new NBTTagCompound());
        }
        data.dialogId = -1;
    }

    public static void questCompletion(EntityPlayerMP player, int questId) {
        PlayerData data = PlayerData.get(player);
        PlayerQuestData playerdata = data.questData;
        QuestData questdata = playerdata.activeQuests.get(questId);
        if (questdata == null)
            return;

        Quest quest = questdata.quest;
        if (!quest.questInterface.isCompleted(player))
            return;

        QuestEvent.QuestTurnedInEvent event = new QuestEvent.QuestTurnedInEvent(data.scriptData.getPlayer(), quest);
        event.expReward = quest.rewardExp;

        List<IItemStack> list = new ArrayList<>();
        for (ItemStack item : quest.rewardItems.items) {
            if (!item.isEmpty())
                list.add(NpcAPI.instance().getIItemStack(item));
        }

        if (!quest.randomReward) {
            event.itemRewards = list.toArray(new IItemStack[list.size()]);
        } else {
            if (!list.isEmpty()) {
                event.itemRewards = new IItemStack[]{list.get(player.getRNG().nextInt(list.size()))};
            }
        }

        EventHooks.onQuestTurnedIn(data.scriptData, event);

        for (IItemStack item : event.itemRewards) {
            if (item != null)
                NoppesUtilServer.GivePlayerItem(player, player, item.getMCItemStack());
        }

        quest.questInterface.handleComplete(player);
        if (event.expReward > 0) {
            NoppesUtilServer.playSound(player, SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 0.1F, 0.5F * ((player.world.rand.nextFloat() - player.world.rand.nextFloat()) * 0.7F + 1.8F));

            player.addExperience(event.expReward);
        }
        quest.factionOptions.addPoints(player);
        if (quest.mail.isValid()) {
            PlayerDataController.instance.addPlayerMessage(player.getServer(), player.getName(), quest.mail);
        }
        if (!quest.command.isEmpty()) {
            FakePlayer cplayer = EntityNPCInterface.CommandPlayer;
            cplayer.setWorld(player.world);
            cplayer.setPosition(player.posX, player.posY, player.posZ);
            NoppesUtilServer.runCommand(cplayer, "QuestCompletion", quest.command, player);
        }
        PlayerQuestController.setQuestFinished(quest, player);
        if (quest.hasNewQuest()) PlayerQuestController.addActiveQuest(quest.getNextQuest(), player);
    }

    public static boolean compareItems(ItemStack item, ItemStack item2, boolean ignoreDamage, boolean ignoreNBT) {
        if (NoppesUtilServer.IsItemStackNull(item) || NoppesUtilServer.IsItemStackNull(item2)) {
            return false;
        }
        boolean oreMatched = false;
        OreDictionary.itemMatches(item, item2, false);
        int[] ids = OreDictionary.getOreIDs(item);
        if (ids.length > 0) {
            for (int id : ids) {
                boolean match1 = false, match2 = false;
                for (ItemStack is : OreDictionary.getOres(OreDictionary.getOreName(id))) {
                    if (compareItemDetails(item, is, ignoreDamage, ignoreNBT)) {
                        match1 = true;
                    }
                    if (compareItemDetails(item2, is, ignoreDamage, ignoreNBT)) {
                        match2 = true;
                    }
                }
                if (match1 && match2)
                    return true;
            }
        }
        return compareItemDetails(item, item2, ignoreDamage, ignoreNBT);
    }

    private static boolean compareItemDetails(ItemStack item, ItemStack item2, boolean ignoreDamage, boolean ignoreNBT) {
        if (item.getItem() != item2.getItem()) {
            return false;
        }
        if (!ignoreDamage && item.getItemDamage() != -1 && item.getItemDamage() != item2.getItemDamage()) {
            return false;
        }
        if (!ignoreNBT && item.getTagCompound() != null && (item2.getTagCompound() == null || !item.getTagCompound().equals(item2.getTagCompound()))) {
            return false;
        }
        return ignoreNBT || item2.getTagCompound() == null || item.getTagCompound() != null;
    }

    public static boolean compareItems(EntityPlayer player, ItemStack item, boolean ignoreDamage, boolean ignoreNBT) {
        int size = 0;
        for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
            ItemStack is = player.inventory.getStackInSlot(i);
            if (!NoppesUtilServer.IsItemStackNull(is) && compareItems(item, is, ignoreDamage, ignoreNBT))
                size += is.getCount();
        }
        return size >= item.getCount();
    }

    public static void consumeItem(EntityPlayer player, ItemStack item, boolean ignoreDamage, boolean ignoreNBT) {
        if (NoppesUtilServer.IsItemStackNull(item))
            return;
        int size = item.getCount();
        for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
            ItemStack is = player.inventory.getStackInSlot(i);
            if (NoppesUtilServer.IsItemStackNull(is) || !compareItems(item, is, ignoreDamage, ignoreNBT))
                continue;
            if (size >= is.getCount()) {
                size -= is.getCount();
                player.inventory.setInventorySlotContents(i, ItemStack.EMPTY);
            } else {
                is.splitStack(size);
                break;
            }
        }
    }

    public static List<ItemStack> countStacks(IInventory inv, boolean ignoreDamage, boolean ignoreNBT) {
        List<ItemStack> list = new ArrayList<>();
        for (int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack item = inv.getStackInSlot(i);
            if (NoppesUtilServer.IsItemStackNull(item))
                continue;
            boolean found = false;
            for (ItemStack is : list) {
                if (compareItems(item, is, ignoreDamage, ignoreNBT)) {
                    is.setCount(is.getCount() + item.getCount());
                    found = true;
                    break;
                }
            }
            if (!found)
                list.add(item.copy());
        }

        return list;
    }

}

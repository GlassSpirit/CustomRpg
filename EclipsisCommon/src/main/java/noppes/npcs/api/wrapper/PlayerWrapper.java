package noppes.npcs.api.wrapper;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.WorldSettings;
import noppes.npcs.*;
import noppes.npcs.api.*;
import noppes.npcs.api.block.IBlock;
import noppes.npcs.api.constants.EntityType;
import noppes.npcs.api.constants.QuestType;
import noppes.npcs.api.entity.IPlayer;
import noppes.npcs.api.entity.data.IPixelmonPlayerData;
import noppes.npcs.api.handler.data.IQuest;
import noppes.npcs.api.item.IItemStack;
import noppes.npcs.client.EntityUtil;
import noppes.npcs.constants.EnumPacketClient;
import noppes.npcs.controllers.DialogController;
import noppes.npcs.controllers.FactionController;
import noppes.npcs.controllers.PixelmonHelper;
import noppes.npcs.controllers.QuestController;
import noppes.npcs.controllers.data.*;
import noppes.npcs.entity.EntityDialogNpc;
import noppes.npcs.util.ValueUtil;

import java.util.ArrayList;
import java.util.List;


public class PlayerWrapper<T extends EntityPlayerMP> extends EntityLivingBaseWrapper<T> implements IPlayer {

    private IContainer inventory;

    private Object pixelmonPartyStorage;
    private Object pixelmonPCStorage;


    public PlayerWrapper(T player) {
        super(player);
    }

    @Override
    public String getName() {
        return entity.getName();
    }

    @Override
    public String getDisplayName() {
        return entity.getDisplayNameString();
    }

    @Override
    public int getHunger() {
        return entity.getFoodStats().getFoodLevel();
    }

    @Override
    public void setHunger(int level) {
        entity.getFoodStats().setFoodLevel(level);
    }

    @Override
    public boolean hasFinishedQuest(int id) {
        PlayerQuestData data = this.getData().questData;
        return data.finishedQuests.containsKey(id);
    }

    @Override
    public boolean hasActiveQuest(int id) {
        PlayerQuestData data = this.getData().questData;
        return data.activeQuests.containsKey(id);
    }

    @Override
    public IQuest[] getActiveQuests() {
        PlayerQuestData data = this.getData().questData;
        List<IQuest> quests = new ArrayList<>();
        for (int id : data.activeQuests.keySet()) {
            IQuest quest = QuestController.instance.quests.get(id);
            if (quest != null) {
                quests.add(quest);
            }
        }
        return quests.toArray(new IQuest[quests.size()]);
    }

    @Override
    public IQuest[] getFinishedQuests() {
        PlayerQuestData data = this.getData().questData;
        List<IQuest> quests = new ArrayList<>();
        for (int id : data.finishedQuests.keySet()) {
            IQuest quest = QuestController.instance.quests.get(id);
            if (quest != null) {
                quests.add(quest);
            }
        }
        return quests.toArray(new IQuest[quests.size()]);
    }

    @Override
    public void startQuest(int id) {
        Quest quest = QuestController.instance.quests.get(id);
        if (quest == null)
            return;
        QuestData questdata = new QuestData(quest);
        PlayerData data = getData();
        data.questData.activeQuests.put(id, questdata);
        Server.sendData(entity, EnumPacketClient.MESSAGE, "quest.newquest", quest.title, 2);
        Server.sendData(entity, EnumPacketClient.CHAT, "quest.newquest", ": ", quest.title);

        data.updateClient = true;
    }

    @Override
    public void sendNotification(String title, String msg, int type) {
        if (type < 0 || type > 3) {
            throw new CustomNPCsException("Wrong type value given " + type);
        }
        Server.sendData(entity, EnumPacketClient.MESSAGE, title, msg, type);
    }

    @Override
    public void finishQuest(int id) {
        Quest quest = QuestController.instance.quests.get(id);
        if (quest == null)
            return;
        PlayerData data = getData();
        data.questData.finishedQuests.put(id, System.currentTimeMillis());
        data.updateClient = true;
    }

    @Override
    public void stopQuest(int id) {
        Quest quest = QuestController.instance.quests.get(id);
        if (quest == null)
            return;
        PlayerData data = getData();
        data.questData.activeQuests.remove(id);
        data.updateClient = true;
    }

    @Override
    public void removeQuest(int id) {
        Quest quest = QuestController.instance.quests.get(id);
        if (quest == null)
            return;
        PlayerData data = getData();
        data.questData.activeQuests.remove(id);
        data.questData.finishedQuests.remove(id);
        data.updateClient = true;
    }

    @Override
    public boolean hasReadDialog(int id) {
        PlayerDialogData data = this.getData().dialogData;
        return data.dialogsRead.contains(id);
    }

    @Override
    public void showDialog(int id, String name) {
        Dialog dialog = DialogController.instance.dialogs.get(id);
        if (dialog == null) {
            throw new CustomNPCsException("Unknown Dialog id: " + id);
        }

        if (!dialog.availability.isAvailable(entity))
            return;

        EntityDialogNpc npc = new EntityDialogNpc(this.getWorld().getMCWorld());
        npc.display.setName(name);
        EntityUtil.Copy(entity, npc);
        DialogOption option = new DialogOption();
        option.dialogId = id;
        option.title = dialog.title;
        npc.dialogs.put(0, option);
        NoppesUtilServer.openDialog(entity, npc, dialog);
    }

    public IContainer showCustomContainer(int columns, int rows, IItemStack items) {
        return null;
    }

    @Override
    public void addFactionPoints(int faction, int points) {
        PlayerData data = getData();
        data.factionData.increasePoints(entity, faction, points);
        data.updateClient = true;
    }

    @Override
    public int getFactionPoints(int faction) {
        return getData().factionData.getFactionPoints(entity, faction);
    }

    @Override
    public float getRotation() {
        return entity.rotationYaw;
    }

    @Override
    public void setRotation(float rotation) {
        entity.rotationYaw = rotation;
    }

    @Override
    public void message(String message) {
        entity.sendMessage(new TextComponentTranslation(NoppesStringUtils.formatText(message, entity)));
    }

    @Override
    public int getGamemode() {
        return entity.interactionManager.getGameType().getID();
    }

    @Override
    public void setGamemode(int type) {
        entity.setGameType(WorldSettings.getGameTypeById(type));
    }

    @Override
    public int inventoryItemCount(IItemStack item) {
        int count = 0;
        for (int i = 0; i < entity.inventory.getSizeInventory(); i++) {
            ItemStack is = entity.inventory.getStackInSlot(i);
            if (is != null && isItemEqual(item.getMCItemStack(), is))
                count += is.getCount();
        }
        return count;
    }

    private boolean isItemEqual(ItemStack stack, ItemStack other) {
        if (other.isEmpty())
            return false;
        if (stack.getItem() != other.getItem())
            return false;
        if (stack.getItemDamage() < 0)
            return true;
        return stack.getItemDamage() == other.getItemDamage();
    }

    @Override
    public int inventoryItemCount(String id, int damage) {
        Item item = Item.REGISTRY.getObject(new ResourceLocation(id));
        if (item == null)
            throw new CustomNPCsException("Unknown item id: " + id);
        return inventoryItemCount(NpcAPI.Instance().getIItemStack(new ItemStack(item, 1, damage)));
    }

    @Override
    public IContainer getInventory() {
        if (inventory == null)
            inventory = new ContainerWrapper(entity.inventory);
        return inventory;
    }

    @Override
    public boolean removeItem(IItemStack item, int amount) {
        int count = inventoryItemCount(item);
        if (amount > count)
            return false;
        else if (count == amount)
            removeAllItems(item);
        else {
            for (int i = 0; i < entity.inventory.getSizeInventory(); i++) {
                ItemStack is = entity.inventory.getStackInSlot(i);
                if (is != null && isItemEqual(item.getMCItemStack(), is)) {
                    if (amount >= is.getCount()) {
                        entity.inventory.setInventorySlotContents(i, ItemStack.EMPTY);
                        amount -= is.getCount();
                    } else {
                        is.splitStack(amount);
                        break;
                    }
                }
            }
        }
        updatePlayerInventory();
        return true;
    }

    @Override
    public boolean removeItem(String id, int damage, int amount) {
        Item item = Item.REGISTRY.getObject(new ResourceLocation(id));
        if (item == null)
            throw new CustomNPCsException("Unknown item id: " + id);
        return removeItem(NpcAPI.Instance().getIItemStack(new ItemStack(item, 1, damage)), amount);
    }

    @Override
    public boolean giveItem(IItemStack item) {
        ItemStack mcItem = item.getMCItemStack();
        if (mcItem.isEmpty())
            return false;

        boolean bo = entity.inventory.addItemStackToInventory(mcItem.copy());
        if (bo) {
            NoppesUtilServer.playSound(entity, SoundEvents.ENTITY_ITEM_PICKUP, 0.2F, ((entity.getRNG().nextFloat() - entity.getRNG().nextFloat()) * 0.7F + 1.0F) * 2.0F);
            updatePlayerInventory();
        }
        return bo;
    }

    @Override
    public boolean giveItem(String id, int damage, int amount) {
        Item item = Item.REGISTRY.getObject(new ResourceLocation(id));
        if (item == null)
            return false;
        ItemStack mcStack = new ItemStack(item);
        IItemStack itemStack = NpcAPI.Instance().getIItemStack(mcStack);
        itemStack.setStackSize(amount);
        itemStack.setItemDamage(damage);

        return giveItem(itemStack);
    }

    @Override
    public void updatePlayerInventory() {
        entity.inventoryContainer.detectAndSendChanges();
        PlayerQuestData playerdata = getData().questData;
        playerdata.checkQuestCompletion(entity, QuestType.ITEM);
    }

    @Override
    public IBlock getSpawnPoint() {
        BlockPos pos = entity.getBedLocation();
        if (pos == null)
            return getWorld().getSpawnPoint();
        return NpcAPI.Instance().getIBlock(entity.world, pos);
    }

    @Override
    public void setSpawnPoint(IBlock block) {
        entity.setSpawnPoint(new BlockPos(block.getX(), block.getY(), block.getZ()), true);
    }

    @Override
    public void setSpawnpoint(int x, int y, int z) {
        x = ValueUtil.CorrectInt(x, -30000000, 30000000);
        z = ValueUtil.CorrectInt(z, -30000000, 30000000);
        y = ValueUtil.CorrectInt(y, 0, 256);
        entity.setSpawnPoint(new BlockPos(x, y, z), true);
    }

    @Override
    public void resetSpawnpoint() {
        entity.setSpawnPoint(null, false);
    }

    @Override
    public void removeAllItems(IItemStack item) {
        for (int i = 0; i < entity.inventory.getSizeInventory(); i++) {
            ItemStack is = entity.inventory.getStackInSlot(i);
            if (is != null && is.isItemEqual(item.getMCItemStack()))
                entity.inventory.setInventorySlotContents(i, ItemStack.EMPTY);
        }
    }

    @Override
    public boolean hasAchievement(String achievement) {
        StatBase statbase = StatList.getOneShotStat(achievement);
//        if(statbase == null || !(statbase instanceof Achievement)){
//        	return false;
//        }
//		return entity.getStatFile().hasAchievementUnlocked((Achievement) statbase);
        return false;
    }

    @Override
    public int getExpLevel() {
        return entity.experienceLevel;
    }

    @Override
    public void setExpLevel(int level) {
        entity.experienceLevel = level;
        entity.addExperienceLevel(0);
    }

    @Override
    public void setPosition(double x, double y, double z) {
        NoppesUtilPlayer.teleportPlayer(entity, x, y, z, entity.dimension);
    }

    @Override
    public void setPos(IPos pos) {
        NoppesUtilPlayer.teleportPlayer(entity, pos.getX(), pos.getY(), pos.getZ(), entity.dimension);
    }

    @Override
    public int getType() {
        return EntityType.PLAYER;
    }

    @Override
    public boolean typeOf(int type) {
        return type == EntityType.PLAYER || super.typeOf(type);
    }

    @Override
    public boolean hasPermission(String permission) {
        return CustomNpcsPermissions.hasPermissionString(entity, permission);
    }

    @Override
    public IPixelmonPlayerData getPixelmonData() {
        if (PixelmonHelper.Enabled) {
            throw new CustomNPCsException("Pixelmon isnt installed");
        }

        return new IPixelmonPlayerData() {

            @Override
            public Object getParty() {
                if (pixelmonPartyStorage == null) {
                    pixelmonPartyStorage = PixelmonHelper.getParty(entity.getUniqueID());
                }
                return pixelmonPartyStorage;
            }

            @Override
            public Object getPC() {
                if (pixelmonPCStorage == null) {
                    pixelmonPCStorage = PixelmonHelper.getPc(entity.getUniqueID());
                }
                return pixelmonPCStorage;
            }

        };
    }

    private PlayerData data;

    private PlayerData getData() {
        if (data == null) {
            data = PlayerData.get(entity);
        }
        return data;
    }

    @Override
    public ITimers getTimers() {
        return getData().timers;
    }

    @Override
    public void removeDialog(int id) {
        PlayerData data = getData();
        data.dialogData.dialogsRead.remove(id);
        data.updateClient = true;
    }

    @Override
    public void addDialog(int id) {
        PlayerData data = getData();
        data.dialogData.dialogsRead.add(id);
        data.updateClient = true;
    }

    @Override
    public void closeGui() {
        Server.sendData(this.entity, EnumPacketClient.GUI_CLOSE, -1, new NBTTagCompound());
    }

    @Override
    public int factionStatus(int factionId) {
        Faction faction = FactionController.instance.getFaction(factionId);
        if (faction == null)
            throw new CustomNPCsException("Unknown faction: " + factionId);
        return faction.playerStatus(this);
    }

    @Override
    public void kick(String message) {
        entity.connection.disconnect(new TextComponentTranslation(message));
    }

    @Override
    public void clearData() {
        PlayerData data = getData();
        data.setNBT(new NBTTagCompound());
        data.save(true);
    }

	/*@Override
	public IContainer showChestGui(int rows) {
		entity.openGui(CustomNpcs.instance, EnumGuiType.CustomChest.ordinal(), entity.world, rows, 0, 0);
		return NpcAPI.Instance().getIContainer(entity.openContainer);
	}

	@Override
	public IContainer getOpenContainer() {
		return NpcAPI.Instance().getIContainer(entity.openContainer);
	}*/
}

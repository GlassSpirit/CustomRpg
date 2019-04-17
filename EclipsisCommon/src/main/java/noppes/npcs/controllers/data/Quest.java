package noppes.npcs.controllers.data;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.ICompatibilty;
import noppes.npcs.NpcMiscInventory;
import noppes.npcs.Server;
import noppes.npcs.VersionCompatibility;
import noppes.npcs.api.CustomNPCsException;
import noppes.npcs.api.constants.QuestType;
import noppes.npcs.api.entity.IPlayer;
import noppes.npcs.api.handler.data.IQuest;
import noppes.npcs.api.handler.data.IQuestCategory;
import noppes.npcs.api.handler.data.IQuestObjective;
import noppes.npcs.constants.EnumPacketClient;
import noppes.npcs.constants.EnumQuestCompletion;
import noppes.npcs.constants.EnumQuestRepeat;
import noppes.npcs.controllers.QuestController;
import noppes.npcs.quests.*;

public class Quest implements ICompatibilty, IQuest {
    public int version = VersionCompatibility.ModRev;
    public int id = -1;
    public int type = QuestType.ITEM;
    public EnumQuestRepeat repeat = EnumQuestRepeat.NONE;
    public EnumQuestCompletion completion = EnumQuestCompletion.Npc;
    public String title = "default";
    public final QuestCategory category;
    public String logText = "";
    public String completeText = "";
    public String completerNpc = "";
    public int nextQuestid = -1;
    public String nextQuestTitle = "";
    public PlayerMail mail = new PlayerMail();
    public String command = "";

    public QuestInterface questInterface = new QuestItem();

    public int rewardExp = 0;
    public NpcMiscInventory rewardItems = new NpcMiscInventory(9);
    public boolean randomReward = false;
    public FactionOptions factionOptions = new FactionOptions();

    public Quest(QuestCategory category) {
        this.category = category;
    }

    public void readNBT(NBTTagCompound compound) {
        id = compound.getInteger("Id");
        readNBTPartial(compound);
    }

    public void readNBTPartial(NBTTagCompound compound) {
        version = compound.getInteger("ModRev");
        VersionCompatibility.CheckAvailabilityCompatibility(this, compound);

        setType(compound.getInteger("Type"));
        title = compound.getString("Title");
        logText = compound.getString("Text");
        completeText = compound.getString("CompleteText");
        completerNpc = compound.getString("CompleterNpc");
        command = compound.getString("QuestCommand");
        nextQuestid = compound.getInteger("NextQuestId");
        nextQuestTitle = compound.getString("NextQuestTitle");
        if (hasNewQuest())
            nextQuestTitle = getNextQuest().title;
        else
            nextQuestTitle = "";
        randomReward = compound.getBoolean("RandomReward");
        rewardExp = compound.getInteger("RewardExp");
        rewardItems.setFromNBT(compound.getCompoundTag("Rewards"));

        completion = EnumQuestCompletion.values()[compound.getInteger("QuestCompletion")];
        repeat = EnumQuestRepeat.values()[compound.getInteger("QuestRepeat")];

        questInterface.readEntityFromNBT(compound);

        factionOptions.readFromNBT(compound.getCompoundTag("QuestFactionPoints"));

        mail.readNBT(compound.getCompoundTag("QuestMail"));
    }

    @Override
    public void setType(int questType) {
        type = questType;
        if (type == QuestType.ITEM)
            questInterface = new QuestItem();
        else if (type == QuestType.DIALOG)
            questInterface = new QuestDialog();
        else if (type == QuestType.KILL || type == QuestType.AREA_KILL)
            questInterface = new QuestKill();
        else if (type == QuestType.LOCATION)
            questInterface = new QuestLocation();
        else if (type == QuestType.MANUAL)
            questInterface = new QuestManual();

        if (questInterface != null)
            questInterface.questId = id;
    }

    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setInteger("Id", id);
        return writeToNBTPartial(compound);
    }

    public NBTTagCompound writeToNBTPartial(NBTTagCompound compound) {
        compound.setInteger("ModRev", version);
        compound.setInteger("Type", type);
        compound.setString("Title", title);
        compound.setString("Text", logText);
        compound.setString("CompleteText", completeText);
        compound.setString("CompleterNpc", completerNpc);
        compound.setInteger("NextQuestId", nextQuestid);
        compound.setString("NextQuestTitle", nextQuestTitle);
        compound.setInteger("RewardExp", rewardExp);
        compound.setTag("Rewards", rewardItems.getToNBT());
        compound.setString("QuestCommand", command);
        compound.setBoolean("RandomReward", randomReward);

        compound.setInteger("QuestCompletion", completion.ordinal());
        compound.setInteger("QuestRepeat", repeat.ordinal());

        this.questInterface.writeEntityToNBT(compound);
        compound.setTag("QuestFactionPoints", factionOptions.writeToNBT(new NBTTagCompound()));
        compound.setTag("QuestMail", mail.writeNBT());

        return compound;
    }

    public boolean hasNewQuest() {
        return getNextQuest() != null;
    }

    public Quest getNextQuest() {
        return QuestController.instance == null ? null : QuestController.instance.quests.get(nextQuestid);
    }

    public boolean complete(EntityPlayer player, QuestData data) {
        if (completion == EnumQuestCompletion.Instant) {
            Server.sendData((EntityPlayerMP) player, EnumPacketClient.QUEST_COMPLETION, data.quest.id);
            return true;
        }
        return false;
    }

    public Quest copy() {
        Quest quest = new Quest(category);
        quest.readNBT(this.writeToNBT(new NBTTagCompound()));
        return quest;
    }

    @Override
    public int getVersion() {
        return version;
    }

    @Override
    public void setVersion(int version) {
        this.version = version;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getName() {
        return title;
    }

    @Override
    public int getType() {
        return type;
    }

    @Override
    public IQuestCategory getCategory() {
        return category;
    }

    @Override
    public void save() {
        QuestController.instance.saveQuest(category, this);
    }

    @Override
    public void setName(String name) {
        this.title = name;
    }

    @Override
    public String getLogText() {
        return logText;
    }

    @Override
    public void setLogText(String text) {
        this.logText = text;
    }

    @Override
    public String getCompleteText() {
        return completeText;
    }

    @Override
    public void setCompleteText(String text) {
        this.completeText = text;
    }

    @Override
    public void setNextQuest(IQuest quest) {
        if (quest == null) {
            nextQuestid = -1;
            nextQuestTitle = "";
        } else {
            if (quest.getId() < 0)
                throw new CustomNPCsException("Quest id is lower than 0");
            nextQuestid = quest.getId();
            nextQuestTitle = quest.getName();
        }
    }

    @Override
    public String getNpcName() {
        return this.completerNpc;
    }

    @Override
    public void setNpcName(String name) {
        this.completerNpc = name;
    }

    @Override
    public IQuestObjective[] getObjectives(IPlayer player) {
        if (!player.hasActiveQuest(id)) {
            throw new CustomNPCsException("Player doesnt have this quest active.");
        }
        return questInterface.getObjectives(player.getMCEntity());
    }
}

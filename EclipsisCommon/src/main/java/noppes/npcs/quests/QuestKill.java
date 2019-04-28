package noppes.npcs.quests;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.NBTTags;
import noppes.npcs.api.CustomNPCsException;
import noppes.npcs.api.constants.QuestType;
import noppes.npcs.api.handler.data.IQuestObjective;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.controllers.data.PlayerQuestData;
import noppes.npcs.controllers.data.QuestData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

public class QuestKill extends QuestInterface {
    public TreeMap<String, Integer> targets = new TreeMap<>();

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        targets = new TreeMap(NBTTags.getStringIntegerMap(compound.getTagList("QuestDialogs", 10)));
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        compound.setTag("QuestDialogs", NBTTags.nbtStringIntegerMap(targets));
    }

    @Override
    public boolean isCompleted(EntityPlayer player) {
        PlayerQuestData playerdata = PlayerData.get(player).questData;
        QuestData data = playerdata.activeQuests.get(questId);
        if (data == null)
            return false;
        HashMap<String, Integer> killed = getKilled(data);
        if (killed.size() != targets.size())
            return false;
        for (String entity : killed.keySet()) {
            if (!targets.containsKey(entity) || targets.get(entity) > killed.get(entity))
                return false;
        }

        return true;
    }

    @Override
    public void handleComplete(EntityPlayer player) {
    }

    public HashMap<String, Integer> getKilled(QuestData data) {
        return NBTTags.getStringIntegerMap(data.extraData.getTagList("Killed", 10));
    }

    public void setKilled(QuestData data, HashMap<String, Integer> killed) {
        data.extraData.setTag("Killed", NBTTags.nbtStringIntegerMap(killed));
    }

    @Override
    public IQuestObjective[] getObjectives(EntityPlayer player) {
        List<IQuestObjective> list = new ArrayList<>();
        for (Entry<String, Integer> entry : targets.entrySet()) {
            list.add(new QuestKillObjective(player, entry.getKey(), entry.getValue()));
        }
        return list.toArray(new IQuestObjective[list.size()]);
    }

    class QuestKillObjective implements IQuestObjective {
        private final EntityPlayer player;
        private final String entity;
        private final int amount;

        public QuestKillObjective(EntityPlayer player, String entity, int amount) {
            this.player = player;
            this.entity = entity;
            this.amount = amount;
        }

        @Override
        public int getProgress() {
            PlayerData data = PlayerData.get(player);
            PlayerQuestData playerdata = data.questData;
            QuestData questdata = playerdata.activeQuests.get(questId);
            HashMap<String, Integer> killed = getKilled(questdata);
            if (!killed.containsKey(entity))
                return 0;
            return killed.get(entity);
        }

        @Override
        public void setProgress(int progress) {
            if (progress < 0 || progress > amount) {
                throw new CustomNPCsException("Progress has to be between 0 and " + amount);
            }
            PlayerData data = PlayerData.get(player);
            PlayerQuestData playerdata = data.questData;
            QuestData questdata = playerdata.activeQuests.get(questId);
            HashMap<String, Integer> killed = getKilled(questdata);

            if (killed.containsKey(entity) && killed.get(entity) == progress) {
                return;
            }
            killed.put(entity, progress);
            setKilled(questdata, killed);
            data.questData.checkQuestCompletion(player, QuestType.KILL);
            data.questData.checkQuestCompletion(player, QuestType.AREA_KILL);
            data.updateClient = true;
        }

        @Override
        public int getMaxProgress() {
            return amount;
        }

        @Override
        public boolean isCompleted() {
            return getProgress() >= amount;
        }

        @Override
        public String getText() {
            String name = "entity." + entity + ".name";
            String transName = I18n.format(name);
            if (name.equals(transName)) {
                transName = entity;
            }
            return transName + ": " + getProgress() + "/" + getMaxProgress();
        }
    }

}

package noppes.npcs.quests;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.translation.I18n;
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

public class QuestManual extends QuestInterface {
    public TreeMap<String, Integer> manuals = new TreeMap<String, Integer>();

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        manuals = new TreeMap(NBTTags.getStringIntegerMap(compound.getTagList("QuestManual", 10)));
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        compound.setTag("QuestManual", NBTTags.nbtStringIntegerMap(manuals));
    }

    @Override
    public boolean isCompleted(EntityPlayer player) {
        PlayerQuestData playerdata = PlayerData.get(player).questData;
        QuestData data = playerdata.activeQuests.get(questId);
        if (data == null)
            return false;
        HashMap<String, Integer> manual = getManual(data);
        if (manual.size() != manuals.size())
            return false;
        for (String entity : manual.keySet()) {
            if (!manuals.containsKey(entity) || manuals.get(entity) > manual.get(entity))
                return false;
        }

        return true;
    }

    @Override
    public void handleComplete(EntityPlayer player) {
    }

    public HashMap<String, Integer> getManual(QuestData data) {
        return NBTTags.getStringIntegerMap(data.extraData.getTagList("Manual", 10));
    }

    public void setManual(QuestData data, HashMap<String, Integer> manual) {
        data.extraData.setTag("Manual", NBTTags.nbtStringIntegerMap(manual));
    }

    @Override
    public IQuestObjective[] getObjectives(EntityPlayer player) {
        List<IQuestObjective> list = new ArrayList<IQuestObjective>();
        for (Entry<String, Integer> entry : manuals.entrySet()) {
            list.add(new QuestManualObjective(player, entry.getKey(), entry.getValue()));
        }
        return list.toArray(new IQuestObjective[list.size()]);
    }

    class QuestManualObjective implements IQuestObjective {
        private final EntityPlayer player;
        private final String entity;
        private final int amount;

        public QuestManualObjective(EntityPlayer player, String entity, int amount) {
            this.player = player;
            this.entity = entity;
            this.amount = amount;
        }

        @Override
        public int getProgress() {
            PlayerData data = PlayerData.get(player);
            PlayerQuestData playerdata = data.questData;
            QuestData questdata = playerdata.activeQuests.get(questId);
            HashMap<String, Integer> manual = getManual(questdata);
            if (!manual.containsKey(entity))
                return 0;
            return manual.get(entity);
        }

        @Override
        public void setProgress(int progress) {
            if (progress < 0 || progress > amount) {
                throw new CustomNPCsException("Progress has to be between 0 and " + amount);
            }
            PlayerData data = PlayerData.get(player);
            PlayerQuestData playerdata = data.questData;
            QuestData questdata = playerdata.activeQuests.get(questId);
            HashMap<String, Integer> manual = getManual(questdata);

            if (manual.containsKey(entity) && manual.get(entity) == progress) {
                return;
            }
            manual.put(entity, progress);
            setManual(questdata, manual);
            data.questData.checkQuestCompletion(player, QuestType.MANUAL);
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
            return I18n.translateToLocal(entity) + ": " + getProgress() + "/" + getMaxProgress();
        }
    }

}

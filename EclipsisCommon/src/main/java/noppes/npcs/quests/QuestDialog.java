package noppes.npcs.quests;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.api.CustomNPCsException;
import noppes.npcs.api.constants.QuestType;
import noppes.npcs.api.handler.data.IQuestObjective;
import noppes.npcs.controllers.DialogController;
import noppes.npcs.controllers.data.Dialog;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.util.NBTTags;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuestDialog extends QuestInterface {

    public Map<Integer, Integer> dialogs = new HashMap<>();

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        dialogs = NBTTags.getIntegerIntegerMap(compound.getTagList("QuestDialogs", 10));
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        compound.setTag("QuestDialogs", NBTTags.nbtIntegerIntegerMap(dialogs));
    }

    @Override
    public boolean isCompleted(EntityPlayer player) {
        for (int dialogId : dialogs.values())
            if (!PlayerData.get(player).dialogData.dialogsRead.contains(dialogId))
                return false;
        return true;
    }

    @Override
    public void handleComplete(EntityPlayer player) {

    }

    @Override
    public IQuestObjective[] getObjectives(EntityPlayer player) {
        List<IQuestObjective> list = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            if (dialogs.containsKey(i)) {
                Dialog dialog = DialogController.instance.dialogs.get(dialogs.get(i));
                if (dialog != null) {
                    list.add(new QuestDialogObjective(player, dialog));
                }
            }
        }
        return list.toArray(new IQuestObjective[list.size()]);
    }

    class QuestDialogObjective implements IQuestObjective {
        private final EntityPlayer player;
        private final Dialog dialog;

        public QuestDialogObjective(EntityPlayer player, Dialog dialog) {
            this.player = player;
            this.dialog = dialog;
        }

        @Override
        public int getProgress() {
            return isCompleted() ? 1 : 0;
        }

        @Override
        public void setProgress(int progress) {
            if (progress < 0 || progress > 1) {
                throw new CustomNPCsException("Progress has to be 0 or 1");
            }
            PlayerData data = PlayerData.get(player);
            boolean completed = data.dialogData.dialogsRead.contains(dialog.id);
            if (progress == 0 && completed) {
                data.dialogData.dialogsRead.remove(dialog.id);
                data.questData.checkQuestCompletion(player, QuestType.DIALOG);
                data.updateClient = true;
            }
            if (progress == 1 && !completed) {
                data.dialogData.dialogsRead.add(dialog.id);
                data.questData.checkQuestCompletion(player, QuestType.DIALOG);
                data.updateClient = true;
            }
        }

        @Override
        public int getMaxProgress() {
            return 1;
        }

        @Override
        public boolean isCompleted() {
            PlayerData data = PlayerData.get(player);
            return data.dialogData.dialogsRead.contains(dialog.id);
        }

        @Override
        public String getText() {
            return dialog.title + (isCompleted() ? " (read)" : " (unread)");
        }
    }
}

package noppes.npcs.roles;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.NBTTags;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.api.CustomNPCsException;
import noppes.npcs.api.constants.OptionType;
import noppes.npcs.api.entity.data.role.IRoleDialog;
import noppes.npcs.controllers.PlayerQuestController;
import noppes.npcs.controllers.QuestController;
import noppes.npcs.controllers.data.Dialog;
import noppes.npcs.controllers.data.DialogOption;
import noppes.npcs.controllers.data.Quest;
import noppes.npcs.entity.EntityNPCInterface;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class RoleDialog extends RoleInterface implements IRoleDialog {

    public String dialog = "";
    public int questId = -1;

    public Map<Integer, String> options = new HashMap<>();
    public Map<Integer, String> optionsTexts = new HashMap<>();

    public RoleDialog(EntityNPCInterface npc) {
        super(npc);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setInteger("RoleQuestId", questId);
        compound.setString("RoleDialog", dialog);
        compound.setTag("RoleOptions", NBTTags.nbtIntegerStringMap(options));
        compound.setTag("RoleOptionTexts", NBTTags.nbtIntegerStringMap(optionsTexts));
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        questId = compound.getInteger("RoleQuestId");
        dialog = compound.getString("RoleDialog");
        options = NBTTags.getIntegerStringMap(compound.getTagList("RoleOptions", 10));
        optionsTexts = NBTTags.getIntegerStringMap(compound.getTagList("RoleOptionTexts", 10));
    }

    @Override
    public void interact(EntityPlayer player) {
        if (dialog.isEmpty())
            npc.say(player, npc.advanced.getInteractLine());
        else {
            Dialog d = new Dialog(null);
            d.text = dialog;
            for (Entry<Integer, String> entry : options.entrySet()) {
                if (entry.getValue().isEmpty())
                    continue;
                DialogOption option = new DialogOption();
                String text = optionsTexts.get(entry.getKey());
                if (text != null && !text.isEmpty())
                    option.optionType = OptionType.ROLE_OPTION;
                else
                    option.optionType = OptionType.QUIT_OPTION;

                option.title = entry.getValue();
                d.options.put(entry.getKey(), option);
            }
            NoppesUtilServer.openDialog(player, npc, d);
        }

        Quest quest = QuestController.instance.quests.get(questId);
        if (quest != null)
            PlayerQuestController.addActiveQuest(quest, player);
    }

    @Override
    public String getDialog() {
        return dialog;
    }

    @Override
    public void setDialog(String text) {
        dialog = text;
    }

    @Override
    public String getOption(int option) {
        return options.get(option);
    }

    @Override
    public void setOption(int option, String text) {
        if (option < 1 || option > 6)
            throw new CustomNPCsException("Wrong dialog option slot given: " + option);
        options.put(option, text);
    }

    @Override
    public String getOptionDialog(int option) {
        return optionsTexts.get(option);
    }

    @Override
    public void setOptionDialog(int option, String text) {
        if (option < 1 || option > 6)
            throw new CustomNPCsException("Wrong dialog option slot given: " + option);
        optionsTexts.put(option, text);
    }
}

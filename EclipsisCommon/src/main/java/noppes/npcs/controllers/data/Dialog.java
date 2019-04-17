package noppes.npcs.controllers.data;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import noppes.npcs.ICompatibilty;
import noppes.npcs.VersionCompatibility;
import noppes.npcs.api.CustomNPCsException;
import noppes.npcs.api.constants.OptionType;
import noppes.npcs.api.handler.data.*;
import noppes.npcs.controllers.DialogController;
import noppes.npcs.controllers.QuestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Dialog implements ICompatibilty, IDialog {
    public int version = VersionCompatibility.ModRev;
    public int id = -1;
    public String title = "";
    public String text = "";
    public int quest = -1;
    public final DialogCategory category;
    public HashMap<Integer, DialogOption> options = new HashMap<Integer, DialogOption>();
    public Availability availability = new Availability();
    public FactionOptions factionOptions = new FactionOptions();
    public String sound;
    public String command = "";
    public PlayerMail mail = new PlayerMail();

    public boolean hideNPC = false;
    public boolean showWheel = false;
    public boolean disableEsc = false;

    public Dialog(DialogCategory category) {
        this.category = category;
    }

    public boolean hasDialogs(EntityPlayer player) {
        for (DialogOption option : options.values())
            if (option != null && option.optionType == OptionType.DIALOG_OPTION && option.hasDialog() && option.isAvailable(player))
                return true;
        return false;
    }

    public void readNBT(NBTTagCompound compound) {
        id = compound.getInteger("DialogId");
        readNBTPartial(compound);
    }

    public void readNBTPartial(NBTTagCompound compound) {
        version = compound.getInteger("ModRev");
        VersionCompatibility.CheckAvailabilityCompatibility(this, compound);

        title = compound.getString("DialogTitle");
        text = compound.getString("DialogText");
        quest = compound.getInteger("DialogQuest");
        sound = compound.getString("DialogSound");
        command = compound.getString("DialogCommand");
        mail.readNBT(compound.getCompoundTag("DialogMail"));

        hideNPC = compound.getBoolean("DialogHideNPC");
        showWheel = compound.getBoolean("DialogShowWheel");
        disableEsc = compound.getBoolean("DialogDisableEsc");

        NBTTagList options = compound.getTagList("Options", 10);
        HashMap<Integer, DialogOption> newoptions = new HashMap<Integer, DialogOption>();
        for (int iii = 0; iii < options.tagCount(); iii++) {
            NBTTagCompound option = options.getCompoundTagAt(iii);
            int opslot = option.getInteger("OptionSlot");
            DialogOption dia = new DialogOption();
            dia.readNBT(option.getCompoundTag("Option"));
            newoptions.put(opslot, dia);
            dia.slot = opslot;
        }
        this.options = newoptions;

        availability.readFromNBT(compound);
        factionOptions.readFromNBT(compound);
    }


    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setInteger("DialogId", id);
        return writeToNBTPartial(compound);
    }

    public NBTTagCompound writeToNBTPartial(NBTTagCompound compound) {
        compound.setString("DialogTitle", title);
        compound.setString("DialogText", text);
        compound.setInteger("DialogQuest", quest);
        compound.setString("DialogCommand", command);
        compound.setTag("DialogMail", mail.writeNBT());
        compound.setBoolean("DialogHideNPC", hideNPC);
        compound.setBoolean("DialogShowWheel", showWheel);
        compound.setBoolean("DialogDisableEsc", disableEsc);

        if (sound != null && !sound.isEmpty())
            compound.setString("DialogSound", sound);

        NBTTagList options = new NBTTagList();
        for (int opslot : this.options.keySet()) {
            NBTTagCompound listcompound = new NBTTagCompound();
            listcompound.setInteger("OptionSlot", opslot);
            listcompound.setTag("Option", this.options.get(opslot).writeNBT());
            options.appendTag(listcompound);
        }
        compound.setTag("Options", options);

        availability.writeToNBT(compound);
        factionOptions.writeToNBT(compound);
        compound.setInteger("ModRev", version);
        return compound;
    }

    public boolean hasQuest() {
        return getQuest() != null;
    }

    public Quest getQuest() {
        if (QuestController.instance == null)
            return null;
        return QuestController.instance.quests.get(quest);
    }

    public boolean hasOtherOptions() {
        for (DialogOption option : options.values())
            if (option != null && option.optionType != OptionType.DISABLED)
                return true;
        return false;
    }

    public Dialog copy(EntityPlayer player) {
        Dialog dialog = new Dialog(category);
        dialog.id = id;
        dialog.text = text;
        dialog.title = title;
        dialog.quest = quest;
        dialog.sound = sound;
        dialog.mail = mail;
        dialog.command = command;
        dialog.hideNPC = hideNPC;
        dialog.showWheel = showWheel;
        dialog.disableEsc = disableEsc;

        for (int slot : options.keySet()) {
            DialogOption option = options.get(slot);
            if (option.optionType == OptionType.DIALOG_OPTION && (!option.hasDialog() || !option.isAvailable(player)))
                continue;
            dialog.options.put(slot, option);
        }
        return dialog;
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
    public List<IDialogOption> getOptions() {
        return new ArrayList<IDialogOption>(options.values());
    }

    @Override
    public IDialogOption getOption(int slot) {
        IDialogOption option = options.get(slot);
        if (option == null)
            throw new CustomNPCsException("There is no DialogOption for slot: " + slot);
        return option;
    }

    @Override
    public IAvailability getAvailability() {
        return availability;
    }

    @Override
    public IDialogCategory getCategory() {
        return category;
    }

    @Override
    public void save() {
        DialogController.instance.saveDialog(category, this);
    }

    @Override
    public void setName(String name) {
        this.title = name;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public void setText(String text) {
        this.text = text;
    }

    @Override
    public void setQuest(IQuest quest) {
        if (quest == null)
            this.quest = -1;
        else {
            if (quest.getId() < 0)
                throw new CustomNPCsException("Quest id is lower than 0");
            this.quest = quest.getId();
        }
    }

    @Override
    public String getCommand() {
        return command;
    }

    @Override
    public void setCommand(String command) {
        this.command = command;
    }

}

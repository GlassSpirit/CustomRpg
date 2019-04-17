package noppes.npcs.controllers.data;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.api.constants.OptionType;
import noppes.npcs.api.handler.data.IDialogOption;
import noppes.npcs.controllers.DialogController;

public class DialogOption implements IDialogOption {
    public int dialogId = -1;
    public String title = "Talk";
    public int optionType = OptionType.DIALOG_OPTION;
    public int optionColor = 0xe0e0e0;
    public String command = "";
    public int slot = -1;

    public void readNBT(NBTTagCompound compound) {
        if (compound == null)
            return;
        title = compound.getString("Title");
        dialogId = compound.getInteger("Dialog");
        optionColor = compound.getInteger("DialogColor");
        optionType = compound.getInteger("OptionType");
        command = compound.getString("DialogCommand");
        if (optionColor == 0) {
            optionColor = 0xe0e0e0;
        }
    }

    public NBTTagCompound writeNBT() {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setString("Title", title);
        compound.setInteger("OptionType", optionType);
        compound.setInteger("Dialog", dialogId);
        compound.setInteger("DialogColor", optionColor);
        compound.setString("DialogCommand", command);
        return compound;
    }


    public boolean hasDialog() {
        if (dialogId <= 0 || optionType != OptionType.DIALOG_OPTION)
            return false;
        if (!DialogController.instance.hasDialog(dialogId)) {
            dialogId = -1;
            return false;
        }
        return true;
    }

    public Dialog getDialog() {
        if (!hasDialog())
            return null;
        return DialogController.instance.dialogs.get(dialogId);
    }

    public boolean isAvailable(EntityPlayer player) {
        if (optionType == OptionType.DISABLED)
            return false;
        if (optionType != OptionType.DIALOG_OPTION)
            return true;
        Dialog dialog = getDialog();
        if (dialog == null)
            return false;

        return dialog.availability.isAvailable(player);
    }

    @Override
    public int getSlot() {
        return slot;
    }

    @Override
    public String getName() {
        return title;
    }

    @Override
    public int getType() {
        return optionType;
    }
}

package noppes.npcs.controllers.data;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import noppes.npcs.api.handler.data.IDialog;
import noppes.npcs.api.handler.data.IDialogCategory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DialogCategory implements IDialogCategory {

    public int id = -1;
    public String title = "";
    public HashMap<Integer, Dialog> dialogs;

    public DialogCategory() {
        dialogs = new HashMap<Integer, Dialog>();
    }

    public void readNBT(NBTTagCompound compound) {
        id = compound.getInteger("Slot");
        title = compound.getString("Title");

        NBTTagList dialogsList = compound.getTagList("Dialogs", 10);
        if (dialogsList != null) {
            for (int ii = 0; ii < dialogsList.tagCount(); ii++) {
                Dialog dialog = new Dialog(this);
                NBTTagCompound comp = dialogsList.getCompoundTagAt(ii);
                dialog.readNBT(comp);
                dialog.id = comp.getInteger("DialogId");
                dialogs.put(dialog.id, dialog);
            }
        }
    }

    public NBTTagCompound writeNBT(NBTTagCompound nbtfactions) {
        nbtfactions.setInteger("Slot", id);
        nbtfactions.setString("Title", title);
        NBTTagList dialogs = new NBTTagList();
        for (Dialog dialog : this.dialogs.values()) {
            dialogs.appendTag(dialog.writeToNBT(new NBTTagCompound()));
        }
        nbtfactions.setTag("Dialogs", dialogs);
        return nbtfactions;
    }

    @Override
    public List<IDialog> dialogs() {
        return new ArrayList<IDialog>(dialogs.values());
    }

    @Override
    public String getName() {
        return title;
    }

    @Override
    public IDialog create() {
        return new Dialog(this);
    }
}

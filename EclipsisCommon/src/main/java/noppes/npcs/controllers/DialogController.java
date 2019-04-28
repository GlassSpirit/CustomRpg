package noppes.npcs.controllers;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import noppes.npcs.CustomNpcs;
import noppes.npcs.LogWriter;
import noppes.npcs.NoppesStringUtils;
import noppes.npcs.Server;
import noppes.npcs.api.constants.OptionType;
import noppes.npcs.api.handler.IDialogHandler;
import noppes.npcs.api.handler.data.IDialog;
import noppes.npcs.api.handler.data.IDialogCategory;
import noppes.npcs.constants.EnumPacketClient;
import noppes.npcs.constants.SyncType;
import noppes.npcs.controllers.data.Dialog;
import noppes.npcs.controllers.data.DialogCategory;
import noppes.npcs.controllers.data.DialogOption;
import noppes.npcs.util.NBTJsonUtil;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

public class DialogController implements IDialogHandler {
    public HashMap<Integer, DialogCategory> categoriesSync = new HashMap<>();
    public HashMap<Integer, DialogCategory> categories = new HashMap<>();
    public HashMap<Integer, Dialog> dialogs = new HashMap<>();
    public static DialogController instance = new DialogController();

    private int lastUsedDialogID = 0;
    private int lastUsedCatID = 0;

    public DialogController() {
        instance = this;
    }

    public void load() {
        LogWriter.info("Loading Dialogs");
        loadCategories();
        LogWriter.info("Done loading Dialogs");
    }

    private void loadCategories() {
        categories.clear();
        dialogs.clear();

        lastUsedCatID = 0;
        lastUsedDialogID = 0;

        try {
            File file = new File(CustomNpcs.INSTANCE.getWorldSaveDirectory(), "dialog.dat");
            if (file.exists()) {
                loadCategoriesOld(file);
                file.delete();
                file = new File(CustomNpcs.INSTANCE.getWorldSaveDirectory(), "dialog.dat_old");
                if (file.exists())
                    file.delete();
                return;
            }
        } catch (Exception e) {
            LogWriter.except(e);
        }

        File dir = getDir();
        if (!dir.exists()) {
            dir.mkdir();
            loadDefaultDialogs();
        } else {
            for (File file : dir.listFiles()) {
                if (!file.isDirectory())
                    continue;
                DialogCategory category = loadCategoryDir(file);
                Iterator<Entry<Integer, Dialog>> ite = category.dialogs.entrySet().iterator();
                while (ite.hasNext()) {
                    Entry<Integer, Dialog> entry = ite.next();
                    int id = entry.getKey();
                    if (id > lastUsedDialogID)
                        lastUsedDialogID = id;
                    Dialog dialog = entry.getValue();
                    if (dialogs.containsKey(id)) {
                        LogWriter.error("Duplicate id " + dialog.id + " from category " + category.title);
                        ite.remove();
                    } else {
                        dialogs.put(id, dialog);
                    }
                }
                lastUsedCatID++;
                category.id = lastUsedCatID;
                categories.put(category.id, category);
            }
        }
    }

    private DialogCategory loadCategoryDir(File dir) {
        DialogCategory category = new DialogCategory();
        category.title = dir.getName();
        for (File file : dir.listFiles()) {
            if (!file.isFile() || !file.getName().endsWith(".json"))
                continue;
            try {
                Dialog dialog = new Dialog(category);
                dialog.id = Integer.parseInt(file.getName().substring(0, file.getName().length() - 5));
                dialog.readNBTPartial(NBTJsonUtil.LoadFile(file));
                category.dialogs.put(dialog.id, dialog);
            } catch (Exception e) {
                LogWriter.error("Error loading: " + file.getAbsolutePath(), e);
            }
        }
        return category;
    }

    private void loadCategoriesOld(File file) throws Exception {
        NBTTagCompound nbttagcompound1 = CompressedStreamTools.readCompressed(new FileInputStream(file));
        NBTTagList list = nbttagcompound1.getTagList("Data", 10);
        if (list == null)
            return;

        for (int i = 0; i < list.tagCount(); i++) {
            DialogCategory category = new DialogCategory();
            category.readNBT(list.getCompoundTagAt(i));
            saveCategory(category);
            Iterator<Entry<Integer, Dialog>> ita = category.dialogs.entrySet().iterator();
            while (ita.hasNext()) {
                Entry<Integer, Dialog> entry = ita.next();
                Dialog dialog = entry.getValue();
                dialog.id = entry.getKey();
                if (dialogs.containsKey(dialog.id))
                    ita.remove();
                else {
                    saveDialog(category, dialog);
                }
            }

        }
    }

    private void loadDefaultDialogs() {
        DialogCategory cat = new DialogCategory();
        cat.id = lastUsedCatID++;
        cat.title = "Villager";

        Dialog dia1 = new Dialog(cat);
        dia1.id = 1;
        dia1.title = "Start";
        dia1.text = "Hello {player}, " + '\n' + '\n' + "Welcome to our village. I hope you enjoy your stay";

        Dialog dia2 = new Dialog(cat);
        dia2.id = 2;
        dia2.title = "Ask about village";
        dia2.text = "This village has been around for ages. Enjoy your stay here.";

        Dialog dia3 = new Dialog(cat);
        dia3.id = 3;
        dia3.title = "Who are you";
        dia3.text = "I'm a villager here. I have lived in this village my whole life.";

        cat.dialogs.put(dia1.id, dia1);
        cat.dialogs.put(dia2.id, dia2);
        cat.dialogs.put(dia3.id, dia3);


        DialogOption option = new DialogOption();
        option.title = "Tell me something about this village";
        option.dialogId = 2;
        option.optionType = OptionType.DIALOG_OPTION;

        DialogOption option2 = new DialogOption();
        option2.title = "Who are you?";
        option2.dialogId = 3;
        option2.optionType = OptionType.DIALOG_OPTION;

        DialogOption option3 = new DialogOption();
        option3.title = "Goodbye";
        option3.optionType = OptionType.QUIT_OPTION;

        dia1.options.put(0, option2);
        dia1.options.put(1, option);
        dia1.options.put(2, option3);


        DialogOption option4 = new DialogOption();
        option4.title = "Back";
        option4.dialogId = 1;

        dia2.options.put(1, option4);
        dia3.options.put(1, option4);

        lastUsedDialogID = 3;
        lastUsedCatID = 1;

        saveCategory(cat);
        saveDialog(cat, dia1);
        saveDialog(cat, dia2);
        saveDialog(cat, dia3);
    }

    public void saveCategory(DialogCategory category) {
        category.title = NoppesStringUtils.cleanFileName(category.title);
        if (categories.containsKey(category.id)) {
            DialogCategory currentCategory = categories.get(category.id);
            if (!currentCategory.title.equals(category.title)) {
                while (containsCategoryName(category))
                    category.title += "_";
                File newdir = new File(getDir(), category.title);
                File olddir = new File(getDir(), currentCategory.title);
                if (newdir.exists())
                    return;
                if (!olddir.renameTo(newdir))
                    return;
            }
            category.dialogs = currentCategory.dialogs;
        } else {
            if (category.id < 0) {
                lastUsedCatID++;
                category.id = lastUsedCatID;
            }
            while (containsCategoryName(category))
                category.title += "_";
            File dir = new File(getDir(), category.title);
            if (!dir.exists())
                dir.mkdirs();
        }
        categories.put(category.id, category);
        Server.sendToAll(CustomNpcs.Server, EnumPacketClient.SYNC_UPDATE, SyncType.DIALOG_CATEGORY, category.writeNBT(new NBTTagCompound()));
    }

    public void removeCategory(int category) {
        DialogCategory cat = categories.get(category);
        if (cat == null)
            return;
        File dir = new File(getDir(), cat.title);
        if (!dir.delete())
            return;
        for (int dia : cat.dialogs.keySet())
            dialogs.remove(dia);
        categories.remove(category);
        Server.sendToAll(CustomNpcs.Server, EnumPacketClient.SYNC_REMOVE, SyncType.DIALOG_CATEGORY, category);
    }

    public boolean containsCategoryName(DialogCategory category) {
        for (DialogCategory cat : categories.values()) {
            if (category.id != cat.id && cat.title.equalsIgnoreCase(category.title))
                return true;
        }
        return false;
    }

    public boolean containsDialogName(DialogCategory category, Dialog dialog) {
        for (Dialog dia : category.dialogs.values()) {
            if (dia.id != dialog.id && dia.title.equalsIgnoreCase(dialog.title))
                return true;
        }
        return false;
    }

    public Dialog saveDialog(DialogCategory category, Dialog dialog) {
        if (category == null)
            return dialog;

        while (containsDialogName(dialog.category, dialog)) {
            dialog.title = dialog.title + "_";
        }
        if (dialog.id < 0) {
            lastUsedDialogID++;
            dialog.id = lastUsedDialogID;
        }

        dialogs.put(dialog.id, dialog);
        category.dialogs.put(dialog.id, dialog);

        File dir = new File(getDir(), category.title);
        if (!dir.exists())
            dir.mkdirs();

        File file = new File(dir, dialog.id + ".json_new");
        File file2 = new File(dir, dialog.id + ".json");

        try {
            NBTTagCompound compound = dialog.writeToNBT(new NBTTagCompound());
            NBTJsonUtil.SaveFile(file, compound);
            if (file2.exists())
                file2.delete();
            file.renameTo(file2);
            Server.sendToAll(CustomNpcs.INSTANCE.getServer(), EnumPacketClient.SYNC_UPDATE, SyncType.DIALOG, compound, category.id);
        } catch (Exception e) {
            LogWriter.except(e);
        }
        return dialog;
    }

    public void removeDialog(Dialog dialog) {
        DialogCategory category = dialog.category;
        File file = new File(new File(getDir(), category.title), dialog.id + ".json");
        if (!file.delete())
            return;
        category.dialogs.remove(dialog.id);
        dialogs.remove(dialog.id);
        Server.sendToAll(CustomNpcs.INSTANCE.getServer(), EnumPacketClient.SYNC_REMOVE, SyncType.DIALOG, dialog.id);
    }

    private File getDir() {
        return new File(CustomNpcs.INSTANCE.getWorldSaveDirectory(), "dialogs");
    }

    public boolean hasDialog(int dialogId) {
        return dialogs.containsKey(dialogId);
    }

    @Override
    public List<IDialogCategory> categories() {
        return new ArrayList(categories.values());
    }

    @Override
    public IDialog get(int id) {
        return dialogs.get(id);
    }
}

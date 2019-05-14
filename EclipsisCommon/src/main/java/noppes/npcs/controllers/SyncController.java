package noppes.npcs.controllers;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import noppes.npcs.util.NBTTags;
import noppes.npcs.Server;
import noppes.npcs.common.CustomNpcs;
import noppes.npcs.constants.EnumPacketClient;
import noppes.npcs.constants.SyncType;
import noppes.npcs.controllers.data.*;
import noppes.npcs.common.objects.items.ItemScripted;

import java.util.HashMap;

public class SyncController {

    public static void syncPlayer(EntityPlayerMP player) {
        NBTTagList list = new NBTTagList();
        NBTTagCompound compound = new NBTTagCompound();

        for (Faction faction : FactionController.instance.factions.values()) {
            list.appendTag(faction.writeNBT(new NBTTagCompound()));
            if (list.tagCount() > 20) {
                compound = new NBTTagCompound();
                compound.setTag("Data", list);
                Server.sendData(player, EnumPacketClient.SYNC_ADD, SyncType.FACTION, compound);
                list = new NBTTagList();
            }
        }
        compound = new NBTTagCompound();
        compound.setTag("Data", list);
        Server.sendData(player, EnumPacketClient.SYNC_END, SyncType.FACTION, compound);

        for (QuestCategory category : QuestController.instance.categories.values()) {
            Server.sendData(player, EnumPacketClient.SYNC_ADD, SyncType.QUEST_CATEGORY, category.writeNBT(new NBTTagCompound()));
        }
        Server.sendData(player, EnumPacketClient.SYNC_END, SyncType.QUEST_CATEGORY, new NBTTagCompound());

        for (DialogCategory category : DialogController.instance.categories.values()) {
            Server.sendData(player, EnumPacketClient.SYNC_ADD, SyncType.DIALOG_CATEGORY, category.writeNBT(new NBTTagCompound()));
        }
        Server.sendData(player, EnumPacketClient.SYNC_END, SyncType.DIALOG_CATEGORY, new NBTTagCompound());

        list = new NBTTagList();
        for (RecipeCarpentry category : RecipeController.instance.globalRecipes.values()) {
            list.appendTag(category.writeNBT());
            if (list.tagCount() > 10) {
                compound = new NBTTagCompound();
                compound.setTag("Data", list);
                Server.sendData(player, EnumPacketClient.SYNC_ADD, SyncType.RECIPE_NORMAL, compound);
                list = new NBTTagList();
            }
        }
        compound = new NBTTagCompound();
        compound.setTag("Data", list);
        Server.sendData(player, EnumPacketClient.SYNC_END, SyncType.RECIPE_NORMAL, compound);

        list = new NBTTagList();
        for (RecipeCarpentry category : RecipeController.instance.anvilRecipes.values()) {
            list.appendTag(category.writeNBT());
            if (list.tagCount() > 10) {
                compound = new NBTTagCompound();
                compound.setTag("Data", list);
                Server.sendData(player, EnumPacketClient.SYNC_ADD, SyncType.RECIPE_CARPENTRY, compound);
                list = new NBTTagList();
            }
        }
        compound = new NBTTagCompound();
        compound.setTag("Data", list);
        Server.sendData(player, EnumPacketClient.SYNC_END, SyncType.RECIPE_CARPENTRY, compound);

        PlayerData data = PlayerData.get(player);
        Server.sendData(player, EnumPacketClient.SYNC_END, SyncType.PLAYER_DATA, data.getNBT());

        syncScriptItems(player);
    }

    public static void syncAllDialogs(MinecraftServer server) {
        for (DialogCategory category : DialogController.instance.categories.values()) {
            Server.sendToAll(server, EnumPacketClient.SYNC_ADD, SyncType.DIALOG_CATEGORY, category.writeNBT(new NBTTagCompound()));
        }
        Server.sendToAll(server, EnumPacketClient.SYNC_END, SyncType.DIALOG_CATEGORY, new NBTTagCompound());
    }

    public static void syncAllQuests(MinecraftServer server) {
        for (QuestCategory category : QuestController.instance.categories.values()) {
            Server.sendToAll(server, EnumPacketClient.SYNC_ADD, SyncType.QUEST_CATEGORY, category.writeNBT(new NBTTagCompound()));
        }
        Server.sendToAll(server, EnumPacketClient.SYNC_END, SyncType.QUEST_CATEGORY, new NBTTagCompound());
    }

    public static void syncScriptItems(EntityPlayerMP player) {
        NBTTagCompound comp = new NBTTagCompound();
        comp.setTag("List", NBTTags.nbtIntegerStringMap(ItemScripted.Companion.getResources()));
        Server.sendData(player, EnumPacketClient.SYNC_END, SyncType.SCRIPTED_ITEM_RESOURCES, comp);
    }

    public static void syncScriptItemsEverybody() {
        NBTTagCompound comp = new NBTTagCompound();
        comp.setTag("List", NBTTags.nbtIntegerStringMap(ItemScripted.Companion.getResources()));
        for (EntityPlayerMP player : CustomNpcs.INSTANCE.getServer().getPlayerList().getPlayers()) {
            Server.sendData(player, EnumPacketClient.SYNC_END, SyncType.SCRIPTED_ITEM_RESOURCES, comp);
        }
    }

    public static void clientSync(int synctype, NBTTagCompound compound, boolean syncEnd) {
        if (synctype == SyncType.FACTION) {
            NBTTagList list = compound.getTagList("Data", 10);
            for (int i = 0; i < list.tagCount(); i++) {
                Faction faction = new Faction();
                faction.readNBT(list.getCompoundTagAt(i));
                FactionController.instance.factionsSync.put(faction.id, faction);
            }
            if (syncEnd) {
                FactionController.instance.factions = FactionController.instance.factionsSync;
                FactionController.instance.factionsSync = new HashMap<>();
            }
        } else if (synctype == SyncType.QUEST_CATEGORY) {
            if (!compound.isEmpty()) {
                QuestCategory category = new QuestCategory();
                category.readNBT(compound);
                QuestController.instance.categoriesSync.put(category.id, category);
            }
            if (syncEnd) {
                HashMap<Integer, Quest> quests = new HashMap<>();
                for (QuestCategory category : QuestController.instance.categoriesSync.values()) {
                    for (Quest quest : category.quests.values()) {
                        quests.put(quest.id, quest);
                    }
                }
                QuestController.instance.categories = QuestController.instance.categoriesSync;
                QuestController.instance.quests = quests;
                QuestController.instance.categoriesSync = new HashMap<>();
            }
        } else if (synctype == SyncType.DIALOG_CATEGORY) {
            if (!compound.isEmpty()) {
                DialogCategory category = new DialogCategory();
                category.readNBT(compound);
                DialogController.instance.categoriesSync.put(category.id, category);
            }
            if (syncEnd) {
                HashMap<Integer, Dialog> dialogs = new HashMap<>();
                for (DialogCategory category : DialogController.instance.categoriesSync.values()) {
                    for (Dialog dialog : category.dialogs.values()) {
                        dialogs.put(dialog.id, dialog);
                    }
                }
                DialogController.instance.categories = DialogController.instance.categoriesSync;
                DialogController.instance.dialogs = dialogs;
                DialogController.instance.categoriesSync = new HashMap<>();
            }
        } else if (synctype == SyncType.RECIPE_NORMAL) {
            NBTTagList list = compound.getTagList("Data", 10);
            for (int i = 0; i < list.tagCount(); i++) {
                RecipeCarpentry recipe = RecipeCarpentry.read(list.getCompoundTagAt(i));
                RecipeController.syncRecipes.put(recipe.id, recipe);
            }
            if (syncEnd) {
                RecipeController.instance.globalRecipes = RecipeController.syncRecipes;
                RecipeController.instance.reloadGlobalRecipes();
                RecipeController.syncRecipes = new HashMap<>();
            }
        } else if (synctype == SyncType.RECIPE_CARPENTRY) {
            NBTTagList list = compound.getTagList("Data", 10);
            for (int i = 0; i < list.tagCount(); i++) {
                RecipeCarpentry recipe = RecipeCarpentry.read(list.getCompoundTagAt(i));
                RecipeController.syncRecipes.put(recipe.id, recipe);
            }
            if (syncEnd) {
                RecipeController.instance.anvilRecipes = RecipeController.syncRecipes;
                RecipeController.syncRecipes = new HashMap<>();
            }
        }
    }

    public static void clientSyncUpdate(int synctype, NBTTagCompound compound, ByteBuf buffer) {
        if (synctype == SyncType.FACTION) {
            Faction faction = new Faction();
            faction.readNBT(compound);
            FactionController.instance.factions.put(faction.id, faction);
        } else if (synctype == SyncType.DIALOG) {
            DialogCategory category = DialogController.instance.categories.get(buffer.readInt());
            Dialog dialog = new Dialog(category);
            dialog.readNBT(compound);
            DialogController.instance.dialogs.put(dialog.id, dialog);
            category.dialogs.put(dialog.id, dialog);
        } else if (synctype == SyncType.DIALOG_CATEGORY) {
            DialogCategory category = new DialogCategory();
            category.readNBT(compound);
            DialogController.instance.categories.put(category.id, category);
        } else if (synctype == SyncType.QUEST) {
            QuestCategory category = QuestController.instance.categories.get(buffer.readInt());
            Quest quest = new Quest(category);
            quest.readNBT(compound);
            QuestController.instance.quests.put(quest.id, quest);
            category.quests.put(quest.id, quest);
        } else if (synctype == SyncType.QUEST_CATEGORY) {
            QuestCategory category = new QuestCategory();
            category.readNBT(compound);
            QuestController.instance.categories.put(category.id, category);
        } else if (synctype == SyncType.RECIPE_NORMAL) {
            RecipeCarpentry recipe = RecipeCarpentry.read(compound);
            RecipeController.instance.globalRecipes.put(recipe.id, recipe);
            RecipeController.instance.reloadGlobalRecipes();
        } else if (synctype == SyncType.RECIPE_CARPENTRY) {
            RecipeCarpentry recipe = RecipeCarpentry.read(compound);
            RecipeController.instance.anvilRecipes.put(recipe.id, recipe);
        }
    }

    public static void clientSyncRemove(int synctype, int id) {
        if (synctype == SyncType.FACTION) {
            FactionController.instance.factions.remove(id);
        } else if (synctype == SyncType.DIALOG) {
            Dialog dialog = DialogController.instance.dialogs.remove(id);
            if (dialog != null) {
                dialog.category.dialogs.remove(id);
            }
        } else if (synctype == SyncType.DIALOG_CATEGORY) {
            DialogCategory category = DialogController.instance.categories.remove(id);
            if (category != null) {
                DialogController.instance.dialogs.keySet().removeAll(category.dialogs.keySet());
            }
        } else if (synctype == SyncType.QUEST) {
            Quest quest = QuestController.instance.quests.remove(id);
            if (quest != null) {
                quest.category.quests.remove(id);
            }
        } else if (synctype == SyncType.QUEST_CATEGORY) {
            QuestCategory category = QuestController.instance.categories.remove(id);
            if (category != null) {
                QuestController.instance.quests.keySet().removeAll(category.quests.keySet());
            }
        } else if (synctype == SyncType.RECIPE_NORMAL) {
            RecipeController.instance.globalRecipes.remove(id);
            RecipeController.instance.reloadGlobalRecipes();
        } else if (synctype == SyncType.RECIPE_CARPENTRY) {
            RecipeController.instance.anvilRecipes.remove(id);
        }
    }

}

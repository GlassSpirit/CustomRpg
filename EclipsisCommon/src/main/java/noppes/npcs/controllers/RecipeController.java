package noppes.npcs.controllers;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import net.minecraftforge.registries.IForgeRegistry;
import noppes.npcs.CustomNpcs;
import noppes.npcs.EventHooks;
import noppes.npcs.Server;
import noppes.npcs.api.handler.IRecipeHandler;
import noppes.npcs.api.handler.data.IRecipe;
import noppes.npcs.constants.EnumPacketClient;
import noppes.npcs.constants.SyncType;
import noppes.npcs.controllers.data.RecipeCarpentry;
import noppes.npcs.controllers.data.RecipesDefault;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RecipeController implements IRecipeHandler {
    public HashMap<Integer, RecipeCarpentry> globalRecipes = new HashMap<>();
    public HashMap<Integer, RecipeCarpentry> anvilRecipes = new HashMap<>();
    public static RecipeController instance;

    public static final int version = 1;
    public int nextId = 1;

    public static HashMap<Integer, RecipeCarpentry> syncRecipes = new HashMap<>();
    public static IForgeRegistry<net.minecraft.item.crafting.IRecipe> Registry;

    public RecipeController() {
        instance = this;
    }

    public void load() {
        loadCategories();
        reloadGlobalRecipes();
        EventHooks.onGlobalRecipesLoaded(this);
    }

    public void reloadGlobalRecipes() {

//        ForgeRegistry<net.minecraft.item.crafting.IRecipe> reg = (ForgeRegistry<net.minecraft.item.crafting.IRecipe>)ForgeRegistries.RECIPES;
//        reg.unfreeze();
//        reg.clear();
//        CraftingHelper.loadRecipes(false);
//		ForgeRegistry reg = (ForgeRegistry) Registry;
//		reg.unfreeze();
//		Iterator<Entry<ResourceLocation, net.minecraft.item.crafting.IRecipe>> list = reg.getEntries().iterator();
//		while(list.hasNext()){
//			Entry<ResourceLocation, net.minecraft.item.crafting.IRecipe> rec = list.next();
//			net.minecraft.item.crafting.IRecipe recipe = rec.getValue();
//			if(recipe instanceof RecipeCarpentry){
//				reg.remove(rec.getKey());
//				RegistryHandler.Remove(CraftingManager.REGISTRY, rec.getKey(), recipe);
//			}
//		}
//		for(RecipeCarpentry recipe : globalRecipes.values()){
//			if(recipe.isValid()){
//				if(recipe.getRegistryName() == null)
//					recipe.setRegistryName(new ResourceLocation("customnpcs", recipe.id + ""));
//				reg.register(recipe);
//			}
//		}
//		reg.freeze();
    }

    private void loadCategories() {
        File saveDir = CustomNpcs.INSTANCE.getWorldSaveDirectory();
        try {
            File file = new File(saveDir, "recipes.dat");
            if (file.exists()) {
                loadCategories(file);
            } else {
                globalRecipes.clear();
                anvilRecipes.clear();
                loadDefaultRecipes(-1);
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                File file = new File(saveDir, "recipes.dat_old");
                if (file.exists()) {
                    loadCategories(file);
                }
            } catch (Exception ee) {
                e.printStackTrace();
            }
        }
    }

    private void loadDefaultRecipes(int i) {
        if (i == version)
            return;
        RecipesDefault.loadDefaultRecipes(i);
        saveCategories();
    }

    private void loadCategories(File file) throws Exception {
        NBTTagCompound nbttagcompound1 = CompressedStreamTools.readCompressed(new FileInputStream(file));
        nextId = nbttagcompound1.getInteger("LastId");
        NBTTagList list = nbttagcompound1.getTagList("Data", 10);
        HashMap<Integer, RecipeCarpentry> globalRecipes = new HashMap<>();
        HashMap<Integer, RecipeCarpentry> anvilRecipes = new HashMap<>();
        if (list != null) {
            for (int i = 0; i < list.tagCount(); i++) {
                RecipeCarpentry recipe = RecipeCarpentry.read(list.getCompoundTagAt(i));
                if (recipe.isGlobal)
                    globalRecipes.put(recipe.id, recipe);
                else
                    anvilRecipes.put(recipe.id, recipe);
                if (recipe.id > nextId)
                    nextId = recipe.id;
            }
        }
        this.anvilRecipes = anvilRecipes;
        this.globalRecipes = globalRecipes;
        loadDefaultRecipes(nbttagcompound1.getInteger("Version"));
    }

    private void saveCategories() {
        try {
            File saveDir = CustomNpcs.INSTANCE.getWorldSaveDirectory();
            NBTTagList list = new NBTTagList();
            for (RecipeCarpentry recipe : globalRecipes.values()) {
                if (recipe.savesRecipe)
                    list.appendTag(recipe.writeNBT());
            }
            for (RecipeCarpentry recipe : anvilRecipes.values()) {
                if (recipe.savesRecipe)
                    list.appendTag(recipe.writeNBT());
            }
            NBTTagCompound nbttagcompound = new NBTTagCompound();
            nbttagcompound.setTag("Data", list);
            nbttagcompound.setInteger("LastId", nextId);
            nbttagcompound.setInteger("Version", version);
            File file = new File(saveDir, "recipes.dat_new");
            File file1 = new File(saveDir, "recipes.dat_old");
            File file2 = new File(saveDir, "recipes.dat");
            CompressedStreamTools.writeCompressed(nbttagcompound, new FileOutputStream(file));
            if (file1.exists()) {
                file1.delete();
            }
            file2.renameTo(file1);
            if (file2.exists()) {
                file2.delete();
            }
            file.renameTo(file2);
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public RecipeCarpentry findMatchingRecipe(InventoryCrafting inventoryCrafting) {
        for (RecipeCarpentry recipe : anvilRecipes.values()) {
            if (recipe.isValid() && recipe.matches(inventoryCrafting, null))
                return recipe;
        }
        return null;
    }

    public RecipeCarpentry getRecipe(int id) {
        if (globalRecipes.containsKey(id))
            return globalRecipes.get(id);
        if (anvilRecipes.containsKey(id))
            return anvilRecipes.get(id);
        return null;
    }

    public RecipeCarpentry saveRecipe(RecipeCarpentry recipe) throws IOException {
        RecipeCarpentry current = getRecipe(recipe.id);
        if (current != null && !current.name.equals(recipe.name)) {
            while (containsRecipeName(recipe.name))
                recipe.name += "_";
        }

        if (recipe.id == -1) {
            recipe.id = getUniqueId();
            while (containsRecipeName(recipe.name))
                recipe.name += "_";
        }
        if (recipe.isGlobal) {
            anvilRecipes.remove(recipe.id);
            globalRecipes.put(recipe.id, recipe);
            Server.sendToAll(CustomNpcs.INSTANCE.getServer(), EnumPacketClient.SYNC_UPDATE, SyncType.RECIPE_NORMAL, recipe.writeNBT());
        } else {
            globalRecipes.remove(recipe.id);
            anvilRecipes.put(recipe.id, recipe);
            Server.sendToAll(CustomNpcs.INSTANCE.getServer(), EnumPacketClient.SYNC_UPDATE, SyncType.RECIPE_CARPENTRY, recipe.writeNBT());
        }
        saveCategories();
        reloadGlobalRecipes();

        return recipe;
    }

    private int getUniqueId() {
        nextId++;
        return nextId;
    }

    private boolean containsRecipeName(String name) {
        name = name.toLowerCase();
        for (RecipeCarpentry recipe : globalRecipes.values()) {
            if (recipe.name.toLowerCase().equals(name))
                return true;
        }
        for (RecipeCarpentry recipe : anvilRecipes.values()) {
            if (recipe.name.toLowerCase().equals(name))
                return true;
        }
        return false;
    }

    @Override
    public RecipeCarpentry delete(int id) {
        RecipeCarpentry recipe = getRecipe(id);
        if (recipe == null)
            return null;
        globalRecipes.remove(recipe.id);
        anvilRecipes.remove(recipe.id);
        if (recipe.isGlobal)
            Server.sendToAll(CustomNpcs.INSTANCE.getServer(), EnumPacketClient.SYNC_REMOVE, SyncType.RECIPE_NORMAL, id);
        else
            Server.sendToAll(CustomNpcs.INSTANCE.getServer(), EnumPacketClient.SYNC_REMOVE, SyncType.RECIPE_CARPENTRY, id);
        saveCategories();
        reloadGlobalRecipes();
        recipe.id = -1;
        return recipe;
    }

    @Override
    public List<IRecipe> getGlobalList() {
        return new ArrayList<>(globalRecipes.values());
    }

    @Override
    public List<IRecipe> getCarpentryList() {
        return new ArrayList<>(anvilRecipes.values());
    }

    @Override
    public IRecipe addRecipe(String name, boolean global, ItemStack result, Object... objects) {
        RecipeCarpentry recipe = new RecipeCarpentry(name);
        recipe.isGlobal = global;
        recipe = RecipeCarpentry.createRecipe(recipe, result, objects);
        try {
            return saveRecipe(recipe);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return recipe;
    }

    @Override
    public IRecipe addRecipe(String name, boolean global, ItemStack result, int width, int height, ItemStack... objects) {
        NonNullList<Ingredient> list = NonNullList.create();
        for (ItemStack item : objects) {
            if (!item.isEmpty())
                list.add(Ingredient.fromStacks(item));
        }
        RecipeCarpentry recipe = new RecipeCarpentry(width, height, list, result);
        recipe.isGlobal = global;
        recipe.name = name;
        try {
            return saveRecipe(recipe);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return recipe;
    }
}

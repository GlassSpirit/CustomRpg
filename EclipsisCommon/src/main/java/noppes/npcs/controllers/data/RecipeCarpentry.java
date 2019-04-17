package noppes.npcs.controllers.data;

import net.minecraft.block.Block;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import noppes.npcs.NBTTags;
import noppes.npcs.NoppesUtilPlayer;
import noppes.npcs.api.handler.data.IRecipe;
import noppes.npcs.controllers.RecipeController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RecipeCarpentry extends ShapedRecipes implements IRecipe {
    public int id = -1;
    public String name = "";
    public Availability availability = new Availability();
    public boolean isGlobal = false;
    public boolean ignoreDamage = false;
    public boolean ignoreNBT = false;
    public boolean savesRecipe = true;

    public RecipeCarpentry(int width, int height, NonNullList<Ingredient> recipe, ItemStack result) {
        super("customnpcs", width, height, recipe, result);
    }

    public RecipeCarpentry(String name) {
        super("customnpcs", 0, 0, NonNullList.create(), ItemStack.EMPTY);
        this.name = name;
    }

    public static RecipeCarpentry read(NBTTagCompound compound) {
        RecipeCarpentry recipe = new RecipeCarpentry(compound.getInteger("Width"), compound.getInteger("Height"),
                NBTTags.getIngredientList(compound.getTagList("Materials", 10)), new ItemStack(compound.getCompoundTag("Item")));
        recipe.name = compound.getString("Name");
        recipe.id = compound.getInteger("ID");
        recipe.availability.readFromNBT(compound.getCompoundTag("Availability"));
        recipe.ignoreDamage = compound.getBoolean("IgnoreDamage");
        recipe.ignoreNBT = compound.getBoolean("IgnoreNBT");
        recipe.isGlobal = compound.getBoolean("Global");

        return recipe;
    }

    public NBTTagCompound writeNBT() {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setInteger("ID", id);
        compound.setInteger("Width", recipeWidth);
        compound.setInteger("Height", recipeHeight);
        if (getRecipeOutput() != null)
            compound.setTag("Item", getRecipeOutput().writeToNBT(new NBTTagCompound()));
        compound.setTag("Materials", NBTTags.nbtIngredientList(recipeItems));
        compound.setTag("Availability", availability.writeToNBT(new NBTTagCompound()));
        compound.setString("Name", name);
        compound.setBoolean("Global", isGlobal);
        compound.setBoolean("IgnoreDamage", ignoreDamage);
        compound.setBoolean("IgnoreNBT", ignoreNBT);
        return compound;
    }

    public static RecipeCarpentry createRecipe(RecipeCarpentry recipe, ItemStack par1ItemStack, Object... par2ArrayOfObj) {
        String var3 = "";
        int var4 = 0;
        int var5 = 0;
        int var6 = 0;
        int var9;

        if (par2ArrayOfObj[var4] instanceof String[]) {
            String[] var7 = (String[]) par2ArrayOfObj[var4++];
            String[] var8 = var7;
            var9 = var7.length;

            for (int var10 = 0; var10 < var9; ++var10) {
                String var11 = var8[var10];
                ++var6;
                var5 = var11.length();
                var3 = var3 + var11;
            }
        } else {
            while (par2ArrayOfObj[var4] instanceof String) {
                String var13 = (String) par2ArrayOfObj[var4++];
                ++var6;
                var5 = var13.length();
                var3 = var3 + var13;
            }
        }

        HashMap var14;

        for (var14 = new HashMap(); var4 < par2ArrayOfObj.length; var4 += 2) {
            Character var16 = (Character) par2ArrayOfObj[var4];
            ItemStack var17 = ItemStack.EMPTY;

            if (par2ArrayOfObj[var4 + 1] instanceof Item) {
                var17 = new ItemStack((Item) par2ArrayOfObj[var4 + 1]);
            } else if (par2ArrayOfObj[var4 + 1] instanceof Block) {
                var17 = new ItemStack((Block) par2ArrayOfObj[var4 + 1], 1, -1);
            } else if (par2ArrayOfObj[var4 + 1] instanceof ItemStack) {
                var17 = (ItemStack) par2ArrayOfObj[var4 + 1];
            }

            var14.put(var16, var17);
        }

        NonNullList<Ingredient> ingredients = NonNullList.create();

        for (var9 = 0; var9 < var5 * var6; ++var9) {
            char var18 = var3.charAt(var9);

            if (var14.containsKey(Character.valueOf(var18))) {
                ingredients.add(var9, Ingredient.fromStacks(((ItemStack) var14.get(Character.valueOf(var18))).copy()));
            } else {
                ingredients.add(var9, Ingredient.EMPTY);
            }
        }

        RecipeCarpentry newrecipe = new RecipeCarpentry(var5, var6, ingredients, par1ItemStack);
        newrecipe.copy(recipe);
        if (var5 == 4 || var6 == 4)
            newrecipe.isGlobal = false;

        return newrecipe;
    }

    @Override
    public boolean matches(InventoryCrafting inventoryCrafting, World world) {
        for (int i = 0; i <= 4 - this.recipeWidth; ++i) {
            for (int j = 0; j <= 4 - this.recipeHeight; ++j) {
                if (this.checkMatch(inventoryCrafting, i, j, true))
                    return true;

                if (this.checkMatch(inventoryCrafting, i, j, false))
                    return true;
            }
        }
        return false;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting var1) {
        if (getRecipeOutput().isEmpty())
            return ItemStack.EMPTY;
        return getRecipeOutput().copy();
    }

    /**
     * Checks if the region of a crafting inventory is match for the recipe.
     */
    private boolean checkMatch(InventoryCrafting inventoryCrafting, int par2, int par3, boolean par4) {

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                int var7 = i - par2;
                int var8 = j - par3;
                Ingredient ingredient = Ingredient.EMPTY;

                if (var7 >= 0 && var8 >= 0 && var7 < this.recipeWidth && var8 < this.recipeHeight) {
                    if (par4)
                        ingredient = this.recipeItems.get(this.recipeWidth - var7 - 1 + var8 * this.recipeWidth);
                    else
                        ingredient = this.recipeItems.get(var7 + var8 * this.recipeWidth);
                }

                ItemStack var10 = inventoryCrafting.getStackInRowAndColumn(i, j);

                if (!var10.isEmpty() || ingredient.getMatchingStacks().length == 0) {
                    return false;
                }

                ItemStack var9 = ingredient.getMatchingStacks()[0];

                if ((!var10.isEmpty() || !var9.isEmpty()) && !NoppesUtilPlayer.compareItems(var9, var10, ignoreDamage, ignoreNBT)) {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inventoryCrafting) {
        NonNullList<ItemStack> list = NonNullList.withSize(inventoryCrafting.getSizeInventory(), ItemStack.EMPTY);

        for (int i = 0; i < list.size(); ++i) {
            ItemStack itemstack = inventoryCrafting.getStackInSlot(i);
            list.set(i, net.minecraftforge.common.ForgeHooks.getContainerItem(itemstack));
        }

        return list;
    }

    public void copy(RecipeCarpentry recipe) {
        this.id = recipe.id;
        this.name = recipe.name;
        this.availability = recipe.availability;
        this.isGlobal = recipe.isGlobal;
        this.ignoreDamage = recipe.ignoreDamage;
        this.ignoreNBT = recipe.ignoreNBT;
    }

    public ItemStack getCraftingItem(int i) {
        if (recipeItems == null || i >= recipeItems.size())
            return ItemStack.EMPTY;
        Ingredient ingredients = recipeItems.get(i);
        if (ingredients.getMatchingStacks().length == 0)
            return ItemStack.EMPTY;
        return ingredients.getMatchingStacks()[0];
    }

    public boolean isValid() {
        if (recipeItems.size() == 0 || getRecipeOutput().isEmpty())
            return false;
        for (Ingredient ingredient : recipeItems) {
            if (ingredient.getMatchingStacks().length > 0)
                return true;
        }
        return false;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ItemStack getResult() {
        return getRecipeOutput();
    }

    @Override
    public boolean isGlobal() {
        return isGlobal;
    }

    @Override
    public void setIsGlobal(boolean bo) {
        isGlobal = bo;
    }

    @Override
    public boolean getIgnoreNBT() {
        return ignoreNBT;
    }

    @Override
    public void setIgnoreNBT(boolean bo) {
        ignoreNBT = bo;
    }

    @Override
    public boolean getIgnoreDamage() {
        return ignoreDamage;
    }

    @Override
    public void setIgnoreDamage(boolean bo) {
        ignoreDamage = bo;
    }

    @Override
    public void save() {
        try {
            RecipeController.instance.saveRecipe(this);
        } catch (IOException e) {

        }
    }

    @Override
    public void delete() {
        RecipeController.instance.delete(id);
    }

    @Override
    public int getWidth() {
        return this.recipeWidth;
    }

    @Override
    public int getHeight() {
        return recipeHeight;
    }

    @Override
    public ItemStack[] getRecipe() {
        List<ItemStack> list = new ArrayList<ItemStack>();
        for (Ingredient ingredient : recipeItems) {
            if (ingredient.getMatchingStacks().length > 0)
                list.add(ingredient.getMatchingStacks()[0]);
        }
        return list.toArray(new ItemStack[list.size()]);
    }

    @Override
    public void saves(boolean bo) {
        savesRecipe = bo;
    }

    @Override
    public boolean saves() {
        return savesRecipe;
    }

    @Override
    public int getId() {
        return id;
    }
}

package noppes.npcs.api.handler;

import net.minecraft.item.ItemStack;
import noppes.npcs.api.handler.data.IRecipe;

import java.util.List;

public interface IRecipeHandler {

    List<IRecipe> getGlobalList();

    List<IRecipe> getCarpentryList();

    IRecipe addRecipe(String name, boolean global, ItemStack result, Object... objects);

    IRecipe addRecipe(String name, boolean global, ItemStack result, int width, int height, ItemStack... recipe);

    IRecipe delete(int id);
}

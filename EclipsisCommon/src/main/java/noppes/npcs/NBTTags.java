package noppes.npcs;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.item.IItemStack;
import noppes.npcs.controllers.IScriptHandler;
import noppes.npcs.controllers.ScriptContainer;

import java.util.*;

public class NBTTags {

    public static List<Integer> getIntegerList(NBTTagList tagList) {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < tagList.tagCount(); i++) {
            NBTTagCompound nbttagcompound = tagList.getCompoundTagAt(i);
            list.add(nbttagcompound.getInteger("Integer"));
        }
        return list;
    }

    public static Set<Integer> getIntegerSet(NBTTagList tagList) {
        HashSet<Integer> list = new HashSet<>();
        for (int i = 0; i < tagList.tagCount(); i++) {
            NBTTagCompound nbttagcompound = tagList.getCompoundTagAt(i);
            list.add(nbttagcompound.getInteger("Integer"));
        }
        return list;
    }

    public static NBTTagList nbtIntegerCollection(Collection<Integer> set) {
        NBTTagList nbttaglist = new NBTTagList();
        if (set == null)
            return nbttaglist;
        for (int slot : set) {
            NBTTagCompound nbttagcompound = new NBTTagCompound();
            nbttagcompound.setInteger("Integer", slot);
            nbttaglist.appendTag(nbttagcompound);
        }
        return nbttaglist;
    }

    public static Map<Integer, Integer> getIntegerIntegerMap(NBTTagList tagList) {
        HashMap<Integer, Integer> list = new HashMap<>();
        for (int i = 0; i < tagList.tagCount(); i++) {
            NBTTagCompound nbttagcompound = tagList.getCompoundTagAt(i);
            list.put(nbttagcompound.getInteger("Slot"), nbttagcompound.getInteger("Integer"));
        }
        return list;
    }

    public static NBTTagList nbtIntegerIntegerMap(Map<Integer, Integer> lines) {
        NBTTagList nbttaglist = new NBTTagList();
        if (lines == null)
            return nbttaglist;
        for (int slot : lines.keySet()) {
            NBTTagCompound nbttagcompound = new NBTTagCompound();
            nbttagcompound.setInteger("Slot", slot);
            nbttagcompound.setInteger("Integer", lines.get(slot));
            nbttaglist.appendTag(nbttagcompound);
        }
        return nbttaglist;
    }

    public static Map<Integer, Boolean> getIntegerBooleanMap(NBTTagList tagList) {
        HashMap<Integer, Boolean> list = new HashMap<>();
        for (int i = 0; i < tagList.tagCount(); i++) {
            NBTTagCompound nbttagcompound = tagList.getCompoundTagAt(i);
            list.put(nbttagcompound.getInteger("Slot"), nbttagcompound.getBoolean("Boolean"));
        }
        return list;
    }

    public static NBTTagList nbtBooleanMap(Map<Integer, Boolean> updatedSlots) {
        NBTTagList nbttaglist = new NBTTagList();
        if (updatedSlots == null)
            return nbttaglist;
        for (Integer slot : updatedSlots.keySet()) {
            NBTTagCompound nbttagcompound = new NBTTagCompound();
            nbttagcompound.setInteger("Slot", slot);
            nbttagcompound.setBoolean("Boolean", updatedSlots.get(slot));
            nbttaglist.appendTag(nbttagcompound);
        }
        return nbttaglist;
    }

    public static Map<Integer, Long> getIntegerLongMap(NBTTagList tagList) {
        HashMap<Integer, Long> list = new HashMap<>();
        for (int i = 0; i < tagList.tagCount(); i++) {
            NBTTagCompound nbttagcompound = tagList.getCompoundTagAt(i);
            list.put(nbttagcompound.getInteger("Slot"), nbttagcompound.getLong("Long"));
        }
        return list;
    }

    public static NBTTagList nbtIntegerLongMap(Map<Integer, Long> lines) {
        NBTTagList nbttaglist = new NBTTagList();
        if (lines == null)
            return nbttaglist;
        for (int slot : lines.keySet()) {
            NBTTagCompound nbttagcompound = new NBTTagCompound();
            nbttagcompound.setInteger("Slot", slot);
            nbttagcompound.setLong("Long", lines.get(slot));
            nbttaglist.appendTag(nbttagcompound);
        }
        return nbttaglist;
    }

    public static Map<Integer, Float> getIntegerFloatMap(NBTTagList tagList) {
        HashMap<Integer, Float> list = new HashMap<>();
        for (int i = 0; i < tagList.tagCount(); i++) {
            NBTTagCompound nbttagcompound = tagList.getCompoundTagAt(i);
            list.put(nbttagcompound.getInteger("Slot"), nbttagcompound.getFloat("Float"));
        }
        return list;
    }

    public static NBTTagList nbtIntegerFloatMap(Map<Integer, Float> map) {
        NBTTagList nbttaglist = new NBTTagList();
        if (map == null)
            return nbttaglist;
        for (int slot : map.keySet()) {
            NBTTagCompound nbttagcompound = new NBTTagCompound();
            nbttagcompound.setInteger("Slot", slot);
            nbttagcompound.setFloat("Float", map.get(slot));
            nbttaglist.appendTag(nbttagcompound);
        }
        return nbttaglist;
    }

    public static Map<Integer, String> getIntegerStringMap(NBTTagList tagList) {
        HashMap<Integer, String> list = new HashMap<>();
        for (int i = 0; i < tagList.tagCount(); i++) {
            NBTTagCompound nbttagcompound = tagList.getCompoundTagAt(i);
            list.put(nbttagcompound.getInteger("Slot"), nbttagcompound.getString("Value"));
        }
        return list;
    }

    public static NBTTagList nbtIntegerStringMap(Map<Integer, String> map) {
        NBTTagList nbttaglist = new NBTTagList();
        if (map == null)
            return nbttaglist;
        for (int slot : map.keySet()) {
            NBTTagCompound nbttagcompound = new NBTTagCompound();
            nbttagcompound.setInteger("Slot", slot);
            nbttagcompound.setString("Value", map.get(slot));
            nbttaglist.appendTag(nbttagcompound);
        }
        return nbttaglist;
    }

    public static List<int[]> getIntegerArrayList(NBTTagList tagList) {
        ArrayList<int[]> set = new ArrayList<>();
        for (int i = 0; i < tagList.tagCount(); i++) {
            NBTTagCompound compound = tagList.getCompoundTagAt(i);
            set.add(compound.getIntArray("Array"));
        }
        return set;
    }

    public static NBTTagList nbtIntegerArrayList(List<int[]> set) {
        NBTTagList nbttaglist = new NBTTagList();
        if (set == null)
            return nbttaglist;
        for (int[] arr : set) {
            NBTTagCompound nbttagcompound = new NBTTagCompound();
            nbttagcompound.setIntArray("Array", arr);
            nbttaglist.appendTag(nbttagcompound);
        }
        return nbttaglist;
    }

    public static List<String> getStringList(NBTTagList tagList) {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < tagList.tagCount(); i++) {
            NBTTagCompound nbttagcompound = tagList.getCompoundTagAt(i);
            String line = nbttagcompound.getString("Line");
            list.add(line);
        }
        return list;
    }

    public static NBTTagList nbtStringList(List<String> list) {
        NBTTagList nbttaglist = new NBTTagList();
        for (String s : list) {
            NBTTagCompound nbttagcompound = new NBTTagCompound();
            nbttagcompound.setString("Line", s);
            nbttaglist.appendTag(nbttagcompound);
        }
        return nbttaglist;
    }

    public static Map<String, Integer> getStringIntegerMap(NBTTagList tagList) {
        HashMap<String, Integer> list = new HashMap<>();
        for (int i = 0; i < tagList.tagCount(); i++) {
            NBTTagCompound nbttagcompound = tagList.getCompoundTagAt(i);
            list.put(nbttagcompound.getString("Slot"), nbttagcompound.getInteger("Value"));
        }
        return list;
    }

    public static NBTTagList nbtStringIntegerMap(Map<String, Integer> map) {
        NBTTagList nbttaglist = new NBTTagList();
        if (map == null)
            return nbttaglist;
        for (String slot : map.keySet()) {
            NBTTagCompound nbttagcompound = new NBTTagCompound();
            nbttagcompound.setString("Slot", slot);
            nbttagcompound.setInteger("Value", map.get(slot));
            nbttaglist.appendTag(nbttagcompound);
        }
        return nbttaglist;
    }

    public static Map<String, String> getStringStringMap(NBTTagList tagList) {
        HashMap<String, String> list = new HashMap<>();
        for (int i = 0; i < tagList.tagCount(); i++) {
            NBTTagCompound nbttagcompound = tagList.getCompoundTagAt(i);
            list.put(nbttagcompound.getString("Slot"), nbttagcompound.getString("Value"));
        }
        return list;
    }

    public static NBTTagList nbtStringStringMap(Map<String, String> map) {
        NBTTagList nbttaglist = new NBTTagList();
        if (map == null)
            return nbttaglist;
        for (String slot : map.keySet()) {
            NBTTagCompound nbttagcompound = new NBTTagCompound();
            nbttagcompound.setString("Slot", slot);
            nbttagcompound.setString("Value", map.get(slot));
            nbttaglist.appendTag(nbttagcompound);
        }
        return nbttaglist;
    }

    public static TreeMap<Long, String> getLongStringMap(NBTTagList tagList) {
        TreeMap<Long, String> list = new TreeMap<>();
        for (int i = 0; i < tagList.tagCount(); i++) {
            NBTTagCompound nbttagcompound = tagList.getCompoundTagAt(i);
            list.put(nbttagcompound.getLong("Long"), nbttagcompound.getString("String"));
        }
        return list;
    }

    public static NBTTagList nbtLongStringMap(Map<Long, String> map) {
        NBTTagList nbttaglist = new NBTTagList();
        if (map == null)
            return nbttaglist;
        for (long slot : map.keySet()) {
            NBTTagCompound nbttagcompound = new NBTTagCompound();
            nbttagcompound.setLong("Long", slot);
            nbttagcompound.setString("String", map.get(slot));

            nbttaglist.appendTag(nbttagcompound);
        }
        return nbttaglist;
    }

    public static void getItemStackList(NBTTagList tagList, NonNullList<ItemStack> items) {
        items.clear();
        for (int i = 0; i < tagList.tagCount(); i++) {
            NBTTagCompound nbttagcompound = tagList.getCompoundTagAt(i);
            try {
                items.set(nbttagcompound.getByte("Slot") & 0xff, new ItemStack(nbttagcompound));
            } catch (ClassCastException e) {
                items.set(nbttagcompound.getInteger("Slot"), new ItemStack(nbttagcompound));
            }
        }
    }

    public static NBTTagList nbtItemStackList(NonNullList<ItemStack> inventory) {
        NBTTagList nbttaglist = new NBTTagList();
        for (int slot = 0; slot < inventory.size(); slot++) {
            ItemStack item = inventory.get(slot);
            if (item.isEmpty())
                continue;
            NBTTagCompound nbttagcompound = new NBTTagCompound();
            nbttagcompound.setByte("Slot", (byte) slot);

            item.writeToNBT(nbttagcompound);

            nbttaglist.appendTag(nbttagcompound);
        }
        return nbttaglist;
    }

    public static Map<Integer, IItemStack> getIItemStackMap(NBTTagList tagList) {
        Map<Integer, IItemStack> list = new HashMap<>();
        for (int i = 0; i < tagList.tagCount(); i++) {
            NBTTagCompound nbttagcompound = tagList.getCompoundTagAt(i);
            ItemStack item = new ItemStack(nbttagcompound);
            if (item.isEmpty())
                continue;
            try {
                list.put(nbttagcompound.getByte("Slot") & 0xff, NpcAPI.Instance().getIItemStack(item));
            } catch (ClassCastException e) {
                list.put(nbttagcompound.getInteger("Slot"), NpcAPI.Instance().getIItemStack(item));
            }
        }
        return list;
    }

    public static NBTTagList nbtIItemStackMap(Map<Integer, IItemStack> inventory) {
        NBTTagList nbttaglist = new NBTTagList();
        if (inventory == null)
            return nbttaglist;
        for (int slot : inventory.keySet()) {
            IItemStack item = inventory.get(slot);
            if (item == null)
                continue;
            NBTTagCompound nbttagcompound = new NBTTagCompound();
            nbttagcompound.setByte("Slot", (byte) slot);

            item.getMCItemStack().writeToNBT(nbttagcompound);

            nbttaglist.appendTag(nbttagcompound);
        }
        return nbttaglist;
    }

    public static NonNullList<Ingredient> getIngredientList(NBTTagList tagList) {
        NonNullList<Ingredient> list = NonNullList.create();
        for (int i = 0; i < tagList.tagCount(); i++) {
            NBTTagCompound nbttagcompound = tagList.getCompoundTagAt(i);
            list.add(nbttagcompound.getByte("Slot") & 0xff, Ingredient.fromStacks(new ItemStack(nbttagcompound)));
        }
        return list;
    }

    public static NBTTagList nbtIngredientList(NonNullList<Ingredient> inventory) {
        NBTTagList nbttaglist = new NBTTagList();
        if (inventory == null)
            return nbttaglist;
        for (int slot = 0; slot < inventory.size(); slot++) {
            Ingredient ingredient = inventory.get(slot);
            NBTTagCompound nbttagcompound = new NBTTagCompound();
            nbttagcompound.setByte("Slot", (byte) slot);

            if (ingredient.getMatchingStacks().length > 0)
                ingredient.getMatchingStacks()[0].writeToNBT(nbttagcompound);

            nbttaglist.appendTag(nbttagcompound);
        }
        return nbttaglist;
    }

    public static NBTTagList nbtDoubleList(double... par1ArrayOfDouble) {
        NBTTagList nbttaglist = new NBTTagList();
        double[] adouble = par1ArrayOfDouble;
        int i = par1ArrayOfDouble.length;

        for (int j = 0; j < i; ++j) {
            double d1 = adouble[j];
            nbttaglist.appendTag(new NBTTagDouble(d1));
        }

        return nbttaglist;
    }

    public static NBTTagCompound NBTMerge(NBTTagCompound data, NBTTagCompound merge) {
        NBTTagCompound compound = data.copy();
        Set<String> names = merge.getKeySet();
        for (String name : names) {
            NBTBase base = merge.getTag(name);
            if (base.getId() == 10)
                base = NBTMerge(compound.getCompoundTag(name), (NBTTagCompound) base);
            compound.setTag(name, base);
        }
        return compound;
    }

    public static List<ScriptContainer> GetScript(NBTTagList list, IScriptHandler handler) {
        List<ScriptContainer> scripts = new ArrayList<>();
        for (int i = 0; i < list.tagCount(); i++) {
            NBTTagCompound compoundd = list.getCompoundTagAt(i);
            ScriptContainer script = new ScriptContainer(handler);
            script.readFromNBT(compoundd);
            scripts.add(script);
        }
        return scripts;
    }

    public static NBTTagList NBTScript(List<ScriptContainer> scripts) {
        NBTTagList list = new NBTTagList();
        for (ScriptContainer script : scripts) {
            NBTTagCompound compound = new NBTTagCompound();
            script.writeToNBT(compound);
            list.appendTag(compound);
        }
        return list;
    }

}

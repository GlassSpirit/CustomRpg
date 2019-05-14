package noppes.npcs.controllers.data;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.WeightedRandom;
import noppes.npcs.util.NBTTags;

import java.util.ArrayList;
import java.util.List;

public class SpawnData extends WeightedRandom.Item {
    public List<String> biomes = new ArrayList<String>();
    public int id = -1;
    public String name = "";
    public NBTTagCompound compound1 = new NBTTagCompound();
    public boolean liquid = false;
    public int type = 0; //0:any, 1:dark only

    public SpawnData() {
        super(10);
    }

    public void readNBT(NBTTagCompound compound) {
        id = compound.getInteger("SpawnId");
        name = compound.getString("SpawnName");
        itemWeight = compound.getInteger("SpawnWeight");
        if (itemWeight == 0)
            itemWeight = 1;

        biomes = NBTTags.getStringList(compound.getTagList("SpawnBiomes", 10));

        compound1 = compound.getCompoundTag("SpawnCompound1");

        type = compound.getInteger("SpawnType");
    }

    public NBTTagCompound writeNBT(NBTTagCompound compound) {
        compound.setInteger("SpawnId", id);
        compound.setString("SpawnName", name);
        compound.setInteger("SpawnWeight", itemWeight);

        compound.setTag("SpawnBiomes", NBTTags.nbtStringList(biomes));

        compound.setTag("SpawnCompound1", compound1);

        compound.setInteger("SpawnType", type);
        return compound;
    }

}

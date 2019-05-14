package noppes.npcs.controllers.data;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import noppes.npcs.EventHooks;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.event.PlayerEvent;
import noppes.npcs.api.wrapper.PlayerWrapper;
import noppes.npcs.controllers.FactionController;

import java.util.HashMap;

public class PlayerFactionData {
    public HashMap<Integer, Integer> factionData = new HashMap<>();

    public void loadNBTData(NBTTagCompound compound) {
        HashMap<Integer, Integer> factionData = new HashMap<>();
        if (compound == null)
            return;
        NBTTagList list = compound.getTagList("FactionData", 10);
        if (list == null) {
            return;
        }

        for (int i = 0; i < list.tagCount(); i++) {
            NBTTagCompound nbttagcompound = list.getCompoundTagAt(i);
            factionData.put(nbttagcompound.getInteger("Faction"), nbttagcompound.getInteger("Points"));
        }
        this.factionData = factionData;
    }

    public void saveNBTData(NBTTagCompound compound) {
        NBTTagList list = new NBTTagList();
        for (int faction : factionData.keySet()) {
            NBTTagCompound nbttagcompound = new NBTTagCompound();
            nbttagcompound.setInteger("Faction", faction);
            nbttagcompound.setInteger("Points", factionData.get(faction));
            list.appendTag(nbttagcompound);
        }

        compound.setTag("FactionData", list);
    }

    public int getFactionPoints(EntityPlayer player, int factionId) {
        Faction faction = FactionController.instance.getFaction(factionId);
        if (faction == null)
            return 0;
        if (!factionData.containsKey(factionId)) {
            if (player.world.isRemote)
                return faction.defaultPoints;
            PlayerScriptData handler = PlayerData.get(player).scriptData;
            PlayerWrapper wrapper = (PlayerWrapper) NpcAPI.instance().getIEntity(player);

            PlayerEvent.FactionUpdateEvent event = new PlayerEvent.FactionUpdateEvent(wrapper, faction, faction.defaultPoints, true);
            EventHooks.OnPlayerFactionChange(handler, event);
            factionData.put(factionId, event.points);
        }
        return factionData.get(factionId);
    }

    public void increasePoints(EntityPlayer player, int factionId, int points) {
        Faction faction = FactionController.instance.getFaction(factionId);
        if (faction == null || player == null || player.world.isRemote)
            return;

        PlayerScriptData handler = PlayerData.get(player).scriptData;
        PlayerWrapper wrapper = (PlayerWrapper) NpcAPI.instance().getIEntity(player);
        if (!factionData.containsKey(factionId)) {
            PlayerEvent.FactionUpdateEvent event = new PlayerEvent.FactionUpdateEvent(wrapper, faction, faction.defaultPoints, true);
            EventHooks.OnPlayerFactionChange(handler, event);
            factionData.put(factionId, event.points);
        }
        PlayerEvent.FactionUpdateEvent event = new PlayerEvent.FactionUpdateEvent(wrapper, faction, points, false);
        EventHooks.OnPlayerFactionChange(handler, event);
        factionData.put(factionId, factionData.get(factionId) + points);
    }

    public NBTTagCompound getPlayerGuiData() {
        NBTTagCompound compound = new NBTTagCompound();
        saveNBTData(compound);

        NBTTagList list = new NBTTagList();
        for (int id : factionData.keySet()) {
            Faction faction = FactionController.instance.getFaction(id);
            if (faction == null || faction.hideFaction)
                continue;
            NBTTagCompound com = new NBTTagCompound();
            faction.writeNBT(com);
            list.appendTag(com);
        }
        compound.setTag("FactionList", list);

        return compound;
    }

}

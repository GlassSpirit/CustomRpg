package noppes.npcs.controllers.data;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.NBTTags;
import noppes.npcs.api.CustomNPCsException;
import noppes.npcs.api.entity.ICustomNpc;
import noppes.npcs.api.entity.IPlayer;
import noppes.npcs.api.handler.data.IFaction;
import noppes.npcs.controllers.FactionController;
import noppes.npcs.entity.EntityNPCInterface;

import java.util.HashSet;

public class Faction implements IFaction {
    public String name = "";
    public int color = Integer.parseInt("FF00", 16);

    public HashSet<Integer> attackFactions;
    public int id = -1;

    public int neutralPoints = 500;
    public int friendlyPoints = 1500;

    public int defaultPoints = 1000;

    public boolean hideFaction = false;
    public boolean getsAttacked = false;

    public Faction() {
        attackFactions = new HashSet<Integer>();
    }

    public Faction(int id, String name, int color, int defaultPoints) {
        this.name = name;
        this.color = color;
        this.defaultPoints = defaultPoints;
        this.id = id;
        attackFactions = new HashSet<Integer>();
    }

    public static String formatName(String name) {
        name = name.toLowerCase().trim();
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    public void readNBT(NBTTagCompound compound) {
        name = compound.getString("Name");
        color = compound.getInteger("Color");
        id = compound.getInteger("Slot");

        neutralPoints = compound.getInteger("NeutralPoints");
        friendlyPoints = compound.getInteger("FriendlyPoints");
        defaultPoints = compound.getInteger("DefaultPoints");

        hideFaction = compound.getBoolean("HideFaction");
        getsAttacked = compound.getBoolean("GetsAttacked");

        attackFactions = NBTTags.getIntegerSet(compound.getTagList("AttackFactions", 10));
    }

    public NBTTagCompound writeNBT(NBTTagCompound compound) {
        compound.setInteger("Slot", id);
        compound.setString("Name", name);
        compound.setInteger("Color", color);

        compound.setInteger("NeutralPoints", neutralPoints);
        compound.setInteger("FriendlyPoints", friendlyPoints);
        compound.setInteger("DefaultPoints", defaultPoints);

        compound.setBoolean("HideFaction", hideFaction);
        compound.setBoolean("GetsAttacked", getsAttacked);

        compound.setTag("AttackFactions", NBTTags.nbtIntegerCollection(attackFactions));
        return compound;
    }

    public boolean isFriendlyToPlayer(EntityPlayer player) {
        PlayerFactionData data = PlayerData.get(player).factionData;
        return data.getFactionPoints(player, id) >= friendlyPoints;
    }

    public boolean isAggressiveToPlayer(EntityPlayer player) {
        if (player.capabilities.isCreativeMode)
            return false;
        PlayerFactionData data = PlayerData.get(player).factionData;
        return data.getFactionPoints(player, id) < neutralPoints;
    }

    public boolean isNeutralToPlayer(EntityPlayer player) {
        PlayerFactionData data = PlayerData.get(player).factionData;
        int points = data.getFactionPoints(player, id);
        return points >= neutralPoints && points < friendlyPoints;
    }

    public boolean isAggressiveToNpc(EntityNPCInterface entity) {
        return attackFactions.contains(entity.faction.id);
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getDefaultPoints() {
        return defaultPoints;
    }

    @Override
    public int getColor() {
        return color;
    }

    @Override
    public int playerStatus(IPlayer player) {
        PlayerFactionData data = PlayerData.get(player.getMCEntity()).factionData;
        int points = data.getFactionPoints(player.getMCEntity(), id);
        if (points >= friendlyPoints)
            return 1;
        if (points < neutralPoints)
            return -1;
        return 0;
    }

    @Override
    public boolean hostileToNpc(ICustomNpc npc) {
        return attackFactions.contains(npc.getFaction().getId());
    }

    @Override
    public void setDefaultPoints(int points) {
        defaultPoints = points;
    }

    @Override
    public boolean hostileToFaction(int factionId) {
        return attackFactions.contains(factionId);
    }

    @Override
    public int[] getHostileList() {
        int[] a = new int[attackFactions.size()];
        int i = 0;
        for (Integer val : attackFactions)
            a[i++] = val;
        return a;
    }

    @Override
    public void addHostile(int id) {
        if (attackFactions.contains(id)) {
            throw new CustomNPCsException("Faction " + this.id + " is already hostile to " + id);
        }
        attackFactions.add(id);
    }

    @Override
    public void removeHostile(int id) {
        attackFactions.remove(id);
    }

    @Override
    public boolean hasHostile(int id) {
        return attackFactions.contains(id);
    }

    @Override
    public boolean getIsHidden() {
        return hideFaction;
    }

    @Override
    public void setIsHidden(boolean bo) {
        hideFaction = bo;
    }

    @Override
    public boolean getAttackedByMobs() {
        return getsAttacked;
    }

    @Override
    public void setAttackedByMobs(boolean bo) {
        getsAttacked = bo;
    }

    @Override
    public void save() {
        FactionController.instance.saveFaction(this);
    }
}

package noppes.npcs.controllers.data;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextComponentTranslation;
import noppes.npcs.controllers.FactionController;

public class FactionOptions {

    public boolean decreaseFactionPoints = false;
    public boolean decreaseFaction2Points = false;

    public int factionId = -1;
    public int faction2Id = -1;

    public int factionPoints = 100;
    public int faction2Points = 100;

    public void readFromNBT(NBTTagCompound compound) {
        factionId = compound.getInteger("OptionFactions1");
        faction2Id = compound.getInteger("OptionFactions2");

        decreaseFactionPoints = compound.getBoolean("DecreaseFaction1Points");
        decreaseFaction2Points = compound.getBoolean("DecreaseFaction2Points");

        factionPoints = compound.getInteger("OptionFaction1Points");
        faction2Points = compound.getInteger("OptionFaction2Points");
    }

    public NBTTagCompound writeToNBT(NBTTagCompound par1NBTTagCompound) {
        par1NBTTagCompound.setInteger("OptionFactions1", factionId);
        par1NBTTagCompound.setInteger("OptionFactions2", faction2Id);

        par1NBTTagCompound.setInteger("OptionFaction1Points", factionPoints);
        par1NBTTagCompound.setInteger("OptionFaction2Points", faction2Points);

        par1NBTTagCompound.setBoolean("DecreaseFaction1Points", decreaseFactionPoints);
        par1NBTTagCompound.setBoolean("DecreaseFaction2Points", decreaseFaction2Points);
        return par1NBTTagCompound;
    }

    public boolean hasFaction(int id) {
        return factionId == id || faction2Id == id;
    }

    public void addPoints(EntityPlayer player) {
        if (factionId < 0 && faction2Id < 0)
            return;

        PlayerData playerdata = PlayerData.get(player);
        PlayerFactionData data = playerdata.factionData;
        if (factionId >= 0 && factionPoints > 0)
            addPoints(player, data, factionId, decreaseFactionPoints, factionPoints);
        if (faction2Id >= 0 && faction2Points > 0)
            addPoints(player, data, faction2Id, decreaseFaction2Points, faction2Points);
        playerdata.updateClient = true;
    }

    private void addPoints(EntityPlayer player, PlayerFactionData data, int factionId, boolean decrease, int points) {
        Faction faction = FactionController.instance.getFaction(factionId);
        if (faction == null)
            return;

        if (!faction.hideFaction) {
            String message = decrease ? "faction.decreasepoints" : "faction.increasepoints";
            player.sendMessage(new TextComponentTranslation(message, faction.name, points));
        }

        data.increasePoints(player, factionId, decrease ? -points : points);

    }
}

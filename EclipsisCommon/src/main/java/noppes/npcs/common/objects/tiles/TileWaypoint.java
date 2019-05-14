package noppes.npcs.common.objects.tiles;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.TextComponentTranslation;
import noppes.npcs.api.constants.QuestType;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.controllers.data.PlayerQuestData;
import noppes.npcs.controllers.data.QuestData;
import noppes.npcs.quests.QuestLocation;

import java.util.ArrayList;
import java.util.List;

public class TileWaypoint extends TileNpcEntity implements ITickable {

    public String name = "";

    private int ticks = 10;
    private List<EntityPlayer> recentlyChecked = new ArrayList<>();
    private List<EntityPlayer> toCheck;
    public int range = 10;

    @Override
    public void update() {
        if (world.isRemote || name.isEmpty())
            return;
        ticks--;
        if (ticks > 0)
            return;
        ticks = 10;

        toCheck = getPlayerList(range, range, range);
        toCheck.removeAll(recentlyChecked);

        List<EntityPlayer> listMax = getPlayerList(range + 10, range + 10, range + 10);
        recentlyChecked.retainAll(listMax);
        recentlyChecked.addAll(toCheck);

        if (toCheck.isEmpty())
            return;
        for (EntityPlayer player : toCheck) {
            PlayerData pdata = PlayerData.get(player);
            PlayerQuestData playerdata = pdata.questData;
            for (QuestData data : playerdata.activeQuests.values()) {
                if (data.quest.type != QuestType.LOCATION)
                    continue;
                QuestLocation quest = (QuestLocation) data.quest.questInterface;
                if (quest.setFound(data, name)) {
                    player.sendMessage(new TextComponentTranslation(name + " " + I18n.format("quest.found")));

                    playerdata.checkQuestCompletion(player, QuestType.LOCATION);
                    pdata.updateClient = true;
                }
            }
        }
    }

    private List<EntityPlayer> getPlayerList(int x, int y, int z) {
        return world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(pos, pos.add(1, 1, 1)).grow(x, y, z));
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        name = compound.getString("LocationName");
        range = compound.getInteger("LocationRange");
        if (range < 2)
            range = 2;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        if (!name.isEmpty())
            compound.setString("LocationName", name);
        compound.setInteger("LocationRange", range);
        return super.writeToNBT(compound);
    }
}

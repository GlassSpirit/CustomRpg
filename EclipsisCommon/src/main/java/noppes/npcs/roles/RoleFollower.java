package noppes.npcs.roles;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextComponentTranslation;
import noppes.npcs.EventHooks;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.NpcMiscInventory;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.constants.JobType;
import noppes.npcs.api.entity.IPlayer;
import noppes.npcs.api.entity.data.role.IRoleFollower;
import noppes.npcs.api.event.RoleEvent;
import noppes.npcs.common.entity.EntityNPCInterface;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.util.NBTTags;
import noppes.npcs.util.NoppesStringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class RoleFollower extends RoleInterface implements IRoleFollower {

    public boolean isFollowing = true;
    public Map<Integer, Integer> rates;
    public NpcMiscInventory inventory;
    public String dialogHire = I18n.format("follower.hireText") + " {days} " + I18n.format("follower.days");
    public String dialogFarewell = I18n.format("follower.farewellText") + " {player}";
    public int daysHired;
    public long hiredTime;
    public boolean disableGui = false;
    public boolean infiniteDays = false;
    public boolean refuseSoulStone = false;
    public EntityPlayer owner = null;
    private String ownerUUID;

    public RoleFollower(EntityNPCInterface npc) {
        super(npc);
        inventory = new NpcMiscInventory(3);
        rates = new HashMap<>();
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbttagcompound) {
        nbttagcompound.setInteger("MercenaryDaysHired", daysHired);
        nbttagcompound.setLong("MercenaryHiredTime", hiredTime);
        nbttagcompound.setString("MercenaryDialogHired", dialogHire);
        nbttagcompound.setString("MercenaryDialogFarewell", dialogFarewell);
        if (hasOwner())
            nbttagcompound.setString("MercenaryOwner", ownerUUID);
        nbttagcompound.setTag("MercenaryDayRates", NBTTags.nbtIntegerIntegerMap(rates));
        nbttagcompound.setTag("MercenaryInv", inventory.getToNBT());
        nbttagcompound.setBoolean("MercenaryIsFollowing", isFollowing);
        nbttagcompound.setBoolean("MercenaryDisableGui", disableGui);
        nbttagcompound.setBoolean("MercenaryInfiniteDays", infiniteDays);
        nbttagcompound.setBoolean("MercenaryRefuseSoulstone", refuseSoulStone);
        return nbttagcompound;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbttagcompound) {
        ownerUUID = nbttagcompound.getString("MercenaryOwner");
        daysHired = nbttagcompound.getInteger("MercenaryDaysHired");
        hiredTime = nbttagcompound.getLong("MercenaryHiredTime");
        dialogHire = nbttagcompound.getString("MercenaryDialogHired");
        dialogFarewell = nbttagcompound.getString("MercenaryDialogFarewell");
        rates = NBTTags.getIntegerIntegerMap(nbttagcompound.getTagList("MercenaryDayRates", 10));
        inventory.setFromNBT(nbttagcompound.getCompoundTag("MercenaryInv"));
        isFollowing = nbttagcompound.getBoolean("MercenaryIsFollowing");
        disableGui = nbttagcompound.getBoolean("MercenaryDisableGui");
        infiniteDays = nbttagcompound.getBoolean("MercenaryInfiniteDays");
        refuseSoulStone = nbttagcompound.getBoolean("MercenaryRefuseSoulstone");
    }

    @Override
    public boolean aiShouldExecute() {
        owner = getOwner();
        if (!infiniteDays && owner != null && getDays() <= 0) {
            RoleEvent.FollowerFinishedEvent event = new RoleEvent.FollowerFinishedEvent(owner, npc.wrappedNPC);
            EventHooks.onNPCRole(npc, event);
            owner.sendMessage(new TextComponentTranslation(NoppesStringUtils.formatText(dialogFarewell, owner, npc)));
            killed();
        }
        return false;
    }

    public EntityPlayer getOwner() {
        if (ownerUUID == null || ownerUUID.isEmpty())
            return null;
        try {
            UUID uuid = UUID.fromString(ownerUUID);
            if (uuid != null)
                return npc.world.getPlayerEntityByUUID(uuid);
        } catch (IllegalArgumentException ex) {

        }

        return npc.world.getPlayerEntityByName(ownerUUID);
    }

    public void setOwner(EntityPlayer player) {
        UUID id = player.getUniqueID();
        if (ownerUUID == null || !ownerUUID.equals(id.toString()))
            killed();
        ownerUUID = id.toString();
    }

    public boolean hasOwner() {
        if (!infiniteDays && daysHired <= 0)
            return false;
        return ownerUUID != null && !ownerUUID.isEmpty();
    }

    @Override
    public void killed() {
        ownerUUID = null;
        daysHired = 0;
        hiredTime = 0;
        isFollowing = true;
    }

    @Override
    public void reset() {
        killed();
    }

    @Override
    public void interact(EntityPlayer player) {
        if (ownerUUID == null || ownerUUID.isEmpty()) {
            npc.say(player, npc.advanced.getInteractLine());
            NoppesUtilServer.sendOpenGui(player, EnumGuiType.PlayerFollowerHire, npc);
        } else if (player == owner && !disableGui) {
            NoppesUtilServer.sendOpenGui(player, EnumGuiType.PlayerFollower, npc);
        }
    }

    @Override
    public boolean defendOwner() {
        return isFollowing() && npc.advanced.job == JobType.GUARD;
    }

    @Override
    public void delete() {

    }

    @Override
    public boolean isFollowing() {
        return owner != null && isFollowing && getDays() > 0;
    }

    @Override
    public int getDays() {
        if (infiniteDays)
            return 100;
        if (daysHired <= 0)
            return 0;
        int days = (int) ((npc.world.getTotalWorldTime() - hiredTime) / 24000);
        return daysHired - days;
    }

    @Override
    public void addDays(int days) {
        daysHired = days + getDays();
        hiredTime = npc.world.getTotalWorldTime();
    }

    @Override
    public boolean getInfinite() {
        return infiniteDays;
    }

    @Override
    public void setInfinite(boolean infinite) {
        this.infiniteDays = infinite;
    }

    @Override
    public boolean getGuiDisabled() {
        return disableGui;
    }

    @Override
    public void setGuiDisabled(boolean disabled) {
        disableGui = disabled;
    }

    @Override
    public boolean getRefuseSoulstone() {
        return refuseSoulStone;
    }

    @Override
    public void setRefuseSoulstone(boolean refuse) {
        refuseSoulStone = refuse;
    }

    @Override
    public IPlayer getFollowing() {
        EntityPlayer owner = getOwner();
        if (owner != null)
            return (IPlayer) NpcAPI.instance().getIEntity(owner);
        return null;
    }

    @Override
    public void setFollowing(IPlayer player) {
        if (player == null)
            setOwner(null);
        else
            setOwner(player.getMCEntity());
    }

}

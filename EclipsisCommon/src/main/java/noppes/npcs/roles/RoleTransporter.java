package noppes.npcs.roles;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextComponentTranslation;
import noppes.npcs.EventHooks;
import noppes.npcs.NoppesUtilPlayer;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.api.entity.data.role.IRoleTransporter;
import noppes.npcs.api.event.RoleEvent;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.controllers.TransportController;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.controllers.data.PlayerTransportData;
import noppes.npcs.controllers.data.TransportLocation;
import noppes.npcs.entity.EntityNPCInterface;

import java.util.List;

public class RoleTransporter extends RoleInterface implements IRoleTransporter {

    public int transportId = -1;
    public String name;

    public RoleTransporter(EntityNPCInterface npc) {
        super(npc);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbttagcompound) {
        nbttagcompound.setInteger("TransporterId", transportId);
        return nbttagcompound;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbttagcompound) {
        transportId = nbttagcompound.getInteger("TransporterId");
        TransportLocation loc = getLocation();
        if (loc != null) {
            name = loc.name;
        }
    }

    private int ticks = 10;

    @Override
    public boolean aiShouldExecute() {
        ticks--;
        if (ticks > 0)
            return false;
        ticks = 10;

        if (!hasTransport())
            return false;

        TransportLocation loc = getLocation();
        if (loc.type != 0)
            return false;

        List<EntityPlayer> inRange = npc.world.getEntitiesWithinAABB(EntityPlayer.class, npc.getEntityBoundingBox().grow(6D, 6D, 6D));
        for (EntityPlayer player : inRange) {
            if (!npc.canSee(player))
                continue;
            unlock(player, loc);
        }
        return false;

    }

    @Override
    public void interact(EntityPlayer player) {
        if (hasTransport()) {
            TransportLocation loc = getLocation();
            if (loc.type == 2) {
                unlock(player, loc);
            }
            NoppesUtilServer.sendOpenGui(player, EnumGuiType.PlayerTransporter, npc);
        }
    }

    public void transport(EntityPlayerMP player, String location) {
        TransportLocation loc = TransportController.getInstance().getTransport(location);
        PlayerTransportData playerdata = PlayerData.get(player).transportData;

        if (loc == null || !loc.isDefault() && !playerdata.transports.contains(loc.id))
            return;

        RoleEvent.TransporterUseEvent event = new RoleEvent.TransporterUseEvent(player, npc.wrappedNPC, loc);
        if (EventHooks.onNPCRole(npc, event))
            return;
        NoppesUtilPlayer.teleportPlayer(player, loc.pos.getX(), loc.pos.getY(), loc.pos.getZ(), loc.dimension);
    }

    private void unlock(EntityPlayer player, TransportLocation loc) {
        PlayerTransportData data = PlayerData.get(player).transportData;
        if (data.transports.contains(transportId))
            return;
        RoleEvent.TransporterUnlockedEvent event = new RoleEvent.TransporterUnlockedEvent(player, npc.wrappedNPC);
        if (EventHooks.onNPCRole(npc, event))
            return;

        data.transports.add(transportId);
        player.sendMessage(new TextComponentTranslation("transporter.unlock", loc.name));
    }

    public TransportLocation getLocation() {
        if (npc.isRemote())
            return null;
        return TransportController.getInstance().getTransport(transportId);
    }

    public boolean hasTransport() {
        TransportLocation loc = getLocation();
        return loc != null && loc.id == transportId;
    }

    public void setTransport(TransportLocation location) {
        transportId = location.id;
        name = location.name;
    }

}

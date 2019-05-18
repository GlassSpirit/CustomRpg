package noppes.npcs.controllers;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.LoadingCallback;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.common.ForgeChunkManager.Type;
import noppes.npcs.CustomNpcs;
import noppes.npcs.api.constants.JobType;
import noppes.npcs.entity.EntityNPCInterface;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class ChunkController implements LoadingCallback {
    public static ChunkController instance;

    private HashMap<Entity, Ticket> tickets = new HashMap<Entity, Ticket>();

    public ChunkController() {
        instance = this;
    }

    public void clear() {
        tickets = new HashMap<Entity, Ticket>();
    }

    public Ticket getTicket(EntityNPCInterface npc) {
        Ticket ticket = tickets.get(npc);
        if (ticket != null)
            return ticket;
        if (size() >= CustomNpcs.ChuckLoaders)
            return null;
        ticket = ForgeChunkManager.requestTicket(CustomNpcs.instance, npc.world, Type.ENTITY);
        if (ticket == null)
            return null;
        ticket.bindEntity(npc);
        ticket.setChunkListDepth(6);
        tickets.put(npc, ticket);
        return null;
    }

    public void deleteNPC(EntityNPCInterface npc) {
        Ticket ticket = tickets.get(npc);
        if (ticket != null) {
            tickets.remove(npc);
            ForgeChunkManager.releaseTicket(ticket);
        }
    }

    @Override
    public void ticketsLoaded(List<Ticket> tickets, World world) {
        for (Ticket ticket : tickets) {
            if (!(ticket.getEntity() instanceof EntityNPCInterface))
                continue;
            EntityNPCInterface npc = (EntityNPCInterface) ticket.getEntity();
            if (npc.advanced.job == JobType.CHUNKLOADER && !tickets.contains(npc)) {
                this.tickets.put(npc, ticket);
                double x = npc.posX / 16;
                double z = npc.posZ / 16;

                ForgeChunkManager.forceChunk(ticket, new ChunkPos(MathHelper.floor(x), MathHelper.floor(z)));
                ForgeChunkManager.forceChunk(ticket, new ChunkPos(MathHelper.ceil(x), MathHelper.ceil(z)));
                ForgeChunkManager.forceChunk(ticket, new ChunkPos(MathHelper.floor(x), MathHelper.ceil(z)));
                ForgeChunkManager.forceChunk(ticket, new ChunkPos(MathHelper.ceil(x), MathHelper.floor(z)));
            }
        }
    }

    public int size() {
        return tickets.size();
    }

    public void unload(int toRemove) {
        Iterator<Entity> ite = tickets.keySet().iterator();
        int i = 0;
        while (ite.hasNext()) {
            if (i >= toRemove)
                return;
            Entity entity = ite.next();
            ForgeChunkManager.releaseTicket(tickets.get(entity));
            ite.remove();
            i++;
        }
    }
}

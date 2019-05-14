package noppes.npcs.roles;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import noppes.npcs.controllers.ChunkController;
import noppes.npcs.common.entity.EntityNPCInterface;

import java.util.ArrayList;
import java.util.List;

public class JobChunkLoader extends JobInterface {

    private List<ChunkPos> chunks = new ArrayList<ChunkPos>();
    private int ticks = 20;
    private long playerLastSeen = 0;

    public JobChunkLoader(EntityNPCInterface npc) {
        super(npc);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setLong("ChunkPlayerLastSeen", playerLastSeen);
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        playerLastSeen = compound.getLong("ChunkPlayerLastSeen");
    }

    @Override
    public boolean aiShouldExecute() {
        ticks--;
        if (ticks > 0)
            return false;
        ticks = 20;

        List players = npc.world.getEntitiesWithinAABB(EntityPlayer.class, npc.getEntityBoundingBox().grow(48, 48, 48));
        if (!players.isEmpty())
            playerLastSeen = System.currentTimeMillis();

        //unload after 10 min
        if (System.currentTimeMillis() > playerLastSeen + 600000) {
            ChunkController.instance.deleteNPC(npc);
            chunks.clear();
            return false;
        }
        Ticket ticket = ChunkController.instance.getTicket(npc);
        if (ticket == null) //Only null when too many active chunkloaders already
            return false;
        double x = npc.posX / 16;
        double z = npc.posZ / 16;

        List<ChunkPos> list = new ArrayList<ChunkPos>();
        list.add(new ChunkPos(MathHelper.floor(x), MathHelper.floor(z)));
        list.add(new ChunkPos(MathHelper.ceil(x), MathHelper.ceil(z)));
        list.add(new ChunkPos(MathHelper.floor(x), MathHelper.ceil(z)));
        list.add(new ChunkPos(MathHelper.ceil(x), MathHelper.floor(z)));

        for (ChunkPos chunk : list) {
            if (!chunks.contains(chunk)) {
                ForgeChunkManager.forceChunk(ticket, chunk);
            } else
                chunks.remove(chunk);
        }

        for (ChunkPos chunk : chunks)
            ForgeChunkManager.unforceChunk(ticket, chunk);

        this.chunks = list;
        return false;
    }

    @Override
    public boolean aiContinueExecute() {
        return false;
    }

    @Override
    public void reset() {
        ChunkController.instance.deleteNPC(npc);
        chunks.clear();
        playerLastSeen = 0;
    }

    public void delete() {
    }
}

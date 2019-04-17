package noppes.npcs.roles;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.CustomNpcs;
import noppes.npcs.api.entity.data.role.IJobBard;
import noppes.npcs.client.controllers.MusicController;
import noppes.npcs.entity.EntityNPCInterface;

import java.util.List;

public class JobBard extends JobInterface implements IJobBard {
    public int minRange = 2;
    public int maxRange = 64;

    public boolean isStreamer = true;
    public boolean hasOffRange = true;

    public String song = "";

    public JobBard(EntityNPCInterface npc) {
        super(npc);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbttagcompound) {
        nbttagcompound.setString("BardSong", song);
        nbttagcompound.setInteger("BardMinRange", minRange);
        nbttagcompound.setInteger("BardMaxRange", maxRange);
        nbttagcompound.setBoolean("BardStreamer", isStreamer);
        nbttagcompound.setBoolean("BardHasOff", hasOffRange);

        return nbttagcompound;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbttagcompound) {
        song = nbttagcompound.getString("BardSong");
        minRange = nbttagcompound.getInteger("BardMinRange");
        maxRange = nbttagcompound.getInteger("BardMaxRange");
        isStreamer = nbttagcompound.getBoolean("BardStreamer");
        hasOffRange = nbttagcompound.getBoolean("BardHasOff");
    }

    private long ticks = 0;

    public void onLivingUpdate() {
        if (!npc.isRemote() || song.isEmpty())
            return;

        if (!MusicController.Instance.isPlaying(song)) {
            List<EntityPlayer> list = npc.world.getEntitiesWithinAABB(EntityPlayer.class, npc.getEntityBoundingBox().grow(minRange, minRange / 2, minRange));
            if (!list.contains(CustomNpcs.proxy.getPlayer()))
                return;
            if (isStreamer)
                MusicController.Instance.playStreaming(song, npc);
            else
                MusicController.Instance.playMusic(song, npc);
        } else if (MusicController.Instance.playingEntity != npc) {
            EntityPlayer player = CustomNpcs.proxy.getPlayer();
            if (npc.getDistanceSq(player) < MusicController.Instance.playingEntity.getDistanceSq(player)) {
                MusicController.Instance.playingEntity = npc;
            }

        } else if (hasOffRange) {
            List<EntityPlayer> list = npc.world.getEntitiesWithinAABB(EntityPlayer.class, npc.getEntityBoundingBox().grow(maxRange, maxRange / 2, maxRange));
            if (!list.contains(CustomNpcs.proxy.getPlayer()))
                MusicController.Instance.stopMusic();
        }

        if (MusicController.Instance.isPlaying(song)) {
            Minecraft.getMinecraft().getMusicTicker().timeUntilNextMusic = 12000;
        }

    }

    @Override
    public void killed() {
        delete();
    }

    @Override
    public void delete() {
        if (npc.world.isRemote && hasOffRange) {
            if (MusicController.Instance.isPlaying(song)) {
                MusicController.Instance.stopMusic();
            }
        }
    }

    @Override
    public String getSong() {
        return this.song;
    }

    @Override
    public void setSong(String song) {
        this.song = song;
        npc.updateClient = true;
    }
}

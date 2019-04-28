package noppes.npcs.controllers.data;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import noppes.npcs.CustomNpcs;
import noppes.npcs.LogWriter;
import noppes.npcs.api.constants.RoleType;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.entity.data.DataTimers;
import noppes.npcs.roles.RoleCompanion;
import noppes.npcs.util.CustomNpcsScheduler;
import noppes.npcs.util.NBTJsonUtil;

import java.io.File;
import java.io.FileInputStream;

public class PlayerData implements ICapabilityProvider {

    @CapabilityInject(PlayerData.class)
    public static Capability<PlayerData> PLAYERDATA_CAPABILITY = null;

    public PlayerDialogData dialogData = new PlayerDialogData();
    public PlayerBankData bankData = new PlayerBankData();
    public PlayerQuestData questData = new PlayerQuestData();
    public PlayerTransportData transportData = new PlayerTransportData();
    public PlayerFactionData factionData = new PlayerFactionData();
    public PlayerItemGiverData itemgiverData = new PlayerItemGiverData();
    public PlayerMailData mailData = new PlayerMailData();
    public PlayerScriptData scriptData;

    public DataTimers timers = new DataTimers(this);

    public EntityNPCInterface editingNpc;
    public NBTTagCompound cloned;

    public EntityPlayer player;

    public String playername = "";
    public String uuid = "";

    private EntityNPCInterface activeCompanion = null;
    public int companionID = 0;

    public int playerLevel = 0;

    public boolean updateClient = false;

    public int dialogId = -1;

    public void setNBT(NBTTagCompound data) {
        dialogData.loadNBTData(data);
        bankData.loadNBTData(data);
        questData.loadNBTData(data);
        transportData.loadNBTData(data);
        factionData.loadNBTData(data);
        itemgiverData.loadNBTData(data);
        mailData.loadNBTData(data);
        timers.readFromNBT(data);

        if (player != null) {
            playername = player.getName();
            uuid = player.getPersistentID().toString();
        } else {
            playername = data.getString("PlayerName");
            uuid = data.getString("UUID");
        }
        companionID = data.getInteger("PlayerCompanionId");

        if (data.hasKey("PlayerCompanion") && !hasCompanion()) {
            EntityCustomNpc npc = new EntityCustomNpc(player.world);
            npc.readEntityFromNBT(data.getCompoundTag("PlayerCompanion"));
            npc.setPosition(player.posX, player.posY, player.posZ);
            if (npc.advanced.role == RoleType.COMPANION) {
                setCompanion(npc);
                ((RoleCompanion) npc.roleInterface).setSitting(false);
                player.world.spawnEntity(npc);
            }
        }
    }

    public NBTTagCompound getSyncNBT() {
        NBTTagCompound compound = new NBTTagCompound();
        dialogData.saveNBTData(compound);
        questData.saveNBTData(compound);
        factionData.saveNBTData(compound);

        return compound;
    }

    public NBTTagCompound getNBT() {
        if (player != null) {
            playername = player.getName();
            uuid = player.getPersistentID().toString();
        }
        NBTTagCompound compound = new NBTTagCompound();
        dialogData.saveNBTData(compound);
        bankData.saveNBTData(compound);
        questData.saveNBTData(compound);
        transportData.saveNBTData(compound);
        factionData.saveNBTData(compound);
        itemgiverData.saveNBTData(compound);
        mailData.saveNBTData(compound);
        timers.writeToNBT(compound);

        compound.setString("PlayerName", playername);
        compound.setString("UUID", uuid);
        compound.setInteger("PlayerCompanionId", companionID);

        if (hasCompanion()) {
            NBTTagCompound nbt = new NBTTagCompound();
            if (activeCompanion.writeToNBTAtomically(nbt))
                compound.setTag("PlayerCompanion", nbt);
        }
        return compound;
    }

    public boolean hasCompanion() {
        return activeCompanion != null && !activeCompanion.isDead;
    }

    public void setCompanion(EntityNPCInterface npc) {
        if (npc != null && npc.advanced.role != RoleType.COMPANION)//shouldnt happen
            return;
        companionID++;
        activeCompanion = npc;
        if (npc != null)
            ((RoleCompanion) npc.roleInterface).companionID = companionID;
        save(false);
    }


    public void updateCompanion(World world) {
        if (!hasCompanion() || world == activeCompanion.world)
            return;
        RoleCompanion role = (RoleCompanion) activeCompanion.roleInterface;
        role.owner = player;
        if (!role.isFollowing())
            return;
        NBTTagCompound nbt = new NBTTagCompound();
        activeCompanion.writeToNBTAtomically(nbt);
        activeCompanion.isDead = true;

        EntityCustomNpc npc = new EntityCustomNpc(world);
        npc.readEntityFromNBT(nbt);
        npc.setPosition(player.posX, player.posY, player.posZ);
        setCompanion(npc);
        ((RoleCompanion) npc.roleInterface).setSitting(false);
        world.spawnEntity(npc);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        return capability == PLAYERDATA_CAPABILITY;
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (hasCapability(capability, facing))
            return (T) this;
        return null;
    }

    private static final ResourceLocation key = new ResourceLocation("customnpcs", "playerdata");

    public static void register(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof EntityPlayer) {
            event.addCapability(key, new PlayerData());
        }
    }

    public synchronized void save(boolean update) {
        final NBTTagCompound compound = getNBT();
        final String filename = uuid + ".json";

        CustomNpcsScheduler.runTack(() -> {
            try {
                File saveDir = CustomNpcs.INSTANCE.getWorldSaveDirectory("playerdata");
                File file = new File(saveDir, filename + "_new");
                File file1 = new File(saveDir, filename);
                NBTJsonUtil.SaveFile(file, compound);
                if (file1.exists()) {
                    file1.delete();
                }
                file.renameTo(file1);
            } catch (Exception e) {
                LogWriter.except(e);
            }
        });

        if (update)
            updateClient = true;
    }

    public static NBTTagCompound loadPlayerDataOld(String player) {
        File saveDir = CustomNpcs.INSTANCE.getWorldSaveDirectory("playerdata");
        String filename = player;
        if (filename.isEmpty())
            filename = "noplayername";
        filename += ".dat";
        try {
            File file = new File(saveDir, filename);
            if (file.exists()) {
                NBTTagCompound comp = CompressedStreamTools.readCompressed(new FileInputStream(file));
                file.delete();
                file = new File(saveDir, filename + "_old");
                if (file.exists())
                    file.delete();
                return comp;
            }
        } catch (Exception e) {
            LogWriter.except(e);
        }
        try {
            File file = new File(saveDir, filename + "_old");
            if (file.exists()) {
                return CompressedStreamTools.readCompressed(new FileInputStream(file));
            }

        } catch (Exception e) {
            LogWriter.except(e);
        }

        return new NBTTagCompound();
    }

    public static NBTTagCompound loadPlayerData(String player) {
        File saveDir = CustomNpcs.INSTANCE.getWorldSaveDirectory("playerdata");
        String filename = player;
        if (filename.isEmpty())
            filename = "noplayername";
        filename += ".json";
        File file = null;
        try {
            file = new File(saveDir, filename);
            if (file.exists()) {
                return NBTJsonUtil.LoadFile(file);
            }
        } catch (Exception e) {
            LogWriter.error("Error loading: " + file.getAbsolutePath(), e);
        }

        return new NBTTagCompound();
    }

    public static PlayerData get(EntityPlayer player) {
        if (player.world.isRemote)
            return CustomNpcs.proxy.getPlayerData(player);
        PlayerData data = player.getCapability(PLAYERDATA_CAPABILITY, null);
        if (data.player == null) {
            data.player = player;
            data.playerLevel = player.experienceLevel;
            data.scriptData = new PlayerScriptData(player);

            NBTTagCompound compound = loadPlayerData(player.getPersistentID().toString());
            if (compound.isEmpty()) {
                compound = loadPlayerDataOld(player.getName());
            }
            data.setNBT(compound);
        }
        return data;
    }
}

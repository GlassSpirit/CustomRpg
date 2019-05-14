package noppes.npcs.common.entity.data;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import noppes.npcs.api.constants.JobType;
import noppes.npcs.api.constants.OptionType;
import noppes.npcs.api.constants.RoleType;
import noppes.npcs.api.entity.data.INPCAdvanced;
import noppes.npcs.controllers.data.DialogOption;
import noppes.npcs.controllers.data.FactionOptions;
import noppes.npcs.controllers.data.Line;
import noppes.npcs.controllers.data.Lines;
import noppes.npcs.common.entity.EntityNPCInterface;
import noppes.npcs.roles.*;
import noppes.npcs.util.ValueUtil;

import java.util.HashMap;

public class DataAdvanced implements INPCAdvanced {

    public Lines interactLines = new Lines();
    public Lines worldLines = new Lines();
    public Lines attackLines = new Lines();
    public Lines killedLines = new Lines();
    public Lines killLines = new Lines();
    public Lines npcInteractLines = new Lines();

    public boolean orderedLines = false;

    private String idleSound = "";
    private String angrySound = "";
    private String hurtSound = "minecraft:entity.player.hurt";
    private String deathSound = "minecraft:entity.player.hurt";
    private String stepSound = "";

    private EntityNPCInterface npc;
    public FactionOptions factions = new FactionOptions();

    public int role = RoleType.NONE;
    public int job = JobType.NONE;

    public boolean attackOtherFactions = false;
    public boolean defendFaction = false;
    public boolean disablePitch = false;
    public DataScenes scenes;

    public DataAdvanced(EntityNPCInterface npc) {
        this.npc = npc;
        scenes = new DataScenes(npc);
    }

    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setTag("NpcLines", worldLines.writeToNBT());
        compound.setTag("NpcKilledLines", killedLines.writeToNBT());
        compound.setTag("NpcInteractLines", interactLines.writeToNBT());
        compound.setTag("NpcAttackLines", attackLines.writeToNBT());
        compound.setTag("NpcKillLines", killLines.writeToNBT());
        compound.setTag("NpcInteractNPCLines", npcInteractLines.writeToNBT());

        compound.setBoolean("OrderedLines", orderedLines);

        compound.setString("NpcIdleSound", idleSound);
        compound.setString("NpcAngrySound", angrySound);
        compound.setString("NpcHurtSound", hurtSound);
        compound.setString("NpcDeathSound", deathSound);
        compound.setString("NpcStepSound", stepSound);

        compound.setInteger("FactionID", npc.getFaction().id);
        compound.setBoolean("AttackOtherFactions", attackOtherFactions);
        compound.setBoolean("DefendFaction", defendFaction);
        compound.setBoolean("DisablePitch", disablePitch);

        compound.setInteger("Role", role);
        compound.setInteger("NpcJob", job);
        compound.setTag("FactionPoints", factions.writeToNBT(new NBTTagCompound()));

        compound.setTag("NPCDialogOptions", nbtDialogs(npc.dialogs));

        compound.setTag("NpcScenes", scenes.writeToNBT(new NBTTagCompound()));

        return compound;
    }

    public void readToNBT(NBTTagCompound compound) {
        interactLines.readNBT(compound.getCompoundTag("NpcInteractLines"));
        worldLines.readNBT(compound.getCompoundTag("NpcLines"));
        attackLines.readNBT(compound.getCompoundTag("NpcAttackLines"));
        killedLines.readNBT(compound.getCompoundTag("NpcKilledLines"));
        killLines.readNBT(compound.getCompoundTag("NpcKillLines"));
        npcInteractLines.readNBT(compound.getCompoundTag("NpcInteractNPCLines"));

        orderedLines = compound.getBoolean("OrderedLines");

        idleSound = compound.getString("NpcIdleSound");
        angrySound = compound.getString("NpcAngrySound");
        hurtSound = compound.getString("NpcHurtSound");
        deathSound = compound.getString("NpcDeathSound");
        stepSound = compound.getString("NpcStepSound");

        npc.setFaction(compound.getInteger("FactionID"));
        npc.faction = npc.getFaction();
        attackOtherFactions = compound.getBoolean("AttackOtherFactions");
        defendFaction = compound.getBoolean("DefendFaction");
        disablePitch = compound.getBoolean("DisablePitch");

        setRole(compound.getInteger("Role"));
        setJob(compound.getInteger("NpcJob"));

        factions.readFromNBT(compound.getCompoundTag("FactionPoints"));

        npc.dialogs = getDialogs(compound.getTagList("NPCDialogOptions", 10));

        scenes.readFromNBT(compound.getCompoundTag("NpcScenes"));
    }

    private HashMap<Integer, DialogOption> getDialogs(NBTTagList tagList) {
        HashMap<Integer, DialogOption> map = new HashMap<Integer, DialogOption>();
        for (int i = 0; i < tagList.tagCount(); i++) {
            NBTTagCompound nbttagcompound = tagList.getCompoundTagAt(i);
            int slot = nbttagcompound.getInteger("DialogSlot");
            DialogOption option = new DialogOption();
            option.readNBT(nbttagcompound.getCompoundTag("NPCDialog"));
            option.optionType = OptionType.DIALOG_OPTION;
            map.put(slot, option);

        }
        return map;
    }


    private NBTTagList nbtDialogs(HashMap<Integer, DialogOption> dialogs2) {
        NBTTagList nbttaglist = new NBTTagList();
        for (int slot : dialogs2.keySet()) {
            NBTTagCompound nbttagcompound = new NBTTagCompound();
            nbttagcompound.setInteger("DialogSlot", slot);
            nbttagcompound.setTag("NPCDialog", dialogs2.get(slot)
                    .writeNBT());
            nbttaglist.appendTag(nbttagcompound);
        }
        return nbttaglist;
    }

    private Lines getLines(int type) {
        if (type == 0)
            return interactLines;
        if (type == 1)
            return attackLines;
        if (type == 2)
            return worldLines;
        if (type == 3)
            return killedLines;
        if (type == 4)
            return killLines;
        if (type == 5)
            return npcInteractLines;

        return null;
    }

    @Override
    public void setLine(int type, int slot, String text, String sound) {
        slot = ValueUtil.CorrectInt(slot, 0, 7);
        Lines lines = getLines(type);
        if (text == null || text.isEmpty())
            lines.lines.remove(slot);
        else {
            Line line = lines.lines.get(slot);
            line.text = text;
            if (sound != null)
                line.sound = sound;
        }
    }

    @Override
    public String getLine(int type, int slot) {
        Line line = getLines(type).lines.get(slot);
        if (line == null)
            return null;
        return line.text;
    }

    @Override
    public int getLineCount(int type) {
        return getLines(type).lines.size();
    }

    @Override
    public String getSound(int type) {
        String sound = null;
        if (type == 0)
            sound = idleSound;
        else if (type == 1)
            sound = angrySound;
        else if (type == 2)
            sound = hurtSound;
        else if (type == 3)
            sound = deathSound;
        else if (type == 4)
            sound = stepSound;

        if (sound != null && sound.isEmpty())
            return null;
        return sound;
    }

    public SoundEvent getSoundEvent(int type) {
        String sound = getSound(type);
        if (sound == null)
            return null;
        ResourceLocation res = new ResourceLocation(sound);
        SoundEvent ev = SoundEvent.REGISTRY.getObject(res);
        if (ev != null) {
            return ev;
        }
        return new SoundEvent(res);
    }

    @Override
    public void setSound(int type, String sound) {
        if (sound == null)
            sound = "";
        if (type == 0)
            idleSound = sound;
        else if (type == 1)
            angrySound = sound;
        else if (type == 2)
            hurtSound = sound;
        else if (type == 3)
            deathSound = sound;
        else if (type == 4)
            stepSound = sound;
    }


    public Line getInteractLine() {
        return interactLines.getLine(!orderedLines);
    }

    public Line getAttackLine() {
        return attackLines.getLine(!orderedLines);
    }

    public Line getKilledLine() {
        return killedLines.getLine(!orderedLines);
    }

    public Line getKillLine() {
        return killLines.getLine(!orderedLines);
    }

    public Line getWorldLine() {
        return worldLines.getLine(!orderedLines);
    }

    public Line getNPCInteractLine() {
        return npcInteractLines.getLine(!orderedLines);
    }

    public void setRole(int i) {
        if (RoleType.MAXSIZE <= i) {
            i -= 2;
        }
        role = i % RoleType.MAXSIZE;
        if (role == RoleType.NONE)
            npc.roleInterface = null;
        else if (role == RoleType.BANK && !(npc.roleInterface instanceof RoleBank))
            npc.roleInterface = new RoleBank(npc);
        else if (role == RoleType.FOLLOWER && !(npc.roleInterface instanceof RoleFollower))
            npc.roleInterface = new RoleFollower(npc);
        else if (role == RoleType.MAILMAN && !(npc.roleInterface instanceof RolePostman))
            npc.roleInterface = new RolePostman(npc);
        else if (role == RoleType.TRADER && !(npc.roleInterface instanceof RoleTrader))
            npc.roleInterface = new RoleTrader(npc);
        else if (role == RoleType.TRANSPORTER && !(npc.roleInterface instanceof RoleTransporter))
            npc.roleInterface = new RoleTransporter(npc);
        else if (role == RoleType.COMPANION && !(npc.roleInterface instanceof RoleCompanion))
            npc.roleInterface = new RoleCompanion(npc);
        else if (role == RoleType.DIALOG && !(npc.roleInterface instanceof RoleDialog))
            npc.roleInterface = new RoleDialog(npc);
    }

    public void setJob(int i) {
        if (npc.jobInterface != null && !npc.world.isRemote)
            npc.jobInterface.reset();

        job = i % JobType.MAXSIZE;
        if (job == JobType.NONE)
            npc.jobInterface = null;
        else if (job == JobType.BARD && !(npc.jobInterface instanceof JobBard))
            npc.jobInterface = new JobBard(npc);
        else if (job == JobType.HEALER && !(npc.jobInterface instanceof JobHealer))
            npc.jobInterface = new JobHealer(npc);
        else if (job == JobType.GUARD && !(npc.jobInterface instanceof JobGuard))
            npc.jobInterface = new JobGuard(npc);
        else if (job == JobType.ITEMGIVER && !(npc.jobInterface instanceof JobItemGiver))
            npc.jobInterface = new JobItemGiver(npc);
        else if (job == JobType.FOLLOWER && !(npc.jobInterface instanceof JobFollower))
            npc.jobInterface = new JobFollower(npc);
        else if (job == JobType.SPAWNER && !(npc.jobInterface instanceof JobSpawner))
            npc.jobInterface = new JobSpawner(npc);
        else if (job == JobType.CONVERSATION && !(npc.jobInterface instanceof JobConversation))
            npc.jobInterface = new JobConversation(npc);
        else if (job == JobType.CHUNKLOADER && !(npc.jobInterface instanceof JobChunkLoader))
            npc.jobInterface = new JobChunkLoader(npc);
        else if (job == JobType.PUPPET && !(npc.jobInterface instanceof JobPuppet))
            npc.jobInterface = new JobPuppet(npc);
        else if (job == JobType.BUILDER && !(npc.jobInterface instanceof JobBuilder))
            npc.jobInterface = new JobBuilder(npc);
        else if (job == JobType.FARMER && !(npc.jobInterface instanceof JobFarmer))
            npc.jobInterface = new JobFarmer(npc);
    }

    public boolean hasWorldLines() {
        return !worldLines.isEmpty();
    }
}

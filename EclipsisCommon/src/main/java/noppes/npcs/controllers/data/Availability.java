package noppes.npcs.controllers.data;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.WorldServer;
import noppes.npcs.CustomNpcs;
import noppes.npcs.ICompatibilty;
import noppes.npcs.VersionCompatibility;
import noppes.npcs.api.CustomNPCsException;
import noppes.npcs.api.entity.IPlayer;
import noppes.npcs.api.handler.data.IAvailability;
import noppes.npcs.constants.*;
import noppes.npcs.controllers.FactionController;
import noppes.npcs.controllers.PlayerQuestController;

import java.util.HashSet;

public class Availability implements ICompatibilty, IAvailability {
    public static HashSet<String> scoreboardValues = new HashSet<String>();

    public int version = VersionCompatibility.ModRev;

    public EnumAvailabilityDialog dialogAvailable = EnumAvailabilityDialog.Always;
    public EnumAvailabilityDialog dialog2Available = EnumAvailabilityDialog.Always;
    public EnumAvailabilityDialog dialog3Available = EnumAvailabilityDialog.Always;
    public EnumAvailabilityDialog dialog4Available = EnumAvailabilityDialog.Always;
    public int dialogId = -1;
    public int dialog2Id = -1;
    public int dialog3Id = -1;
    public int dialog4Id = -1;

    public EnumAvailabilityQuest questAvailable = EnumAvailabilityQuest.Always;
    public EnumAvailabilityQuest quest2Available = EnumAvailabilityQuest.Always;
    public EnumAvailabilityQuest quest3Available = EnumAvailabilityQuest.Always;
    public EnumAvailabilityQuest quest4Available = EnumAvailabilityQuest.Always;
    public int questId = -1;
    public int quest2Id = -1;
    public int quest3Id = -1;
    public int quest4Id = -1;

    public EnumDayTime daytime = EnumDayTime.Always;

    public int factionId = -1;
    public int faction2Id = -1;

    public EnumAvailabilityFactionType factionAvailable = EnumAvailabilityFactionType.Always;
    public EnumAvailabilityFactionType faction2Available = EnumAvailabilityFactionType.Always;

    public EnumAvailabilityFaction factionStance = EnumAvailabilityFaction.Friendly;
    public EnumAvailabilityFaction faction2Stance = EnumAvailabilityFaction.Friendly;

    public EnumAvailabilityScoreboard scoreboardType = EnumAvailabilityScoreboard.EQUAL;
    public EnumAvailabilityScoreboard scoreboard2Type = EnumAvailabilityScoreboard.EQUAL;

    public String scoreboardObjective = "";
    public String scoreboard2Objective = "";

    public int scoreboardValue = 1;
    public int scoreboard2Value = 1;

    public int minPlayerLevel = 0;

    public void readFromNBT(NBTTagCompound compound) {
        version = compound.getInteger("ModRev");
        VersionCompatibility.CheckAvailabilityCompatibility(this, compound);

        dialogAvailable = EnumAvailabilityDialog.values()[compound.getInteger("AvailabilityDialog")];
        dialog2Available = EnumAvailabilityDialog.values()[compound.getInteger("AvailabilityDialog2")];
        dialog3Available = EnumAvailabilityDialog.values()[compound.getInteger("AvailabilityDialog3")];
        dialog4Available = EnumAvailabilityDialog.values()[compound.getInteger("AvailabilityDialog4")];

        dialogId = compound.getInteger("AvailabilityDialogId");
        dialog2Id = compound.getInteger("AvailabilityDialog2Id");
        dialog3Id = compound.getInteger("AvailabilityDialog3Id");
        dialog4Id = compound.getInteger("AvailabilityDialog4Id");

        questAvailable = EnumAvailabilityQuest.values()[compound.getInteger("AvailabilityQuest")];
        quest2Available = EnumAvailabilityQuest.values()[compound.getInteger("AvailabilityQuest2")];
        quest3Available = EnumAvailabilityQuest.values()[compound.getInteger("AvailabilityQuest3")];
        quest4Available = EnumAvailabilityQuest.values()[compound.getInteger("AvailabilityQuest4")];

        questId = compound.getInteger("AvailabilityQuestId");
        quest2Id = compound.getInteger("AvailabilityQuest2Id");
        quest3Id = compound.getInteger("AvailabilityQuest3Id");
        quest4Id = compound.getInteger("AvailabilityQuest4Id");

        setFactionAvailability(compound.getInteger("AvailabilityFaction"));
        setFactionAvailabilityStance(compound.getInteger("AvailabilityFactionStance"));

        setFaction2Availability(compound.getInteger("AvailabilityFaction2"));
        setFaction2AvailabilityStance(compound.getInteger("AvailabilityFaction2Stance"));

        factionId = compound.getInteger("AvailabilityFactionId");
        faction2Id = compound.getInteger("AvailabilityFaction2Id");

        scoreboardObjective = compound.getString("AvailabilityScoreboardObjective");
        scoreboard2Objective = compound.getString("AvailabilityScoreboard2Objective");

        initScore(scoreboardObjective);
        initScore(scoreboard2Objective);

        scoreboardType = EnumAvailabilityScoreboard.values()[compound.getInteger("AvailabilityScoreboardType")];
        scoreboard2Type = EnumAvailabilityScoreboard.values()[compound.getInteger("AvailabilityScoreboard2Type")];

        scoreboardValue = compound.getInteger("AvailabilityScoreboardValue");
        scoreboard2Value = compound.getInteger("AvailabilityScoreboard2Value");

        daytime = EnumDayTime.values()[compound.getInteger("AvailabilityDayTime")];

        minPlayerLevel = compound.getInteger("AvailabilityMinPlayerLevel");
    }

    private void initScore(String objective) {
        if (objective.isEmpty() || scoreboardValues.contains(objective))
            return;
        scoreboardValues.add(objective);

        if (CustomNpcs.Server == null)
            return;
        for (WorldServer world : CustomNpcs.Server.worlds) {
            ServerScoreboard board = (ServerScoreboard) world.worldScoreboard;
            ScoreObjective so = board.getObjective(objective);
            if (so != null) {
                board.addObjective(so);
            }
        }
    }

    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setInteger("ModRev", version);

        compound.setInteger("AvailabilityDialog", dialogAvailable.ordinal());
        compound.setInteger("AvailabilityDialog2", dialog2Available.ordinal());
        compound.setInteger("AvailabilityDialog3", dialog3Available.ordinal());
        compound.setInteger("AvailabilityDialog4", dialog4Available.ordinal());

        compound.setInteger("AvailabilityDialogId", dialogId);
        compound.setInteger("AvailabilityDialog2Id", dialog2Id);
        compound.setInteger("AvailabilityDialog3Id", dialog3Id);
        compound.setInteger("AvailabilityDialog4Id", dialog4Id);

        compound.setInteger("AvailabilityQuest", questAvailable.ordinal());
        compound.setInteger("AvailabilityQuest2", quest2Available.ordinal());
        compound.setInteger("AvailabilityQuest3", quest3Available.ordinal());
        compound.setInteger("AvailabilityQuest4", quest4Available.ordinal());

        compound.setInteger("AvailabilityQuestId", questId);
        compound.setInteger("AvailabilityQuest2Id", quest2Id);
        compound.setInteger("AvailabilityQuest3Id", quest3Id);
        compound.setInteger("AvailabilityQuest4Id", quest4Id);

        compound.setInteger("AvailabilityFaction", factionAvailable.ordinal());
        compound.setInteger("AvailabilityFaction2", faction2Available.ordinal());

        compound.setInteger("AvailabilityFactionStance", factionStance.ordinal());
        compound.setInteger("AvailabilityFaction2Stance", faction2Stance.ordinal());

        compound.setInteger("AvailabilityFactionId", factionId);
        compound.setInteger("AvailabilityFaction2Id", faction2Id);

        compound.setString("AvailabilityScoreboardObjective", scoreboardObjective);
        compound.setString("AvailabilityScoreboard2Objective", scoreboard2Objective);

        compound.setInteger("AvailabilityScoreboardType", scoreboardType.ordinal());
        compound.setInteger("AvailabilityScoreboard2Type", scoreboard2Type.ordinal());

        compound.setInteger("AvailabilityScoreboardValue", scoreboardValue);
        compound.setInteger("AvailabilityScoreboard2Value", scoreboard2Value);

        compound.setInteger("AvailabilityDayTime", daytime.ordinal());
        compound.setInteger("AvailabilityMinPlayerLevel", minPlayerLevel);
        return compound;
    }

    public void setFactionAvailability(int value) {
        factionAvailable = EnumAvailabilityFactionType.values()[value];
    }

    public void setFaction2Availability(int value) {
        faction2Available = EnumAvailabilityFactionType.values()[value];
    }

    public void setFactionAvailabilityStance(int integer) {
        factionStance = EnumAvailabilityFaction.values()[integer];
    }

    public void setFaction2AvailabilityStance(int integer) {
        faction2Stance = EnumAvailabilityFaction.values()[integer];
    }

    public boolean isAvailable(EntityPlayer player) {
        if (daytime == EnumDayTime.Day) {
            long time = player.world.getWorldTime() % 24000;
            if (time > 12000)
                return false;
        }
        if (daytime == EnumDayTime.Night) {
            long time = player.world.getWorldTime() % 24000;
            if (time < 12000)
                return false;
        }

        if (!dialogAvailable(dialogId, dialogAvailable, player))
            return false;
        if (!dialogAvailable(dialog2Id, dialog2Available, player))
            return false;
        if (!dialogAvailable(dialog3Id, dialog3Available, player))
            return false;
        if (!dialogAvailable(dialog4Id, dialog4Available, player))
            return false;

        if (!questAvailable(questId, questAvailable, player))
            return false;
        if (!questAvailable(quest2Id, quest2Available, player))
            return false;
        if (!questAvailable(quest3Id, quest3Available, player))
            return false;
        if (!questAvailable(quest4Id, quest4Available, player))
            return false;

        if (!factionAvailable(factionId, factionStance, factionAvailable, player))
            return false;
        if (!factionAvailable(faction2Id, faction2Stance, faction2Available, player))
            return false;

        if (!scoreboardAvailable(player, scoreboardObjective, scoreboardType, scoreboardValue))
            return false;
        if (!scoreboardAvailable(player, scoreboard2Objective, scoreboard2Type, scoreboard2Value))
            return false;

        return player.experienceLevel >= minPlayerLevel;
    }

    private boolean scoreboardAvailable(EntityPlayer player, String objective, EnumAvailabilityScoreboard type, int value) {
        if (objective.isEmpty())
            return true;

        ScoreObjective sbObjective = player.getWorldScoreboard().getObjective(objective);
        if (sbObjective == null)
            return false;

        if (!player.getWorldScoreboard().entityHasObjective(player.getName(), sbObjective))
            return false;

        int i = player.getWorldScoreboard().getOrCreateScore(player.getName(), sbObjective).getScorePoints();
        if (type == EnumAvailabilityScoreboard.EQUAL)
            return i == value;
        if (type == EnumAvailabilityScoreboard.BIGGER)
            return i > value;
        return i < value;
    }

    private boolean factionAvailable(int id, EnumAvailabilityFaction stance, EnumAvailabilityFactionType available, EntityPlayer player) {
        if (available == EnumAvailabilityFactionType.Always)
            return true;

        Faction faction = FactionController.instance.getFaction(id);
        if (faction == null)
            return true;

        PlayerFactionData data = PlayerData.get(player).factionData;
        int points = data.getFactionPoints(player, id);

        EnumAvailabilityFaction current = EnumAvailabilityFaction.Neutral;
        if (points < faction.neutralPoints)
            current = EnumAvailabilityFaction.Hostile;
        if (points >= faction.friendlyPoints)
            current = EnumAvailabilityFaction.Friendly;

        if (available == EnumAvailabilityFactionType.Is && stance == current) {
            return true;
        }
        return available == EnumAvailabilityFactionType.IsNot && stance != current;

    }

    public boolean dialogAvailable(int id, EnumAvailabilityDialog en, EntityPlayer player) {
        if (en == EnumAvailabilityDialog.Always)
            return true;
        boolean hasRead = PlayerData.get(player).dialogData.dialogsRead.contains(id);
        if (hasRead && en == EnumAvailabilityDialog.After)
            return true;
        else return !hasRead && en == EnumAvailabilityDialog.Before;
    }

    public boolean questAvailable(int id, EnumAvailabilityQuest en, EntityPlayer player) {
        if (en == EnumAvailabilityQuest.Always)
            return true;
        else if (en == EnumAvailabilityQuest.After && PlayerQuestController.isQuestFinished(player, id))
            return true;
        else if (en == EnumAvailabilityQuest.Before && !PlayerQuestController.isQuestFinished(player, id))
            return true;
        else if (en == EnumAvailabilityQuest.Active && PlayerQuestController.isQuestActive(player, id))
            return true;
        else if (en == EnumAvailabilityQuest.NotActive && !PlayerQuestController.isQuestActive(player, id))
            return true;
        else return en == EnumAvailabilityQuest.Completed && PlayerQuestController.isQuestCompleted(player, id);
    }

    @Override
    public int getVersion() {
        return version;
    }

    @Override
    public void setVersion(int version) {
        this.version = version;
    }

    @Override
    public boolean isAvailable(IPlayer player) {
        return isAvailable(player.getMCEntity());
    }

    @Override
    public int getDaytime() {
        return daytime.ordinal();
    }

    @Override
    public void setDaytime(int type) {
        daytime = EnumDayTime.values()[MathHelper.clamp(type, 0, 2)];
    }

    @Override
    public int getMinPlayerLevel() {
        return minPlayerLevel;
    }

    @Override
    public void setMinPlayerLevel(int level) {
        this.minPlayerLevel = level;
    }

    @Override
    public int getDialog(int i) {
        if (i < 0 && i > 3)
            throw new CustomNPCsException(i + " isnt between 0 and 3");
        if (i == 0) {
            return dialogId;
        } else if (i == 1) {
            return dialog2Id;
        } else if (i == 2) {
            return dialog3Id;
        }
        return dialog4Id;
    }

    @Override
    public void setDialog(int i, int id, int type) {
        if (i < 0 && i > 3)
            throw new CustomNPCsException(i + " isnt between 0 and 3");
        EnumAvailabilityDialog e = EnumAvailabilityDialog.values()[MathHelper.clamp(type, 0, 2)];
        if (i == 0) {
            dialogId = id;
            dialogAvailable = e;
        } else if (i == 1) {
            dialog2Id = id;
            dialog2Available = e;
        } else if (i == 2) {
            dialog3Id = id;
            dialog3Available = e;
        } else if (i == 3) {
            dialog4Id = id;
            dialog4Available = e;
        }
    }

    @Override
    public void removeDialog(int i) {
        if (i < 0 && i > 3)
            throw new CustomNPCsException(i + " isnt between 0 and 3");
        if (i == 0) {
            dialogId = -1;
            dialogAvailable = EnumAvailabilityDialog.Always;
        } else if (i == 1) {
            dialog2Id = -1;
            dialog2Available = EnumAvailabilityDialog.Always;
        } else if (i == 2) {
            dialog3Id = -1;
            dialog3Available = EnumAvailabilityDialog.Always;
        } else if (i == 3) {
            dialog4Id = -1;
            dialog4Available = EnumAvailabilityDialog.Always;
        }
    }

    @Override
    public int getQuest(int i) {
        if (i < 0 && i > 3)
            throw new CustomNPCsException(i + " isnt between 0 and 3");
        if (i == 0) {
            return questId;
        } else if (i == 1) {
            return quest2Id;
        } else if (i == 2) {
            return quest3Id;
        }
        return quest4Id;
    }

    @Override
    public void setQuest(int i, int id, int type) {
        if (i < 0 && i > 3)
            throw new CustomNPCsException(i + " isnt between 0 and 3");
        EnumAvailabilityQuest e = EnumAvailabilityQuest.values()[MathHelper.clamp(type, 0, 5)];
        if (i == 0) {
            questId = id;
            questAvailable = e;
        } else if (i == 1) {
            quest2Id = id;
            quest2Available = e;
        } else if (i == 2) {
            quest3Id = id;
            quest3Available = e;
        } else if (i == 3) {
            quest4Id = id;
            quest4Available = e;
        }
    }

    @Override
    public void removeQuest(int i) {
        if (i < 0 && i > 3)
            throw new CustomNPCsException(i + " isnt between 0 and 3");
        if (i == 0) {
            questId = -1;
            questAvailable = EnumAvailabilityQuest.Always;
        } else if (i == 1) {
            quest2Id = -1;
            quest2Available = EnumAvailabilityQuest.Always;
        } else if (i == 2) {
            quest3Id = -1;
            quest3Available = EnumAvailabilityQuest.Always;
        } else if (i == 3) {
            quest4Id = -1;
            quest4Available = EnumAvailabilityQuest.Always;
        }
    }

    @Override
    public void setFaction(int i, int id, int type, int stance) {
        if (i < 0 && i > 1)
            throw new CustomNPCsException(i + " isnt between 0 and 1");

        EnumAvailabilityFactionType e = EnumAvailabilityFactionType.values()[MathHelper.clamp(type, 0, 2)];
        EnumAvailabilityFaction ee = EnumAvailabilityFaction.values()[MathHelper.clamp(stance, 0, 2)];
        if (i == 0) {
            factionId = id;
            factionAvailable = e;
            factionStance = ee;
        } else if (i == 1) {
            faction2Id = id;
            faction2Available = e;
            faction2Stance = ee;
        }
    }

    @Override
    public void setScoreboard(int i, String objective, int type, int value) {
        if (i < 0 && i > 1)
            throw new CustomNPCsException(i + " isnt between 0 and 1");
        if (objective == null)
            objective = "";

        EnumAvailabilityScoreboard e = EnumAvailabilityScoreboard.values()[MathHelper.clamp(type, 0, 2)];
        if (i == 0) {
            scoreboardObjective = objective;
            scoreboardType = e;
            scoreboardValue = value;
        } else if (i == 1) {
            scoreboard2Objective = objective;
            scoreboard2Type = e;
            scoreboard2Value = value;
        }
    }

    @Override
    public void removeFaction(int i) {
        if (i < 0 && i > 1)
            throw new CustomNPCsException(i + " isnt between 0 and 1");

        if (i == 0) {
            factionId = -1;
            factionAvailable = EnumAvailabilityFactionType.Always;
            factionStance = EnumAvailabilityFaction.Friendly;
        } else if (i == 1) {
            faction2Id = -1;
            faction2Available = EnumAvailabilityFactionType.Always;
            faction2Stance = EnumAvailabilityFaction.Friendly;
        }
    }
}

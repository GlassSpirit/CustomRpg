package noppes.npcs.api.wrapper;

import net.minecraft.command.CommandBase;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.scoreboard.*;
import net.minecraft.server.MinecraftServer;
import noppes.npcs.api.CustomNPCsException;
import noppes.npcs.api.IScoreboard;
import noppes.npcs.api.IScoreboardObjective;
import noppes.npcs.api.IScoreboardTeam;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ScoreboardWrapper implements IScoreboard {
    private Scoreboard board;
    private MinecraftServer server;

    protected ScoreboardWrapper(MinecraftServer server) {
        this.server = server;
        board = server.getWorld(0).getScoreboard();
    }

    @Override
    public IScoreboardObjective[] getObjectives() {
        List<ScoreObjective> collection = new ArrayList<ScoreObjective>(board.getScoreObjectives());
        IScoreboardObjective[] objectives = new IScoreboardObjective[collection.size()];
        for (int i = 0; i < collection.size(); i++) {
            objectives[i] = new ScoreboardObjectiveWrapper(board, collection.get(i));
        }
        return objectives;
    }

    @Override
    public String[] getPlayerList() {
        Collection<String> collection = board.getObjectiveNames();
        return collection.toArray(new String[collection.size()]);
    }

    @Override
    public IScoreboardObjective getObjective(String name) {
        ScoreObjective obj = board.getObjective(name);
        if (obj == null)
            return null;
        return new ScoreboardObjectiveWrapper(board, obj);
    }

    @Override
    public boolean hasObjective(String objective) {
        return board.getObjective(objective) != null;
    }

    @Override
    public void removeObjective(String objective) {
        ScoreObjective obj = board.getObjective(objective);
        if (obj != null)
            board.removeObjective(obj);
    }

    @Override
    public IScoreboardObjective addObjective(String objective, String criteria) {
        IScoreCriteria icriteria = IScoreCriteria.INSTANCES.get(criteria);
        if (icriteria == null)
            throw new CustomNPCsException("Unknown score criteria: %s", criteria);

        if (objective.length() <= 0 || objective.length() > 16)
            throw new CustomNPCsException("Score objective must be between 1-16 characters: %s", objective);

        ScoreObjective obj = board.addScoreObjective(objective, icriteria);
        return new ScoreboardObjectiveWrapper(board, obj);
    }

    @Override
    public void setPlayerScore(String player, String objective, int score, String datatag) {
        ScoreObjective objec = getObjectiveWithException(objective);
        if (objec.getCriteria().isReadOnly() || score < Integer.MIN_VALUE || score > Integer.MAX_VALUE || !test(datatag))
            return;

        Score sco = board.getOrCreateScore(player, objec);
        sco.setScorePoints(score);
    }

    private boolean test(String datatag) {
        if (datatag.isEmpty())
            return true;
        try {
            Entity entity = CommandBase.getEntity(server, server, datatag);
            NBTTagCompound nbttagcompound = JsonToNBT.getTagFromJson(datatag);
            NBTTagCompound nbttagcompound1 = new NBTTagCompound();
            entity.writeToNBT(nbttagcompound1);

            return NBTUtil.areNBTEquals(nbttagcompound, nbttagcompound1, true);
        } catch (Exception e) {
            return false;
        }
    }

    private ScoreObjective getObjectiveWithException(String objective) {
        ScoreObjective objec = board.getObjective(objective);
        if (objec == null)
            throw new CustomNPCsException("Score objective does not exist: %s", objective);
        return objec;
    }

    @Override
    public int getPlayerScore(String player, String objective, String datatag) {
        ScoreObjective objec = getObjectiveWithException(objective);
        if (objec.getCriteria().isReadOnly() || !test(datatag))
            return 0;

        return board.getOrCreateScore(player, objec).getScorePoints();
    }

    @Override
    public boolean hasPlayerObjective(String player, String objective, String datatag) {
        ScoreObjective objec = getObjectiveWithException(objective);
        if (!test(datatag))
            return false;
        return board.getObjectivesForEntity(player).get(objec) != null;
    }

    @Override
    public void deletePlayerScore(String player, String objective, String datatag) {
        ScoreObjective objec = getObjectiveWithException(objective);
        if (!test(datatag))
            return;

        if (board.getObjectivesForEntity(player).remove(objec) != null)
            board.removePlayerFromTeams(player);
    }

    @Override
    public IScoreboardTeam[] getTeams() {
        List<ScorePlayerTeam> list = new ArrayList<ScorePlayerTeam>(board.getTeams());
        IScoreboardTeam[] teams = new IScoreboardTeam[list.size()];
        for (int i = 0; i < list.size(); i++) {
            teams[i] = new ScoreboardTeamWrapper(list.get(i), board);
        }
        return teams;
    }

    @Override
    public boolean hasTeam(String name) {
        return board.getTeam(name) != null;
    }

    @Override
    public IScoreboardTeam addTeam(String name) {
        if (hasTeam(name))
            throw new CustomNPCsException("Team %s already exists", name);
        return new ScoreboardTeamWrapper(board.createTeam(name), board);
    }

    @Override
    public IScoreboardTeam getTeam(String name) {
        ScorePlayerTeam team = board.getTeam(name);
        if (team == null)
            return null;
        return new ScoreboardTeamWrapper(team, board);
    }

    @Override
    public void removeTeam(String name) {
        ScorePlayerTeam team = board.getTeam(name);
        if (team != null)
            board.removeTeam(team);
    }

    @Override
    public IScoreboardTeam getPlayerTeam(String player) {
        ScorePlayerTeam team = this.board.getPlayersTeam(player);
        if (team == null)
            return null;
        return new ScoreboardTeamWrapper(team, board);
    }

    @Override
    public void removePlayerTeam(String player) {
        board.removePlayerFromTeams(player);
    }
}

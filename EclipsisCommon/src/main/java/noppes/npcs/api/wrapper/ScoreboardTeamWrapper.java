package noppes.npcs.api.wrapper;

import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.text.TextFormatting;
import noppes.npcs.api.CustomNPCsException;
import noppes.npcs.api.IScoreboardTeam;

import java.util.ArrayList;
import java.util.List;

public class ScoreboardTeamWrapper implements IScoreboardTeam {
    private ScorePlayerTeam team;
    private Scoreboard board;

    protected ScoreboardTeamWrapper(ScorePlayerTeam team, Scoreboard board) {
        this.team = team;
        this.board = board;
    }

    @Override
    public String getName() {
        return team.getName();
    }

    @Override
    public String getDisplayName() {
        return team.getDisplayName();
    }

    @Override
    public void setDisplayName(String name) {
        if (name.length() <= 0 || name.length() > 32)
            throw new CustomNPCsException("Score team display name must be between 1-32 characters: %s", name);
        team.setDisplayName(name);
    }

    @Override
    public void addPlayer(String player) {
        board.addPlayerToTeam(player, getName());
    }

    @Override
    public void removePlayer(String player) {
        board.removePlayerFromTeam(player, team);
    }

    @Override
    public String[] getPlayers() {
        List<String> list = new ArrayList<String>(team.getMembershipCollection());
        return list.toArray(new String[list.size()]);
    }

    @Override
    public void clearPlayers() {
        List<String> list = new ArrayList<String>(team.getMembershipCollection());
        for (String player : list) {
            board.removePlayerFromTeam(player, team);
        }
    }

    @Override
    public boolean getFriendlyFire() {
        return team.getAllowFriendlyFire();
    }

    @Override
    public void setFriendlyFire(boolean bo) {
        team.setAllowFriendlyFire(bo);
    }

    @Override
    public void setColor(String color) {
        TextFormatting enumchatformatting = TextFormatting.getValueByName(color);

        if (enumchatformatting == null || enumchatformatting.isFancyStyling())
            throw new CustomNPCsException("Not a proper color name: %s", color);

        team.setPrefix(enumchatformatting.toString());
        team.setSuffix(TextFormatting.RESET.toString());
    }

    @Override
    public String getColor() {
        String prefix = team.getPrefix();
        if (prefix == null || prefix.isEmpty())
            return null;
        for (TextFormatting format : TextFormatting.values()) {
            if (prefix.equals(format.toString()) && format != TextFormatting.RESET)
                return format.getFriendlyName();
        }
        return null;
    }

    @Override
    public void setSeeInvisibleTeamPlayers(boolean bo) {
        team.setSeeFriendlyInvisiblesEnabled(bo);
    }

    @Override
    public boolean getSeeInvisibleTeamPlayers() {
        return team.getSeeFriendlyInvisiblesEnabled();
    }

    @Override
    public boolean hasPlayer(String player) {
        return board.getPlayersTeam(player) != null;
    }
}

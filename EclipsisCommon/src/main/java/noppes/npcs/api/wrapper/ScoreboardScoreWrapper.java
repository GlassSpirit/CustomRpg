package noppes.npcs.api.wrapper;

import net.minecraft.scoreboard.Score;
import noppes.npcs.api.IScoreboardScore;

public class ScoreboardScoreWrapper implements IScoreboardScore {
    private Score score;

    public ScoreboardScoreWrapper(Score score) {
        this.score = score;
    }

    @Override
    public int getValue() {
        return score.getScorePoints();
    }

    @Override
    public void setValue(int val) {
        score.setScorePoints(val);
    }

    @Override
    public String getPlayerName() {
        return score.getPlayerName();
    }

}

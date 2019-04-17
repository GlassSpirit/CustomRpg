package noppes.npcs.api;

public interface IScoreboard {

    IScoreboardObjective[] getObjectives();


    /**
     * @return Returns null if the objective is not found
     */
    IScoreboardObjective getObjective(String name);


    boolean hasObjective(String objective);


    void removeObjective(String objective);


    /**
     * @param objective Scoreboard objective name (1-16 chars)
     * @param criteria  The criteria see http://minecraft.gamepedia.com/Scoreboard#Objectives
     * @return Returns the created ScoreboardObjective
     */
    IScoreboardObjective addObjective(String objective, String criteria);


    /**
     * @param datatag Can be left empty
     */
    void setPlayerScore(String player, String objective, int score, String datatag);


    /**
     * @param datatag Can be left empty
     */
    int getPlayerScore(String player, String objective, String datatag);


    /**
     * @param datatag Can be left empty
     */
    boolean hasPlayerObjective(String player, String objective, String datatag);


    /**
     * @param datatag Can be left empty
     */
    void deletePlayerScore(String player, String objective, String datatag);


    IScoreboardTeam[] getTeams();


    boolean hasTeam(String name);


    IScoreboardTeam addTeam(String name);


    IScoreboardTeam getTeam(String name);


    void removeTeam(String name);

    /**
     * @param player the player whos team you want to get
     * @return The players team
     */
    IScoreboardTeam getPlayerTeam(String player);

    /**
     * @param player The players who should be removed from his team
     */
    void removePlayerTeam(String player);


    String[] getPlayerList();
}

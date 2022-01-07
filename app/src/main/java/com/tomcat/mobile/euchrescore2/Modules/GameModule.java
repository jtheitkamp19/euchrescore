package com.tomcat.mobile.euchrescore2.Modules;

import android.content.ContentValues;

import com.tomcat.mobile.euchrescore2.DataTables.GameHistoryTable;
import com.tomcat.mobile.euchrescore2.DataTables.GameTable;
import com.tomcat.mobile.euchrescore2.Utility.Util;

public class GameModule {
    private int id;
    private String gameName;
    private int[] potentialScores;
    private boolean areScoresInvertible;
    private int rowsPerTeam;
    private int numberOfTeams;
    private int targetScore;

    public GameModule() {

    }

    public GameModule(String name, String scores, boolean invertible, int teamCount, int target) {
        this.gameName = name;
        setPotentialScores(scores);
        setAreScoresInvertible(invertible);
        setNumberOfTeams(teamCount);
        setTargetScore(target);
    }

    public int getId() {
        return id;
    }

    public String getGameName() {
        return gameName;
    }

    public int[] getPotentialScores() {
        return potentialScores;
    }

    public boolean getAreScoresInvertible() {
        return areScoresInvertible;
    }

    public int getRowsPerTeam() {
        return rowsPerTeam;
    }

    public int getNumberOfTeams() {
        return numberOfTeams;
    }

    public int getTargetScore() {
        return targetScore;
    }

    public void setId(int value) {
        id = value;
    }

    public void setGameName(String name) {
        this.gameName = name;
    }

    public void setPotentialScores(String potentialScores) {
        this.potentialScores = parsePotentialScores(potentialScores);
    }

    public void setAreScoresInvertible(boolean areScoresInvertible) {
        this.areScoresInvertible = areScoresInvertible;
        this.rowsPerTeam = (areScoresInvertible) ? 2 : 1;
    }

    public void setNumberOfTeams(int teams) {
        this.numberOfTeams = teams;
    }

    public void setTargetScore(int score) {
        this.targetScore = score;
    }

    public boolean verifyGameIsValid() {
        return this.potentialScores.length > 0 && this.numberOfTeams > 0 && this.rowsPerTeam > 0 && this.rowsPerTeam < 3 &&
                this.targetScore > 0;
    }

    public ContentValues getContentValuesForGame() {
        ContentValues cv = new ContentValues();

        cv.put(GameTable.COLUMN_GAME_NAME, Util.getInstance().formatStringForDatabase(gameName));
        cv.put(GameTable.COLUMN_NUMBER_OF_TEAMS, numberOfTeams);
        cv.put(GameTable.COLUMN_POTENTIAL_SCORES, Util.getInstance().convertScoresArrayToString(potentialScores));
        cv.put(GameTable.COLUMN_ARE_SCORES_INVERTIBLE, areScoresInvertible);
        cv.put(GameTable.COLUMN_TARGET_SCORE, targetScore);

        return cv;
    }

    private int[] parsePotentialScores(String potentialScores) {
        String[] scoreStrings = potentialScores.split(",");
        int[] scores = new int[scoreStrings.length];

        for (int i = 0; i < scoreStrings.length; i++) {
            try {
                scores[i] = Integer.parseInt(scoreStrings[i]);
            } catch (Exception e) {
                Util.getInstance().warn(e.getMessage());
                scores[i] = 0;
            }
        }

        return scores;
    }
}

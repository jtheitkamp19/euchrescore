package com.tomcat.mobile.euchrescore2.Modules;

import android.content.ContentValues;

import com.tomcat.mobile.euchrescore2.DataTables.GameHistoryTable;
import com.tomcat.mobile.euchrescore2.Utility.Util;

public class GameScoreModule {
    private int id = 0;
    private String gameName = "";
    private String[] teamNames = {};
    private int[] teamScores = {};
    private String timeOfGame = "";

    public GameScoreModule() {

    }

    public GameScoreModule(CharSequence gameName, CharSequence[] teamNames, int[] teamScores, String timeOfGame) {
        String[] teams = new String[teamNames.length];

        for (int i = 0; i < teamNames.length; i++) {
            teams[i] = teamNames[i].toString();
        }

        this.gameName = gameName.toString();
        this.teamNames = teams;
        this.teamScores = teamScores;
        this.timeOfGame = timeOfGame;
    }

    public int getId() {
        return id;
    }

    public String getGameName() {
        return gameName;
    }

    public String[] getTeamNames() {
        return teamNames;
    }

    public int[] getTeamScores() {
        return teamScores;
    }

    public String getTimeOfGame() {
        return timeOfGame;
    }

    public void setId(int i) {
        id = i;
    }

    public void setGameName(String name) {
        gameName = name;
    }

    public void setTeamNames(String[] strings) {
        teamNames = strings;
    }

    public void setTeamScores(int[] scores) {
        teamScores = scores;
    }

    public void setTimeOfGame(String date) {
        timeOfGame = date;
    }

    public ContentValues getContentValuesForGame() {
        ContentValues cv = new ContentValues();

        cv.put(GameHistoryTable.COLUMN_GAME_NAME, Util.getInstance().formatStringForDatabase(gameName));
        cv.put(GameHistoryTable.COLUMN_TEAM_NAMES, Util.getInstance().formatStringForDatabase(Util.getInstance().convertNamesArrayToString(teamNames)));
        cv.put(GameHistoryTable.COLUMN_TEAM_SCORES, Util.getInstance().convertScoresArrayToString(teamScores));
        cv.put(GameHistoryTable.COLUMN_TIME_OF_GAME, timeOfGame);

        return cv;
    }

    private String convertToString(String[] str) {
        String returnString = "";

        for (String o : str) {
            returnString += (returnString.isEmpty()) ?  str : ", " + o;
        }

        return returnString;
    }

    private String convertToString(int[] is) {
        String returnString = "";

        for (int i : is) {
            returnString += (returnString.isEmpty()) ? i : ", " + i;
        }

        return returnString;
    }

    @Override
    public String toString() {
        return getId() + ", " + getGameName() + ", " + convertToString(getTeamNames()) + ", " + convertToString(getTeamScores()) + ", " + getTimeOfGame();
    }
}

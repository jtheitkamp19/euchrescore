package com.tomcat.mobile.euchrescore2.DataTables;

public class GameHistoryTable {
    public final static String TABLE_NAME = "score_history";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_GAME_NAME = "game_name";
    public static final String COLUMN_TEAM_NAMES = "team_names";
    public static final String COLUMN_TEAM_SCORES = "team_scores";
    public static final String COLUMN_TIME_OF_GAME = "time_of_game";

    public static final String[] COLUMNS = {COLUMN_ID, COLUMN_GAME_NAME, COLUMN_TEAM_NAMES, COLUMN_TEAM_SCORES, COLUMN_TIME_OF_GAME};
    public static final String[] DATA_TYPES = {"integer primary key autoincrement", "text not null", "text not null", "text not null", "text not null"};

    public static String getDatabaseTableCreationStatement() {
        String tableCreationStatement = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(";

        for (int i = 0; i < COLUMNS.length; i++) {
            if (i != 0) {
                tableCreationStatement += ", ";
            }
            tableCreationStatement += COLUMNS[ i ] + " " + DATA_TYPES[ i ];
        }
        tableCreationStatement += ");";
        return tableCreationStatement;
    }
}

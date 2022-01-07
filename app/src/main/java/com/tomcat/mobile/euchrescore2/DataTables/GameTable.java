package com.tomcat.mobile.euchrescore2.DataTables;

public class GameTable extends DataTable {
    public final static String TABLE_NAME = "games";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_GAME_NAME = "game_name";
    public static final String COLUMN_NUMBER_OF_TEAMS = "number_of_teams";
    public static final String COLUMN_POTENTIAL_SCORES = "potential_scores";
    public static final String COLUMN_ARE_SCORES_INVERTIBLE = "scores_invertible";
    public static final String COLUMN_TARGET_SCORE = "target_score";

    public static final String[] COLUMNS = {COLUMN_ID, COLUMN_GAME_NAME, COLUMN_NUMBER_OF_TEAMS, COLUMN_POTENTIAL_SCORES, COLUMN_ARE_SCORES_INVERTIBLE, COLUMN_TARGET_SCORE};
    public static final String[] DATA_TYPES = {"integer primary key autoincrement", "text not null", "integer not null", "text not null", "integer not null", "integer not null"};

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

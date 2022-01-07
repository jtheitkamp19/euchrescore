package com.tomcat.mobile.euchrescore2.Utility;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.tomcat.mobile.euchrescore2.Modules.GameScoreModule;
import com.tomcat.mobile.euchrescore2.DataTables.GameHistoryTable;

import java.util.ArrayList;

public class ScoreUtil {
    private DataAccessHelper dataHelper;
    private SQLiteDatabase database;

    public ScoreUtil() {
        dataHelper = DataAccessHelper.getInstance();
    }

    public void open() throws SQLException {
        database = dataHelper.getWritableDatabase();
    }

    public void close() {
        dataHelper.close();
    }

    public void addGameStats(ContentValues cv) {
        open();

        try {
            database.insertOrThrow(GameHistoryTable.TABLE_NAME, null, cv);
        } catch (SQLException sqle) {
            Util.getInstance().error(sqle.getLocalizedMessage());
        }

        close();
    }

    public GameScoreModule[] getGamesFromName(String gameName) {
        String select = "SELECT * FROM " + GameHistoryTable.TABLE_NAME + " WHERE " + GameHistoryTable.COLUMN_GAME_NAME + " = " + Util.getInstance().wrap(Util.getInstance().formatStringForDatabase(gameName));
        GameScoreModule[] gameScores = null;

        try {
            open();
            Util.getInstance().log("Executing select query: " + select);
            Cursor cursor = database.rawQuery(select, null);
            cursor.moveToFirst();
            gameScores = new GameScoreModule[cursor.getCount()];

            for (int i = 0; i < gameScores.length; i++) {
                gameScores[i] = parseGame(cursor);
                cursor.moveToNext();
            }
        } catch (SQLException sqle) {
            Util.getInstance().error(sqle.getMessage());
        } catch (IndexOutOfBoundsException ie) {
            Util.getInstance().error(ie.getMessage());
        } finally {
            close();
        }

        return gameScores;
    }

    public void clearGameDataFromDatabase(String gameName) {
        try {
            open();
            Util.getInstance().log("Executing delete statement for " + gameName);
            database.delete(GameHistoryTable.TABLE_NAME, GameHistoryTable.COLUMN_GAME_NAME + "=" + Util.getInstance().wrap(Util.getInstance().formatStringForDatabase(gameName)), null);
        } catch (SQLException sqle) {
            Util.getInstance().error(sqle.getMessage());
        } catch (IndexOutOfBoundsException ie) {
            Util.getInstance().error(ie.getMessage());
        } finally {
            close();
        }
    }

    private GameScoreModule parseGame(Cursor cursor) {
        GameScoreModule module = new GameScoreModule();

        module.setId(cursor.getInt(0));
        module.setGameName(Util.getInstance().formatStringFromDatabase(cursor.getString(1)));
        module.setTeamNames(Util.getInstance().convertDBStringToStringArray(Util.getInstance().formatStringFromDatabase(cursor.getString(2))));
        module.setTeamScores(Util.getInstance().convertDBStringToIntArray(cursor.getString(3)));
        module.setTimeOfGame(cursor.getString(4));

        return module;
    }
}

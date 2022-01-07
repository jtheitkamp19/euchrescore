package com.tomcat.mobile.euchrescore2.Utility;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.tomcat.mobile.euchrescore2.DataTables.GameHistoryTable;
import com.tomcat.mobile.euchrescore2.DataTables.GameTable;
import com.tomcat.mobile.euchrescore2.Modules.GameModule;
import com.tomcat.mobile.euchrescore2.Modules.GameScoreModule;

public class GameUtil {
    private DataAccessHelper dataHelper;
    private SQLiteDatabase database;

    public GameUtil() {
        dataHelper = DataAccessHelper.getInstance();
    }

    public void open() throws SQLException {
        database = dataHelper.getWritableDatabase();
    }

    public void close() {
        dataHelper.close();
    }

    public GameModule[] getAllGamesAvailable() {
        String select = "SELECT * FROM " + GameTable.TABLE_NAME;
        GameModule[] games = null;
        open();

        try {
            Cursor cursor = database.rawQuery(select, null);
            cursor.moveToFirst();
            games = new GameModule[cursor.getCount()];

            for (int i = 0; i < games.length; i++) {
                games[i] = parseGame(cursor);
                cursor.moveToNext();
            }
        } catch (SQLException sqle) {
            Util.getInstance().error(sqle.getMessage());
        } catch (IndexOutOfBoundsException ie) {
            Util.getInstance().error(ie.getMessage());
        } finally {
            close();
        }

        return games;
    }

    public GameModule getGameFromName(String name) {
        GameModule game = null;
        String select = "SELECT * FROM " + GameTable.TABLE_NAME + " WHERE " + GameTable.COLUMN_GAME_NAME + " = " + Util.getInstance().wrap(Util.getInstance().formatStringForDatabase(name));

        try {
            open();
            Cursor cursor = database.rawQuery(select, null);
            cursor.moveToFirst();
            game = parseGame(cursor);
        } catch (SQLException sqle) {
            Util.getInstance().error(sqle.getMessage());
        } catch (IndexOutOfBoundsException ie) {
            Util.getInstance().error(ie.getMessage());
        } finally {
            close();
        }

        return game;
    }

    public void addGame(ContentValues cv) {
        open();

        try {
            database.insertOrThrow(GameTable.TABLE_NAME, null, cv);
        } catch (SQLException sqle) {
            Util.getInstance().error(sqle.getLocalizedMessage());
        }

        close();
    }

    public void clearGameDataFromDatabase(String gameName) {
        try {
            open();
            Util.getInstance().log("Executing delete statement for " + gameName);
            database.delete(GameTable.TABLE_NAME, GameTable.COLUMN_GAME_NAME + "=" + Util.getInstance().wrap(Util.getInstance().formatStringForDatabase(gameName)), null);
        } catch (SQLException sqle) {
            Util.getInstance().error(sqle.getMessage());
        } catch (IndexOutOfBoundsException ie) {
            Util.getInstance().error(ie.getMessage());
        } finally {
            close();
            ScoreUtil scoreUtil = new ScoreUtil();
            scoreUtil.clearGameDataFromDatabase(gameName);
        }
    }

    private GameModule parseGame(Cursor cursor) {
        GameModule module = new GameModule();

        module.setId(cursor.getInt(0));
        module.setGameName(Util.getInstance().formatStringFromDatabase(cursor.getString(1)));
        module.setNumberOfTeams(cursor.getInt(2));
        module.setPotentialScores(cursor.getString(3));
        module.setAreScoresInvertible(cursor.getInt(4) != 0);
        module.setTargetScore(cursor.getInt(5));

        return module;
    }
}

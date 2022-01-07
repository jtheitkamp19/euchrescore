package com.tomcat.mobile.euchrescore2.Utility;

import android.content.Context;
import android.os.Handler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Message;

import com.tomcat.mobile.euchrescore2.DataTables.GameHistoryTable;
import com.tomcat.mobile.euchrescore2.DataTables.GameTable;
import com.tomcat.mobile.euchrescore2.Modules.GameModule;

import java.util.ArrayList;

public class DataAccessHelper extends SQLiteOpenHelper {
    private static DataAccessHelper dah = null;
    private static final String DATABASE_NAME = "scoresdata.db";
    private static final int DATABASE_VERSION = 1;
    private Handler.Callback callback;

    private ArrayList<String> creationStatements = new ArrayList<>();

    private DataAccessHelper(Context context, Handler.Callback callback) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Util.getInstance().log("Initializing Database...");
        creationStatements.add(GameHistoryTable.getDatabaseTableCreationStatement());
        creationStatements.add(GameTable.getDatabaseTableCreationStatement());
        this.callback = callback;
        onCreate(this.getWritableDatabase());
    }

    public static void setInstance(Context context, final Handler.Callback callback) {
        if (dah == null) {
            dah = new DataAccessHelper(context, callback);
        }
    }

    public static synchronized DataAccessHelper getInstance() {
        return dah;
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        Util.getInstance().log("" + Util.getInstance().getPropertyHasLocalData());
        if (!Util.getInstance().getPropertyHasLocalData()) {
            for (int i = 0; i < creationStatements.size(); i++) {
                Util.getInstance().log(creationStatements.get(i));
                database.execSQL(creationStatements.get(i));
            }

            ArrayList<GameModule> defaultData = getDefaultGameModules();

            for (int i = 0; i < defaultData.size(); i++) {
                database.insertOrThrow(GameTable.TABLE_NAME, null, defaultData.get(i).getContentValuesForGame());
            }

            Util.getInstance().setPropertyHasLocalData(true);
        }

        callback.handleMessage(new Message());
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        resetLocalData(database);
        onCreate(database);
    }

    @Override
    public String getDatabaseName() {
        return DATABASE_NAME;
    }

    public void resetLocalData(SQLiteDatabase database) {
        final String DROP_TABLE_STATEMENT = "DROP TABLE IF EXISTS";
        database.execSQL(DROP_TABLE_STATEMENT + GameHistoryTable.TABLE_NAME);
        database.execSQL(DROP_TABLE_STATEMENT + GameTable.TABLE_NAME);
        Util.getInstance().setPropertyHasLocalData(false);
    }

    private ArrayList<GameModule> getDefaultGameModules() {
        ArrayList<GameModule> games = new ArrayList<>();
        games.add(new GameModule("Bid Euchre", "1,2,3,4,5,6,8,12", true, 2, 32));
        games.add(new GameModule("Cornhole", "1,2,3,4,5,6,7,8,9,10,11,12", true, 2, 32));

        return games;
    }
}

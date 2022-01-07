package com.tomcat.mobile.euchrescore2.Utility;

import android.app.Activity;
import androidx.appcompat.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import com.tomcat.mobile.euchrescore2.Modules.GameModule;
import com.tomcat.mobile.euchrescore2.TeamButton;

import java.lang.reflect.Field;
import java.text.ParseException;

public class Util {
    private static Util util = null;
    private static Context appContext;
    private static SharedPreferences pref;
    private final String APP_PROPERTY_HAS_LOCAL_DATA = "propertyHasLocalData";
    private final String APP_PROPERTY_RECENT_GAME_SCORE = "propertyRecentGameScore-";
    private final String APP_PROPERTY_RECENT_GAME_NAMES = "propertyRecentGameNames-";
    private final String TEAM_SUFFIX = "-Team=";

    private Util() {

    }

    public static void setInstance(Context context) {
        appContext = context;
        pref = appContext.getSharedPreferences("com.tomcat.mobile.gamescores.PREFERENCES", Context.MODE_PRIVATE);
        util = new Util();
    }

    public static synchronized Util getInstance() {
        return util;
    }

    private SharedPreferences.Editor getPropertyEditor() {
        return pref.edit();
    }

    public void setPropertyHasLocalData(boolean hasLocalData) {
        getPropertyEditor().putBoolean(APP_PROPERTY_HAS_LOCAL_DATA, hasLocalData).commit();
    }

    public boolean getPropertyHasLocalData() {
        return pref.getBoolean(APP_PROPERTY_HAS_LOCAL_DATA, false);
    }

    public void setPropertyRecentGameScore(String gameName, int teamNumber, int score) {
        String propertyName = APP_PROPERTY_RECENT_GAME_SCORE + gameName + TEAM_SUFFIX + teamNumber;
        propertyName = propertyName.replace(" ", "_");
        getPropertyEditor().putInt(propertyName, score).commit();
    }

    public int getPropertyRecentGameScore(String gameName, int teamNumber) {
        String propertyName = APP_PROPERTY_RECENT_GAME_SCORE + gameName + TEAM_SUFFIX + teamNumber;
        propertyName = propertyName.replace(" ", "_");
        return pref.getInt(propertyName, 0);
    }

    public void setPropertyRecentGameName(String gameName, int teamNumber, String teamName) {
        String propertyName = APP_PROPERTY_RECENT_GAME_NAMES + gameName + TEAM_SUFFIX + teamNumber;
        propertyName = propertyName.replace(" ", "_");
        getPropertyEditor().putString(propertyName, teamName).commit();
    }

    public String getPropertyRecentGameName(String gameName, int teamNumber) {
        String propertyName = APP_PROPERTY_RECENT_GAME_NAMES + gameName + TEAM_SUFFIX + teamNumber;
        propertyName = propertyName.replace(" ", "_");
        return pref.getString(propertyName, "");
    }

    public void resetGameProperties(String gameName, int numberOfTeams, boolean sameTeamNames) {
        for (int i = 0; i < numberOfTeams; i++) {
            if (!sameTeamNames) {
                setPropertyRecentGameName(gameName, i + 1, "");
            }
            setPropertyRecentGameScore(gameName, i + 1, 0);
        }
    }

    public AlertDialog.Builder createAlertDialogWithTitleAndMessage(Context context, String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message);
        return builder;
    }

    public String formatStringForDatabase(String rawString) {
        return rawString.replace(" ", "_space_")
                .replace(".", "_dot_")
                .replace("\'", "_apostrophe_")
                .replace("\"", "_quote_");
    }

    public String formatStringFromDatabase(String dbString) {
        return dbString.replace("_space_", " ")
                .replace("_dot", ".")
                .replace("_apostrophe_", "\'")
                .replace("_quote_", "\"");
    }

    public int getResourceIdFromName(String name, Class<?> c) {
        int resourceId = 0;
        try {
            Field idField = c.getDeclaredField(name);
            resourceId = idField.getInt(idField);
        } catch (Exception e) {
            Util.getInstance().error(e.getMessage());
            resourceId = -1;
        }

        return resourceId;
    }

    public CharSequence convertToCharSequence(String str) {
        return str.subSequence(0, str.length());
    }

    public CharSequence convertToCharSequence(int i) {
        String stringValue = String.valueOf(i);
        return stringValue.subSequence(0, stringValue.length());
    }

    public String convertNamesArrayToString(String[] names) {
        String joinedNames = "";

        for (String name : names) {
            joinedNames += (joinedNames.isEmpty()) ? name : "," + name;
        }

        return joinedNames;
    }

    public String convertScoresArrayToString(int[] scores) {
        String joinedNames = "";

        for (int name : scores) {
            joinedNames += (joinedNames.isEmpty()) ? name : "," + name;
        }

        return joinedNames;
    }

    public String[] convertDBStringToStringArray(String namesString) {
        return namesString.split(",");
    }

    public int[] convertDBStringToIntArray(String scoresString) {
        String[] scoreStrings = scoresString.split(",");
        int[] scores = new int[scoreStrings.length];

        for (int i = 0; i < scoreStrings.length; i++) {
            try {
                scores[i] = Integer.parseInt(scoreStrings[i]);
            } catch (Exception pe) {
                Util.getInstance().warn("Score could not be parsed");
                scores[i] = 0;
            }
        }

        return scores;
    }

    public void closeKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager)activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();

        if (view == null) {
            view = new View(activity);
        }

        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public String wrap(String str) {
        return "'" + str + "'";
    }

    public String wrap(int i) {
        return "'" + i + "'";
    }

    public int decodeScoreButtonClick(TeamButton buttonClicked) {
        CharSequence clickValue = buttonClicked.getText();
        int value = 0;

        try {
            value = Integer.parseInt(clickValue.toString());
        } catch (Exception e) {
            log(e.getMessage());
        }

        return value;
    }

    public void log(String message) {
        Log.e("INFO", message);
    }

    public void warn(String message) {
        Log.e("WARN", message);
    }

    public void error(String message) {
        Log.e("ERROR", message);
    }
}

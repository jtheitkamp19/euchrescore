package com.tomcat.mobile.euchrescore2;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.tomcat.mobile.euchrescore2.Utility.GameUtil;
import com.tomcat.mobile.euchrescore2.Utility.ScoreUtil;
import com.tomcat.mobile.euchrescore2.Utility.Util;

public class GameOptionSelector extends AppCompatActivity {
    private String gameName = "";
    private final int OPTION_TYPE_GAME = 0;
    private final int OPTION_TYPE_HISTORY = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_option_selector);

        gameName = getIntent().getStringExtra(getString(R.string.extra_game_name));

        if (gameName instanceof String && gameName.length() > 0) {
            ((TextView)findViewById(R.id.m_tvGameName)).setText(Util.getInstance().convertToCharSequence(gameName));
        } else {
            returnHome();
        }
    }

    public void onClearGameDataClickHandler(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(R.string.clear_data)
                .setMessage(R.string.clear_data_confirmation)
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ScoreUtil util = new ScoreUtil();
                        util.clearGameDataFromDatabase(getIntent().getStringExtra(getString(R.string.extra_game_name)));
                        dialog.dismiss();
                    }
                });

        builder.show();
    }

    private void returnHome() {
        Intent mainActivityIntent = new Intent(this, MainActivity.class);
        startActivity(mainActivityIntent);
    }

    public void onGameStartClickHandler(View view) {
        Class startGameClass = getNextActivityClassFromGameType(OPTION_TYPE_GAME);
        startGameIntent(startGameClass);
    }

    public void onViewHistoryClickHandler(View view) {
        Class viewHistoryClass = getNextActivityClassFromGameType(OPTION_TYPE_HISTORY);
        startGameIntent(viewHistoryClass);
    }

    public void onDeleteGameClickHandler(View view) {
        final Context currentContext = this;

        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(R.string.delete_game)
                .setMessage(R.string.delete_game_confirmation)
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        GameUtil gameUtil = new GameUtil();
                        gameUtil.clearGameDataFromDatabase(gameName);
                        returnHome();
                    }
                });

        builder.show();
    }

    private void startGameIntent(Class gameClass) {
        if (gameClass instanceof Class) {
            Intent gameIntent = new Intent(this, gameClass);
            gameIntent.putExtra(getString(R.string.extra_game_name), gameName);
            startActivity(gameIntent);
        } else {
            Util.getInstance().error("No start game intent could be found");
        }
    }

    private Class getNextActivityClassFromGameType(int gameOptionType) {
        return (gameOptionType == OPTION_TYPE_GAME) ? ScoreActivity.class : ScoreHistoryActivity.class;
    }
}
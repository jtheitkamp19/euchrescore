package com.tomcat.mobile.euchrescore2;

import androidx.appcompat.app.AlertDialog;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tomcat.mobile.euchrescore2.Modules.GameScoreModule;
import com.tomcat.mobile.euchrescore2.Utility.ScoreUtil;
import com.tomcat.mobile.euchrescore2.Utility.Util;

import java.util.ArrayList;

public class ScoreHistoryActivity extends Activity {
    private String gameName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_history);
        LinearLayout historyLayout = findViewById(R.id.m_llHistory);

        gameName = getIntent().getStringExtra(getString(R.string.extra_game_name));

        ((TextView)findViewById(R.id.m_tvGameName)).setText(Util.getInstance().convertToCharSequence(gameName));

        GameScoreModule[] gameScores = getGameData();

        for (GameScoreModule gameScore : gameScores) {
            historyLayout.addView(createHistoricalButton(gameScore));
        }
    }

    private GameScoreModule[] getGameData() {
        ScoreUtil util = new ScoreUtil();
        return util.getGamesFromName(getIntent().getStringExtra(getString(R.string.extra_game_name)));
    }

    private Button createHistoricalButton(final GameScoreModule historicalGame) {
        Button button = new Button(this, null, 0, R.style.MenuButton_Dynamic_Small);
        button.setText(createGameButtonText(historicalGame));
        final Context currentContext = this;

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View historyView = getLayoutInflater().inflate(R.layout.game_history_view, null);
                ((TextView)historyView.findViewById(R.id.m_tvGameName)).setText("\n" + historicalGame.getGameName());
                ((TextView)historyView.findViewById(R.id.m_tvGameDate)).setText("\n" + historicalGame.getTimeOfGame());
                ((TextView)historyView.findViewById(R.id.m_tvTeamNames)).setText("\n" + createDisplayableString(historicalGame.getTeamNames()));
                ((TextView)historyView.findViewById(R.id.m_tvTeamScores)).setText("\n" + createDisplayableString(historicalGame.getTeamScores()));

                AlertDialog.Builder builder = new AlertDialog.Builder(currentContext)
                        .setView(historyView)
                        .setCancelable(false)
                        .setPositiveButton(R.string.continu, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                builder.show();
            }
        });

        return button;
    }

    private CharSequence createGameButtonText(GameScoreModule historicalGame) {
        return historicalGame.getTimeOfGame();
    }

    private String createDisplayableString(String[] strings) {
        String returnString = "";

        for (String str : strings) {
            returnString += (returnString.isEmpty()) ? str : "\n" + str;
        }

        return returnString;
    }

    private String createDisplayableString(int[] ints) {
        String returnString = "";

        for (int i : ints) {
            returnString += (returnString.isEmpty()) ? i : "\n" + i;
        }

        return returnString;
    }
}
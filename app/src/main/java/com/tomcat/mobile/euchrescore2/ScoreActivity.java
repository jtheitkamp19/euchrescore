package com.tomcat.mobile.euchrescore2;

import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tomcat.mobile.euchrescore2.Modules.GameModule;
import com.tomcat.mobile.euchrescore2.Modules.GameScoreModule;
import com.tomcat.mobile.euchrescore2.Utility.GameUtil;
import com.tomcat.mobile.euchrescore2.Utility.ScoreUtil;
import com.tomcat.mobile.euchrescore2.Utility.Util;

import org.w3c.dom.Text;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Date;

public class ScoreActivity extends FragmentActivity implements TeamNameDialogFragment.TeamNameDialogListener {
    private String gameName = "";
    private CharSequence[] teamNames;
    private int[] teamScores;
    private Handler uiHandler;
    private View.OnClickListener onScoreButtonClickHandler;
    private GameModule currentGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        gameName = getIntent().getStringExtra(getString(R.string.extra_game_name));
        ((TextView)findViewById(R.id.m_tvGameName)).setText(Util.getInstance().convertToCharSequence(gameName));

        currentGame = getGameScoreClass();

        if (currentGame instanceof GameModule && initializeAppHandlers()) {
            boolean isGameValid = true;

            teamNames = new CharSequence[currentGame.getNumberOfTeams()];
            teamScores = new int[currentGame.getNumberOfTeams()];

            for (int i = 0; i < currentGame.getNumberOfTeams(); i++) {
                teamNames[i] = Util.getInstance().getPropertyRecentGameName(gameName, i + 1);
                Util.getInstance().log("Found name = " + teamNames[i]);
                teamScores[i] = Util.getInstance().getPropertyRecentGameScore(gameName, i + 1);

                if (teamNames[i].length() == 0) {
                    isGameValid = false;
                }
            }

            createGameScoreUI(currentGame);

            if (isGameValid) {
                Util.getInstance().log("Game is valid: Setting up UI");
                displayContinueOldGameDialog();
            } else {
                Util.getInstance().error("Game Invalid: Returning Home");
                startNewGameInstance(false);
            }
        } else {
            Util.getInstance().error("Game Could not be found");
            returnHome();
        }
    }

    private boolean initializeAppHandlers() {
        uiHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                updateApplicationProperties();
                updateUIElements();
                checkIfTeamHasWon();
            }
        };

        onScoreButtonClickHandler = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TeamButton b = (TeamButton)v;
                int value = Util.getInstance().decodeScoreButtonClick(b);
                int teamIndex = b.getTeamIndex();
                Util.getInstance().log("Adding " + value + " points to team " + (teamIndex + 1) + "'s score");

                teamScores[teamIndex] = teamScores[teamIndex] + value;
                uiHandler.sendEmptyMessage(0);
            }
        };

        return true;
    }

    public GameModule getGameScoreClass() {
        GameUtil util = new GameUtil();
        return util.getGameFromName(getIntent().getStringExtra(getString(R.string.extra_game_name)));
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        EditText et = dialog.getDialog().findViewById(R.id.m_etNameEntry);
        CharSequence name = et.getText();

        TextView tv = dialog.getDialog().findViewById(R.id.m_tvNameLabel);
        int currentTeamAttempt = Globals.TEAM_ONE_ID;

        try {
            String[] sections = tv.getText().toString().split(" ");
            currentTeamAttempt = Integer.parseInt(sections[sections.length - 1]);
        } catch (Exception e) {
            Util.getInstance().warn("Unable to parse string: " + e.getMessage());
        }


        if (name.length() == 0) {
            showTeamNameDialog(currentTeamAttempt);
        } else {
            if (teamNames[currentTeamAttempt - 1].length() == 0) {
                teamNames[currentTeamAttempt - 1] = name;
                dialog.dismiss();

                if (currentTeamAttempt < currentGame.getNumberOfTeams()) {
                    showTeamNameDialog(currentTeamAttempt + 1);
                } else {
                    continueExistingGame();
                }
            }
        }
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        returnHome();
    }

    public void showTeamNameDialog(int teamNumber) {
        DialogFragment dialog = new TeamNameDialogFragment(teamNumber);
        dialog.show(getSupportFragmentManager(), "TeamNameDialogFragment");
    }

    private void returnHome() {
        Intent mainMenuIntent = new Intent(this, MainActivity.class);
        startActivity(mainMenuIntent);
    }

    private void resetGameProperties(boolean sameTeamNames) {
        Util.getInstance().resetGameProperties(gameName, currentGame.getNumberOfTeams(), sameTeamNames);

        for (int i = 0; i < teamNames.length; i++) {
            if (!sameTeamNames) {
                teamNames[i] = "";
            }

            teamScores[i] = 0;
        }
    }

    private void startNewGameInstance(boolean sameTeamNames) {
        Util.getInstance().log("Starting a new game of " + gameName);
        resetGameProperties(sameTeamNames);

        if (!sameTeamNames) {
            showTeamNameDialog(Globals.TEAM_ONE_ID);
        } else {
            uiHandler.sendEmptyMessage(0);
        }
    }

    private void continueExistingGame() {
        uiHandler.sendEmptyMessage(0);
    }

    private void updateUIElements() {
        Util.getInstance().log("updateUIElements.setting text on views");
        LinearLayout teamHeaderLayout = findViewById(R.id.m_llTeamNames);

        for (int i = 0; i < teamHeaderLayout.getChildCount(); i++) {
            LinearLayout layoutChild = (LinearLayout)teamHeaderLayout.getChildAt(i);
            ((TextView)layoutChild.findViewById(R.id.m_tvTeamName)).setText(teamNames[i]);
            ((TextView)layoutChild.findViewById(R.id.m_tvTeamScore)).setText(Util.getInstance().convertToCharSequence(teamScores[i]));
        }
    }

    private void updateApplicationProperties() {
        for (int i = 0; i < currentGame.getNumberOfTeams(); i++) {
            Util.getInstance().setPropertyRecentGameScore(gameName, i + 1, teamScores[i]);
            Util.getInstance().setPropertyRecentGameName(gameName, i + 1, teamNames[i].toString());
        }
    }

    private void createGameScoreUI(GameModule game) {
        LinearLayout scoreLayout = (LinearLayout)findViewById(R.id.m_llScoreArea);
        LinearLayout teamsLayout = (LinearLayout)findViewById(R.id.m_llTeamNames);
        int totalColumns = game.getRowsPerTeam() * game.getNumberOfTeams();
        float weightPerColumn = 100 / totalColumns;

        for (int i = 0; i < game.getNumberOfTeams(); i++) {
            teamsLayout.addView(getTeamNameHeader(weightPerColumn, i));

            scoreLayout.addView(createButtonLayoutView(i, weightPerColumn, false, game.getPotentialScores()));

            if (game.getAreScoresInvertible()) {
                scoreLayout.addView(createButtonLayoutView(i, weightPerColumn, true, game.getPotentialScores()));
            }
        }
    }

    private LinearLayout createButtonLayoutView(int teamIndex, float layoutWeight, boolean invertScores, int[] potentialScores) {
        LinearLayout layout = createLinearLayoutWithWidthAndOrientation(layoutWeight, LinearLayout.VERTICAL);
        LinearLayout buttonColumn = layout.findViewById(R.id.m_llButtonColumn);

        for (int potentialScore : potentialScores) {
            int value = (!invertScores) ? potentialScore : 0 - potentialScore;
            buttonColumn.addView(createButtonWithText(value, teamIndex));
        }

        return layout;
    }

    private LinearLayout createLinearLayoutWithWidthAndOrientation(float width, int orientation) {
        LinearLayout layout = (LinearLayout)getLayoutInflater().inflate(R.layout.team_score_button_layout, null);
        layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, width));

        return layout;
    }

    private LinearLayout getTeamNameHeader(float width, int teamIndex) {
        CharSequence name = Util.getInstance().convertToCharSequence(teamIndex + 1);
        LinearLayout layout = (LinearLayout)getLayoutInflater().inflate(R.layout.team_name_layout, null);
        layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, width));
        ((TextView)layout.findViewById(R.id.m_tvTeamName)).setText(name);

        if (teamNames.length >= 3) {
            ((TextView)layout.findViewById(R.id.m_tvTeamName)).setTextAppearance(R.style.HeaderText_Small);
        } else if (teamNames.length >= 4) {
            ((TextView)layout.findViewById(R.id.m_tvTeamName)).setTextAppearance(R.style.HeaderText_XSmall);
        } else {
            ((TextView)layout.findViewById(R.id.m_tvTeamName)).setTextAppearance(R.style.HeaderText);
        }

        return layout;
    }

    private TeamButton createButtonWithText(int value, int teamNumber) {
        CharSequence text = (value > 0) ?
                "+" + Util.getInstance().convertToCharSequence(value) :
                Util.getInstance().convertToCharSequence(value);
        int buttonResourceId = (teamNumber % 2 == 0) ?
                R.style.ScoreButtonStyle_EvenTeam :
                R.style.ScoreButtonStyle_OddTeam;

        TeamButton button = new TeamButton(this, null, buttonResourceId, teamNumber);

        button.setText(text);

        button.setOnClickListener(onScoreButtonClickHandler);
        return button;
    }

    private void checkIfTeamHasWon() {
        for (int i = 0; i < currentGame.getNumberOfTeams(); i++) {
            if (teamScores[i] >= currentGame.getTargetScore()) {
                saveGameDataToDatabase();
                showTeamHasWonDialog(i);
            }
        }
    }

    private void showTeamHasWonDialog(int winningTeamIndex) {
        CharSequence teamWonMessage = getString(R.string.congratulations_you_won)
                .replace("team_name", teamNames[winningTeamIndex]);

        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setMessage(teamWonMessage)
                .setTitle(R.string.congratulations)
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        returnHome();
                        resetGameProperties(false);
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        displayGameRestartDialog();
                    }
                });

        builder.show();
    }

    private void displayGameRestartDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setMessage(R.string.same_team_names)
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startNewGameInstance(false);
                    }
                })
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startNewGameInstance(true);
                    }
                });

        builder.show();
    }

    private void displayContinueOldGameDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setMessage(R.string.continue_old_game)
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startNewGameInstance(false);
                    }
                })
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        continueExistingGame();
                    }
                });

        builder.show();
    }

    private void saveGameDataToDatabase() {
        ScoreUtil util = new ScoreUtil();
        GameScoreModule module = new GameScoreModule(gameName, teamNames, teamScores, new Date().toString());
        Util.getInstance().log(module.toString());
        util.addGameStats(module.getContentValuesForGame());
    }
}
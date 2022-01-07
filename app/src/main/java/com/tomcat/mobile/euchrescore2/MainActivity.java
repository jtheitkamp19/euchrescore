package com.tomcat.mobile.euchrescore2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tomcat.mobile.euchrescore2.Modules.GameModule;
import com.tomcat.mobile.euchrescore2.Utility.DataAccessHelper;
import com.tomcat.mobile.euchrescore2.Utility.GameUtil;
import com.tomcat.mobile.euchrescore2.Utility.Util;

public class MainActivity extends AppCompatActivity implements CustomGameDialogFragment.CustomGameDialogListener {
    GameModule customGame;
    private Handler uiHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Util.setInstance(this);
        initializeAppHandlers();
        Globals.setInstance();

        if (!(DataAccessHelper.getInstance() instanceof DataAccessHelper)) {
            DataAccessHelper.setInstance(this, createLayoutUIHandler());
        } else {
            uiHandler.sendEmptyMessage(0);
        }
    }

    private void initializeAppHandlers() {
        uiHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                createUI();
            }
        };
    }

    private void createUI() {
        ((TextView)findViewById(R.id.m_tvVersion)).setText(getString(R.string.version) + " " + BuildConfig.VERSION_NAME);
        LinearLayout layout = findViewById(R.id.m_llDefault);
        layout.removeAllViews();

        GameModule[] availableGames = getAllGamesAvailable();

        for (GameModule game : availableGames) {
            Util.getInstance().log("Creating menu button for " + game.getGameName());
            layout.addView(createGameButton(game.getGameName()));
        }
    }

    private GameModule[] getAllGamesAvailable() {
        GameUtil gameUtil = new GameUtil();
        return gameUtil.getAllGamesAvailable();
    }

    public Button createGameButton(CharSequence gameName) {
        Button gameButton = new Button(this, null, 0, R.style.MenuButton_Dynamic);
        gameButton.setText(gameName);

        gameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence selectedGame = ((Button)v).getText();

                Intent gameIntent = new Intent(getApplicationContext(), GameOptionSelector.class);
                gameIntent.putExtra(getString(R.string.extra_game_name), selectedGame);
                startActivity(gameIntent);
            }
        });

        return gameButton;
    }

    public void onVersionHistoryClick(View v) {
        Intent historyIntent = new Intent(this, VersionHistory.class);
        startActivity(historyIntent);
    }

    public void onCreateGameClickHandler(View v) {
        customGame = new GameModule();
        createCustomGameDialog(getString(R.string.custom_game_wizard_enter_game_name));
    }

    private void createCustomGameDialog(String title) {
        DialogFragment dialog = new CustomGameDialogFragment(title);
        dialog.show(getSupportFragmentManager(), "CustomNameDialogFragment");
    }

    private Handler.Callback createLayoutUIHandler() {
        return new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                onCreationResponseListener();
                return true;
            }
        };
    }

    private void onCreationResponseListener() {
        Util.getInstance().log("Database initialized");
        uiHandler.sendEmptyMessage(0);
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        EditText et = dialog.getDialog().findViewById(R.id.m_etNameEntry);
        String value = et.getText().toString();

        TextView tv = dialog.getDialog().findViewById(R.id.m_tvNameLabel);

        if (!value.isEmpty()) {
            if (tv.getText().equals(getString(R.string.custom_game_wizard_enter_game_name)) && !checkForSameNameGame(value)) {
                customGame.setGameName(value);
                createCustomGameDialog(getString(R.string.custom_game_wizard_enter_game_scores));
            } else if (tv.getText().equals(getString(R.string.custom_game_wizard_enter_game_scores))) {
                customGame.setPotentialScores(value);
                createCustomGameDialog(getString(R.string.custom_game_wizard_enter_number_of_teams));
            } else if (tv.getText().equals(getString(R.string.custom_game_wizard_enter_number_of_teams))) {
                customGame.setNumberOfTeams(parseString(value));
                createCustomGameDialog(getString(R.string.custom_game_wizard_enter_target_score));
            } else if (tv.getText().equals(getString(R.string.custom_game_wizard_enter_target_score))) {
                customGame.setTargetScore(parseString(value));
                createScoresInvertibleDialog();
            }
        } else {
            createCustomGameDialog(tv.getText().toString());
        }
    }

    private void createScoresInvertibleDialog() {
        final Context currentContext = this;
        final GameUtil gameUtil = new GameUtil();

        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setMessage(R.string.custom_game_wizard_have_negative_scores)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        customGame.setAreScoresInvertible(true);

                        if (customGame.verifyGameIsValid()) {
                            gameUtil.addGame(customGame.getContentValuesForGame());
                            displayGameCreatedDialog();
                        } else {
                            displayGameNotValidDialog();
                        }
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        customGame.setAreScoresInvertible(false);

                        if (customGame.verifyGameIsValid()) {
                            gameUtil.addGame(customGame.getContentValuesForGame());
                            displayGameCreatedDialog();
                        } else {
                            displayGameNotValidDialog();
                        }
                    }
                });

        builder.show();
    }

    private boolean checkForSameNameGame(String name) {
        GameModule[] games = getAllGamesAvailable();
        boolean sharesName = false;

        for (GameModule game : games) {
            if (name.equals(game.getGameName())) {
                sharesName = true;
                break;
            }
        }

        return sharesName;
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        customGame = null;
        dialog.dismiss();
    }

    private void displayGameNotValidDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(R.string.game_could_not_be_created)
                .setCancelable(false)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builder.show();
    }

    private void displayGameCreatedDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(R.string.game_successfully_created)
                .setCancelable(false)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        uiHandler.sendEmptyMessage(0);
                        dialog.dismiss();
                    }
                });

        builder.show();
    }

    private int parseString(String value) {
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            Util.getInstance().warn(e.getMessage());
        }

        return -1;
    }
}
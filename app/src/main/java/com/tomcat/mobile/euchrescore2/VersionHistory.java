package com.tomcat.mobile.euchrescore2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tomcat.mobile.euchrescore2.Utility.Util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

public class VersionHistory extends AppCompatActivity {
    private final String VERSION_CODE_REGEX = "^Version (\\d{1,})\\.(\\d{1,})\\.(\\d{1,})\\.(\\d{1,})$";
    private LinearLayout mainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_version_history);
        mainLayout = (LinearLayout)findViewById(R.id.m_linlayVersion);

        createVersionHistory(mainLayout, "History.txt");
        for (int i = 0; i < mainLayout.getChildCount(); i++) {
            mainLayout.getChildAt(i).performClick();
        }
    }

    private LinearLayout createAndFormatNewLinearLayout() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOnClickListener(onClickListener);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setTop(R.dimen.space_margin);
        layout.setLeft(R.dimen.space_margin);
        return layout;
    }

    private void createVersionHistory(LinearLayout view, String versionHistoryFile) {
        BufferedReader reader;
        AssetManager manager = getAssets();
        String bufferedInput = "";
        String input;

        try {
            reader = new BufferedReader(
                    new InputStreamReader(manager.open(versionHistoryFile), StandardCharsets.UTF_8)
            );
            LinearLayout currentLayout = createAndFormatNewLinearLayout();

            while ((input = reader.readLine()) != null) {
                Util.getInstance().log(input);
                if (input.equals("")) {

                } else if (Pattern.matches(VERSION_CODE_REGEX, input)) {
                    if (bufferedInput.length() > 0)  {
                        TextView tv = new TextView(this);
                        tv.setTextAppearance(R.style.DefaultText_Small);
                        tv.setText(bufferedInput);
                        currentLayout.addView(tv);
                        bufferedInput = "";
                        view.addView(currentLayout);
                        currentLayout = createAndFormatNewLinearLayout();
                    }
                    TextView mvtv = new TextView(this);
                    mvtv.setTextAppearance(R.style.DefaultText_Gold);
                    mvtv.setText(input);
                    currentLayout.addView(mvtv);
                } else {
                    bufferedInput = input + "\n" + bufferedInput;
                }
            }

            if (bufferedInput.length() > 0)  {
                TextView tv = new TextView(this);
                tv.setTextAppearance(R.style.DefaultText_Small);
                tv.setText(bufferedInput);
                currentLayout.addView(tv);
                view.addView(currentLayout);
            }

        } catch (IOException ioe) {
            Util.getInstance().error(ioe.getMessage());
        }
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Util.getInstance().log(((LinearLayout)view).getChildCount() + "");
            TextView infoView = (TextView)((LinearLayout)view).getChildAt(1);
            if (infoView.getVisibility() == View.VISIBLE) {
                infoView.setVisibility(View.GONE);
            } else {
                infoView.setVisibility(View.VISIBLE);
            }
        }
    };

    private void showVersionHistory() {
        String versionHistory = "";
        BufferedReader reader;
        AssetManager manager = getAssets();
        String versionHistoryFile = "History.txt";
        String input;

        try {
            reader = new BufferedReader(
                    new InputStreamReader(manager.open(versionHistoryFile), StandardCharsets.UTF_8));
            while((input = reader.readLine()) != null) {
                versionHistory += "\n" + input;
            }
        } catch (IOException ioe) {
            Util.getInstance().error(ioe.getMessage());
        }

        ((TextView)findViewById(R.id.m_tvHistory)).setText(versionHistory);
    }
}
package com.tomcat.mobile.euchrescore2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

@SuppressLint("AppCompatCustomView")
public class TeamButton extends Button {
    private int teamIndex;

    public TeamButton(Context context, AttributeSet attr, int defStyleAttr, int teamIndex) {
        super(context, attr, 0, defStyleAttr);
        this.setPadding(10, 0, 10, 0);
        this.teamIndex = teamIndex;
    }

    public int getTeamIndex() {
        return teamIndex;
    }
}

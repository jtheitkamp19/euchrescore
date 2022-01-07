package com.tomcat.mobile.euchrescore2;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.tomcat.mobile.euchrescore2.Utility.Util;

public class TeamNameDialogFragment extends DialogFragment {
    public interface TeamNameDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog);
        public void onDialogNegativeClick(DialogFragment dialog);
    }

    TeamNameDialogListener listener;
    private int teamNumber;

    public TeamNameDialogFragment(int teamNumber) {
        this.teamNumber = teamNumber;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View attemptedView = modifyView(inflater);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setView(attemptedView)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Util.getInstance().warn("User selected to cancel the dialog... Returning to Home Page");
                        listener.onDialogNegativeClick(TeamNameDialogFragment.this);
                    }
                })
                .setPositiveButton(R.string.continu, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Util.getInstance().warn("User selected to submit a name");
                        listener.onDialogPositiveClick(TeamNameDialogFragment.this);
                    }
                });

        Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (TeamNameDialogListener) context;
        } catch (ClassCastException cce) {
            Util.getInstance().error(cce.getMessage());
        }
    }

    private View modifyView(LayoutInflater inflater) {
        View view = inflater.inflate(R.layout.dialog_name_entry, null);

        CharSequence textLabel = getString(R.string.please_enter_team_name)
                .replace("team_number", "" + teamNumber);

        CharSequence teamDefault = getString(R.string.default_team_name)
                .replace("team_number", "" + teamNumber);

        ((TextView)view.findViewById(R.id.m_tvNameLabel)).setText(textLabel);
        ((EditText)view.findViewById(R.id.m_etNameEntry)).setText(teamDefault);

        return view;
    }
}

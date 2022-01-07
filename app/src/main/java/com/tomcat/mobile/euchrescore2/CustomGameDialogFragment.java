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

public class CustomGameDialogFragment extends DialogFragment {
    public interface CustomGameDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog);
        public void onDialogNegativeClick(DialogFragment dialog);
    }

    CustomGameDialogFragment.CustomGameDialogListener listener;
    private CharSequence dialogTitle;

    public CustomGameDialogFragment(String title) {
        dialogTitle = title;
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
                        listener.onDialogNegativeClick(CustomGameDialogFragment.this);
                    }
                })
                .setPositiveButton(R.string.continu, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Util.getInstance().warn("User selected to submit a name");
                        listener.onDialogPositiveClick(CustomGameDialogFragment.this);
                    }
                });

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (CustomGameDialogFragment.CustomGameDialogListener) context;
        } catch (ClassCastException cce) {
            Util.getInstance().error(cce.getMessage());
        }
    }

    private View modifyView(LayoutInflater inflater) {
        View view = inflater.inflate(R.layout.dialog_name_entry, null);
        ((TextView)view.findViewById(R.id.m_tvNameLabel)).setText(dialogTitle);
        ((EditText)view.findViewById(R.id.m_etNameEntry)).setText("");

        return view;
    }
}

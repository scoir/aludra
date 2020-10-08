package org.canis.aludra.ui.connections;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import org.canis.aludra.R;

public class AcceptConnectionFragment extends DialogFragment {

    public interface AcceptConnectionDialogListener {
        void onAcceptConnectionClick(String connectionID);
        void onCancelConnectionClick(DialogFragment dialog);
    }

    AcceptConnectionDialogListener listener;
    String connectionID;
    String label;

    public AcceptConnectionFragment(String connectionID, String label) {
        this.connectionID = connectionID;
        this.label = label;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setMessage("Accept connection from: " + label + "?")
                .setIcon(R.drawable.ic_share_black_24dp)
                .setTitle(R.string.title_connections)
                .setPositiveButton(R.string.create, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        listener.onAcceptConnectionClick(connectionID);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        listener.onCancelConnectionClick(AcceptConnectionFragment.this);
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    @Override
    public void setTargetFragment(@Nullable Fragment fragment, int requestCode) {
        super.setTargetFragment(fragment, requestCode);

        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            listener = (AcceptConnectionDialogListener) fragment;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(fragment.toString()
                    + " must implement NoticeDialogListener");
        }
    }
}
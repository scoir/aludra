package org.canis.aludra.ui.connections;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import org.canis.aludra.R;
import org.canis.aludra.databinding.NewConnectionFragmentBinding;

public class NewConnectionFragment extends DialogFragment {

    public interface NewConnectionDialogListener {
        void onDialogPositiveClick(String invitation);
        void onDialogNegativeClick(DialogFragment dialog);
    }

    NewConnectionDialogListener listener;
    NewConnectionFragmentBinding binding;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        binding = NewConnectionFragmentBinding.inflate(requireActivity().getLayoutInflater());
        binding.setInvitation("{\n" +
                "  \"serviceEndpoint\": \"ws://172.17.0.1:9001\",\n" +
                "  \"recipientKeys\": [\n" +
                "   \"Bu17tGjGXDyo2AY88jLgfktxHJ8NbEPYxQiAWLJvddru\"\n" +
                "  ],\n" +
                "  \"@id\": \"d19d1674-4b2a-4eb4-a7f7-909f676d046c\",\n" +
                "  \"label\": \"Hogwarts School of Witchcraft and Wizardry\",\n" +
                "  \"@type\": \"https://didcomm.org/didexchange/1.0/invitation\"\n" +
                " }");


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(binding.getRoot());
        builder.setMessage(R.string.paste_invitation)
                .setPositiveButton(R.string.create, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        binding.executePendingBindings();
                        listener.onDialogPositiveClick(binding.getInvitation());
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        listener.onDialogNegativeClick(NewConnectionFragment.this);
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            listener = (NewConnectionDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString()
                    + " must implement NoticeDialogListener");
        }
    }
}
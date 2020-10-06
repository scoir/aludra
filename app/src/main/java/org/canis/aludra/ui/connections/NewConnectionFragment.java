package org.canis.aludra.ui.connections;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import org.canis.aludra.R;
import org.canis.aludra.databinding.NewConnectionFragmentBinding;

public class NewConnectionFragment extends DialogFragment {

    public interface NewConnectionDialogListener {
        void onInvite(String invitation);
        void onCancel(DialogFragment dialog);
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
                "   \"B4urTEJg9Vts6ZEApA2ZB43Yji9Siyfvvit2HNX4yXiC\"\n" +
                "  ],\n" +
                "  \"@id\": \"3762abcc-6b7d-462b-9fc0-f7d74161858d\",\n" +
                "  \"label\": \"Hogwarts School of Witchcraft and Wizardry\",\n" +
                "  \"@type\": \"https://didcomm.org/didexchange/1.0/invitation\"\n" +
                " }\n");


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(binding.getRoot());
        builder.setMessage(R.string.paste_invitation)
                .setPositiveButton(R.string.create, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        binding.executePendingBindings();
                        listener.onInvite(binding.getInvitation());
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        listener.onCancel(NewConnectionFragment.this);
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
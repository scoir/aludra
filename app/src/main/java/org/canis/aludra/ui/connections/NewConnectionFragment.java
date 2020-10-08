package org.canis.aludra.ui.connections;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import org.canis.aludra.R;
import org.canis.aludra.databinding.NewConnectionFragmentBinding;

public class NewConnectionFragment extends DialogFragment {

    public interface NewConnectionDialogListener {
        void onInvite(String invitation);
        void onCancel(DialogFragment dialog);
    }

    NewConnectionDialogListener listener;
    NewConnectionFragmentBinding binding;

    final String demoInvitation = "{\n" +
            "  \"serviceEndpoint\": \"ws://172.17.0.1:9001\",\n" +
            "  \"recipientKeys\": [\n" +
            "   \"7tzKzkUBz1PudXavJvBkrgC5TZ5veDCpZtVraUGQPZSH\"\n" +
            "  ],\n" +
            "  \"@id\": \"7f82f100-bff6-45ae-af94-c77118483c14\",\n" +
            "  \"label\": \"Hogwarts School of Witchcraft and Wizardry\",\n" +
            "  \"@type\": \"https://didcomm.org/didexchange/1.0/invitation\"\n" +
            " }\n";

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        binding = NewConnectionFragmentBinding.inflate(requireActivity().getLayoutInflater());
        binding.setInvitation(demoInvitation);


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(binding.getRoot());
        builder.setMessage(R.string.paste_invitation)
                .setPositiveButton(R.string.create, (dialog, id) -> {
                    binding.executePendingBindings();
                    listener.onInvite(binding.getInvitation());
                })
                .setNegativeButton(R.string.cancel, (dialog, id) -> listener.onCancel(NewConnectionFragment.this));
        // Create the AlertDialog object and return it
        return builder.create();
    }

    @Override
    public void setTargetFragment(@Nullable Fragment fragment, int requestCode) {
        super.setTargetFragment(fragment, requestCode);

        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            listener = (NewConnectionDialogListener) fragment;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(fragment.toString()
                    + " must implement NoticeDialogListener");
        }
    }
}
package org.canis.aludra.ui.credentials;

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
import org.canis.aludra.databinding.CredentialOfferFragmentBinding;

public class CredentialOfferFragment extends DialogFragment {

    public interface OfferDialogListener {
        void onAcceptCredentialClick(String piid, String label);
        void onRejectCredentialClick(DialogFragment dialog);
    }

    OfferDialogListener listener;
    CredentialOfferFragmentBinding binding;
    String piid;
    String hint;

    public CredentialOfferFragment(String piid, String hint) {
        this.piid = piid;
        this.hint = hint;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        binding = CredentialOfferFragmentBinding.inflate(requireActivity().getLayoutInflater());
        binding.setCredentialHint(hint);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(binding.getRoot());
        builder.setMessage(R.string.accept_credential).setTitle(R.string.title_credentials).setIcon(R.drawable.ic_school_black_24dp)
                .setPositiveButton(R.string.create, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        binding.executePendingBindings();
                        String name = binding.getCredentialName() == null ? hint : binding.getCredentialName();
                        listener.onAcceptCredentialClick(piid, name);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        listener.onRejectCredentialClick(CredentialOfferFragment.this);
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
            listener = (OfferDialogListener) fragment;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(fragment.toString()
                    + " must implement NoticeDialogListener");
        }
    }
}
package org.canis.aludra.ui.credentials;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.canis.aludra.model.Credential;

import java.util.List;

public class CredentialsViewModel extends ViewModel {

    private MutableLiveData<List<Credential>> credentials;

    public MutableLiveData<List<Credential>> getCredentials() {
        if (credentials == null) {
            credentials = new MutableLiveData<>();
        }
        return credentials;
    }

}
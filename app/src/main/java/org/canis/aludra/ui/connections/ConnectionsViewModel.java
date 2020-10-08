package org.canis.aludra.ui.connections;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.canis.aludra.model.Connection;

import java.util.List;

public class ConnectionsViewModel extends ViewModel {

    private MutableLiveData<List<Connection>> connections;

    public MutableLiveData<List<Connection>> getConnections() {
        if (connections == null) {
            connections = new MutableLiveData<>();
        }
        return connections;
    }
}
package org.canis.aludra.ui.connections;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.crypto.tink.subtle.Base64;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.canis.aludra.MainActivity;
import org.canis.aludra.R;
import org.canis.aludra.model.Connection;
import org.canis.aludra.model.ConnectionRequest;
import org.canis.aludra.model.ConnectionResult;
import org.canis.aludra.model.Invitation;
import org.canis.aludra.model.InvitationResult;
import org.canis.aludra.service.AcceptInvitationTask;
import org.canis.aludra.service.ListConnectionsTask;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class ConnectionsFragment extends Fragment implements AcceptConnectionFragment.AcceptConnectionDialogListener,
        ScanInvitationFragment.ScanInvitationListener, ListConnectionsTask.ListConnectionsTaskHandler,
        AcceptInvitationTask.AcceptInvitationTaskHandler {

    ConnectionsViewModel mViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        final View root = inflater.inflate(R.layout.connections_fragment, container, false);

        final ListView listview = root.findViewById(R.id.connectionListView);
        MainActivity mainActivity = (MainActivity) getActivity();
        assert mainActivity != null;

        final List<Connection> list = new ArrayList<>();
        final ConnectionArrayAdapter adapter = new ConnectionArrayAdapter(mainActivity,
                android.R.layout.simple_list_item_1, list);
        listview.setAdapter(adapter);

        listview.setOnItemClickListener((parent, view, position, id) -> {
        });

        FloatingActionButton fab = root.findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(view -> {
            //Calling with the result of the scanned barcode.  Short circuited here because my barcode scanner isn't working
            //Navigation.findNavController(view).navigate(R.id.action_connections_to_scan);
            this.onScanSuccess("eyJAaWQiOiJkNTg3Y2E3ZC0xNjViLTQ3ODMtYTc2MC1kNzEzNmQxODU5NWUiLCJsYWJlbCI6IlNwcmluZ2ZpZWxkIEhpZ2ggU2Nob29sIiwiZGlkIjoiZGlkOnNvdjpLbjJxWTd1NnJuWEQ2c1JFZEtETHZUIiwiQHR5cGUiOiJodHRwczovL2RpZGNvbW0ub3JnL2RpZGV4Y2hhbmdlLzEuMC9pbnZpdGF0aW9uIn0=");
        });

        mViewModel = new ViewModelProvider(this).get(ConnectionsViewModel.class);

        final Observer<List<Connection>> connectionObserver = new Observer<List<Connection>>() {
            @Override
            public void onChanged(List<Connection> connections) {
                MutableLiveData<List<Connection>> conns = mViewModel.getConnections();
                adapter.clear();
                adapter.addAll(Objects.requireNonNull(conns.getValue()));
            }
        };
        mViewModel.getConnections().observe(getViewLifecycleOwner(), connectionObserver);

        try {
            getConnections();
        } catch (Exception e) {
            e.printStackTrace();
        }

        mainActivity.setScanInvitationListener(this);
        return root;
    }

    @Override
    public void onScanSuccess(String invitation) {
        try {
            MainActivity mainActivity = (MainActivity) getActivity();
            assert mainActivity != null;

            Invitation req = new Invitation();
            req.invitation = invitation;
            GsonBuilder gsonb = new GsonBuilder();
            Gson gson = gsonb.disableHtmlEscaping().create();
            String json = gson.toJson(req);

            byte[] signature = mainActivity.Sign(json.getBytes(StandardCharsets.UTF_8));

            AcceptInvitationTask task = new AcceptInvitationTask(
                    this,
                    mainActivity.getCloudAgentId(),
                    Base64.encodeToString(signature, Base64.URL_SAFE | Base64.NO_WRAP)
            );

            task.execute(req);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void getConnections() {

        MainActivity mainActivity = (MainActivity) getActivity();
        assert mainActivity != null;

        ConnectionRequest req = new ConnectionRequest();
        GsonBuilder gsonb = new GsonBuilder();
        Gson gson = gsonb.disableHtmlEscaping().create();
        String json = gson.toJson(req);

        List<Connection> out = new ArrayList<>();
        try {
            byte[] signature = mainActivity.Sign(json.getBytes(StandardCharsets.UTF_8));

            ListConnectionsTask task = new ListConnectionsTask(
                    this,
                    mainActivity.getCloudAgentId(),
                    Base64.encodeToString(signature, Base64.URL_SAFE | Base64.NO_WRAP)
            );

            task.execute(req);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void onInvited(String connectionID, String label) {
        DialogFragment connFrag = new AcceptConnectionFragment(connectionID, label);
        connFrag.setTargetFragment(this, 0);
        connFrag.show(getParentFragmentManager(), "connections");
    }

    @Override
    public void onAcceptConnectionClick(String connectionID) {
    }

    @Override
    public void onCancelConnectionClick(DialogFragment dialog) {

    }

    @Override
    public void HandleConnections(ConnectionResult connections) {
        if (connections.count > 0) {
            mViewModel.getConnections().setValue(connections.connections);
        }
    }

    @Override
    public void HandleConnections(InvitationResult result) {
        System.out.println("*********************************************");
        System.out.println("Accepted invitation");
        System.out.println("*********************************************");
        this.getConnections();
    }

    private static class ConnectionArrayAdapter extends ArrayAdapter<Connection> {

        HashMap<Connection, Integer> mIdMap = new HashMap<>();

        public ConnectionArrayAdapter(Context context, int textViewResourceId,
                                      List<Connection> objects) {
            super(context, textViewResourceId, objects);
            for (int i = 0; i < objects.size(); ++i) {
                Connection conn = objects.get(i);
                mIdMap.put(conn, i);
            }
        }

        @Override
        public long getItemId(int position) {
            Connection item = getItem(position);
            Integer pos = mIdMap.get(item);
            if (pos == null) {
                return -1;
            }

            return pos;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

    }
}
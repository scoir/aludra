package org.canis.aludra.ui.connections;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.canis.aludra.MainActivity;
import org.canis.aludra.R;
import org.canis.aludra.model.Connection;
import org.canis.aludra.model.QueryConnectionResults;
import org.hyperledger.aries.api.AriesController;
import org.hyperledger.aries.api.DIDExchangeController;
import org.hyperledger.aries.models.CommandError;
import org.hyperledger.aries.models.RequestEnvelope;
import org.hyperledger.aries.models.ResponseEnvelope;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class ConnectionsFragment extends Fragment implements NewConnectionFragment.NewConnectionDialogListener,
        DIDExchangeHandler.DIDExchangeCallback, AcceptConnectionFragment.AcceptConnectionDialogListener {

    ConnectionsViewModel mViewModel;
    DIDExchangeController didExchangeController;
    DIDExchangeHandler didExchangeHandler;

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

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
//                view.animate().setDuration(2000).alpha(0)
//                        .withEndAction(new Runnable() {
//                            @Override
//                            public void run() {
//                                adapter.notifyDataSetChanged();
//                                view.setAlpha(1);
//                            }
//                        });
            }

        });

        FloatingActionButton fab = root.findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment newFragment = new NewConnectionFragment();
                newFragment.show(getParentFragmentManager(), "connections");
            }
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
            AriesController agent = mainActivity.getAgent();

            didExchangeController = agent.getDIDExchangeController();
            mViewModel.getConnections().setValue(getConnections());

            didExchangeHandler = new DIDExchangeHandler(agent.getDIDExchangeController(), this);
            String registrationID = agent.registerHandler(didExchangeHandler, "didexchange_states");
            System.out.println("didexchange handler registered as: " + registrationID);
        } catch (Exception e) {
            e.printStackTrace();
        }


        return root;
    }

    @Override
    public void onInvite(String invitation) {
        // create options
        ResponseEnvelope res;
        try {
            // perform an operation
            byte[] data = invitation.getBytes(StandardCharsets.UTF_8);
            res = didExchangeController.receiveInvitation(new RequestEnvelope(data));
            if (res.getError() != null) {
                CommandError err = res.getError();
                System.out.println(err.toString());
            } else {
                String actionsResponse = new String(res.getPayload(), StandardCharsets.UTF_8);
                System.out.println(actionsResponse);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onCancel(DialogFragment dialog) {

    }

    public List<Connection> getConnections() {

        ArrayList<Connection> out = new ArrayList<>();
        try {
            byte[] data = "{}".getBytes(StandardCharsets.UTF_8);
            ResponseEnvelope resp = didExchangeController.queryConnections(new RequestEnvelope(data));

            if (resp.getError() != null) {
                CommandError err = resp.getError();
                System.out.println(err.toString());
            } else {
                GsonBuilder gsonb = new GsonBuilder();
                Gson gson = gsonb.create();

                String actionsResponse = new String(resp.getPayload(), StandardCharsets.UTF_8);
                System.out.println(actionsResponse);
                QueryConnectionResults results = gson.fromJson(actionsResponse, QueryConnectionResults.class);
                if (results.results != null) {
                    out.addAll(results.results);
                }
                return out;
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        return out;
    }


    @Override
    public void onInvited(String connectionID, String label) {
        DialogFragment connFrag = new AcceptConnectionFragment(connectionID, label);
        connFrag.setTargetFragment(this, 0);
        connFrag.show(getParentFragmentManager(), "connections");
    }

    @Override
    public void onAcceptConnectionClick(String connectionID) {
        didExchangeHandler.Continue(connectionID, "");
    }

    @Override
    public void onCancelConnectionClick(DialogFragment dialog) {

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
package org.canis.aludra.ui.credentials;

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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.canis.aludra.MainActivity;
import org.canis.aludra.R;
import org.canis.aludra.model.Credential;
import org.canis.aludra.model.QueryCredentialResults;
import org.hyperledger.aries.api.AriesController;
import org.hyperledger.aries.api.VerifiableController;
import org.hyperledger.aries.models.CommandError;
import org.hyperledger.aries.models.RequestEnvelope;
import org.hyperledger.aries.models.ResponseEnvelope;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class CredentialsFragment extends Fragment implements IssueCredentialHandler.IssueCredentialCallback, CredentialOfferFragment.OfferDialogListener {

    private CredentialsViewModel mViewModel;
    private CredentialsFragment.CredentialArrayAdapter adapter;
    IssueCredentialHandler issueCredHandler;
    VerifiableController verifiableController;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.credentials_fragment, container, false);

        final ListView listview = root.findViewById(R.id.credentialListView);
        MainActivity mainActivity = (MainActivity) getActivity();
        assert mainActivity != null;


        final List<Credential> list = new ArrayList<>();
        adapter = new CredentialsFragment.CredentialArrayAdapter(mainActivity,
                android.R.layout.simple_list_item_1, list);
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                final Credential item = (Credential) parent.getItemAtPosition(position);
                view.animate().setDuration(2000).alpha(0)
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                adapter.notifyDataSetChanged();
                                view.setAlpha(1);
                            }
                        });
            }

        });

        mViewModel = new ViewModelProvider(this).get(CredentialsViewModel.class);

        final Observer<List<Credential>> credentialObserver = new Observer<List<Credential>>() {
            @Override
            public void onChanged(List<Credential> credentials) {
                MutableLiveData<List<Credential>> creds = mViewModel.getCredentials();
                adapter.clear();
                adapter.addAll(Objects.requireNonNull(creds.getValue()));
            }
        };
        mViewModel.getCredentials().observe(getViewLifecycleOwner(), credentialObserver);


        try {
            AriesController agent = mainActivity.getAgent();

            verifiableController = agent.getVerifiableController();
            mViewModel.getCredentials().setValue(getCredentials());

            issueCredHandler = new IssueCredentialHandler(agent.getIssueCredentialController(), this);
            String registrationID = agent.registerHandler(issueCredHandler, "issue-credential_actions");
            System.out.println("isseu credential handler registered as: " + registrationID);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return root;
    }

    @Override
    public void onOffer(String piid, String label) {
        DialogFragment offerFrag = new CredentialOfferFragment(piid, label);
        offerFrag.setTargetFragment(this, 0);
        offerFrag.show(getParentFragmentManager(), "offers");
    }

    @Override
    public void accepted(String piid) {
        mViewModel.getCredentials().setValue(getCredentials());
    }

    @Override
    public void onAcceptCredentialClick(String piid, String label) {
        issueCredHandler.acceptOffer(piid, label);
    }

    @Override
    public void onRejectCredentialClick(DialogFragment dialog) {

    }


    private static class CredentialArrayAdapter extends ArrayAdapter<Credential> {

        HashMap<Credential, Integer> mIdMap = new HashMap<>();

        public CredentialArrayAdapter(Context context, int textViewResourceId,
                                      List<Credential> objects) {
            super(context, textViewResourceId, objects);
            for (int i = 0; i < objects.size(); ++i) {
                Credential conn = objects.get(i);
                System.out.println(conn.Name + " : " + conn.SNID + " : " + conn.MyDID + " : " + conn.TheirDID);
                mIdMap.put(objects.get(i), i);
            }
        }

        @Override
        public long getItemId(int position) {
            Credential item = getItem(position);
            Integer pos = mIdMap.get(item);
            if(pos == null) {
                return -1;
            }

            return pos;
        }


        @Override
        public boolean hasStableIds() {
            return true;
        }
    }

    public List<Credential> getCredentials() {

        ArrayList<Credential> out = new ArrayList<>();
        try {

            byte[] data = "{}".getBytes(StandardCharsets.UTF_8);
            ResponseEnvelope resp = verifiableController.getCredentials(new RequestEnvelope(data));

            if (resp.getError() != null) {
                CommandError err = resp.getError();
                System.out.println(err.toString());
            } else {
                GsonBuilder gsonb = new GsonBuilder();
                Gson gson = gsonb.create();

                String actionsResponse = new String(resp.getPayload(), StandardCharsets.UTF_8);
                System.out.println(actionsResponse);
                QueryCredentialResults results = gson.fromJson(actionsResponse, QueryCredentialResults.class);
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


}
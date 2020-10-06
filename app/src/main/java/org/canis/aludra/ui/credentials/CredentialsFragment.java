package org.canis.aludra.ui.credentials;

import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.canis.aludra.MainActivity;
import org.canis.aludra.R;
import org.canis.aludra.model.Connection;
import org.canis.aludra.model.Credential;
import org.canis.aludra.ui.connections.ConnectionsFragment;

import java.util.HashMap;
import java.util.List;

public class CredentialsFragment extends Fragment {

    private CredentialsViewModel mViewModel;

    public static CredentialsFragment newInstance() {
        return new CredentialsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.credentials_fragment, container, false);

        final ListView listview = (ListView) root.findViewById(R.id.credentialListView);
        MainActivity mainActivity = (MainActivity) getActivity();

        final List<Credential> list = mainActivity.getCredentials();
        final CredentialsFragment.CredentialArrayAdapter adapter = new CredentialsFragment.CredentialArrayAdapter(mainActivity,
                android.R.layout.simple_list_item_1, list);
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                final String item = (String) parent.getItemAtPosition(position);
                view.animate().setDuration(2000).alpha(0)
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                list.remove(item);
                                adapter.notifyDataSetChanged();
                                view.setAlpha(1);
                            }
                        });
            }

        });

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(CredentialsViewModel.class);
        // TODO: Use the ViewModel
    }

    private static class CredentialArrayAdapter extends ArrayAdapter<Credential> {

        HashMap<Credential, Integer> mIdMap = new HashMap<Credential, Integer>();

        public CredentialArrayAdapter(Context context, int textViewResourceId,
                                  List<Credential> objects) {
            super(context, textViewResourceId, objects);
            for (int i = 0; i < objects.size(); ++i) {
                Credential conn = objects.get(i);
                System.out.println(conn.Name + " : " +  conn.SNID + " : " + conn.MyDID + " : " + conn.TheirDID );
                mIdMap.put(objects.get(i), i);
            }
        }

        @Override
        public long getItemId(int position) {
            Credential item = getItem(position);
            return mIdMap.get(item);
        }



        @Override
        public boolean hasStableIds() {
            return true;
        }

    }

}
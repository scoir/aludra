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

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.canis.aludra.MainActivity;
import org.canis.aludra.R;
import org.canis.aludra.model.Connection;

import java.util.HashMap;
import java.util.List;

public class ConnectionsFragment extends Fragment {


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        final View root = inflater.inflate(R.layout.connections_fragment, container, false);

        final ListView listview = (ListView) root.findViewById(R.id.connectionListView);
        MainActivity mainActivity = (MainActivity) getActivity();

        final List<Connection> list = mainActivity.getConnections();
        final ConnectionArrayAdapter adapter = new ConnectionArrayAdapter(mainActivity,
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

        FloatingActionButton fab = root.findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment newFragment = new NewConnectionFragment();
                newFragment.show(getParentFragmentManager(), "connections");
            }
        });

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        mViewModel = new ViewModelProvider(this).get(ConnectionsViewModel.class);
        // TODO: Use the ViewModel
    }

    private static class ConnectionArrayAdapter extends ArrayAdapter<Connection> {

        HashMap<Connection, Integer> mIdMap = new HashMap<Connection, Integer>();

        public ConnectionArrayAdapter(Context context, int textViewResourceId,
                                      List<Connection> objects) {
            super(context, textViewResourceId, objects);
            for (int i = 0; i < objects.size(); ++i) {
                Connection conn = objects.get(i);
                mIdMap.put(objects.get(i), i);
            }
        }

        @Override
        public long getItemId(int position) {
            Connection item = getItem(position);
            return mIdMap.get(item);
        }



        @Override
        public boolean hasStableIds() {
            return true;
        }

    }

}
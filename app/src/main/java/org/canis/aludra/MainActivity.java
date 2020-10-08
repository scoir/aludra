package org.canis.aludra;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.canis.aludra.model.Connection;
import org.canis.aludra.model.QueryConnectionResults;
import org.canis.aludra.ui.connections.AcceptConnectionFragment;
import org.canis.aludra.ui.connections.DIDExchangeHandler;
import org.canis.aludra.ui.connections.NewConnectionFragment;
import org.hyperledger.aries.api.AriesController;
import org.hyperledger.aries.api.DIDExchangeController;
import org.hyperledger.aries.ariesagent.Ariesagent;
import org.hyperledger.aries.config.Options;
import org.hyperledger.aries.models.CommandError;
import org.hyperledger.aries.models.RequestEnvelope;
import org.hyperledger.aries.models.ResponseEnvelope;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements
        NewConnectionFragment.NewConnectionDialogListener,
        DIDExchangeHandler.DIDExchangeCallback, AcceptConnectionFragment.AcceptConnectionDialogListener
         {

    AriesController agent;
    DIDExchangeHandler didExchangeHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_connections, R.id.navigation_dashboard, R.id.navigation_credentials)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        Options opts = new Options();
        opts.setUseLocalAgent(false);
        opts.setLogLevel("DEBUG");
        opts.setAgentURL("http://10.0.2.2:5533");
        opts.setWebsocketURL("ws://10.0.2.2:5533/ws");

        try {
            agent = Ariesagent.new_(opts);
            didExchangeHandler = new DIDExchangeHandler(agent.getDIDExchangeController(), this);

            // create an aries agent instance
            String registrationID = agent.registerHandler(didExchangeHandler, "didexchange_states");
            System.out.println("didexchange handler registered as: " + registrationID);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public AriesController getAgent() {
        return agent;
    }

    @Override
    public void onInvite(String invitation) {
        // create options
        ResponseEnvelope res;
        try {
            // create a controller
            DIDExchangeController i = agent.getDIDExchangeController();

            // perform an operation
            byte[] data = invitation.getBytes(StandardCharsets.UTF_8);
            res = i.receiveInvitation(new RequestEnvelope(data));
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
            DIDExchangeController i = agent.getDIDExchangeController();

            byte[] data = "{}".getBytes(StandardCharsets.UTF_8);
            ResponseEnvelope resp = i.queryConnections(new RequestEnvelope(data));

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
        connFrag.show(getSupportFragmentManager(), "connections");
    }

    @Override
    public void onAcceptConnectionClick(String connectionID) {
        didExchangeHandler.Continue(connectionID, "");
    }

    @Override
    public void onCancelConnectionClick(DialogFragment dialog) {

    }

}
package org.canis.aludra;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.canis.aludra.ui.connections.ConnectionsFragment;
import org.canis.aludra.ui.connections.ScanInvitationFragment;
import org.hyperledger.aries.api.AriesController;
import org.hyperledger.aries.ariesagent.Ariesagent;
import org.hyperledger.aries.config.Options;


public class MainActivity extends AppCompatActivity implements AppBarConfiguration.OnNavigateUpListener {

    AriesController agent;
    AppBarConfiguration appBarConfiguration;
    ScanInvitationFragment.ScanInvitationListener scanInvitationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Options opts = new Options();
        opts.setUseLocalAgent(false);
        opts.setLogLevel("INFO");
        opts.setAgentURL("http://10.0.2.2:5533");
        opts.setWebsocketURL("ws://10.0.2.2:5533/ws");

        try {
            agent = Ariesagent.new_(opts);
        } catch (Exception e) {
            e.printStackTrace();
        }

        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_connections, R.id.navigation_dashboard, R.id.navigation_credentials)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void setScanInvitationListener(ScanInvitationFragment.ScanInvitationListener scanInvitationListener) {
        this.scanInvitationListener = scanInvitationListener;
    }

    public ScanInvitationFragment.ScanInvitationListener getScanInvitationListener() {
        return scanInvitationListener;
    }

    public AriesController getAgent() {
        return agent;
    }

}
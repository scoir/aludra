package org.canis.aludra;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.crypto.tink.signature.SignatureConfig;
import com.google.crypto.tink.subtle.Base64;
import com.google.crypto.tink.subtle.Ed25519Sign;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


import org.canis.aludra.model.CloudAgent;
import org.canis.aludra.model.Registration;
import org.canis.aludra.service.RegisterTask;
import org.canis.aludra.ui.connections.ScanInvitationFragment;

import java.security.GeneralSecurityException;


public class MainActivity extends AppCompatActivity implements AppBarConfiguration.OnNavigateUpListener, RegisterTask.RegisterTaskHandler {

    AppBarConfiguration appBarConfiguration;
    ScanInvitationFragment.ScanInvitationListener scanInvitationListener;

    private String CloudAgentId;
    private Ed25519Sign.KeyPair signKeyPair;
    private Ed25519Sign.KeyPair nextKeyPair;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_connections, R.id.navigation_dashboard, R.id.navigation_credentials)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        try {
            SignatureConfig.register();

            this.signKeyPair = Ed25519Sign.KeyPair.newKeyPair();
            byte[] signPubKeyBytes = signKeyPair.getPublicKey();

            this.nextKeyPair = Ed25519Sign.KeyPair.newKeyPair();
            byte[] nextPubKeyBytes = nextKeyPair.getPublicKey();

            String encodedSignKey = Base64.encodeToString(signPubKeyBytes, Base64.DEFAULT | Base64.NO_WRAP);
            String encodedNextKey = Base64.encodeToString(nextPubKeyBytes, Base64.DEFAULT | Base64.NO_WRAP);

            Registration reg = new Registration(
                    encodedSignKey,
                    encodedNextKey,
                    "ArwXoACJgOleVZ2PY7kXn7rA0II0mHYDhc6WrBH8fDAc"
            );

            GsonBuilder gsonb = new GsonBuilder();
            Gson gson = gsonb.create();
            String json = gson.toJson(reg);

            RegisterTask task = new RegisterTask(this);
            task.execute(reg);

        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
    }

    public byte[] Sign(byte[] data) throws GeneralSecurityException {
        Ed25519Sign signer = new Ed25519Sign(this.signKeyPair.getPrivateKey());
        return signer.sign(data);
    }

    public String getCloudAgentId() {
        return this.CloudAgentId;
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

    @Override
    public void HandleCloudAgent(CloudAgent cloudAgent) {
        this.CloudAgentId = cloudAgent.cloudAgentId;
    }
}
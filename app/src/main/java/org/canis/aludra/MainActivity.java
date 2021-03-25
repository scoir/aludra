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
import org.canis.aludra.model.Wallet;
import org.canis.aludra.service.RegisterTask;
import org.canis.aludra.ui.connections.ScanInvitationFragment;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.security.GeneralSecurityException;


public class MainActivity extends AppCompatActivity implements AppBarConfiguration.OnNavigateUpListener,
        RegisterTask.RegisterTaskHandler {

    private static String WALLET_FILE_NAME = "wallet.json";

    AppBarConfiguration appBarConfiguration;
    ScanInvitationFragment.ScanInvitationListener scanInvitationListener;

    private Wallet wallet;


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

        this.wallet = new Wallet();

        try {
            InputStream in = this.openFileInput(WALLET_FILE_NAME);
            GsonBuilder gsonb = new GsonBuilder();
            Gson gson = gsonb.disableHtmlEscaping().create();

            this.wallet = gson.fromJson(new InputStreamReader(in), Wallet.class);

        } catch (FileNotFoundException e) {
            System.out.println("*****************************************************************");
            System.out.println("exception finding file, initializing cloud agent");
            System.out.println("*****************************************************************");
            initializeCloudAgent();
        }

    }

    private void initializeCloudAgent() {
        try {
            SignatureConfig.register();


            Ed25519Sign.KeyPair signKeyPair = Ed25519Sign.KeyPair.newKeyPair();
            byte[] signPubKeyBytes = signKeyPair.getPublicKey();
            byte[] signPrivKeyBytes = signKeyPair.getPrivateKey();

            Ed25519Sign.KeyPair nextKeyPair = Ed25519Sign.KeyPair.newKeyPair();
            byte[] nextPubKeyBytes = nextKeyPair.getPublicKey();
            byte[] nextPrivKeyBytes = nextKeyPair.getPrivateKey();

            this.wallet.publicSigningKey = Base64.encodeToString(signPubKeyBytes, Base64.DEFAULT | Base64.NO_WRAP);
            this.wallet.publicNextKey = Base64.encodeToString(nextPubKeyBytes, Base64.DEFAULT | Base64.NO_WRAP);

            this.wallet.privateSigningKey = Base64.encodeToString(signPrivKeyBytes, Base64.DEFAULT | Base64.NO_WRAP);
            this.wallet.privateNextKey = Base64.encodeToString(nextPrivKeyBytes, Base64.DEFAULT | Base64.NO_WRAP);

            Registration reg = new Registration(
                    this.wallet.publicSigningKey,
                    this.wallet.publicNextKey,
                    "ArwXoACJgOleVZ2PY7kXn7rA0II0mHYDhc6WrBH8fDAc"
            );

            RegisterTask task = new RegisterTask(this);
            task.execute(reg);

        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
    }

    public byte[] Sign(byte[] data) throws GeneralSecurityException {
        byte[] privKey = Base64.decode(this.wallet.privateSigningKey, Base64.DEFAULT | Base64.NO_WRAP);
        Ed25519Sign signer = new Ed25519Sign(privKey);
        return signer.sign(data);
    }

    public String getCloudAgentId() {
        return this.wallet.cloudAgentId;
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
        this.wallet.cloudAgentId = cloudAgent.cloudAgentId;

        GsonBuilder gsonb = new GsonBuilder();
        Gson gson = gsonb.disableHtmlEscaping().create();
        String json = gson.toJson(this.wallet);

        try {
            OutputStream out = this.openFileOutput(WALLET_FILE_NAME, MODE_APPEND);
            out.write(json.getBytes());
            out.flush();
            out.close();
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
}
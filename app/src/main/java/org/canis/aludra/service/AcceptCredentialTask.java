package org.canis.aludra.service;

import android.os.AsyncTask;

import org.canis.aludra.model.AcceptCredentialResult;
import org.canis.aludra.model.Credential;
import org.canis.aludra.model.Invitation;
import org.canis.aludra.model.InvitationResult;

import java.io.IOException;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AcceptCredentialTask extends AsyncTask<Credential, Void, AcceptCredentialResult> {

    public interface AcceptCredentialTaskHandler {
        void HandleConnections(AcceptCredentialResult result);
    }

    private final AcceptCredentialTaskHandler handler;

    private String cloudAgentId;
    private String signature;

    public AcceptCredentialTask(AcceptCredentialTaskHandler handler, String cloudAgentId, String signature) {
        this.handler = handler;
        this.cloudAgentId = cloudAgentId;
        this.signature = signature;
    }


    @Override
    protected AcceptCredentialResult doInBackground(Credential... creds) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:11004")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        CanisService service = retrofit.create(CanisService.class);

        Call<AcceptCredentialResult> call = service.AcceptCredential(this.cloudAgentId, this.signature, creds[0].id, new HashMap());
        try {
            Response<AcceptCredentialResult> resp = call.execute();
            if (resp.isSuccessful()) {
                return resp.body();
            } else {
                throw new IOException(resp.errorBody().string());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(AcceptCredentialResult result) {
        super.onPostExecute(result);
        this.handler.HandleConnections(result);
    }
}

package org.canis.aludra.service;

import android.os.AsyncTask;

import org.canis.aludra.model.ConnectionRequest;
import org.canis.aludra.model.ConnectionResult;
import org.canis.aludra.model.CredentialRequest;
import org.canis.aludra.model.CredentialResult;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ListCredentialsTask extends AsyncTask<CredentialRequest, Void, CredentialResult> {

    public interface ListCredentialsTaskHandler {
        void HandleCredentials(CredentialResult result);
    }

    private final ListCredentialsTaskHandler handler;

    private String cloudAgentId;
    private String signature;

    public ListCredentialsTask(ListCredentialsTaskHandler handler, String cloudAgentId, String signature) {
        this.handler = handler;
        this.cloudAgentId = cloudAgentId;
        this.signature = signature;
    }


    @Override
    protected CredentialResult doInBackground(CredentialRequest... reqs) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:11004")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        CanisService service = retrofit.create(CanisService.class);

        Call<CredentialResult> call = service.ListCredentials(this.cloudAgentId, this.signature, reqs[0]);
        try {
            Response<CredentialResult> resp = call.execute();
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
    protected void onPostExecute(CredentialResult result) {
        super.onPostExecute(result);
        this.handler.HandleCredentials(result);
    }
}

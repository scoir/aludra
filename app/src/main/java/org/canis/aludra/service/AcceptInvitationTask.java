package org.canis.aludra.service;

import android.os.AsyncTask;

import org.canis.aludra.model.Invitation;
import org.canis.aludra.model.InvitationResult;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AcceptInvitationTask extends AsyncTask<Invitation, Void, InvitationResult> {

    public interface AcceptInvitationTaskHandler {
        void HandleConnections(InvitationResult result);
    }

    private final AcceptInvitationTaskHandler handler;

    private String cloudAgentId;
    private String signature;

    public AcceptInvitationTask(AcceptInvitationTaskHandler handler, String cloudAgentId, String signature) {
        this.handler = handler;
        this.cloudAgentId = cloudAgentId;
        this.signature = signature;
    }


    @Override
    protected InvitationResult doInBackground(Invitation... invites) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://canis.scoir.ninja")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        CanisService service = retrofit.create(CanisService.class);

        Call<InvitationResult> call = service.AcceptInvitation(this.cloudAgentId, this.signature, invites[0]);
        try {
            Response<InvitationResult> resp = call.execute();
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
    protected void onPostExecute(InvitationResult result) {
        super.onPostExecute(result);

        this.handler.HandleConnections(result);
    }
}

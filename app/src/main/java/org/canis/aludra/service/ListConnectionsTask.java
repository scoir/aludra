package org.canis.aludra.service;

import android.os.AsyncTask;

import org.canis.aludra.model.CloudAgent;
import org.canis.aludra.model.Connection;
import org.canis.aludra.model.ConnectionRequest;
import org.canis.aludra.model.ConnectionResult;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ListConnectionsTask extends AsyncTask<ConnectionRequest, Void, ConnectionResult> {

    public interface ListConnectionsTaskHandler {
        void HandleConnections(ConnectionResult result);
    }

    private final ListConnectionsTaskHandler handler;

    private String cloudAgentId;
    private String signature;

    public ListConnectionsTask(ListConnectionsTaskHandler handler, String cloudAgentId, String signature) {
        this.handler = handler;
        this.cloudAgentId = cloudAgentId;
        this.signature = signature;
    }


    @Override
    protected ConnectionResult doInBackground(ConnectionRequest... reqs) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://canis.scoir.ninja")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        CanisService service = retrofit.create(CanisService.class);

        Call<ConnectionResult> call = service.ListConnections(this.cloudAgentId, this.signature, reqs[0]);
        try {
            Response<ConnectionResult> resp = call.execute();
            if (resp.isSuccessful()) {
                System.out.println("*****************************************************************");
                System.out.println(resp.toString());
                System.out.println("*************************************************************");

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
    protected void onPostExecute(ConnectionResult result) {
        super.onPostExecute(result);

        System.out.println("*****************************************************************");
        System.out.println(result.count);
        if (result.count > 0) {
            result.connections.forEach((connection -> System.out.println(connection.ConnectionID)));
        }
        System.out.println("*****************************************************************");

        this.handler.HandleConnections(result);
    }
}

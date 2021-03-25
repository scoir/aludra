package org.canis.aludra.service;

import android.os.AsyncTask;

import org.canis.aludra.model.CloudAgent;
import org.canis.aludra.model.Registration;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegisterTask extends AsyncTask<Registration, Void, CloudAgent> {

    public interface RegisterTaskHandler {
        void HandleCloudAgent(CloudAgent cloudAgent);
    }

    private final RegisterTaskHandler handler;

    public RegisterTask(RegisterTaskHandler handler) {
        this.handler = handler;
    }

    @Override
    protected CloudAgent doInBackground(Registration... registrations) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:11004/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        CanisService service = retrofit.create(CanisService.class);

        Call<CloudAgent> call = service.Register(
                registrations[0]
        );

        try {
            Response<CloudAgent> resp = call.execute();
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
    protected void onPostExecute(CloudAgent cloudAgent) {
        super.onPostExecute(cloudAgent);
        handler.HandleCloudAgent(cloudAgent);
    }
}

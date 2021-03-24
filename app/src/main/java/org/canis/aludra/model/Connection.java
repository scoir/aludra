package org.canis.aludra.model;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class Connection {
    @SerializedName("id")public String ConnectionID;
    @SerializedName("status")public String Status;
    @SerializedName("name")public String Name;
    @SerializedName("their_did")public String TheirDID;
    @SerializedName("my_did")public String MyDID;
    @SerializedName("last_updated") public Date LastUpdated;

    @NonNull
    @Override
    public String toString() {
        return this.Name;
    }
}

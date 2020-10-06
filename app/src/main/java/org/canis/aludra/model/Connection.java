package org.canis.aludra.model;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

public class Connection {
    @SerializedName("ConnectionID")public String ConnectionID;
    @SerializedName("State")public String State;
    @SerializedName("ThreadID")public String ThreadID;
    @SerializedName("ParentThreadID")public String ParentThreadID;
    @SerializedName("TheirLabel")public String TheirLabel;
    @SerializedName("TheirDID")public String TheirDID;
    @SerializedName("MyDID")public String MyDID;
    @SerializedName("ServiceEndPoint")public String ServiceEndPoint;
    @SerializedName("RecipientKeys")public String[] RecipientKeys;
    @SerializedName("RoutingKeys")public String []RoutingKeys;
    @SerializedName("InvitationID")public String InvitationID;
    @SerializedName("InvitationDID")public String InvitationDID;
    @SerializedName("Implicit")public Boolean Implicit;
    @SerializedName("Namespace")public String Namespace;

    @NonNull
    @Override
    public String toString() {
        return this.TheirLabel;
    }
}

package org.canis.aludra.model;

import com.google.gson.annotations.SerializedName;

public class Invitation extends BaseModel {
    @SerializedName("label")public String label;
    @SerializedName("recipientKeys")public String[] recipientKeys;
    @SerializedName("serviceEndpoint")public String  serviceEndpoint;
}


package org.canis.aludra.model;

import com.google.gson.annotations.SerializedName;

public class StateMsg <T extends BaseModel, K>{
    @SerializedName("ProtocolName") public String ProtocolName;
    @SerializedName("Type") public String Type;
    @SerializedName("StateID") public String StateID;

    @SerializedName("Message") public T message;
    @SerializedName("Properties")public K properties;
}

package org.canis.aludra.model;

import com.google.gson.annotations.SerializedName;

public class ActionMsg <T extends BaseModel, K>{
    @SerializedName("Message") public T message;
    @SerializedName("Properties")public K properties;
    @SerializedName("ProtocolName") public String ProtocolName;
}

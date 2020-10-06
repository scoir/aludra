package org.canis.aludra.model;

import com.google.gson.annotations.SerializedName;

public class StateProtocolMsg<T extends StateMsg<?, ?>>{
    @SerializedName("id") public String id;
    @SerializedName("topic") public String topic;
    @SerializedName("message") public T message;
}

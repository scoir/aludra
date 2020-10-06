package org.canis.aludra.model;

import com.google.gson.annotations.SerializedName;

public class ProtocolMsg <T extends ActionMsg<?, ?>>{
    @SerializedName("id") public String id;
    @SerializedName("topic") public String topic;
    @SerializedName("message") public T message;
}

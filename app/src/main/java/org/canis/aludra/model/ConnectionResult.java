package org.canis.aludra.model;

import com.google.gson.annotations.SerializedName;

public class ConnectionResult {
    @SerializedName("code") public int code;
    @SerializedName("message") public String message;
}

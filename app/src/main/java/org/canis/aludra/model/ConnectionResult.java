package org.canis.aludra.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ConnectionResult {
    @SerializedName("count") public int count;
    @SerializedName("connections") public List<Connection> connections;
}

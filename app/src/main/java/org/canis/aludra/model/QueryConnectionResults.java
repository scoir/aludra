package org.canis.aludra.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class QueryConnectionResults {
    @SerializedName("results")public List<Connection> results;
}

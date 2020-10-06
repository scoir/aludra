package org.canis.aludra.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class QueryCredentialResults {
    @SerializedName("result")public List<Credential> results;
}

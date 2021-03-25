package org.canis.aludra.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CredentialResult {
    @SerializedName("count") public int count;
    @SerializedName("credentials") public List<Credential> credentials;
}

package org.canis.aludra.model;

import com.google.gson.annotations.SerializedName;

public class Registration {
    @SerializedName("public_key") public String publicKey;
    @SerializedName("next_key") public String nextKey;
    @SerializedName("secret") public String secret;

    public Registration(String publicKey, String nextKey, String secret) {
        this.publicKey = publicKey;
        this.nextKey = nextKey;
        this.secret = secret;
    }

}

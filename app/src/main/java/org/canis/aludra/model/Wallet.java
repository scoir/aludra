package org.canis.aludra.model;

import com.google.gson.annotations.SerializedName;

public class Wallet {

    @SerializedName("cloud_agent_id") public String cloudAgentId;
    @SerializedName("private_signing_key") public String privateSigningKey;
    @SerializedName("public_signing_key") public String publicSigningKey;

    @SerializedName("private_next_key") public String privateNextKey;
    @SerializedName("public_next_key") public String publicNextKey;



}

package org.canis.aludra.model;

import com.google.gson.annotations.SerializedName;

public class CredentialOfferActionMsg extends ActionMsg<Offer, CredentialOfferActionMsg.Properties> {

    public String getMyDID() {
        return properties.myDID;
    }

    public String getTheirDID() {
        return properties.theirDID;
    }
    public String getPIID() { return properties.piid; }

    static class Properties {
        @SerializedName("myDID") public String myDID;
        @SerializedName("theirDID") public String theirDID;
        @SerializedName("piid") public String piid;
    }

}

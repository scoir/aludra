package org.canis.aludra.model;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

public class Offer extends BaseModel {
    @SerializedName("comment")public String comment;
    @SerializedName("credential_preview") CredentialPreview preview;
    @SerializedName("formats") Format[] formats;
    @SerializedName("offers~attach") OfferAttach[] offers;

    static public class CredentialPreview {
        @SerializedName("@type")public String type;
        @SerializedName("attributes")public Map<String, String>[] attributes;
    }

    static public class Format {
        @SerializedName("attach_id") String attachID;
        @SerializedName("format") String format;
    }

    static public class OfferAttach {
        @SerializedName("@id")public String id;
        @SerializedName("data") public Data data;
    }
}

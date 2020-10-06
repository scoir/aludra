package org.canis.aludra.model;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

public class Credential {
    @SerializedName("name") public String Name;
    @SerializedName("id") public String SNID;
    @SerializedName("context") public String[] SNContext;
    @SerializedName("type") public String[] SNType;
    @SerializedName("sibject_id") public String SNSubjectID;
    // MyDID and TheirDID contains information about participants who were involved in the process
    // of issuing a credential or presentation.
    @SerializedName("my_did") public String MyDID;
    @SerializedName("their_did") public String TheirDID;

    @NonNull
    public String toString() {
        return Name;
    }

}

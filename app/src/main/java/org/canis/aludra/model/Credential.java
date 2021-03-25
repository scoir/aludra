package org.canis.aludra.model;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

public class Credential {
    @SerializedName("comment") public String Comment;
    @SerializedName("status") public String Status;
    @SerializedName("credential_id") public String id;
    @SerializedName("subject_id") public String SNSubjectID;
    @SerializedName("my_did") public String MyDID;
    @SerializedName("their_did") public String TheirDID;

    @NonNull
    public String toString() {
        return Comment;
    }

}

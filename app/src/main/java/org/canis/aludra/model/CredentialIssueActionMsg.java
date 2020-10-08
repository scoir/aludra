package org.canis.aludra.model;

import com.google.gson.annotations.SerializedName;

public class CredentialIssueActionMsg extends ActionMsg<Issue, CredentialIssueActionMsg.Properties> {

    public String getMyDID() {
        return properties.myDID;
    }
    public String getComment() {
        return message.comment;
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

package org.canis.aludra.model;

import com.google.gson.annotations.SerializedName;

public class InviteStateMsg extends StateMsg<Invitation, InviteStateMsg.Properties> {

    public String getConnectionID() {
        return properties.connectionID;
    }

    public String getInvitationID() {
        return properties.invitationID;
    }

    static class Properties {
        @SerializedName("connectionID") public String connectionID;
        @SerializedName("invitationID") public String invitationID;
    }
}



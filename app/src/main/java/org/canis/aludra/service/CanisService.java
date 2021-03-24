package org.canis.aludra.service;

import org.canis.aludra.model.CloudAgent;
import org.canis.aludra.model.ConnectionRequest;
import org.canis.aludra.model.ConnectionResult;
import org.canis.aludra.model.Invitation;
import org.canis.aludra.model.InvitationResult;
import org.canis.aludra.model.Registration;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface CanisService {

    @POST("cloudagents")
    Call<CloudAgent> Register(@Body Registration body);

    @POST("cloudagents/connections")
    Call<ConnectionResult> ListConnections(
            @Header("x-canis-cloud-agent-id") String cloudAgentId,
            @Header("x-canis-cloud-agent-signature") String signature,
            @Body ConnectionRequest request
    );

    @POST("cloudagents/invitation")
    Call<InvitationResult> AcceptInvitation(
            @Header("x-canis-cloud-agent-id") String cloudAgentId,
            @Header("x-canis-cloud-agent-signature") String signature,
            @Body Invitation invitation
    );

}

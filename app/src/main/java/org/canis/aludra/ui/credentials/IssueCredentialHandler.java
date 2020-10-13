package org.canis.aludra.ui.credentials;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.canis.aludra.model.CredentialIssueActionMsg;
import org.canis.aludra.model.CredentialOfferActionMsg;
import org.canis.aludra.model.PlainProtocolMsg;
import org.canis.aludra.model.ProtocolMsg;
import org.hyperledger.aries.api.Handler;
import org.hyperledger.aries.api.IssueCredentialController;
import org.hyperledger.aries.models.RequestEnvelope;
import org.hyperledger.aries.models.ResponseEnvelope;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Formatter;
import java.util.Locale;


public class IssueCredentialHandler implements Handler {

    String lastTopic, lastMessage;
    private IssueCredentialController ctrl;
    private IssueCredentialCallback cb;


    public interface IssueCredentialCallback {
        void onOffer(String piid, String label);
        void accepted(String piid);
    }


    final static String IssueCredentialMsgType = "https://didcomm.org/issue-credential/2.0/issue-credential";
    final static String OfferCredentialMsgType = "https://didcomm.org/issue-credential/2.0/offer-credential";


    public IssueCredentialHandler(IssueCredentialController ctrl, IssueCredentialCallback cb) {
        this.ctrl = ctrl;
        this.cb = cb;
    }

    @Override
    public void handle(String topic, byte[] message) throws Exception {
        lastTopic = topic;
        lastMessage = new String(message, StandardCharsets.UTF_8);

        GsonBuilder gsonb = new GsonBuilder();
        Gson gson = gsonb.create();

        PlainProtocolMsg plain = gson.fromJson(lastMessage, PlainProtocolMsg.class);

        switch (plain.getType()) {
            case OfferCredentialMsgType:
                Type msgType = new TypeToken<ProtocolMsg<CredentialOfferActionMsg>>(){}.getType();
                ProtocolMsg<CredentialOfferActionMsg> msg = gson.fromJson(lastMessage, msgType);
                StringBuilder sb = new StringBuilder();
                Formatter formatter = new Formatter(sb, Locale.US);
                String accept = formatter.format("{\"piid\": \"%s\"}", msg.message.getPIID()).toString();
                byte[] data = accept.getBytes(StandardCharsets.US_ASCII);

                RequestEnvelope env = new RequestEnvelope(null);
                env.setPayload(data);
                ResponseEnvelope res = ctrl.acceptOffer(env);
                if (res.getError() != null && !res.getError().getMessage().isEmpty()) {
                    Log.d("failed to accept offer: ", res.getError().toString());
                } else {
                    String receiveInvitationResponse = new String(res.getPayload(), StandardCharsets.UTF_8);
                    Log.d("accepting offer with: ", receiveInvitationResponse);
                }

            case (IssueCredentialMsgType):
                Type issueMsgType = new TypeToken<ProtocolMsg<CredentialIssueActionMsg>>(){}.getType();
                ProtocolMsg<CredentialIssueActionMsg> issueMsg = gson.fromJson(lastMessage, issueMsgType);
                cb.onOffer(issueMsg.message.getPIID(), issueMsg.message.getComment());
        }

        Log.d("received notification topic: ", lastTopic);
        Log.d("received notification message: ", lastMessage);

    }

    public void acceptOffer(String piid, String label) {
        StringBuilder sb = new StringBuilder();
        Formatter formatter = new Formatter(sb, Locale.US);
        String accept = formatter.format("{\"piid\": \"%s\", \"names\": [\"%s\"]}", piid, label).toString();
        byte[] data = accept.getBytes(StandardCharsets.US_ASCII);

        RequestEnvelope env = new RequestEnvelope(null);
        env.setPayload(data);
        ResponseEnvelope res = ctrl.acceptCredential(env);
        if (res.getError() != null && !res.getError().getMessage().isEmpty()) {
            Log.d("failed to accept offer: ", res.getError().toString());
        } else {
            String receiveInvitationResponse = new String(res.getPayload(), StandardCharsets.UTF_8);
            Log.d("accepting offer with: ", receiveInvitationResponse);
        }

        cb.accepted(piid);
    }
}

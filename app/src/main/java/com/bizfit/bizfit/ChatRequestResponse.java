package com.bizfit.bizfit;

import com.bizfit.bizfit.network.NetMessage;
import com.bizfit.bizfit.network.Network;
import com.bizfit.bizfit.network.NetworkReturn;
import com.bizfit.bizfit.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

/**
 * Created by attey on 16/03/2017.
 */

public class ChatRequestResponse
{
    boolean response;
    boolean hasBeenSent;
    String customer;
    String coach;
    String message="";
    UUID uuid;
    public ChatRequestResponse(ChatRequest chatRequest,boolean response){
        this.customer =chatRequest.customer;
        this.coach=chatRequest.coach;
        hasBeenSent=false;
        this.response=response;
        this.uuid=chatRequest.uuid;
    }
    public JSONObject toJSON(){
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("response",response);
            jsonObject.put("customer", customer);
            jsonObject.put("coach",coach);
            jsonObject.put("message",message);
            jsonObject.put(Constants.UUID,uuid.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
    private void checkToResend(){
        if(!hasBeenSent){
            sentToNet();
        }
    }
    private void sentToNet(){
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put(Constants.job,"handleChatRequest");
            jsonObject.put("chatRequestResponse",this.toJSON());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(!hasBeenSent){
            Network.addNetMessage(new NetMessage(null, new NetworkReturn() {
                @Override
                public void returnMessage(String message) {
                    if(message.equals(Constants.networkconn_failed)){
                        checkToResend();
                    }else{
                        ChatRequestResponse.this.hasBeenSent=true;
                    }
                }
            },jsonObject));
        }
    }
    public void sentResponse(){
        BackgroundThread.addOurRunnable(new OurRunnable(true,1000*60) {
            @Override
            public void run() {
                checkToResend();
                if(hasBeenSent){
                    repeat=false;
                }
            }
        });
    }
}

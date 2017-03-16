package com.bizfit.bizfit;

import com.bizfit.bizfit.network.NetMessage;
import com.bizfit.bizfit.network.Network;
import com.bizfit.bizfit.network.NetworkReturn;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by attey on 16/03/2017.
 */

public class ChatRequestResponse {
    boolean response;
    boolean hasBeenSent;
    String costomer;
    String coach;
    String message="";
    public ChatRequestResponse(ChatRequest chatRequest,boolean response){
        this.costomer=chatRequest.customer;
        this.coach=chatRequest.coach;
        hasBeenSent=false;
        this.response=response;
    }
    public JSONObject toJSON(){
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("response",response);
            jsonObject.put("costomer",costomer);
            jsonObject.put("coach",coach);
            jsonObject.put("message",message);
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
        if(!hasBeenSent){
            Network.addNetMessage(new NetMessage(null, new NetworkReturn() {
                @Override
                public void returnMessage(String message) {
                    if(message.equals("failed")){
                        checkToResend();
                    }else{
                        ChatRequestResponse.this.hasBeenSent=true;
                    }
                }
            },this.toJSON()));
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

package com.bizfit.bizfit;

import com.bizfit.bizfit.network.NetMessage;
import com.bizfit.bizfit.network.Network;
import com.bizfit.bizfit.network.NetworkReturn;
import com.bizfit.bizfit.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by attey on 06/03/2017.
 */

public class ChatRequest {
    String costomer;
    String expert;
    String message;
    public ChatRequest(String costomer,String expert){
        this.costomer=costomer;
        this.expert=expert;
        message="no message";
    }
    public ChatRequest(String costomer,String expert,String message){
        this.costomer=costomer;
        this.expert=expert;
        if(message!=null){
            this.message=message;
        }else{
            this.message="no message";
        }
    }

    public JSONObject toJSON(){
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("costomer",costomer);
            jsonObject.put("expert",expert);
            jsonObject.put("message",message);
            jsonObject.put(Constants.job,"ChatRequest");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public void sendToNet(){
        Network.addNetMessage(new NetMessage(null, new NetworkReturn() {
            @Override
            public void returnMessage(String message) {

            }
        },this.toJSON()));
    }
}

package com.bizfit.bizfitUusYritysKeskusAlpha;

import android.content.Context;
import android.widget.Toast;

import com.bizfit.bizfitUusYritysKeskusAlpha.network.NetMessage;
import com.bizfit.bizfitUusYritysKeskusAlpha.network.Network;
import com.bizfit.bizfitUusYritysKeskusAlpha.network.NetworkReturn;
import com.bizfit.bizfitUusYritysKeskusAlpha.utils.Constants;

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
    public ChatRequestResponse(JSONObject jsonObject){
        try {
            if(jsonObject.has("response")) {
                this.response = jsonObject.getBoolean("response");
            }
            if(jsonObject.has("customer")){
                this.customer=jsonObject.getString("customer");
            }
            if(jsonObject.has("coach")){
                this.coach=jsonObject.getString("coach");
            }
            if(jsonObject.has("message")){
                this.message=jsonObject.getString("message");
            }
            if(jsonObject.has(Constants.UUID)){
                this.uuid=UUID.fromString(jsonObject.getString(Constants.UUID));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    public JSONObject toJSON(){
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("response",response);
            jsonObject.put("customer", customer);
            jsonObject.put("coach",coach);
            jsonObject.put("message",message);
            jsonObject.put(Constants.UUID,uuid.toString());
        } catch (JSONException e)
        {
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
    public void sentResponse()
    {
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

    /**
     * Using a toast to show the user the result of the chat, accepted or declined
     * by jarij 17.3.17
     */
    public void showChatResponse(Context context)
    {
        String text="";
        if(response)
        {
            text = "Your chat request with " + coach + " is accepted";
        }
        else
        {
            text = "Your chat request with " + coach + " is declined";
        }
        int duration = Toast.LENGTH_SHORT;
       // NotificationSender.sendNotification(context,"Chat Request",text);
        //Toast toast = Toast.makeText(context, text, duration);
        //toast.show();
    }
}

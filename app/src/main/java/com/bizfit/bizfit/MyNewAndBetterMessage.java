package com.bizfit.bizfit;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by attey on 02/12/2016.
 */

public class MyNewAndBetterMessage implements NetworkReturn {

    String resipient="";
    String sender="";
    String message="";
    long creationTime=0;
    boolean hasBeenSent=false;

    public MyNewAndBetterMessage(JSONObject jsonObject){
        try {
            sender=jsonObject.getString("sender");
            resipient=jsonObject.getString("resipient");
            if(jsonObject.has("message")){
                message=jsonObject.getString("message");
            }
            creationTime=jsonObject.getLong("creationTime");
            hasBeenSent=jsonObject.getBoolean("hasBeenSent");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public MyNewAndBetterMessage(String resipient,String sender,String message){
        this.resipient=resipient;
        this.sender=sender;
        this.message=message;
        creationTime=System.currentTimeMillis();
    }

    public void sendMessage(String targetAddress){
        JSONObject message=new JSONObject();
        try {
            //// TODO: 02/12/2016 check what correct job was
            message.put("job","message");
            message.put("message",this.toJson());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new Thread(new MyNetwork(targetAddress,this,message)).start();

    }
    public void reSend(){
        if(!hasBeenSent){
            sendMessage(null);
        }
    }

    @Override
    public void returnMessage(String message) {
        if (!message.equals("failed")){
            hasBeenSent=true;
        }
    }
    public JSONObject toJson(){
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("sender",sender);
            jsonObject.put("resipient",resipient);
            jsonObject.put("message",message);
            jsonObject.put("creationTime",creationTime);
            jsonObject.put("hasBeenSent",hasBeenSent);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
}

package com.bizfit.bizfitUusYritysKeskusAlpha;

import com.bizfit.bizfitUusYritysKeskusAlpha.network.NetMessage;
import com.bizfit.bizfitUusYritysKeskusAlpha.network.Network;
import com.bizfit.bizfitUusYritysKeskusAlpha.network.NetworkReturn;
import com.bizfit.bizfitUusYritysKeskusAlpha.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.UUID;

/**
 * Created by attey on 06/03/2017.
 */

public class ChatRequest
{
    public String customer="";
    public String coach="";
    public Need need = Need.UNDEFINED;
    public Skill skill = Skill.UNDEFINED;
    public String message="";
    public UUID uuid = new UUID(0L, 0L);

    public ChatRequest(){
        uuid=UUID.randomUUID();
    }
    public ChatRequest(JSONObject jsonObject){
            this();
            try {
                if(jsonObject.has(Constants.customer)) {
                    customer =jsonObject.getString(Constants.customer);
                }
                if(jsonObject.has(Constants.coach)){
                    coach =jsonObject.getString(Constants.coach);
                }
                if(jsonObject.has(Constants.message_ChatRequest)){
                    message=jsonObject.getString(Constants.message_ChatRequest);
                }
                if(jsonObject.has(Constants.UUID)){
                    uuid=UUID.fromString(jsonObject.getString(Constants.UUID));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

    }
    public ChatRequest(String customer, String coach){
        this();
        this.customer = customer;
        this.coach = coach;
        message="no message";
        need = Need.UNDEFINED;
        skill = skill.UNDEFINED;
    }

    public ChatRequest(String customer, String coach, String message){
        this();
        this.customer = customer;
        this.coach = coach;
        if(message!=null){
            this.message=message;
        }else{
            this.message="no message";
        }
        need = Need.UNDEFINED;
        skill = Skill.UNDEFINED;
    }

    public ChatRequest(String customer, String coach, String need, String skill, String message) {
        this();
        this.customer = customer;
        this.coach = coach;
        this.need = Need.valueOf(need);
        this.skill = Skill.valueOf(skill);
        if(message!=null){
            this.message=message;
        }else{
            this.message="no message";
        }
    }


    public JSONObject toJSON(){
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put(Constants.customer, customer);
            jsonObject.put(Constants.coach, coach);
            jsonObject.put(Constants.message_ChatRequest,message);
            jsonObject.put(Constants.job,"ChatRequest");
            jsonObject.put(Constants.UUID,uuid.toString());
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
        User.getLastUser(null,null,null).addChatRquestFromMe(this);
    }

    public static void addToList(List<ChatRequest> list,ChatRequest chatRequest){
        if(!chatRequest.allReadyInList(list)){
            list.add(chatRequest);
        }
    }
    public boolean allReadyInList(List<ChatRequest>list){
        for(int i=0;i<list.size();i++){
            if(list.get(i).uuid.equals(this.uuid)){
                return true;
            }
        }
        return false;
    }
    public void cancelRequest(){
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put(Constants.job,"cancelChatRequest");
            jsonObject.put("coach",coach);
            jsonObject.put(Constants.UUID,uuid.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Network.addNetMessage(new NetMessage(null, new NetworkReturn() {
            @Override
            public void returnMessage(String message) {

            }
        },jsonObject));
    }
    public boolean ownChatRequest()
    {
        return User.getLastUser(null,null,null).userName.equals(customer);
    }
    public enum Need {
        UNDEFINED, PROBLEM, LEARN
    }

    public enum Skill {
        UNDEFINED, BEGINNER, INTERMEDIATE, EXPERT
    }

    public String getCustomer()
    {
        return customer;
    }
    public String getCoach()
    {
        return coach;
    }

    public String getNeed()
    {
        return need.toString();
    }
    public String getSkill()
    {
        return skill.toString();
    }
    public String getMessage()
    {
        return message;
    }
    public String getUUID()
    {
        return uuid.toString();
    }


}

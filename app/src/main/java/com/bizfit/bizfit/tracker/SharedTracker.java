package com.bizfit.bizfit.tracker;

import com.bizfit.bizfit.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by attey on 20/01/2017.
 */

public class SharedTracker implements java.io.Serializable{
    String userName;
    long creationTime;
    public SharedTracker (String userName,long creationTime){
        this.userName=userName;
        this.creationTime=creationTime;
    }

    public SharedTracker(JSONObject jsonObject){
        try {
            if(jsonObject.has(Constants.user_name)){
                userName=jsonObject.getString(Constants.user_name);
            }
            if (jsonObject.has(Constants.creationTime)){
                creationTime=jsonObject.getLong(Constants.creationTime);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getUserName(){
        return userName;
    }
    public JSONObject toJSON(){
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put(Constants.user_name,userName);
            jsonObject.put(Constants.creationTime,creationTime);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
    public boolean equals(SharedTracker sharedTracker){
        return sharedTracker!=null&&sharedTracker.creationTime==this.creationTime&&sharedTracker.userName!=null&&sharedTracker.userName.equals(this.userName);
    }

    public static boolean combineLists(List<SharedTracker> to, List<SharedTracker> from){
        List<SharedTracker> temp=new ArrayList<>();
        for (int i=0;i<from.size();i++){
            SharedTracker sharedTracker=from.get(i);
            if (!sharedTracker.alreadyInList(to)){
                temp.add(sharedTracker);
            }
        }
        return to.addAll(temp);
    }
    public boolean alreadyInList(List<SharedTracker> list){
        for(int i=0;i<list.size();i++){
            if(list.get(i).equals(this)){
                return true;
            }
        }
        return false;
    }
}

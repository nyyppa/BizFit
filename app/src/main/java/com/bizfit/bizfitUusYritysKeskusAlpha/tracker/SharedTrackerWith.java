package com.bizfit.bizfitUusYritysKeskusAlpha.tracker;

import com.bizfit.bizfitUusYritysKeskusAlpha.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by attey on 20/01/2017.
 */

public class SharedTrackerWith implements java.io.Serializable {
    String userName;
    public SharedTrackerWith(String userName){
        this.userName=userName;
    }
    public SharedTrackerWith(JSONObject jsonObject){
        try {
            if (jsonObject.has(Constants.getUser_Name())) {
                userName=jsonObject.getString(Constants.getUser_Name());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public JSONObject toJSON(){
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put(Constants.getUser_Name(),userName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
    public String getUserName(){
        return userName;
    }
    public boolean equals(SharedTrackerWith sharedTrackerWith){
        return this.userName.equals(sharedTrackerWith.userName);
    }
    public boolean equals(String userName){
        return userName.equals(getUserName());
    }
    public static boolean alreadySharedWith(String userName, List<SharedTrackerWith> list){
        for(int i=0;i<list.size();i++){
            if(list.get(i).equals(userName)){
                return true;
            }
        }
        return false;
    }
}

package com.bizfit.bizfit.tracker;

import com.bizfit.bizfit.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

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
            if (jsonObject.has(Constants.user_name)) {
                userName=jsonObject.getString(Constants.user_name);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public JSONObject toJSON(){
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put(Constants.user_name,userName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
    public boolean equals(SharedTrackerWith sharedTrackerWith){
        return this.userName.equals(sharedTrackerWith.userName);
    }
}

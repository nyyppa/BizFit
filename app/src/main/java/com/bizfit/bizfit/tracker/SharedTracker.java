package com.bizfit.bizfit.tracker;

import android.os.Debug;

import com.bizfit.bizfit.DebugPrinter;
import com.bizfit.bizfit.User;
import com.bizfit.bizfit.network.NetMessage;
import com.bizfit.bizfit.network.Network;
import com.bizfit.bizfit.network.NetworkReturn;
import com.bizfit.bizfit.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by attey on 20/01/2017.
 */

public class SharedTracker implements java.io.Serializable{
    String userName;
    UUID uuid;
    String trackerName;
    public SharedTracker (String userName,UUID uuid, String trackerName){
        this.userName=userName;
        this.uuid=uuid;
        this.trackerName=trackerName;
    }

    public SharedTracker(JSONObject jsonObject){
        try {
            if(jsonObject.has(Constants.getUser_Name())){
                userName=jsonObject.getString(Constants.getUser_Name());
            }
            if (jsonObject.has(Constants.UUID)){
                uuid=UUID.fromString(jsonObject.getString(Constants.UUID));
            }
            if(jsonObject.has(Constants.shared_tracker_name))
            {
                trackerName=jsonObject.getString(Constants.shared_tracker_name);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void removeFromNet()
    {
        JSONObject jsonObject = new JSONObject();
        try
        {
            jsonObject.put(Constants.job, "CancelTrackerSharing");
            jsonObject.put(Constants.shared_trackers, this.toJSON());
            jsonObject.put(Constants.shared_tracker_name, trackerName);
            jsonObject.put(Constants.getUser_Name(), User.getLastUser(null,null,null).userName);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        NetMessage netMessage = new NetMessage(null, new NetworkReturn()
        {
            @Override
            //TODO error handling
            public void returnMessage(String message)
            {
                if(message.equals(Constants.networkconn_failed))
                {
                    removeFromNet();
                }

            }
        }, jsonObject);
        Network.addNetMessage(netMessage);
    }

    public boolean equals(Tracker t)
    {
        DebugPrinter.Debug("täällä ollaan");
        DebugPrinter.Debug("UUID: "+t.uuid.toString());
        DebugPrinter.Debug("UUID: "+this.uuid.toString());
        DebugPrinter.Debug("UUID: "+t.uuid.equals(this.uuid));
        return t.uuid.equals(this.uuid);
    }

    public String getUserName(){
        return userName;
    }
    public JSONObject toJSON(){
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put(Constants.getUser_Name(),userName);
            jsonObject.put(Constants.UUID,uuid);
            jsonObject.put(Constants.shared_tracker_name,trackerName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
    public boolean equals(SharedTracker sharedTracker){
        return uuid.equals(sharedTracker.uuid);
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

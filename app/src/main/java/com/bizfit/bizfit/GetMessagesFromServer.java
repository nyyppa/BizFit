package com.bizfit.bizfit;

import com.bizfit.bizfit.utils.RecyclerViewAdapterMessages;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by attey on 22/08/2016.
 */
public class GetMessagesFromServer extends Thread implements NetworkReturn{
    RecyclerViewAdapterMessages mAdapter;
    public  GetMessagesFromServer(RecyclerViewAdapterMessages mAdapter){
        this.mAdapter=mAdapter;
    }

    @Override
    public void run() {
        super.run();
        while (true){
            try {
                sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void returnMessage(String message) {
        JSONArray array;
        try {
            array = new JSONArray(message);
            for(int i=0;i<array.length();i++){
                Message m=new Message(new JSONObject((String) array.get(i)));
                mAdapter.addData(m);
                mAdapter.notifyItemInserted(0);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

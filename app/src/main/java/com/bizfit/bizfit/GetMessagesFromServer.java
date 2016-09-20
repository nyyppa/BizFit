package com.bizfit.bizfit;

import android.app.Activity;

import com.bizfit.bizfit.utils.RecyclerViewAdapterMessages;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by attey on 22/08/2016.
 */
public class GetMessagesFromServer extends Thread implements NetworkReturn{
    RecyclerViewAdapterMessages mAdapter;
    Activity activity;
    public  GetMessagesFromServer(RecyclerViewAdapterMessages mAdapter,Activity activity){
        this.mAdapter=mAdapter;
        this.activity=activity;
    }

    @Override
    public void run() {
        super.run();
        while (true){
            try {
                JSONObject jsonObject=new JSONObject();
                Message m=mAdapter.getLastRecievedMessage();
                try {
                    jsonObject.put("_id", "atte.yliverronen@gmail.com");
                    jsonObject.put("resipiant", "atte.yliverronen@gmail.com");
                    jsonObject.put("Job", "get_message");
                    if(m!=null){
                        jsonObject.put("creationTime",m.getCreationTime());
                    }else{
                        jsonObject.put("creationTime", -1);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                new Thread(new MyNetwork(null,this,jsonObject)).start();
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
                final JSONObject jsonObject=new JSONObject((String) array.get(i));
                if (jsonObject.has("text")) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Message m=new Message(jsonObject);
                            mAdapter.addData(m);
                            mAdapter.notifyItemInserted(0);
                            System.out.println(m.toJSON().toString());
                        }
                    });
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

package com.bizfit.bizfit;
import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;


import javax.net.ssl.HttpsURLConnection;

/**
 *
 */
public class Message {
    private String payload;
    private Type type=Type.RECEIVED;
    String sender;
    String resipiant="atte.yliverronen@gmail.com";
    long creationTime;
    User user2;
    public Message(String sender,String resipiant,String text){
        this.sender=sender;
        this.resipiant=resipiant;
        this.payload=text;
        creationTime=System.currentTimeMillis();
    }

    public Message(JSONObject jsonObject){
        try {
            sender=jsonObject.getString("sender");
            resipiant=jsonObject.getString("resipiant");
            if(jsonObject.has("text")){
                payload=jsonObject.getString("text");
            }
            creationTime=jsonObject.getLong("creationTime");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public long getCreationTime(){
        return creationTime;
    }

    public JSONObject toJSON(){
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("sender",sender);
            jsonObject.put("resipiant",resipiant);
            jsonObject.put("text",payload);
            jsonObject.put("creationTime",creationTime);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
    public Message(final String payload, Type type,Context c) {
        User.getLastUser(new User.UserLoadedListener() {
            @Override
            public void UserLoaded(User user) {
                user2=user;
                sender=user.userName;
                System.out.println("haist");
                System.out.println(payload);
                sendMessage();
            }
        },c);
        this.payload = payload;
        this.type = type;
    }

    public String getPayload() {
        return payload;
    }

    public Type getType() {
        return type;
    }

    // TODO better naming scheme
    public enum Type {
        RECEIVED, SENT
    }
    public  void sendMessage(){
        final  JSONObject jsonObject=this.toJSON();
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                InputStream is = null;
                // Only display the first 500 characters of the retrieved
                // web page content.
                int len = 500;

                try {
                    URL url = new URL("https://bizfit-kaupunkiapina.c9users.io");
                    HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                    conn.setReadTimeout(10000 /* milliseconds */);
                    conn.setConnectTimeout(15000 /* milliseconds */);
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    OutputStream os = conn.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(
                            new OutputStreamWriter(os, "UTF-8"));
                    JSONObject jsonObject1 = new JSONObject();
                    try {
                        jsonObject1.put("_id", sender);
                        jsonObject1.put("resipiant",resipiant);
                        jsonObject1.put("Job","send_message");
                        jsonObject1.put("message", jsonObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    writer.write(jsonObject1.toString());
                    writer.flush();
                    conn.connect();
                    int response = conn.getResponseCode();
                    if (response==200) {
                        is = conn.getInputStream();
                        // Convert the InputStream into a string
                        BufferedReader r = new BufferedReader(new InputStreamReader(is));
                        StringBuilder total = new StringBuilder();
                        String line;
                        while ((line = r.readLine()) != null) {
                            System.out.println(line);
                            total.append(line).append('\n');
                        }
                        System.out.println(total.toString());
                    }

                    // Makes sure that the InputStream is closed after the app is
                    // finished using it.
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (is != null) {
                        try {
                            is.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }

            }
        });
        t.start();
    }
}



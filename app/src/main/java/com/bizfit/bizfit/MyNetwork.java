package com.bizfit.bizfit;

import android.net.Network;

import com.bizfit.bizfit.NetworkReturn;

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
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by attey on 09/08/2016.
 */
public class MyNetwork implements Runnable {
    public String connectionAddress="https://bizfit-nyyppa.c9users.io";
    public NetworkReturn networkReturn;
    public JSONObject message;

    public MyNetwork(String connectionAddress, NetworkReturn networkReturn, JSONObject message){
        if(connectionAddress!=null){
            this.connectionAddress=connectionAddress;
        }
        this.networkReturn=networkReturn;
        this.message=message;
    }

    @Override
    public void run() {
        InputStream is = null;
        // Only display the first 500 characters of the retrieved
        // web page content.
        int len = 500;

        try {
            URL url = new URL(connectionAddress);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));

            writer.write(message.toString());
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
                networkReturn.returnMessage(total.toString());
            }else{
                networkReturn.returnMessage("failed");
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
}

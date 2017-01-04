package com.bizfit.bizfit;

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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by attey on 08/12/2016.
 */

public class NewAndBetterNetwork extends Thread{
    List<NetMessage> netMessagesList;
    private boolean exit=false;
    private boolean paused=false;
    List<NetMessage> messagesToAdd;
    static NewAndBetterNetwork newAndBetterNetwork;
    public NewAndBetterNetwork(){
        netMessagesList=new ArrayList<>();
        messagesToAdd=new ArrayList<>();
    }


    @Override
    public void run(){
        while (true){
            synchronized (this){
                while (paused){
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            Iterator<NetMessage> iterator=netMessagesList.iterator();
            while (iterator.hasNext()){
                NetMessage netMessage=iterator.next();
                if (netMessage!=null&&netMessage.getConnectionAddress()!=null&&netMessage.getMessage()!=null) {

                    InputStream is = null;
                    // Only display the first 500 characters of the retrieved
                    // web page content.
                    int len = 500;

                    try {
                        URL url = new URL(netMessage.getConnectionAddress());
                        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                        conn.setReadTimeout(10000 /* milliseconds */);
                        conn.setConnectTimeout(15000 /* milliseconds */);
                        conn.setRequestMethod("POST");
                        conn.setDoInput(true);
                        conn.setDoOutput(true);
                        OutputStream os = conn.getOutputStream();
                        BufferedWriter writer = new BufferedWriter(
                                new OutputStreamWriter(os, "UTF-8"));

                        writer.write(netMessage.getMessage().toString());
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
                            returnMessage(netMessage,total.toString());
                            //netMessage.getNetworkReturn().returnMessage(total.toString());
                            iterator.remove();
                            //networkReturn.returnMessage(total.toString());
                        }else{
                            System.out.println("response"+conn.getResponseCode());
                            returnMessage(netMessage,"failed");
                            iterator.remove();
                            //netMessage.getNetworkReturn().returnMessage("failed");
                            //networkReturn.returnMessage("failed");
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
            netMessagesList.addAll(messagesToAdd);
            synchronized (messagesToAdd){
                messagesToAdd.clear();
            }

            if(netMessagesList.size()==0){
                paused=true;
            }
            if(exit){
                return;
            }
        }

    }

    private void returnMessage(NetMessage netMessage,String message ){
        if(netMessage.getNetworkReturn()!=null){
            netMessage.getNetworkReturn().returnMessage(message);
        }
    }

    public void onResume(){
        synchronized (this){
            paused=false;
            this.notifyAll();
        }
    }
    public void exit(){
        exit=true;
        if(paused){
            onResume();
        }
    }
    public void addMessage(NetMessage message){
        if (message!=null) {
            if(alreadyInQueue(message)){
                message.networkReturn.returnMessage("failed");
            }else{
                messagesToAdd.add(message);
            }
            onResume();
        }
    }
    private boolean alreadyInQueue(NetMessage message){
        synchronized (messagesToAdd){
            for(int i=0;i<messagesToAdd.size();i++){
                if(messagesToAdd.get(i)!=null&&messagesToAdd.get(i).equals(message)){
                    return true;
                }
            }
        }
        synchronized (netMessagesList){
            for(int i=0;i<netMessagesList.size();i++){
                if(netMessagesList.get(i)!=null&&netMessagesList.get(i).equals(netMessagesList)){
                    return true;
                }
            }
        }

        return false;
    }
    public static void addNetMessage(NetMessage message){
        if(newAndBetterNetwork==null||!newAndBetterNetwork.isAlive()){
            newAndBetterNetwork=new NewAndBetterNetwork();
            newAndBetterNetwork.start();
        }
        newAndBetterNetwork.addMessage(message);
    }


}

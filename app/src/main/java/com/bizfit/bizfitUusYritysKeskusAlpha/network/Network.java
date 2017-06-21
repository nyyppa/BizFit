package com.bizfit.bizfitUusYritysKeskusAlpha.network;

import com.bizfit.bizfitUusYritysKeskusAlpha.DebugPrinter;
import com.bizfit.bizfitUusYritysKeskusAlpha.MyApplication;
import com.bizfit.bizfitUusYritysKeskusAlpha.utils.Constants;
import com.bizfit.bizfitUusYritysKeskusAlpha.utils.Utils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManagerFactory;


/**
 * Created by attey on 08/12/2016.
 */

public class Network extends Thread{
    List<NetMessage> netMessagesList;
    private boolean exit=false;
    private boolean paused=false;
    List<NetMessage> messagesToAdd;
    static Network network;
    public Network(){
        netMessagesList=new ArrayList<>();
        messagesToAdd=new ArrayList<>();
        setName("NetWorkThread");
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

                        // Load CAs from an InputStream
                        // (could be from a resource or ByteArrayInputStream or ...)
                        CertificateFactory cf = CertificateFactory.getInstance("X.509","BC");
                        // From https://www.washington.edu/itconnect/security/ca/load-der.crt
                        ;
                        InputStream caInput = new BufferedInputStream(MyApplication.getContext().getAssets().open("certs/server_ca_signed.crt"));
                        Certificate ca;
                        try {
                            ca = cf.generateCertificate(caInput);
                            System.out.println("ca=" + ((X509Certificate) ca).getSubjectDN());
                        } finally {
                            caInput.close();
                        }

                        // Create a KeyStore containing our trusted CAs
                        String keyStoreType = KeyStore.getDefaultType();
                        KeyStore keyStore = KeyStore.getInstance(keyStoreType);
                        keyStore.load(null, null);
                        keyStore.setCertificateEntry("ca", ca);

                        // Create a TrustManager that trusts the CAs in our KeyStore
                        String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
                        TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
                        tmf.init(keyStore);

                        // Create an SSLContext that uses our TrustManager
                        SSLContext context = SSLContext.getInstance("TLS");
                        context.init(null, tmf.getTrustManagers(), null);

                        // Tell the URLConnection to use a SocketFactory from our SSLContext
                        URL url = new URL(netMessage.getConnectionAddress());
                        HttpsURLConnection urlConnection =
                                (HttpsURLConnection)url.openConnection();
                        urlConnection.setSSLSocketFactory(context.getSocketFactory());
                        //InputStream in = urlConnection.getInputStream();


                        //HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        urlConnection.setReadTimeout(10000 /* milliseconds */);
                        urlConnection.setConnectTimeout(15000 /* milliseconds */);
                        urlConnection.setRequestMethod("POST");
                        urlConnection.setDoInput(true);
                        urlConnection.setDoOutput(true);
                        //TODO replace with actual verification
                        urlConnection.setHostnameVerifier(new HostnameVerifier() {
                            @Override
                            public boolean verify(String hostname, SSLSession session) {
                                DebugPrinter.Debug("hostname:"+Constants.connection_address.split(":")[1].replaceFirst("//",""));
                                if(hostname.startsWith(Constants.connection_address.split(":")[1].replaceFirst("//","")))
                                {
                                    return true;
                                }
                                return false;
                            }
                        });
                        OutputStream os = urlConnection.getOutputStream();
                        BufferedWriter writer = new BufferedWriter(
                                new OutputStreamWriter(os, "UTF-8"));

                        writer.write(netMessage.getMessage().toString());
                        writer.flush();
                        urlConnection.connect();
                        int response = urlConnection.getResponseCode();
                        if (response==200) {
                            is = urlConnection.getInputStream();
                            // Convert the InputStream into a string
                            BufferedReader r = new BufferedReader(new InputStreamReader(is));
                            StringBuilder total = new StringBuilder();
                            String line;
                            while ((line = r.readLine()) != null) {
                                total.append(line).append('\n');
                            }
                            returnMessage(netMessage,total.toString());
                            //netMessage.getNetworkReturn().returnMessage(total.toString());
                            iterator.remove();
                            //networkReturn.returnMessage(total.toString());
                        }else{
                            returnMessage(netMessage, Constants.networkconn_failed);
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
                    } catch (CertificateException e) {
                        e.printStackTrace();
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    } catch (KeyManagementException e) {
                        e.printStackTrace();
                    } catch (KeyStoreException e) {
                        e.printStackTrace();
                    } catch (NoSuchProviderException e) {
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
            netMessagesList.clear();
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
        if(netMessage!=null&&netMessage.getNetworkReturn()!=null){
            netMessage.getNetworkReturn().returnMessage(message);
        }
    }

    public void onResume(){
        synchronized (this){
            paused=false;
            this.notify();
        }
    }
    public void exit(){
        exit=true;
        if(paused){
            onResume();
        }
    }
    public static void onExit()
    {
        if(network !=null)
        {
            network.exit();
        }
    }
    public void addMessage(NetMessage message){
        if (message!=null) {
            if(alreadyInQueue(message)){
                returnMessage(message, Constants.networkconn_failed);
            }else{
                messagesToAdd.add(message);
            }
            onResume();
        }
    }
    //TODO Something is leaking in slownet
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
                if(netMessagesList.get(i)!=null&&netMessagesList.get(i).equals(message)){
                    return true;
                }
            }
        }

        return false;
    }
    public static void addNetMessage(NetMessage message){
        if(network ==null||!network.isAlive()){
            network =new Network();
            network.start();
        }
        network.addMessage(message);
    }

    public static Network getNetwork()
    {
        return network;
    }


}

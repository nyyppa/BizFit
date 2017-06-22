package com.bizfit.bizfitUusYritysKeskusAlpha.network;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.net.URL;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;

/**
 * Created by attey on 22/06/2017.
 */

public class MyConnection{
    HttpsURLConnection httpsURLConnection;
    HttpURLConnection httpURLConnection;

    MyConnection(URL url) throws IOException {
        if (url.getProtocol().equals("https"))
        {
            httpsURLConnection=(HttpsURLConnection) url.openConnection();
        }
        else
        {
            httpURLConnection=(HttpURLConnection)url.openConnection();
        }
    }

    public void setSSLSocketFactory(SSLSocketFactory socketFactory) {
        if(socketFactory!=null&&httpsURLConnection!=null){
            httpsURLConnection.setSSLSocketFactory(socketFactory);
        }

    }

    public void setReadTimeout(int i) {
        if(httpsURLConnection!=null)
        {
            httpsURLConnection.setReadTimeout(i);
        }
        else
        {
            httpURLConnection.setReadTimeout(i);
        }
    }

    public void setConnectTimeout(int i) {
        if(httpsURLConnection!=null)
        {
            httpsURLConnection.setConnectTimeout(i);
        }
        else
        {
            httpURLConnection.setConnectTimeout(i);
        }
    }

    public void setRequestMethod(String post) throws ProtocolException {
        if(httpsURLConnection!=null)
        {
            httpsURLConnection.setRequestMethod(post);
        }
        else
        {
            httpURLConnection.setRequestMethod(post);
        }
    }

    public void setDoInput(boolean b) {
        if(httpsURLConnection!=null)
        {
            httpsURLConnection.setDoInput(b);
        }
        else
        {
            httpURLConnection.setDoInput(b);
        }
    }

    public void setDoOutput(boolean b) {
        if(httpsURLConnection!=null)
        {
            httpsURLConnection.setDoOutput(b);
        }
        else
        {
            httpURLConnection.setDoOutput(b);
        }
    }

    public void setHostnameVerifier(HostnameVerifier hostnameVerifier) {
        if(hostnameVerifier!=null&&httpsURLConnection!=null)
        {
            httpsURLConnection.setHostnameVerifier(hostnameVerifier);
        }
    }

    public OutputStream getOutputStream() throws IOException {
        if(httpsURLConnection!=null)
        {
            return httpsURLConnection.getOutputStream();
        }
        else
        {
            return httpURLConnection.getOutputStream();
        }
    }

    public void connect() throws SocketTimeoutException,IOException {
        if(httpsURLConnection!=null)
        {
            httpsURLConnection.connect();
        }
        else
        {
            httpURLConnection.connect();
        }
    }

    public int getResponseCode() throws IOException {
        if(httpsURLConnection!=null)
        {
            return httpsURLConnection.getResponseCode();
        }
        else
        {
            return httpURLConnection.getResponseCode();
        }
    }

    public InputStream getInputStream() throws IOException {
        if(httpsURLConnection!=null)
        {
            return httpsURLConnection.getInputStream();
        }
        else
        {
            return httpURLConnection.getInputStream();
        }
    }
}
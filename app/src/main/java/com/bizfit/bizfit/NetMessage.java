package com.bizfit.bizfit;

import org.json.JSONObject;

/**
 * Created by attey on 08/12/2016.
 */

public class NetMessage {
    public String connectionAddress="https://bizfit-nyyppa.c9users.io";
    public NetworkReturn networkReturn;
    public JSONObject message;

    public NetMessage(String connectionAddress, NetworkReturn networkReturn, JSONObject message) {
        if (connectionAddress != null) {
            this.connectionAddress = connectionAddress;
        }
        this.networkReturn = networkReturn;
        this.message = message;
    }

    public String getConnectionAddress() {
        return connectionAddress;
    }

    public NetworkReturn getNetworkReturn() {
        return networkReturn;
    }

    public JSONObject getMessage() {
        return message;
    }
}

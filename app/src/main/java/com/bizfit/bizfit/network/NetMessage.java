package com.bizfit.bizfit.network;

import com.bizfit.bizfit.utils.Constants;

import org.json.JSONObject;

/**
 * Created by attey on 08/12/2016.
 */

public class NetMessage {
    public String connectionAddress = Constants.connection_address;
    public NetworkReturn networkReturn;
    public JSONObject message;

    public NetMessage(String connectionAddress, NetworkReturn networkReturn, JSONObject message) {
        if (connectionAddress != null)
        {
            this.connectionAddress = connectionAddress;
        }
        this.networkReturn = networkReturn;
        this.message = message;
    }

    //todo better way to compare JSONs
    public boolean equals(NetMessage netMessage){
        if(netMessage.getConnectionAddress().equals(getConnectionAddress())&&netMessage.getMessage().toString().equals(getMessage().toString())){
            return true;
        }
        return false;
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

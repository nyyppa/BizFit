package com.bizfit.bizfit;

/**
 *
 */
public class Message {
    private String payload;
    private Type type;

    public Message(String payload, Type type) {
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
}



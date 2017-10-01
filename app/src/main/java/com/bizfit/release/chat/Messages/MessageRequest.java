package com.bizfit.release.chat.Messages;

import android.view.View;

import com.bizfit.release.chat.Conversation;
import com.bizfit.release.chat.Messages.FileObjects.FileObject;

/**
 * Created by iipa on 1.10.2017.
 */

public class MessageRequest extends Message2 {

    protected MessageRequest(Conversation conversation, String resipient, String sender, String message, MessageType messageType, FileObject file) {
        super(conversation, resipient, sender, message, messageType, file);
    }

    @Override
    public void returnMessage(String message) {

    }

    @Override
    public void drawThySelf(View view) {

        // TODO: define views and attach data

    }

    public void hasRequestBeenPaid() {



    }
}

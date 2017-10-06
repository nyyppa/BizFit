package com.bizfit.release.chat.Messages;

import android.database.Cursor;
import android.view.View;
import android.widget.TextView;

import com.bizfit.release.R;
import com.bizfit.release.chat.Conversation;

import org.json.JSONObject;

/**
 * Created by attey on 06/09/2017.
 */

public class MessageText extends Message2 {



    protected MessageText(Cursor cursor) {
        super(cursor);
    }


    public MessageText(Conversation conversation, String resipient, String sender, String message, MessageType messageType) {
        super(conversation, resipient, sender, message, messageType);
    }

    public MessageText(JSONObject jsonObject, Conversation conversation) {
        super(jsonObject,conversation);
    }

    @Override
    public void drawThySelf(View view) {
        TextView message=view.findViewById(R.id.message);
        TextView timestamp=view.findViewById(R.id.tVtimestamp);
        timestamp.setText(getTimestamp());
        message.setText(this.message);
    }

    @Override
    public void returnMessage(String message) {

    }
}

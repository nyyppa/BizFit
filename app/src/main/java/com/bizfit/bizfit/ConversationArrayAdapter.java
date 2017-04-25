package com.bizfit.bizfit;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bizfit.bizfit.activities.MessageActivity;
import com.bizfit.bizfit.chat.Conversation;
import com.bizfit.bizfit.chat.Message;
import com.bizfit.bizfit.utils.Constants;
import com.bizfit.bizfit.utils.Utils;

import java.util.ArrayList;

/**
 * Created by attey on 01/02/2017.
 */
public class ConversationArrayAdapter extends ArrayAdapter<Conversation> {
    public ConversationArrayAdapter(Context context, ArrayList<Conversation> conversations) {
        super(context, 0, conversations);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        Conversation conversation = getItem(position);

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_message_tab_preview,parent,false);
        }

        TextView textView = (TextView)convertView.findViewById(R.id.tVName);
        textView.setText(conversation.getOther());

        textView = (TextView)convertView.findViewById(R.id.tVPreview);

        Message message = conversation.getLastRecievedMessage();

        if (message != null){
            textView.setText(message.getMessage());
        }

        ImageView imageView = (ImageView)convertView.findViewById(R.id.iVRecipient);
        imageView.setImageDrawable(ContextCompat.getDrawable(getContext(), Utils.getDrawableID(conversation.getOther())));

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView textView1=(TextView)v.findViewById(R.id.tVName);
                MessageActivity.startChat(v,textView1.getText()+"");
            }
        });

        return convertView;
    }
}

package com.bizfit.bizfit;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bizfit.bizfit.activities.MessageActivity;
import com.bizfit.bizfit.chat.Conversation;
import com.bizfit.bizfit.chat.Message;

import java.util.ArrayList;

/**
 * Created by attey on 10/03/2017.
 */

public class ChatRequestArrayAdapter extends ArrayAdapter<ChatRequest> {
    public ChatRequestArrayAdapter(Context context, ArrayList<ChatRequest> chatRequests) {
        super(context, 0, chatRequests);
    }

    public View getView(int position, View convertView, ViewGroup parent){
        final ChatRequest chatRequest=getItem(position);
        if(convertView==null){
            convertView= LayoutInflater.from(getContext()).inflate(R.layout.list_item_chat_request,parent,false);
        }
        TextView textView=(TextView)convertView.findViewById(R.id.textView);
        textView.setText(chatRequest.message);

        Button button=(Button) convertView.findViewById(R.id.accept);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ChatRequestResponse(chatRequest,true).sentResponse();
            }
        });
        button=(Button)convertView.findViewById(R.id.cancel);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ChatRequestResponse(chatRequest,false).sentResponse();
            }
        });
        return convertView;
    }
}

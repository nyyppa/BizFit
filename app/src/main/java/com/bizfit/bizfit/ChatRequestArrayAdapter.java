package com.bizfit.bizfit;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
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

        final TextView tvNeed = (TextView) convertView.findViewById(R.id.chat_request_need);
        if(chatRequest.need == ChatRequest.Need.PROBLEM) {
            tvNeed.setText("I have a problem I want to solve.");
        } else {
            tvNeed.setText("I want to learn from coach's area of expertise.");
        }
        tvNeed.setVisibility(View.GONE);

        final TextView tvSkill = (TextView) convertView.findViewById(R.id.chat_request_skill);
        switch(chatRequest.skill) {
            case BEGINNER:
                tvSkill.setText("My skill level is currently BEGINNER");
                break;
            case INTERMEDIATE:
                tvSkill.setText("My skill level is currently INTERMEDIATE");
                break;
            case EXPERT:
                tvSkill.setText("My skill level is currently EXPERT");
                break;
            case UNDEFINED:
                tvSkill.setText("UNDEFINED");
                break;
        }
        tvSkill.setVisibility(View.GONE);

        final TextView tvDetails = (TextView)convertView.findViewById(R.id.chat_request_details);
        tvDetails.setText(chatRequest.message);
        tvDetails.setVisibility(View.GONE);

        final View separator = convertView.findViewById(R.id.separator_1);
        separator.setVisibility(View.GONE);

        final Drawable more = ContextCompat.getDrawable(getContext(), R.drawable.ic_action_show_more);
        final Drawable less = ContextCompat.getDrawable(getContext(), R.drawable.ic_action_show_less);

        button = (Button) convertView.findViewById(R.id.chat_request_show);
        button.setOnClickListener(new View.OnClickListener() {
            boolean shown = false;

            @Override
            public void onClick(View v) {
                if(shown) {
                    shown = false;
                    tvNeed.setVisibility(View.GONE);
                    tvDetails.setVisibility(View.GONE);
                    tvSkill.setVisibility(View.GONE);
                    separator.setVisibility(View.GONE);
                    Button b = (Button) v;
                    b.setText("Show Details");
                } else {
                    shown = true;
                    separator.setVisibility(View.VISIBLE);
                    tvNeed.setVisibility(View.VISIBLE);
                    tvDetails.setVisibility(View.VISIBLE);
                    if(!tvSkill.getText().equals("UNDEFINED")) {
                        tvSkill.setVisibility(View.VISIBLE);
                    }
                    Button b = (Button) v;
                    b.setText("Hide Details");
                }
            }
        });
        return convertView;
    }
}

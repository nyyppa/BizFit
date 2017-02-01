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
import com.bizfit.bizfit.utils.Constants;

import java.util.ArrayList;

/**
 * Created by attey on 01/02/2017.
 */
public class ConversationArrayAdapter extends ArrayAdapter<Conversation> {
    public ConversationArrayAdapter(Context context, ArrayList<Conversation> conversations) {
        super(context, 0, conversations);
    }
    private Drawable findDrawable(String coach){
        int id=-1;
        switch (coach)
        {
            case Constants.atte_email:
                id=R.drawable.atte;
                break;
            case Constants.jariM_email:
                id=R.drawable.mylly;
                break;
            case Constants.pasi_email:
                id=R.drawable.pasi;
                break;
            case Constants.jari_email:
                id=R.drawable.jartsa;
                break;
            default:
                id=R.drawable.tmp2;
                break;

        }
        return ContextCompat.getDrawable(getContext(),id);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        Conversation conversation=getItem(position);
        if(convertView==null){
            convertView= LayoutInflater.from(getContext()).inflate(R.layout.list_item_message_tab_preview,parent,false);
        }
        TextView textView=(TextView)convertView.findViewById(R.id.tVName);
        textView.setText(conversation.getOther());
        textView=(TextView)convertView.findViewById(R.id.tVPreview);
        if(conversation.getMessages()!=null&&conversation.getMessages().size()>0){
            textView.setText(conversation.getMessages().get(0).getMessage());
        }
        ImageView imageView=(ImageView)convertView.findViewById(R.id.iVRecipient);
        imageView.setImageDrawable(findDrawable(conversation.getOther()));
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

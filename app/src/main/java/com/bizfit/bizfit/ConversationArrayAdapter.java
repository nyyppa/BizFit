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
import com.bizfit.bizfit.views.ConversationTabView;

import java.util.ArrayList;
import java.util.Comparator;

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

        TextView tVname = (TextView)convertView.findViewById(R.id.tVName);
        tVname.setText(Utils.getCouchName(conversation.getOther()));

        ConversationTabView conversationTabView = (ConversationTabView) convertView.findViewById(R.id.tVPreview);
        conversation.setNewMessageRecievedListener(conversationTabView);
        Message previewMessage = conversation.getLastRecievedMessage();

        if (previewMessage != null)
        {
            conversationTabView.setText(previewMessage.getMessage()+"");
        }

        ImageView iVrecipient = (ImageView)convertView.findViewById(R.id.iVRecipient);
        iVrecipient.setImageDrawable(ContextCompat.getDrawable(getContext(), Utils.getDrawableID(conversation.getOther())));

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView tVname=(TextView)v.findViewById(R.id.tVName);
                String coachID = Utils.getCouchID(tVname.getText()+"");
                MessageActivity.startChat(v.getContext(), coachID);
            }
        });

        return convertView;
    }
    @Override
    public void notifyDataSetChanged() {
        sort(new Comparator<Conversation>() {
            @Override
            public int compare(Conversation o1, Conversation o2) {
                if(o1.getMessages()==null||o1.getMessages().get(0)==null)
                {
                    return -1;
                }
                if(o2.getMessages()==null||o2.getMessages().get(0)==null)
                {
                    return 1;
                }
                if(o1.getMessages().get(0).getCreationTime()<o2.getMessages().get(0).getCreationTime())
                {
                    return -1;
                }
                else
                {
                    return 1;
                }
            }
        });
        //do your sorting here

        super.notifyDataSetChanged();
    }
}

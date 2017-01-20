package com.bizfit.bizfit.utils;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bizfit.bizfit.DebugPrinter;
import com.bizfit.bizfit.R;
import com.bizfit.bizfit.User;
import com.bizfit.bizfit.activities.MessageActivity;
import com.bizfit.bizfit.chat.Conversation;

import java.util.List;

/**
 *
 */
public class RecyclerViewAdapterTabMessages extends RecyclerView.Adapter
{
    private Conversation conversation=null;
    private String resipient =null;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_message_tab_preview, parent, false);

                //TODO: Tee tämä!
                showConversationInfo(v, parent);


        return new ViewHolder(v);
    }
    private int getDrawableID(String coach)
    {
        int id=-1;
        switch (coach)
        {
            case "atte.yliverronen@gmail.com":
                id=R.drawable.atte;
                break;
            default:
                id=R.drawable.tmp2;
                break;

        }
        return id;
    }
    // jariJ 20.1.17
    private void showConversationInfo(View v, ViewGroup parent)
    {
        User user= User.getLastUser(new User.UserLoadedListener() {
            @Override
            public void informationUpdated()
            {
            }
        }, null,null);

        List<Conversation> conversations= user.getConversations();
        for(int i=0; i < conversations.size();i++)
        {
            conversation = conversations.get(i);
            resipient = conversation.getOther();
            DebugPrinter.Debug("CoachID keskustelulle:" + resipient);

        }

        ImageView iVResipient=(ImageView)v.findViewById(R.id.iVRecipient);
        iVResipient.setImageDrawable(ContextCompat.getDrawable(parent.getContext(), getDrawableID(resipient)));

        TextView tVuName=(TextView) v.findViewById(R.id.tVName);
        String uName = user.getUserName(null, conversation.getOther());
        tVuName.setText(uName);

        conversation.sortConversation();
        if(conversation.getMessages()!=null && conversation.getMessages().size()>0)
        {
            TextView tVpreview=(TextView) v.findViewById(R.id.tVPreview);
            tVpreview.setText(conversation.getMessages().get(0).getMessage());
        }



    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 1;
    }


    private class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            v.getContext().startActivity(new Intent(v.getContext(), MessageActivity.class));
        }
    }
}

package com.bizfit.bizfit.utils;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bizfit.bizfit.MyNewAndBetterConversation;
import com.bizfit.bizfit.MyNewAndBetterMessage;
import com.bizfit.bizfit.R;
import com.bizfit.bizfit.User;
import com.bizfit.bizfit.fragments.ChatFragment;

import java.util.List;

/**
 *
 */
public class RecyclerViewAdapterMessages extends RecyclerView.Adapter {

    MyNewAndBetterConversation conversation;
    ChatFragment chatFragment;
    Intent intent=null;
    //private ArrayList<Message> messages;

    protected float messageHorizontalMarginSmall;
    protected float messageHorizontalMarginLarge;

    public RecyclerViewAdapterMessages(final Intent intent, Context context) {
        this.intent=intent;
        User.getLastUser(new User.UserLoadedListener() {
            @Override
            public void UserLoaded(User user) {
                if(intent!=null&&intent.hasExtra("coachID")){
                    conversation=user.addConversation(new MyNewAndBetterConversation(user.userName,intent.getStringExtra("coachID"),user));
                }else{
                    conversation=user.addConversation(new MyNewAndBetterConversation(user.userName,user.userName.equals("default")?"atte.yliverronen@gmail.com":"default",user));
                }

               // conversation.setChatFragment(chatFragment);
                if(conversation.getMessages().size()==0){
                    for (int i = 0; i < 25; i++) {
                        //conversation.createMessage(dummyText[((int)(Math.random() * dummyText.length))]);
                    }
                }
                conversation.getNewMessagesAndSendOldOnes();
            }
        },context);



        /*
        messages = new ArrayList<>();
        for (int i = 0; i < 25; i++) {
            if (i % 5 == 0 || i % 4 == 0) {
                messages.add(new Message(dummyText[((int)(Math.random() * dummyText.length))], Message.Type.RECEIVED,context));
            } else {
                messages.add(new Message(dummyText[((int)(Math.random() * dummyText.length))], Message.Type.SENT,context));
            }
        }*/

        messageHorizontalMarginSmall = context.getResources().getDimension(R.dimen.list_item_message_horizontal_margin_small);
        messageHorizontalMarginLarge = context.getResources().getDimension(R.dimen.list_item_message_horizontal_margin_large);
    }

    public void setChatFragment(final ChatFragment chatFragment)
    {


        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                while(true)
                {
                    if(getConversation()==null)
                    {
                        synchronized (this){
                            try
                            {
                                wait(1);
                            }
                            catch (InterruptedException e)
                            {
                                e.printStackTrace();
                            }
                        }

                    }
                    else
                    {
                        getConversation().setChatFragment(chatFragment);
                        return;
                    }
                }
            }
        }).start();


        this.chatFragment=chatFragment;
    }




    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = null;

        if (viewType == MyNewAndBetterMessage.Job.INCOMING.ordinal()) {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_message_received, parent, false);
        } else if ( viewType == MyNewAndBetterMessage.Job.OUTGOING.ordinal()) {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_message_sent, parent, false);
        }

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        MarginSize marginSize;
        List<MyNewAndBetterMessage> list=conversation.getMessages();
        if (position == list.size() - 1) {
            marginSize = MarginSize.SMALL;
        } else {
            // If the previous message has same type, use a small margin.
            marginSize = (list.get(position).getJob() == list.get(position + 1).getJob()) ?
                    MarginSize.SMALL :
                    MarginSize.LARGE;

        }

        ((ViewHolder) holder).prepareToDisplay(list.get(position), marginSize);
    }

    @Override
    public int getItemCount() {
        return (conversation.getMessages() != null) ? conversation.getMessages().size() : 0;
    }

    @Override
    public int getItemViewType(int position) {
        return conversation.getMessages().get(position).getJob().ordinal();
    }


    /**
     * Holds references to Views which need to be accessed upon recycle.
     */
    private class ViewHolder extends RecyclerView.ViewHolder {

        /**
         * Displays message's payload.
         */
        private TextView message;

        public ViewHolder(View itemView) {
            super(itemView);
            message = (TextView) itemView.findViewById(R.id.message);
        }

        public void prepareToDisplay(MyNewAndBetterMessage message, MarginSize mSize) {
            // TODO Check previous layout param state used for this particular item.
            this.message.setText(message.getMessage());
            if (this.message.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) this.message.getLayoutParams();

                // Determine if small or large margin should be used.
                int top = (int)((mSize == MarginSize.SMALL) ? RecyclerViewAdapterMessages.this.messageHorizontalMarginSmall :
                        RecyclerViewAdapterMessages.this.messageHorizontalMarginLarge);

                // Apply margins.
                p.setMargins(p.leftMargin, top, p.rightMargin, p.bottomMargin);
                this.message.requestLayout();
            }
        }
    }

    public MyNewAndBetterConversation getConversation(){
        return conversation;
    }

    enum MarginSize {
        LARGE, SMALL;
    }
}

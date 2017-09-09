package com.bizfit.bizfit.RecyclerViews;

import android.content.Context;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bizfit.bizfit.DebugPrinter;
import com.bizfit.bizfit.chat.Conversation;
import com.bizfit.bizfit.chat.Message;
import com.bizfit.bizfit.R;
import com.bizfit.bizfit.User;
import com.bizfit.bizfit.chat.Messages.Message2;
import com.bizfit.bizfit.fragments.ChatFragment;
import com.bizfit.bizfit.utils.Constants;

import java.util.List;

/**
 *
 */
public class RecyclerViewAdapterMessages extends RecyclerView.Adapter {

    Conversation conversation;
    ChatFragment chatFragment;
    Intent intent=null;
    ViewGroup parent;


    //private ArrayList<Message> messages;

    protected float messageHorizontalMarginSmall;
    protected float messageHorizontalMarginLarge;

    public RecyclerViewAdapterMessages(final Intent intent, Context context) {
        this.intent=intent;
        User user=User.getLastUser(null,context, null);
        if(intent!=null&&intent.hasExtra(Constants.coach_id)){
            conversation=user.addConversation(new Conversation(user.userName,intent.getStringExtra(Constants.coach_id),user));
        }else{
            conversation=user.addConversation(new Conversation(user.userName,user.userName.equals("default")?"atte.yliverronen@gmail.com":"default",user));
        }
        conversation.getNewMessagesAndSendOldOnes();



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
        this.parent = parent;

        if (viewType == Message.Job.INCOMING.ordinal()) {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_message_received, parent, false);
        } else if ( viewType == Message.Job.OUTGOING.ordinal()) {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_message_sent, parent, false);
        }

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        MarginSize marginSize;
        List<Message2> list=conversation.getMessages();
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
    public int getItemCount()
    {
        if(conversation==null){
            return 0;
        }
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
        private TextView tVtimestamp;
        ConstraintLayout layout;
        View view;
        public ViewHolder(View itemView)
        {
            super(itemView);
            message = (TextView) itemView.findViewById(R.id.message);
            tVtimestamp = (TextView) itemView.findViewById(R.id.tVtimestamp);
            view=itemView;

        }

        public void prepareToDisplay(final Message2 message, MarginSize mSize)
        {
            // TODO Check previous layout param state used for this particular item.

            //this.message.setText(message.getMessage());
            if(tVtimestamp!=null)
            {
                //this.tVtimestamp.setText(message.getTimestamp());
            }
            message.drawThySelf(view);
            if (this.message.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) this.message.getLayoutParams();

                // Determine if small or large margin should be used.
                int top = (int)((mSize == MarginSize.SMALL) ? RecyclerViewAdapterMessages.this.messageHorizontalMarginSmall :
                        RecyclerViewAdapterMessages.this.messageHorizontalMarginLarge);

                // Apply margins.
                p.setMargins(p.leftMargin, top, p.rightMargin, p.bottomMargin);
                this.message.requestLayout();

            }
/*
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    message.editPinnedUser(User.getLastUser(null,null,null).userName);
                    return false;
                }
            });

            this.message.setMaxWidth((parent.getWidth()/4) * 3);
            switch (message.getJob())
            {
                case INCOMING:
                    leftSide();
                    break;
                case OUTGOING:
                    leftSide();
                    rightSide();
                    break;
            }
        }
        private void rightSide()
        {

        }
        private void leftSide()
        {

            DebugPrinter.Debug("herperderker: "+message.getLayout());
            DebugPrinter.Debug("herperderker2: "+message.getParent());


            ViewGroup row = (ViewGroup) tVtimestamp.getParent();
            for (int itemPos = 0; itemPos < row.getChildCount(); itemPos++) {
                View view = row.getChildAt(itemPos);
                ConstraintSet constraintSet = new ConstraintSet();
                ConstraintLayout constraintLayout = (ConstraintLayout) view.getParent();
                constraintSet.clone(constraintLayout);
                constraintSet.connect(view.getId(), ConstraintSet.LEFT, constraintLayout.getId(), ConstraintSet.LEFT, 0);
                constraintSet.applyTo(constraintLayout);
            }
            */
        }
    }

    public Conversation getConversation(){
        return conversation;
    }

    enum MarginSize {
        LARGE, SMALL;
    }
}

package com.bizfit.bizfit.utils;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bizfit.bizfit.Message;
import com.bizfit.bizfit.R;

import java.util.ArrayList;

/**
 *
 */
public class RecyclerViewAdapterMessages extends RecyclerView.Adapter {

    private ArrayList<Message> messages;

    public RecyclerViewAdapterMessages(String[] dummyText) {
        messages = new ArrayList<>();
        for (int i = 0; i < 25; i++) {
            if (i % 5 == 0 || i % 4 == 0) {
                messages.add(new Message(dummyText[((int)(Math.random() * dummyText.length))], Message.Type.RECEIVED));
            } else {
                messages.add(new Message(dummyText[((int)(Math.random() * dummyText.length))], Message.Type.SENT));
            }
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = null;

        if (viewType == Message.Type.RECEIVED.ordinal()) {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_message_received, parent, false);
        } else if ( viewType == Message.Type.SENT.ordinal()) {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_message_sent, parent, false);
        }

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((ViewHolder) holder).prepareToDisplay(messages.get(position));
    }

    @Override
    public int getItemCount() {
        return (messages != null) ? messages.size() : 0;
    }

    @Override
    public int getItemViewType(int position) {
        return messages.get(position).getType().ordinal();
    }

    public void appendData(Message message) {
        if (messages == null) messages = new ArrayList<>();
        messages.add(message);
    }

    public void replaceDataSet(ArrayList<Message> messages) {
        this.messages = messages;
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

        public void prepareToDisplay(Message message) {
            this.message.setText(message.getPayload());
        }
    }
}

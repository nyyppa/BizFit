package com.bizfit.bizfit.utils;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.bizfit.bizfit.Message;
import com.bizfit.bizfit.R;

import java.util.ArrayList;

/**
 *
 */
public class RecyclerViewAdapterMessages extends RecyclerView.Adapter {

    private ArrayList<Message> messages;

    protected float messageHorizontalMarginSmall;
    protected float messageHorizontalMarginLarge;

    public RecyclerViewAdapterMessages(String[] dummyText, Context context) {
        messages = new ArrayList<>();
        for (int i = 0; i < 25; i++) {
            if (i % 5 == 0 || i % 4 == 0) {
                messages.add(new Message(dummyText[((int)(Math.random() * dummyText.length))], Message.Type.RECEIVED,context));
            } else {
                messages.add(new Message(dummyText[((int)(Math.random() * dummyText.length))], Message.Type.SENT,context));
            }
        }

        messageHorizontalMarginSmall = context.getResources().getDimension(R.dimen.list_item_message_horizontal_margin_small);
        messageHorizontalMarginLarge = context.getResources().getDimension(R.dimen.list_item_message_horizontal_margin_large);
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
        MarginSize marginSize;

        if (position == messages.size() - 1) {
            marginSize = MarginSize.SMALL;
        } else {
            // If the previous message has same type, use a small margin.
            marginSize = (messages.get(position).getType() == messages.get(position + 1).getType()) ?
                    MarginSize.SMALL :
                    MarginSize.LARGE;
        }

        ((ViewHolder) holder).prepareToDisplay(messages.get(position), marginSize);
    }

    @Override
    public int getItemCount() {
        return (messages != null) ? messages.size() : 0;
    }

    @Override
    public int getItemViewType(int position) {
        return messages.get(position).getType().ordinal();
    }

    public void addData(Message message) {
        if (messages == null) messages = new ArrayList<>();
        messages.add(0, message);
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

        public void prepareToDisplay(Message message, MarginSize mSize) {
            // TODO Check previous layout param state used for this particular item.
            this.message.setText(message.getPayload());
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

    enum MarginSize {
        LARGE, SMALL;
    }
}

package com.bizfit.bizfit.views;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.bizfit.bizfit.R;
import com.bizfit.bizfit.chat.Conversation;
import com.bizfit.bizfit.chat.Message;


/**
 * Created by Atte Ylivrronen on 26.4.2017.
 */

public class ConversationTabView extends android.support.v7.widget.AppCompatTextView implements Conversation.NewMessageRecievedListener {

    public ConversationTabView(Context context) {
        super(context);
    }

    public ConversationTabView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ConversationTabView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void newMessageRecieved(Message message) {
        Activity activity=getActivity();
        if(message==null||message.getMessage()==null)
        {
            return;
        }
        final String text=message.getMessage();
        if(activity!=null)
        {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setText(text);
                }
            });
        }

    }
    private Activity getActivity()
    {
        Context context = getContext();
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity)context;
            }
            context = ((ContextWrapper)context).getBaseContext();
        }
        return null;
    }
}

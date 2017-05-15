package com.bizfit.bizfit.views;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bizfit.bizfit.ConversationArrayAdapter;
import com.bizfit.bizfit.DebugPrinter;
import com.bizfit.bizfit.R;
import com.bizfit.bizfit.chat.Conversation;
import com.bizfit.bizfit.chat.Message;


/**
 * Created by Atte Ylivrronen on 26.4.2017.
 */

public class ConversationTabView extends android.support.v7.widget.AppCompatTextView implements Conversation.NewMessageRecievedListener {
    TextView unreadMessages;
    public ConversationTabView(Context context) {
        super(context);
    }

    public ConversationTabView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ConversationTabView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    public void setUnreadMessages(TextView textView)
    {
        this.unreadMessages=textView;
    }

    @Override
    public void newMessageRecieved(Message message) {
        Activity activity=getActivity();
        if(message==null||message.getMessage()==null)
        {
            return;
        }
        final String text=message.getMessage();
        ;
        DebugPrinter.Debug("ID:"+"");

        //((ConversationArrayAdapter)((ListView)((LinearLayout)getParent()).findViewById(R.id.lista)).getAdapter()).notifyDataSetChanged();

        //((ConversationArrayAdapter)((ListView)getParent()).getAdapter()).notifyDataSetChanged();
        if(activity!=null)
        {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((ConversationArrayAdapter)((ListView)((RelativeLayout)((LinearLayout)getParent()).getParent()).getParent()).getAdapter()).testi();
                    setText(text);
                }
            });
        }

    }

    @Override
    public void setUnreadMessageNumber(int number) {
        Activity activity=getActivity();
        if(unreadMessages==null)
        {
            return;
        }
        final int unreadMessageNumber=number;
        if(activity!=null)
        {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(unreadMessageNumber>99)
                    {
                        unreadMessages.setText(99+"+");
                        unreadMessages.setVisibility(VISIBLE);
                    }
                    else if(unreadMessageNumber>0)
                    {
                        unreadMessages.setText(unreadMessageNumber+"");
                        unreadMessages.setVisibility(VISIBLE);
                    }
                    else
                    {
                        unreadMessages.setVisibility(INVISIBLE);
                    }
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

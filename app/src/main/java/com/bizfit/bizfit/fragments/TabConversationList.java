package com.bizfit.bizfit.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.bizfit.bizfit.ConversationArrayAdapter;
import com.bizfit.bizfit.R;
import com.bizfit.bizfit.User;

import java.util.ArrayList;

/**
 * Created by attey on 01/02/2017.
 */

public class TabConversationList extends Fragment{

    public TabConversationList(){

    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.list_conversations, container, false);
    }

    public void onViewCreated(View view, Bundle savedInstanceState){
        User user= User.getLastUser(null,null,null);
        ListView listView= (ListView) view.findViewById(R.id.lista);

        listView.setAdapter(new ConversationArrayAdapter(getContext(),(ArrayList)user.getConversations()));

    }
}

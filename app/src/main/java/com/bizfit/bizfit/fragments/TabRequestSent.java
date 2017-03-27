package com.bizfit.bizfit.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.bizfit.bizfit.ChatRequestArrayAdapter;
import com.bizfit.bizfit.R;
import com.bizfit.bizfit.User;

import java.util.ArrayList;

/**
 * Created by iipa on 27.3.2017.
 */

public class TabRequestSent extends Fragment {

    View view;

    public TabRequestSent(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.list_chat_requests, container, false);
    }

    public void onViewCreated(View view, Bundle savedInstanceState){
        this.view=view;
        User user=User.getLastUser(new User.UserLoadedListener() {
            @Override
            public void informationUpdated() {
                if(getActivity()!=null){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ListView listView= (ListView) TabRequestSent.this.view.findViewById(R.id.chat_request_lista);
                            listView.setAdapter(new ChatRequestArrayAdapter(getContext(),(ArrayList)User.getLastUser(null,null,null).getMySentChatRequests()));
                        }
                    });
                }
            }
        }, null, null);
        ListView listView= (ListView) view.findViewById(R.id.chat_request_lista);
        listView.setAdapter(new ChatRequestArrayAdapter(getContext(),(ArrayList)user.getMySentChatRequests()));
    }
}

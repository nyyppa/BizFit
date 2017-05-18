package com.bizfit.bizfitUusYritysKeskusAlpha.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.bizfit.bizfitUusYritysKeskusAlpha.ChatRequestArrayAdapter;
import com.bizfit.bizfitUusYritysKeskusAlpha.R;
import com.bizfit.bizfitUusYritysKeskusAlpha.User;

import java.util.ArrayList;

/**
 * Created by iipa on 27.3.2017.
 */

public class TabRequestReceived extends Fragment {

    View view;

    public TabRequestReceived(){

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
                            ListView listView= (ListView) TabRequestReceived.this.view.findViewById(R.id.chat_request_lista);
                            listView.setAdapter(new ChatRequestArrayAdapter(getContext(),(ArrayList)User.getLastUser(null,null,null).getRequestsForMe()));
                        }
                    });
                }
            }
        }, null, null);
        ListView listView= (ListView) view.findViewById(R.id.chat_request_lista);
        listView.setAdapter(new ChatRequestArrayAdapter(getContext(),(ArrayList)user.getRequestsForMe()));
    }
}

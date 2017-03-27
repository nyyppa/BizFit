package com.bizfit.bizfit.fragments;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.bizfit.bizfit.ChatRequestArrayAdapter;
import com.bizfit.bizfit.R;
import com.bizfit.bizfit.User;

import java.util.ArrayList;

/**
 * Created by attey on 13/03/2017.
 */

public class TabConversationRequests extends Fragment {
    // Fragment TabHost as mTabHost
    private FragmentTabHost mTabHost;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tab_fragment_requests, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        mTabHost = (FragmentTabHost) getActivity().findViewById(android.R.id.tabhost);
        mTabHost.setup(getActivity().getApplicationContext(), getActivity().getSupportFragmentManager(), R.id.realtabcontent);

        mTabHost.addTab(mTabHost.newTabSpec("tabSent").setIndicator("Sent requests"),
                TabRequestSent.class, null);
        mTabHost.addTab(mTabHost.newTabSpec("tabReceived").setIndicator("Received requests"),
                TabRequestReceived.class, null);
    }
}

package com.bizfit.bizfit.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bizfit.bizfit.R;
import com.bizfit.bizfit.User;
import com.bizfit.bizfit.tracker.Tracker;
import com.bizfit.bizfit.RecyclerViews.RecyclerViewAdapterMessages;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class ChatFragment extends Fragment implements View.OnClickListener {

    private RecyclerViewAdapterMessages mAdapter;
    private RecyclerView mRecyclerView;
    private TextView input;
    private Snackbar noTrackers;
    private Snackbar noTrackersShared;

    // TODO: Rename and change types and number of parameters
    public static ChatFragment newInstance(String param1, String param2) {
        ChatFragment fragment = new ChatFragment();
        return fragment;
    }

    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // Set values from the bundle package if needed.
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {

        View v = inflater.inflate(R.layout.fragment_messages, container, false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.fragment_messages_recyclerView);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mAdapter = new RecyclerViewAdapterMessages(getActivity().getIntent(), getContext());
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setChatFragment(this);
        //new GetMessagesFromServer(mAdapter,getActivity()).start();
        //mAdapter.getConversation().getNewMessagesAndSentOldOnes();

        noTrackers = Snackbar.make(v, "You don't have any trackers to share", Snackbar.LENGTH_LONG);
        noTrackersShared = Snackbar.make(v, "No trackers were shared", Snackbar.LENGTH_LONG);

        v.findViewById(R.id.button_send_message).setOnClickListener(this);
        v.findViewById(R.id.shareTracker).setOnClickListener(this);
        input = (TextView) v.findViewById(R.id.message);
        return v;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.button_send_message:
                // TODO A finer solution with text trimming.
                if(!String.valueOf(input.getText()).trim().isEmpty())
                {
                    mAdapter.getConversation().createMessage((input.getText()+""));
                    mAdapter.notifyItemInserted(0);
                    mRecyclerView.smoothScrollToPosition(0);
                    input.setText("");
                }

                //mAdapter.addData(new Message(String.valueOf(input.getText()), Message.Type.SENT,getContext()));

                mAdapter.getConversation().getNewMessagesAndSendOldOnes();


                break;
            case R.id.shareTracker:
                if(User.getLastUser(null,null,null).getTrackers().length>0){


                    shareTrackers();

                } else {

                    // Notifies user if no trackers are found
                    noTrackers.show();
                }

                break;
        }
    }

    /**
     *  Method for asking user which trackers to share, and sharing them.
     */

    public void shareTrackers() {

        // Inflating xml-layout into a view

        LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.fragment_select_tracker, null);

        // Adding trackers into a list

        final ListView lv = (ListView) layout.findViewById(R.id.share_tracker_listview);
        final Tracker [] trackerarray = User.getLastUser(null, null, null).getTrackers();

        final List<String> trackerlist = new ArrayList();


        for(int i = 0; i < trackerarray.length; i++) {
            trackerlist.add(trackerarray[i].getName());
        }

        // Setting the tracker list into listview

        ArrayAdapter<String> adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_multiple_choice, trackerlist);
        lv.setAdapter(adapter);
        lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        // Setting up popup attributes

        final PopupWindow popupSelect = new PopupWindow(getActivity());
        popupSelect.setContentView(layout);
        popupSelect.setWidth(LinearLayout.LayoutParams.WRAP_CONTENT);
        popupSelect.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        popupSelect.setFocusable(true);
        popupSelect.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // Showing popup

        popupSelect.showAtLocation(layout, Gravity.CENTER, 0, 0);

        // Cancel-button listener

        Button close = (Button) layout.findViewById(R.id.tracker_sharing_cancel);
        close.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                popupSelect.dismiss();
                noTrackersShared.show();
            }
        });

        // Share-button listener

        Button share = (Button) layout.findViewById(R.id.tracker_sharing_share);
        share.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO: share selected trackers
                popupSelect.dismiss();

                boolean shared = false;

                // Iterating through trackers and sharing selected ones

                for(int i = 0; i < trackerarray.length; i++) {
                    if(lv.isItemChecked(i)) {
                        trackerarray[i].shareTracker(mAdapter.getConversation().getOwner());
                        shared = true;
                    }
                }

                // If trackers were shared, create message. Else notify user that nothing was shared.

                if(shared) {
                    mAdapter.getConversation().createMessage("Shared tracker(s)");
                    mAdapter.notifyItemInserted(0);
                    mRecyclerView.smoothScrollToPosition(0);
                } else {
                    noTrackersShared.show();
                }
            }
        });
    }

    public RecyclerViewAdapterMessages getmAdapter()
    {
        return mAdapter;
    }

    public RecyclerView getmRecyclerView()
    {
        return mRecyclerView;
    }
}


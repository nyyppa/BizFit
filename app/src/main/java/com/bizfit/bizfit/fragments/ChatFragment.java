package com.bizfit.bizfit.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bizfit.bizfit.R;
import com.bizfit.bizfit.User;
import com.bizfit.bizfit.tracker.Tracker;
import com.bizfit.bizfit.utils.RecyclerViewAdapterMessages;

/**
 *
 */
public class ChatFragment extends Fragment implements View.OnClickListener {

    private RecyclerViewAdapterMessages mAdapter;
    private RecyclerView mRecyclerView;
    private TextView input;

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
                mAdapter.getConversation().createMessage("code share_tracker"+User.getLastUser(null,null,null).getTrackers()[0].shareTracker(mAdapter.getConversation().getOwner()).toString());
                mAdapter.notifyItemInserted(0);
                mRecyclerView.smoothScrollToPosition(0);
                break;
        }
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


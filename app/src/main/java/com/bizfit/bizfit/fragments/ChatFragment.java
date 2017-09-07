package com.bizfit.bizfit.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bizfit.bizfit.R;

import com.bizfit.bizfit.RecyclerViews.RecyclerViewAdapterMessages;
import com.bizfit.bizfit.activities.MessageActivity;
import com.bizfit.bizfit.utils.Utils;


/**
 *
 */
public class ChatFragment extends Fragment implements View.OnClickListener {

    private RecyclerViewAdapterMessages mAdapter;
    private RecyclerView mRecyclerView;
    private EditText input;

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

        View v = inflater.inflate(R.layout.fragment_messages2, container, false);
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
        ImageView iVcoach=(ImageView)v.findViewById(R.id.coach_image);
        iVcoach.setImageDrawable(ContextCompat.getDrawable(getContext(),Utils.getDrawableID(mAdapter.getConversation().getOther())));

        String coachName = Utils.getCoachName(mAdapter.getConversation().getOther());

        TextView tVname=(TextView)v.findViewById(R.id.coach_name);
        tVname.setText(coachName);

        String [] coachNameSplit = coachName.split(" ");

        input = (EditText) v.findViewById(R.id.message);
        if(mAdapter.getItemCount() == 0) {
            input.setHint(getString(R.string.hint_message_field, coachNameSplit[0]));
        }

        ImageButton imgb = (ImageButton) v.findViewById(R.id.button_messages_menu);
        imgb.setOnClickListener(this);

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
                    input.setHint("");
                }

                //mAdapter.addData(new Message(String.valueOf(input.getText()), Message.Type.SENT,getContext()));

                mAdapter.getConversation().getNewMessagesAndSendOldOnes();

                break;
            case R.id.button_messages_menu:
                MessageActivity ma = (MessageActivity) getActivity();
                ma.switchToPinned();
                break;
        }
    }

    /**
     *  Method for asking user which trackers to share, and sharing them.
     */

    public void shareTrackers() {

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


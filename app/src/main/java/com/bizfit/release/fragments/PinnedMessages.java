package com.bizfit.release.fragments;

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

import com.bizfit.release.R;
import com.bizfit.release.RecyclerViews.RecyclerViewAdapterPinnedMessages;
import com.bizfit.release.activities.MessageActivity;
import com.bizfit.release.utils.Utils;


public class PinnedMessages extends Fragment implements View.OnClickListener {

    private RecyclerViewAdapterPinnedMessages mAdapter;
    private RecyclerView mRecyclerView;
    private EditText input;

    // TODO: Rename and change types and number of parameters
    public static PinnedMessages newInstance(String param1, String param2) {
        PinnedMessages fragment = new PinnedMessages();
        return fragment;
    }

    public PinnedMessages() {
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

        View v = inflater.inflate(R.layout.fragment_pinnedmessages, container, false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.fragment_messages_recyclerView);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mAdapter = new RecyclerViewAdapterPinnedMessages(getActivity().getIntent(), getContext());
        mRecyclerView.setAdapter(mAdapter);
        //new GetMessagesFromServer(mAdapter,getActivity()).start();
        //mAdapter.getConversation().getNewMessagesAndSentOldOnes();

        ImageView iVcoach=(ImageView)v.findViewById(R.id.coach_image);
        iVcoach.setImageDrawable(ContextCompat.getDrawable(getContext(), Utils.getDrawableID(mAdapter.getConversation().getOther())));

        String coachName = Utils.getCoachName(mAdapter.getConversation().getOther());

        TextView tVname=(TextView)v.findViewById(R.id.coach_name);
        tVname.setText(coachName + " - " + getString(R.string.pinned));

        String [] coachNameSplit = coachName.split(" ");

        ImageButton imgb = (ImageButton) v.findViewById(R.id.button_messages_menu);
        imgb.setOnClickListener(this);

        return v;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.button_messages_menu:
                MessageActivity ma = (MessageActivity) getActivity();
                ma.switchToRegular();
                break;
        }
    }

    /**
     *  Method for asking user which trackers to share, and sharing them.
     */

    public void shareTrackers() {

    }

    public RecyclerViewAdapterPinnedMessages getmAdapter()
    {
        return mAdapter;
    }

    public RecyclerView getmRecyclerView()
    {
        return mRecyclerView;
    }
}

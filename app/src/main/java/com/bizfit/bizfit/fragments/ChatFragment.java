package com.bizfit.bizfit.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
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
import android.widget.CheckBox;

import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bizfit.bizfit.R;

import com.bizfit.bizfit.RecyclerViews.RecyclerViewAdapterMessages;
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

        Bundle extras = getActivity().getIntent().getExtras();

        if (extras != null) {
            // Set values from the bundle package if needed.
            String name = extras.getString("coachName", "ei loutunut");
            String id = extras.getString("coachID", "noup");
            System.out.println("NAME: " + name + "ID: " + id);
            System.out.println("NAME: " + name + "ID: " + id);
            System.out.println("NAME: " + name + "ID: " + id);
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
        ImageView imageView=(ImageView)v.findViewById(R.id.coach_image);

        imageView.setImageDrawable(ContextCompat.getDrawable(getContext(),Utils.getDrawableID(mAdapter.getConversation().getOther())));
        TextView textView=(TextView)v.findViewById(R.id.coach_name);
        textView.setText(Utils.getCouchName(mAdapter.getConversation().getOther()));



        input = (EditText) v.findViewById(R.id.message);

        if(mAdapter.getItemCount() == 0) {
            input.setHint(getString(R.string.hint_message_field, "Pekka"));
        }

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


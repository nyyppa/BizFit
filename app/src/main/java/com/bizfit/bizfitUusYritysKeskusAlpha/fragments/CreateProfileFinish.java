package com.bizfit.bizfitUusYritysKeskusAlpha.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.bizfit.bizfitUusYritysKeskusAlpha.R;
import com.bizfit.bizfitUusYritysKeskusAlpha.activities.CreateProfile;

/**
 * Created by iipa on 28.8.2017.
 */

public class CreateProfileFinish extends Fragment implements View.OnClickListener {

    CreateProfile parentActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        parentActivity = (CreateProfile) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_profile_finish, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        Button b1 = (Button) getActivity().findViewById(R.id.createExpert);
        Button b2 = (Button) getActivity().findViewById(R.id.backHome);

        b1.setOnClickListener(this);
        b2.setOnClickListener(this);

    }

    public void onClick(View v) {

        switch(v.getId()) {

            case R.id.createExpert:
                break;

            case R.id.backHome:
                // TODO
                // back to home
                // local preferences: canAsk = true;

                parentActivity.setResult(1);
                parentActivity.finish();
                break;

        }

    }
}
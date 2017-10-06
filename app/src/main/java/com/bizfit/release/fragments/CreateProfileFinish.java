package com.bizfit.release.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.bizfit.release.R;
import com.bizfit.release.activities.CreateProfile;

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

        Button button = (Button) getActivity().findViewById(R.id.backHome);

        button.setOnClickListener(this);

    }

    public void onClick(View v) {

        switch(v.getId()) {

            case R.id.backHome:
                parentActivity.finish();
                break;

        }

    }
}
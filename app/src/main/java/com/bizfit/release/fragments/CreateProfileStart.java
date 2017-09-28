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
 * Created by iipa on 15.8.2017.
 */

public class CreateProfileStart extends Fragment implements View.OnClickListener {

    CreateProfile parentActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        parentActivity = (CreateProfile) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_profile_start, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        Button b1 = (Button) getActivity().findViewById(R.id.createProfile);
        Button b2 = (Button) getActivity().findViewById(R.id.askLater);
        Button b3 = (Button) getActivity().findViewById(R.id.neverAsk);

        b1.setOnClickListener(this);
        b2.setOnClickListener(this);
        b3.setOnClickListener(this);

    }

    public void onClick(View v) {

        switch(v.getId()) {

            case R.id.createProfile:
                // TODO
                // go to select which profile to create
                break;

            case R.id.askLater:
                // TODO
                // back to home
                // local preferences: canAsk = true;

                parentActivity.setResult(1);
                parentActivity.finish();
                break;

            case R.id.neverAsk:
                // TODO
                // back to home
                // local preferences: canAsk = false;
                break;

        }

    }
}

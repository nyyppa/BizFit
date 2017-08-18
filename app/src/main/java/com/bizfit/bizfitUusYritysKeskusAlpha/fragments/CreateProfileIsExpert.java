package com.bizfit.bizfitUusYritysKeskusAlpha.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;

import com.bizfit.bizfitUusYritysKeskusAlpha.R;
import com.bizfit.bizfitUusYritysKeskusAlpha.activities.CreateProfile;

/**
 * Created by iipa on 17.8.2017.
 */

public class CreateProfileIsExpert extends Fragment implements View.OnClickListener {

    CreateProfile parentActivity;

    Button proceed;
    RadioButton yes;
    RadioButton no;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        parentActivity = (CreateProfile) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_profile_isexpert, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        proceed = (Button) getActivity().findViewById(R.id.proceed);
        proceed.setOnClickListener(this);
        proceed.setVisibility(View.GONE);

        yes = (RadioButton) getActivity().findViewById(R.id.yes);
        yes.setOnClickListener(this);

        no = (RadioButton) getActivity().findViewById(R.id.no);
        no.setOnClickListener(this);

        if(parentActivity.checkIfIsExpert()) {
            yes.setChecked(true);
            proceed.setVisibility(View.VISIBLE);
        } else if(parentActivity.checkIfAskedExpert()) {
            no.setChecked(true);
            proceed.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {

        switch(v.getId()) {

            case R.id.proceed:
                if(yes.isChecked()) {
                    parentActivity.setExpert(true);
                    parentActivity.setAskedExpert(true);
                } else {
                    parentActivity.setExpert(false);
                    parentActivity.setAskedExpert(true);
                }
                parentActivity.switchPhase(CreateProfile.Phase.ISEXPERT);
                break;

            case R.id.yes:
                proceed.setVisibility(View.VISIBLE);
                no.setChecked(false);
                break;

            case R.id.no:
                proceed.setVisibility(View.VISIBLE);
                yes.setChecked(false);
                break;

        }

    }

}

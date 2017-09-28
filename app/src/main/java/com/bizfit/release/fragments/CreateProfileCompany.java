package com.bizfit.release.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.bizfit.release.Profile;
import com.bizfit.release.R;
import com.bizfit.release.activities.CreateProfile;

/**
 * Created by iipa on 17.8.2017.
 */

public class CreateProfileCompany extends Fragment implements View.OnClickListener {

    CreateProfile parentActivity;

    Button proceed;
    Button skip;

    EditText companyName;
    EditText companyField;
    EditText companyDesc;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        parentActivity = (CreateProfile) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_profile_company, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        proceed = (Button) getActivity().findViewById(R.id.proceed);
        proceed.setOnClickListener(this);
        proceed.setVisibility(View.GONE);

        skip = (Button) getActivity().findViewById(R.id.skip);
        skip.setOnClickListener(this);

        companyName = (EditText) getActivity().findViewById(R.id.companyName);
        companyField = (EditText) getActivity().findViewById(R.id.companyField);
        companyDesc = (EditText) getActivity().findViewById(R.id.companyDesc);

        // check if information already exists
        // if it does, add to fields

        if(parentActivity.getProfile() != null) {

            Profile profile = parentActivity.getProfile();

            if (profile.companyName != null) {
                companyName.setText(profile.companyName);
                proceed.setVisibility(View.VISIBLE);
            }

            if(profile.companyField != null) {
                companyField.setText(profile.companyField);
                proceed.setVisibility(View.VISIBLE);
            }

            if(profile.companyDesc != null) {
                companyDesc.setText(profile.companyDesc);
                proceed.setVisibility(View.VISIBLE);
            }

        }

        // if there is input in any of the fields, show proceed button

        TextWatcher tw = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String name = companyName.getText().toString();
                String field = companyField.getText().toString();
                String desc = companyDesc.getText().toString();

                if(!name.isEmpty() || !field.isEmpty() || !desc.isEmpty()) {
                    proceed.setVisibility(View.VISIBLE);
                } else {
                    proceed.setVisibility(View.GONE);
                }

            }

            @Override
            public void afterTextChanged(Editable s) { }
        };

        companyName.addTextChangedListener(tw);
        companyField.addTextChangedListener(tw);
        companyDesc.addTextChangedListener(tw);

        String name = parentActivity.getCompanyName();
        String field = parentActivity.getCompanyField();
        String desc = parentActivity.getCompanyDesc();

        if(name != null && !name.isEmpty()) {
            companyName.setText(name);
        }

        if(field != null && !field.isEmpty()) {
            companyField.setText(field);
        }

        if(desc != null && !desc.isEmpty()) {
            companyDesc.setText(desc);
        }

    }

    @Override
    public void onClick(View v) {

        switch(v.getId()) {
            case R.id.skip:
                parentActivity.switchPhase(CreateProfile.Phase.COMPANY);
                break;

            case R.id.proceed:
                parentActivity.setCompanyName(companyName.getText().toString());
                parentActivity.setCompanyField(companyField.getText().toString());
                parentActivity.setCompanyDesc(companyDesc.getText().toString());
                parentActivity.switchPhase(CreateProfile.Phase.COMPANY);
                break;
        }

    }
}
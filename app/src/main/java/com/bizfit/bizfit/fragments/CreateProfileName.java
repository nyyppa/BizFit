package com.bizfit.bizfit.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.bizfit.bizfit.Profile;
import com.bizfit.bizfit.R;
import com.bizfit.bizfit.activities.CreateProfile;

/**
 * Created by iipa on 17.8.2017.
 */

public class CreateProfileName extends Fragment implements View.OnClickListener {

    CreateProfile parentActivity;

    EditText firstName;
    EditText lastName;

    Button proceed;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        parentActivity = (CreateProfile) getActivity();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_profile_name, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        proceed = (Button) getActivity().findViewById(R.id.proceed);
        proceed.setOnClickListener(this);
        proceed.setVisibility(View.GONE);

        firstName = (EditText) getActivity().findViewById(R.id.firstName);
        lastName = (EditText) getActivity().findViewById(R.id.lastName);

        // check if information already exists
        // if it does, add to fields

        if(parentActivity.getProfile() != null) {
            Profile profile = parentActivity.getProfile();

            if(profile.firstName != null) {
                firstName.setText(profile.firstName);
            }

            if(profile.lastName != null) {
                lastName.setText(profile.lastName);
            }

            if(!firstName.getText().toString().isEmpty() && !lastName.getText().toString().isEmpty()) {
                proceed.setVisibility(View.VISIBLE);
            }

        }

        // if there is input in first and last name, show the proceed button

        TextWatcher tw = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String first = firstName.getText().toString();
                String last = lastName.getText().toString();

                if(!first.isEmpty() && !last.isEmpty()) {
                    proceed.setVisibility(View.VISIBLE);
                } else {
                    proceed.setVisibility(View.GONE);
                }

            }

            @Override
            public void afterTextChanged(Editable s) { }
        };

        firstName.addTextChangedListener(tw);
        lastName.addTextChangedListener(tw);

        // if there is already a name, show it and make it able to proceed

        String first = parentActivity.getFirstName();
        String last = parentActivity.getLastName();
        if(first != null && !first.isEmpty()) {
            firstName.setText(first);
        }

        if(last != null && !last.isEmpty()) {
            lastName.setText(last);
        }

        if(!firstName.getText().toString().isEmpty() && !lastName.getText().toString().isEmpty()) {
            proceed.setVisibility(View.VISIBLE);
        }

    }

    public void onClick(View v) {

        switch(v.getId()) {

            case R.id.proceed:
                // save the information and move to the next step
                parentActivity.setFirstName(firstName.getText().toString());
                parentActivity.setLastName(lastName.getText().toString());
                parentActivity.switchPhase(CreateProfile.Phase.NAME);
                break;
        }

    }

}
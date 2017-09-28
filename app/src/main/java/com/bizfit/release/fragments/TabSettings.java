package com.bizfit.release.fragments;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bizfit.release.Profile;
import com.bizfit.release.R;
import com.bizfit.release.User;
import com.bizfit.release.activities.LoginActivity2;
import com.bizfit.release.activities.MessageActivity;
import com.bizfit.release.activities.Security;
import com.bizfit.release.activities.SelectProfileType;
import com.bizfit.release.activities.Support;

/**
 * Created by iipa on 16.6.2017.
 */

public class TabSettings extends Fragment implements View.OnClickListener {

    public Profile userProfile;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tab_fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        ConstraintLayout profile = getActivity().findViewById(R.id.selectProfile);
        ConstraintLayout support = getActivity().findViewById(R.id.support);
        ConstraintLayout security = getActivity().findViewById(R.id.security);
        ConstraintLayout logout = getActivity().findViewById(R.id.logOut);

        profile.setOnClickListener(this);
        support.setOnClickListener(this);
        security.setOnClickListener(this);
        logout.setOnClickListener(this);

        Button b = getActivity().findViewById(R.id.chat_test);
        b.setOnClickListener(this);

        userProfile = User.getUserProfile();

        addInfoToView();

    }

    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.selectProfile:
                startActivity(new Intent(getActivity(), SelectProfileType.class));
                break;

            case R.id.support:
                Intent intent2 = new Intent(getActivity(), Support.class);
                startActivity(intent2);
                break;

            case R.id.security:
                Intent intent3 = new Intent(getActivity(), Security.class);
                startActivity(intent3);
                break;

            case R.id.logOut:
                Intent intent = new Intent(getActivity(), LoginActivity2.class);
                User.signOut(intent, getActivity());
                break;

            case R.id.chat_test:
                MessageActivity.startChat(getContext(), "testi@gmail.com");
                break;
        }
    }

    public void addInfoToView() {

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                userProfile = User.getUserProfile();

                ImageView profPic = getActivity().findViewById(R.id.profilePicture);
                TextView tv = getActivity().findViewById(R.id.textView22);

                if(userProfile != null && userProfile.imageUUID != null) {
                    userProfile.drawToImgView(profPic, null);
                }

                if(userProfile != null && !userProfile.firstName.isEmpty() && !userProfile.lastName.isEmpty()) {
                    tv.setText(userProfile.firstName + " " + userProfile.lastName);
                } else {
                    tv.setText(User.getLastUser(null, null, null).userName);
                }

            }
        });

    }

}

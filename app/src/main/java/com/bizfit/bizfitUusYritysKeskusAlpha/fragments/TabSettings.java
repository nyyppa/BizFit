package com.bizfit.bizfitUusYritysKeskusAlpha.fragments;

import android.content.Context;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.bizfit.bizfitUusYritysKeskusAlpha.R;
import com.bizfit.bizfitUusYritysKeskusAlpha.User;
import com.bizfit.bizfitUusYritysKeskusAlpha.activities.CreateProfile;
import com.bizfit.bizfitUusYritysKeskusAlpha.activities.LoginActivity2;
import com.bizfit.bizfitUusYritysKeskusAlpha.activities.MainPage;
import com.bizfit.bizfitUusYritysKeskusAlpha.activities.Security;
import com.bizfit.bizfitUusYritysKeskusAlpha.activities.SelectProfileType;
import com.bizfit.bizfitUusYritysKeskusAlpha.activities.Support;

import java.util.ArrayList;

/**
 * Created by iipa on 16.6.2017.
 */

public class TabSettings extends Fragment implements View.OnClickListener {

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

        // TODO: add settings

        ConstraintLayout profile = getActivity().findViewById(R.id.selectProfile);
        ConstraintLayout support = getActivity().findViewById(R.id.support);
        ConstraintLayout security = getActivity().findViewById(R.id.security);
        ConstraintLayout logout = getActivity().findViewById(R.id.logOut);

        profile.setOnClickListener(this);
        support.setOnClickListener(this);
        security.setOnClickListener(this);
        logout.setOnClickListener(this);

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
        }
    }

}

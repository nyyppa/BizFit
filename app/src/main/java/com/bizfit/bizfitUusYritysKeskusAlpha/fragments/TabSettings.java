package com.bizfit.bizfitUusYritysKeskusAlpha.fragments;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
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
import com.bizfit.bizfitUusYritysKeskusAlpha.activities.LoginActivity2;
import com.bizfit.bizfitUusYritysKeskusAlpha.activities.MainPage;
import com.bizfit.bizfitUusYritysKeskusAlpha.activities.Security;
import com.bizfit.bizfitUusYritysKeskusAlpha.activities.Support;

import java.util.ArrayList;

/**
 * Created by iipa on 16.6.2017.
 */

public class TabSettings extends Fragment {

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

    }

}

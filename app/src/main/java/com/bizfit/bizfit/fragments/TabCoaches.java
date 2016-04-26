package com.bizfit.bizfit.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bizfit.bizfit.R;
import com.bizfit.bizfit.utils.RecyclerViewAdapterCoaches;
import com.bizfit.bizfit.utils.StoreRow;

import java.util.LinkedList;

public class TabCoaches extends Fragment implements PagerAdapter.TaggedFragment {

    public static final String TAG = "tab_coaches";
    public LinkedList<StoreRow> storeRows;

    public TabCoaches() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tab_fragment_coaches, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        storeRows = new LinkedList<>();

        for (int i = 0; i < 10; i++) {
            LinkedList<StoreRow.StoreItem> items = new LinkedList<>();

            for(int j = 0; j < 10; j++) {
                items.add(new StoreRow.StoreItem("name"));
            }

            storeRows.add(new StoreRow("Title " + i, "", items));
        }

        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.tab_fragment_coaches_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(new RecyclerViewAdapterCoaches(storeRows));
    }

    @Override
    public String fragmentTag() {
        return TAG;
    }
}

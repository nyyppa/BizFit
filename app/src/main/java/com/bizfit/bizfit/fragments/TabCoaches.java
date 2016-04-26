package com.bizfit.bizfit.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bizfit.bizfit.R;
import com.bizfit.bizfit.scrollCoordinators.EndlessRecyclerOnScrollListener;
import com.bizfit.bizfit.utils.RecyclerViewAdapterCoaches;
import com.bizfit.bizfit.utils.StoreRow;

import java.util.LinkedList;

public class TabCoaches extends Fragment implements PagerAdapter.TaggedFragment {

    public static final String TAG = "tab_coaches";
    private LinkedList<StoreRow> storeRows;
    private RecyclerViewAdapterCoaches adapter;

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

            for (int j = 0; j < 10; j++) {
                items.add(new StoreRow.StoreItem("name"));
            }

            storeRows.add(new StoreRow("Title " + i, "", items));
        }

        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.tab_fragment_coaches_recycler_view);
        LinearLayoutManager mManager = new LinearLayoutManager(getContext());
        adapter = new RecyclerViewAdapterCoaches(storeRows);

        mRecyclerView.setLayoutManager(mManager);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(mManager) {
            @Override
            public void onLoadMore(int current_page) {
                storeRows = new LinkedList<>();
                int count = adapter.getItemCount();
                for (int i = count; i < count + 10; i++) {
                    LinkedList<StoreRow.StoreItem> items = new LinkedList<>();

                    for (int j = 0; j < 10; j++) {
                        items.add(new StoreRow.StoreItem("name"));
                    }

                    adapter.addData(new StoreRow("Title " + i, "", items));
                }

                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public String fragmentTag() {
        return TAG;
    }
}

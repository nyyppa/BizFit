package com.bizfit.bizfitUusYritysKeskusAlpha.fragments;


import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bizfit.bizfitUusYritysKeskusAlpha.R;
import com.bizfit.bizfitUusYritysKeskusAlpha.scrollCoordinators.EndlessRecyclerOnScrollListener;
import com.bizfit.bizfitUusYritysKeskusAlpha.RecyclerViews.RecyclerViewAdapterCoaches;
import com.bizfit.bizfitUusYritysKeskusAlpha.utils.StoreRow;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * Displays a google play store like marketplace for coaches.
 */
public class TabCoaches extends Fragment {

    /**
     * List of currently loaded rows.
     */
    private LinkedList<StoreRow> storeRows;

    /**
     * RecyclerViewAdapter which contains all the store rows.
     */
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
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        storeRows = new LinkedList<>();

        final String[] firstNames = getResources().getStringArray(R.array.random_first_name);
        final String[] lastNames = getResources().getStringArray(R.array.random_last_name);
        final TypedArray imgIDs = getResources().obtainTypedArray(R.array.random_imgs);
        final String[] jobTitles=getResources().getStringArray(R.array.random_job_title);
        final TypedArray worksIds = getResources().obtainTypedArray(R.array.work_imgs);


        for (int i = 0; i < jobTitles.length; i++)
        {
            LinkedList<StoreRow.StoreItem> items = new LinkedList<>();
            if(i==1)
            {
                for(int j = 0; j < 4; j++)
                {
                    Drawable jari = (ContextCompat.getDrawable(this.getContext(), R.drawable.jartsa));
                    Drawable pasi = (ContextCompat.getDrawable(this.getContext(), R.drawable.pasi));
                    Drawable atte = (ContextCompat.getDrawable(this.getContext(), R.drawable.atte));
                    Drawable mylly = (ContextCompat.getDrawable(this.getContext(), R.drawable.mylly));
                    int imageID = worksIds.getResourceId(j, -1);
                    if(imageID==-1)
                    {
                        items.add(new StoreRow.StoreItem("Jari Myllymäki", mylly,imageID,(int) (Math.random()*400),"jari.myllymaki@gmail.com","0447358220"));
                    }
                    if(j==0)
                    {
                        items.add(new StoreRow.StoreItem("Jari Järvenpää", jari,imageID,(int) (Math.random()*400),"jari.k4rita@gmail.com",null));
                    }
                   if(j==1)
                    {
                        items.add(new StoreRow.StoreItem("Pasi Ojanen", pasi,imageID,(int) (Math.random()*400),"pasojan@gmail.com","0407283115"));
                    }
                    if(j==2)
                    {
                        items.add(new StoreRow.StoreItem("Atte Yliverronen", atte,imageID,(int) (Math.random()*400),"atte.yliverronen@gmail.com",null));
                    }
                    if(j==3)
                    {
                        items.add(new StoreRow.StoreItem("Jari Myllymäki", mylly,imageID,(int) (Math.random()*400),"jari.myllymaki@gmail.com","0447358220"));
                    }

                }
            }
            else
            {
                for (int j = 0; j < 4; j++)
                {
                    // Assign a random name and image.
                    // TODO Parse info from server
                    int imageId = imgIDs.getResourceId((int) (Math.random() * imgIDs.length()), -1);
                    items.add(new StoreRow.StoreItem(firstNames[(int) (Math.random() * firstNames.length)] + " " +
                            lastNames[(int) (Math.random() * lastNames.length)],
                            ContextCompat.getDrawable(view.getContext(), imageId), imageId,(int) (Math.random()*400),null,null));

                }
            }

            HashMap<String, String> stringData = new HashMap<>();
            stringData.put(StoreRow.TITLE,jobTitles[i]);
            storeRows.add(new StoreRow(stringData, items));
        }

        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.tab_fragment_coaches_recycler_view);
        LinearLayoutManager mManager = new LinearLayoutManager(getContext());
        adapter = new RecyclerViewAdapterCoaches(storeRows);

        mRecyclerView.setLayoutManager(mManager);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(mManager) {
            @Override
            public void onLoadMore(int current_page) {
                // TODO Request data from server.
            }
        });

        imgIDs.recycle();
        worksIds.recycle();

    }
}

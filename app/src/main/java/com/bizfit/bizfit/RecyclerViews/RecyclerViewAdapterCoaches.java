package com.bizfit.bizfit.RecyclerViews;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bizfit.bizfit.MyApplication;
import com.bizfit.bizfit.R;
import com.bizfit.bizfit.scrollCoordinators.EndlessRecyclerOnScrollListener;
import com.bizfit.bizfit.utils.StoreRow;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Manages the content in TabCoaches.
 */
public class RecyclerViewAdapterCoaches extends RecyclerView.Adapter implements View.OnClickListener {

    /**
     * Is the default layout for a row.
     */
    public static final int REGULAR_SCROLLABLE = 0;

    /**
     * Is a single big item.
     */
    public static final int BIG_PROMOTION = 1;

    /**
     * Is a list of default layouts.
     */
    private List<StoreRow> data;

    /**
     * Creates a new adapter with an empty data set.
     */
    public RecyclerViewAdapterCoaches() {
        this.data = new LinkedList<>();
    }

    /**
     * Creates a new adapter with the provided data set.
     *
     * @param data Tab content.
     */
    public RecyclerViewAdapterCoaches(List<StoreRow> data) {
        this.data = data;
    }
    private RecyclerView parent;
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh = null;
        this.parent= (RecyclerView) parent;
        switch (viewType) {
            case BIG_PROMOTION:
                View v1 = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_store_big_promotion, parent, false);
                vh = new BigPromotion(v1);
                List<Button> viewList=((BigPromotion)vh).getViewList();
                for(int i=0;i<viewList.size();i++){
                    viewList.get(i).setOnClickListener(this);
                }
                break;

            case REGULAR_SCROLLABLE:
                View v2 = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_store_row_regular, parent, false);
                vh = new RegularScrollable(v2);
                break;
        }

        return vh;
    }

    //// TODO: 2.1.2017  vaihda smoothscrolll?
    public void onClick(View view){

        /*
        switch (view.getId()){
            case R.id.promotion_button_1:
                parent.scrollToPosition(1);
                break;
            case R.id.promotion_button_2:
                parent.scrollToPosition(2);
                break;
            case R.id.promotion_button_3:
                parent.scrollToPosition(3);
                break;
        }
        */
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case BIG_PROMOTION:
                break;

            case REGULAR_SCROLLABLE:
                ((RegularScrollable) holder).prepareForDisplay(data.get(position - 1));
                break;
        }
    }

    /**
     * Adds a new row to the end of the data set.
     *
     * @param row Row to be added.
     */
    public void addData(StoreRow row) {
        notifyDataSetChanged();
        data.add(row);
    }

    @Override
    public int getItemViewType(int position) {
        return (position == 0) ? BIG_PROMOTION : REGULAR_SCROLLABLE;
    }

    @Override
    public int getItemCount() {
        // TODO placeholder. +1 because there is one single large item that is not included in data variable.
        return (data != null) ? data.size() + 1 : 1;
    }



    /**
     * Holds View's of a ViewGroup that need changing when a View is recycled.
     * <p/>
     * Upon recycling a view, ViewHolder is tasked to change the necessary
     * information within a view so it can be repurposed to show data at a
     * different index.
     * <p/>
     * ViewHolder contains references to all View's which need changing. This
     * is so to reduce overhead created by repeated findViewById() calls.
     * <p/>
     * This specific ViewHolder represents a horizontally single scrollable
     * row in the store. Contains multiple items.
     */
    public static class RegularScrollable extends RecyclerView.ViewHolder {

        private TextView title;
        private RecyclerView container;
        private RecyclerViewAdapterStoreRow adapter;

        public RegularScrollable(View itemView) {
            super(itemView);
            adapter = new RecyclerViewAdapterStoreRow();
            LinearLayoutManager mLayout = new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.HORIZONTAL, false);
            container = (RecyclerView) itemView.findViewById(R.id.view3);
            container.setLayoutManager(mLayout);
            container.setAdapter(adapter);
            container.setNestedScrollingEnabled(false);

            container.addOnScrollListener(new EndlessRecyclerOnScrollListener(mLayout) {
                @Override
                public void onLoadMore(int current_page) {
                    // TODO Request data from server and display a loading
                    // animation.
                }
            });
            title = (TextView) itemView.findViewById(R.id.textView8);
        }

        /**
         * Changes the look of the view for displaying.
         *
         * @param row Row to display.
         */
        public void prepareForDisplay(StoreRow row) {
            ((RecyclerViewAdapterStoreRow) container.getAdapter()).setData(row.items);
            title.setText(row.getString(StoreRow.TITLE));
            container.getAdapter().notifyDataSetChanged();
        }
    }

    /**
     * Holds View's of a ViewGroup that need changing when a View is recycled.
     * <p/>
     * Upon recycling a view, ViewHolder is tasked to change the necessary
     * information within a view so it can be repurposed to show data at a
     * different index.
     * <p/>
     * ViewHolder contains references to all View's which need changing. This
     * is so to reduce overhead created by repeated findViewById() calls.
     * <p/>
     * This specific ViewHolder represents a single big item.
     */
    public static class BigPromotion extends RecyclerView.ViewHolder {
        List<Button> viewList;
        public BigPromotion(View itemView) {
            super(itemView);
            final String[] jobTitles=itemView.getResources().getStringArray(R.array.random_job_title);
            viewList=new ArrayList<Button>(0);
            viewList.addAll(getViewsByTag((ViewGroup)itemView.getRootView(),"ballText"));
            for(int i=0;i<viewList.size();i++){
                viewList.get(i).setText(jobTitles[i]+"");
            }



        }

        public List<Button> getViewList() {
            return viewList;
        }

        private ArrayList<Button> getViewsByTag(ViewGroup root, String tag) {
            ArrayList<Button> views = new ArrayList<Button>();
            final int childCount = root.getChildCount();
            for (int i = 0; i < childCount; i++) {
                final View child = root.getChildAt(i);
                if (child instanceof ViewGroup) {
                    views.addAll(getViewsByTag((ViewGroup) child, tag));
                }

                final Object tagObj = child.getTag();
                if (tagObj != null && tagObj.equals(tag)) {
                    views.add((Button) child);
                }

            }
            return views;
        }
    }
}

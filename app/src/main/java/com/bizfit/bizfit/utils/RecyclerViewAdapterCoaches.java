package com.bizfit.bizfit.utils;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bizfit.bizfit.R;
import com.bizfit.bizfit.scrollCoordinators.EndlessRecyclerOnScrollListener;

import java.util.LinkedList;
import java.util.List;

/**
 * Manages the content in TabCoaches.
 */
public class RecyclerViewAdapterCoaches extends RecyclerView.Adapter {

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
     *
     * TODO  more flexiblw data structure
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

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh = null;

        switch (viewType) {
            case BIG_PROMOTION:
                View v1 = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_store_big_promotion, parent, false);
                vh = new BigPromotion(v1);
                break;

            case REGULAR_SCROLLABLE:
                View v2 = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_store_row_regular, parent, false);
                vh = new RegularScrollable(v2);
                break;
        }

        return vh;
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
        data.add(row);
    }

    @Override
    public int getItemViewType(int position) {
        return (position == 0) ? BIG_PROMOTION : REGULAR_SCROLLABLE;
    }

    @Override
    public int getItemCount() {
        return (data != null) ? data.size() + 1 : 1;
    }

    /**
     * Describes a single scrollable row within the RecyclerView.
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
                    if (adapter.getItemCount() < 30) {
                        adapter.addData(new StoreRow.StoreItem("name"));
                    }

                    adapter.notifyDataSetChanged();
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
            title.setText(row.title);
            container.getAdapter().notifyDataSetChanged();
        }
    }

    public static class BigPromotion extends RecyclerView.ViewHolder {

        public BigPromotion(View itemView) {
            super(itemView);
        }
    }
}

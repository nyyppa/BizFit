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


public class RecyclerViewAdapterCoaches extends RecyclerView.Adapter {

    private List<StoreRow> data;

    public RecyclerViewAdapterCoaches() {
        this.data = new LinkedList<>();
    }

    public RecyclerViewAdapterCoaches(List<StoreRow> data) {
        this.data = data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_store_row, parent, false);
        ViewHolderStoreRow vh = new ViewHolderStoreRow(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((ViewHolderStoreRow)holder).prepareForDisplay(data.get(position));
    }

    public void addData(StoreRow row) {
        data.add(row);
    }

    @Override
    public int getItemCount() {
        return (data != null) ? data.size() : 0;
    }

    private class ViewHolderStoreRow extends RecyclerView.ViewHolder {
        private TextView title;
        private RecyclerView container;
        private RecyclerViewAdapterStoreRow adapter;

        public ViewHolderStoreRow(View itemView) {
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
            title = (TextView)itemView.findViewById(R.id.textView8);
        }

        public void prepareForDisplay(StoreRow row) {
            ((RecyclerViewAdapterStoreRow)container.getAdapter()).setData(row.items);
            title.setText(row.title);
            container.getAdapter().notifyDataSetChanged();
        }
    }
}

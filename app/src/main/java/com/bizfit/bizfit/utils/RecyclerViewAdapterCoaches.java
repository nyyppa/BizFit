package com.bizfit.bizfit.utils;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bizfit.bizfit.R;

import java.util.LinkedList;
import java.util.List;


public class RecyclerViewAdapterCoaches extends RecyclerView.Adapter {

    List<StoreRow> data;

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

    @Override
    public int getItemCount() {
        return (data != null) ? data.size() : 0;
    }

    private class ViewHolderStoreRow extends RecyclerView.ViewHolder {
        private TextView title;
        private RecyclerView container;

        public ViewHolderStoreRow(View itemView) {
            super(itemView);
            container = (RecyclerView) itemView.findViewById(R.id.view3);
            container.setLayoutManager(new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.HORIZONTAL, false));
            container.setAdapter(new RecyclerViewAdapterStoreRow());
            container.setNestedScrollingEnabled(false);
            title = (TextView)itemView.findViewById(R.id.textView8);
        }

        public void prepareForDisplay(StoreRow row) {
            ((RecyclerViewAdapterStoreRow)container.getAdapter()).setData(row.items);
            title.setText(row.title);
            container.getAdapter().notifyDataSetChanged();
        }
    }
}

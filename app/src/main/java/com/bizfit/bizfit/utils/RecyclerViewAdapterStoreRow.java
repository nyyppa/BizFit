package com.bizfit.bizfit.utils;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bizfit.bizfit.R;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by Käyttäjä on 26.4.2016.
 */
public class RecyclerViewAdapterStoreRow extends RecyclerView.Adapter {

    private List<StoreRow.StoreItem> data;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_store_item, parent, false);
        ViewHolderStoreItem vh = new ViewHolderStoreItem(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((ViewHolderStoreItem) holder).prepForDisplay(data.get(position));
    }

    @Override
    public int getItemCount() {
        return (data != null) ? data.size() : 0;
    }

    public void appendData(StoreRow.StoreItem item) {
        data.add(item);
    }

    public void setData(List<StoreRow.StoreItem> data) {
        this.data = data;
    }

    public class ViewHolderStoreItem extends RecyclerView.ViewHolder {
        private TextView title;

        public ViewHolderStoreItem(View itemView) {
            super(itemView);
            title = (TextView)itemView.findViewById(R.id.textView9);
        }

        public void prepForDisplay(StoreRow.StoreItem item) {
            title.setText(item.name);
        }
    }
}

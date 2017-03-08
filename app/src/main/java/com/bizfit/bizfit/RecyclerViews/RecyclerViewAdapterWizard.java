package com.bizfit.bizfit.RecyclerViews;

/**
 * Created by iipa on 7.3.2017.
 */

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bizfit.bizfit.R;

import java.util.ArrayList;

public class RecyclerViewAdapterWizard extends RecyclerView.Adapter<RecyclerViewAdapterWizard.MyViewHolder>{

    private ArrayList<View> mViews;

    public RecyclerViewAdapterWizard(ArrayList<View> views) {
            mViews = views;
    }

    @Override
    public RecyclerViewAdapterWizard.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflatedView = null;
        if(viewType == 1) {
            inflatedView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_wizard_message_sent, parent, false);
        } else {
            inflatedView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_wizard_message_received, parent, false);
        }
        return new MyViewHolder(inflatedView);
    }

    @Override
    public void onBindViewHolder(RecyclerViewAdapterWizard.MyViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return mViews.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public MyViewHolder(View v) {
            super(v);
        }

        @Override
        public void onClick(View v) {

        }
    }
}

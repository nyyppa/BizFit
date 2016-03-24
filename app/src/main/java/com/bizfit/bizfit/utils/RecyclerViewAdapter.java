package com.bizfit.bizfit.utils;


import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bizfit.bizfit.R;
import com.bizfit.bizfit.Tracker;
import com.bizfit.bizfit.fragments.TabTrackables;

public class RecyclerViewAdapter extends RecyclerView.Adapter {

    private int position;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_trackable_main, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((RecyclerViewAdapter.ViewHolder) holder)
                .fillInfo(TabTrackables.trackers[position]);
    }

    @Override
    public int getItemCount() {
        return TabTrackables.trackers.length;
    }

    public int getPosition() {
        return position;
    }

    protected void setPosition(int position) {
        this.position = position;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener, View.OnClickListener {

        private View v;

        public ViewHolder(View itemView) {
            super(itemView);
            this.v = itemView;
            v.setOnCreateContextMenuListener(this);
            v.setOnClickListener(this);
            v.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    setPosition(getAdapterPosition());
                    return false;
                }
            });
        }

        // TODO Make this async task!
        public ViewHolder fillInfo(Tracker data) {

            TextView trackerName = (TextView) v.findViewById(R.id.tracker_name);
            TextView targetAmount = (TextView) v.findViewById(R.id.tracker_target);
            ((CardView) v.findViewById(R.id.card_view)).setCardBackgroundColor(data.getColor());
            //TextView timeLeftAmount = (TextView) v.findViewById(R.id.time_left_amount);
            //TextView progressPercent = (TextView) v.findViewById(R.id.progress_percentage);

            trackerName.setTypeface(AssetManagerOur.getFont(AssetManagerOur.medium));
            targetAmount.setTypeface(AssetManagerOur.getFont(AssetManagerOur.regular));
            //timeLeftAmount.setTypeface(AssetManagerOur.getFont(AssetManagerOur.regular));
            //progressPercent.setTypeface(AssetManagerOur.getFont(AssetManagerOur.regular));
            //((TextView) v.findViewById(R.id.target_label)).setTypeface(AssetManagerOur.getFont(AssetManagerOur.regular));
            //((TextView) v.findViewById(R.id.time_left_label)).setTypeface(AssetManagerOur.getFont(AssetManagerOur.regular));


            //Tracker.RemainingTime time = data.getTimeRemaining();
            //timeLeftAmount.setText((int) time.getTimeRemaining() + " " + time.getTimeType());
            trackerName.setText(data.getName());
            //trackerName.setTextColor(data.getColor());
            targetAmount.setText((int) data.getTargetProgress() + "");
            //progressPercent.setText(((int) (Math.floor(data.getProgressPercent() * 100))) + "");

            return this;
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle(((TextView) (v.findViewById(R.id.tracker_name))).getText());
            (menu.add(Menu.NONE, TabTrackables.deleteID
                    , Menu.NONE
                    , v.getResources().getString(R.string.action_delete))).setActionView(v);
        }

        @Override
        public void onClick(View v) {
            Context c;
            if ((c = v.getContext()) instanceof RecyclerViewItemClicked)
                ((RecyclerViewItemClicked) c).onItemClick(this);
        }

        public View getV() {
            return v;
        }
    }

    public interface RecyclerViewItemClicked {
        void onItemClick(RecyclerView.ViewHolder vh);
    }
}

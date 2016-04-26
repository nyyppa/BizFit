package com.bizfit.bizfit.utils;


import android.content.Context;
import android.graphics.PorterDuff;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bizfit.bizfit.R;
import com.bizfit.bizfit.Tracker;
import com.bizfit.bizfit.fragments.TabTrackables;

/**
 * Manages binding of View's of RecyclerView within Trackers tab.
 */
public class RecyclerViewAdapterTrackers extends RecyclerView.Adapter {

    /**
     * Position of the last long clicked item within the RecyclerView.
     */
    private int position;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_trackable, parent, false);
        ViewHolderTracker vh = new ViewHolderTracker(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((ViewHolderTracker) holder)
                .prepareForDisplay(TabTrackables.trackers[position]);
    }

    @Override
    public int getItemCount() {
        return (TabTrackables.trackers != null) ? TabTrackables.trackers.length : 0;
    }

    /**
     * Gets the position of last clicked element.
     *
     * @return
     */
    public int getPosition() {
        return position;
    }

    /**
     * Sets the position of last clicked element.
     *
     * @param position Element's position.
     */
    protected void setPosition(int position) {
        this.position = position;
    }


    /**
     * Holds View's of a ViewGroup that need changing when a View is recycled.
     *
     * Used to store references to View's components for easier recycling.
     * This way costly findViewById() calls can be avoided when the view
     * is relocated within it's containing RecyclerView.
     */
    public class ViewHolderTracker extends RecyclerView.ViewHolder implements
            View.OnCreateContextMenuListener, View.OnClickListener {

        /**
         * View whose look ViewHolderTracker manages.
         */
        private View v;

        /**
         * Component which displays Tracker name.
         */
        private TextView trackerName;

        /**
         * Component which displays notes associated with Tracker.
         */
        private TextView notes;

        /**
         * Component which displays daily target.
         */
        private TextView progress;

        /**
         * Component which is shown when daily target is met.
         */
        private ImageView done;

        /**
         * Background for progress.
         */
        private ImageView bg;

        /**
         * Constructs a new ViewHolderTracker with references to alterable Views.
         *
         * Also performs one time styling events. Namely, fonts.
         *
         * @param itemView View that is styled on recycle.
         */
        public ViewHolderTracker(View itemView) {
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

            trackerName = (TextView) v.findViewById(R.id.view_trackable_tracker_name);
            trackerName.setTypeface(AssetManagerOur.getFont(AssetManagerOur.medium));

            notes = (TextView) v.findViewById(R.id.view_trackable_notes);
            notes.setTypeface(AssetManagerOur.getFont(AssetManagerOur.regular));

            progress = (TextView) v.findViewById(R.id.view_trackable_progress_needed);
            progress.setTypeface(AssetManagerOur.getFont(AssetManagerOur.medium));

            done = (ImageView) v.findViewById(R.id.view_trackable_done);

            bg = (ImageView) v.findViewById(R.id.view_trackable_progress_needed_bg);
        }

        /**
         * Adjusts the View's look when it's relocated within RecyclerVew.
         *
         * @param data Tracker from which information is pulled from.
         */
        public void prepareForDisplay(Tracker data) {
            trackerName.setText(data.getName());
            bg.getDrawable().mutate().setColorFilter(data.getColor(), PorterDuff.Mode.SRC_OVER);

            /**
             * Tracker is not able to provide info if daily goal has been met.
             * Pseudo code for two distinct outlooks for completed and uncompleted states.
             *
             * Notes functionality not yet implemented.
             * notes.setVisibility(((data.hasNotes) ? View.VISIBLE : View.GONE));
             *
             * if (dailyGoalMet) {
             *      bg.getDrawable().mutate.setColorFilter(*completed color*, PorterDuff.Mode.SRC_OVER);
             *      trackerName.setColor(*completed color*);
             *      notes.setColor(*completed color*);
             *      progress.setVisibility(View.GONE);
             *      done.setVisibility(View.VISIBLE);
             * } else {
             *      bg.getDrawable().mutate().setColorFilter(data.getColor(), PorterDuff.Mode.SRC_OVER);
             *      trackerName.setColor(black87);
             *      notes.setColor(black54);
             *      progress.setVisibility(View.VISIBLE);
             *      done.setVisibility(View.GONE);
             * }
             */
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle(((TextView) (v.findViewById(R.id.view_trackable_tracker_name))).getText());
            (menu.add(Menu.NONE, TabTrackables.DELETE_ID
                    , Menu.NONE
                    , v.getResources().getString(R.string.action_delete))).setActionView(v);
        }

        @Override
        public void onClick(View v) {
            Context c;

            // Initiate callback if interface is implemented.
            if ((c = v.getContext()) instanceof RecyclerViewItemClicked)
                ((RecyclerViewItemClicked) c).onItemClick(this);
        }

        /**
         * Gets the View associated with this ViewHolderTracker.
         *
         * @return ViewHolderTracker's View.
         */
        public View getV() {
            return v;
        }
    }

    /**
     * Callback interface for when RecyclerView item is interacted with.
     */
    public interface RecyclerViewItemClicked {

        /**
         * Notifies which item was clicked.
         *
         * @param vh ViewHolderTracker associated with the click event.
         */
        void onItemClick(RecyclerView.ViewHolder vh);
    }
}

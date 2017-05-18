package com.bizfit.bizfitUusYritysKeskusAlpha.RecyclerViews;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bizfit.bizfitUusYritysKeskusAlpha.R;
import com.bizfit.bizfitUusYritysKeskusAlpha.utils.StoreRow;

import java.util.List;

/**
 * Represents a single horizontally scrollable store row.
 * <p/>
 * The row displays a series of items which the use can click to access more
 * in depth information about. A single item is a "card" of a coach from whom
 * the user can buy services.
 */
public class RecyclerViewAdapterStoreRow extends RecyclerView.Adapter {

    /**
     * Set of items to show.
     */
    protected List<StoreRow.StoreItem> data;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_store_item, parent, false);

        ViewHolderStoreItem vh = new ViewHolderStoreItem(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((ViewHolderStoreItem) holder).prepForDisplay(data.get(position), position);
    }

    @Override
    public int getItemCount() {
        return (data != null) ? data.size() : 0;
    }

    /**
     * Appends a new StoreItem.
     *
     * @param item A StoreItem to display.
     */
    public void addData(StoreRow.StoreItem item) {
        data.add(item);
    }

    /**
     * Sets a whole new data set to be displayed.
     *
     * @param data A new set of StoreItems to display.
     */
    public void setData(List<StoreRow.StoreItem> data) {
        this.data = data;
    }

    /**
     * Callback interface for when RecyclerView item is interacted with.
     */
    public interface StoreItemClicked {

        /**
         * Notifies which item was clicked.
         *
         * @param viewHolderStoreItem ViewHolderTracker associated with the
         *                            click event.
         */
        void itemClicked(StoreRow.StoreItem viewHolderStoreItem);
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
     */
    public class ViewHolderStoreItem extends RecyclerView.ViewHolder implements View.OnClickListener {

        /**
         * Is a placeholder TextView to test functionality.
         */
        private TextView name;
        private ImageView image;
        private int index;
        private TextView testimonials;

        public ViewHolderStoreItem(View itemView)
        {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.coach_item_name);
            image = (ImageView) itemView.findViewById(R.id.iVCoach);
            //testimonials = (TextView) itemView.findViewById(R.id.tVTestimonials);
            //RelativeLayout layout=(RelativeLayout)itemView.findViewById(R.id.store_item_layout);
            ConstraintLayout layout = (ConstraintLayout) itemView.findViewById(R.id.store_item_layout);
            ViewGroup.LayoutParams params=layout.getLayoutParams();
            params.height = (int)(itemView.getResources().getConfiguration().fontScale*params.height);
            itemView.setOnClickListener(this);
        }

        /**
         * Prepares the Views for displaying.
         *
         * @param item Item to display.
         */
        protected void prepForDisplay(StoreRow.StoreItem item, int index)
        {
            this.index = index;
            name.setText(item.name);
            image.setImageDrawable(item.image);
            //testimonials.setText(item.testimonials +"");
        }

        public String getName() {
            return ((String) name.getText());
        }

        public Drawable getImage() {
            return image.getDrawable();
        }

        @Override
        public void onClick(View v) {
            Context mContext = v.getContext();
            if (mContext instanceof StoreItemClicked)
                ((StoreItemClicked) mContext).itemClicked(RecyclerViewAdapterStoreRow.this.data.get(index));
        }
    }
}

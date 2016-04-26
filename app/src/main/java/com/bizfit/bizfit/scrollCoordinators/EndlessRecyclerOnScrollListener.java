package com.bizfit.bizfit.scrollCoordinators;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * Enables endless scrolling for RecyclerView.
 *
 * Courtesy of GitHub user ssinss. Code is as is, but with added commenting.
 * In the future most likely will be expanded to enable endless scroll in both
 * directions to enable the use of RecyclerView as a custom "slider selector".
 */
public abstract class EndlessRecyclerOnScrollListener extends RecyclerView.OnScrollListener {

    /**
     * The total number of items in the dataset after the last load.
     */
    private int previousTotal = 0;

    /**
     * True if we are still waiting for the last set of data to load
     */
    private boolean loading = true;

    /**
     * The minimum amount of items to have below your current scroll position before loading more.
     */
    private int visibleThreshold = 0;

    /**
     * Used to determine if more content should be loaded.
     */
    int firstVisibleItem, visibleItemCount, totalItemCount;

    private int current_page = 1;

    /**
     * LayoutManager of the RecyclerView this listener is attached to.
     */
    private LinearLayoutManager mLinearLayoutManager;

    public EndlessRecyclerOnScrollListener(LinearLayoutManager linearLayoutManager) {
        this.mLinearLayoutManager = linearLayoutManager;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        visibleItemCount = recyclerView.getChildCount();
        totalItemCount = mLinearLayoutManager.getItemCount();
        firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition();

        if (loading) {
            // Done loading.
            if (totalItemCount > previousTotal) {
                loading = false;
                previousTotal = totalItemCount;
            }
        }

        // Begin loading if bottom of the page is reached.
        if (!loading && (totalItemCount - visibleItemCount)
                <= (firstVisibleItem + visibleThreshold)) {
            current_page++;
            onLoadMore(current_page);
            loading = true;
        }
    }

    /**
     * Signals that more content needs to be loaded.
     *
     * @param current_page Which page needs to be loaded next.
     */
    public abstract void onLoadMore(int current_page);
}

package com.bizfit.bizfit.utils;

import android.os.AsyncTask;

import com.bizfit.bizfit.tracker.Tracker;

public class TrackerLoader extends AsyncTask<Void, Void, Void> {

    private OnFinishListener listener;
    private Tracker[] trackers;


    public TrackerLoader(OnFinishListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        // TODO display loading icon
    }

    @Override
    protected Void doInBackground(Void... params) {
        // TODO load trackers from database
        return null;
    }
    
    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (listener != null) listener.onFinish(trackers);
    }

    /**
     * Notifies listeners of task being complete.
     */
    public interface OnFinishListener {
        /**
         * Is called when task if finished.
         * 
         * @param trackers Loaded trackers.
         */
        void onFinish(Tracker[] trackers);
    }
}

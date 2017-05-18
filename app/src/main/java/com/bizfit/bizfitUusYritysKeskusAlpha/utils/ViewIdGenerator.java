package com.bizfit.bizfitUusYritysKeskusAlpha.utils;

import android.annotation.SuppressLint;
import android.os.Build;
import android.view.View;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by iipa on 13.3.2017.
 */

public class ViewIdGenerator {
    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);

    @SuppressLint("NewApi")
    public static int generateViewId() {

        int id = -1;
        boolean success = false;

        if (Build.VERSION.SDK_INT < 17) {
            while (!success) {
                final int result = sNextGeneratedId.get();
                // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
                int newValue = result + 1;
                if (newValue > 0x00FFFFFF)
                    newValue = 1; // Roll over to 1, not 0.
                if (sNextGeneratedId.compareAndSet(result, newValue)) {
                    id = result;
                    success = true;
                }
            }
        } else {
            id = View.generateViewId();
        }

        return id;

    }
}

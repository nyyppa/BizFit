package com.bizfit.bizfit;

import android.app.Application;
import android.content.Context;

import org.acra.*;
import org.acra.annotation.*;

/**
 * Created by attey on 06/04/2017.
 */

@ReportsCrashes(
        mailTo = "atte.yliverronen@gmail.com",
        mode = ReportingInteractionMode.TOAST,
        resToastText = R.string.crash_toast_text
)
public class MyApplication extends Application {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        // The following line triggers the initialization of ACRA
        ACRA.init(this);
    }
}

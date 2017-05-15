package com.bizfit.bizfit;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.support.multidex.MultiDex;

import org.acra.*;
import org.acra.annotation.*;
import org.acra.sender.HttpSender;

import java.util.Locale;

/**
 * Created by attey on 06/04/2017.
 */



@ReportsCrashes
(
        httpMethod = HttpSender.Method.PUT,
        reportType = HttpSender.Type.JSON,
        formUri = "http://51.15.37.28:5984/acra-myapp/_design/acra-storage/_update/report",
        formUriBasicAuthLogin = "CrashReport1",
        formUriBasicAuthPassword = "Crash1",
        mode = ReportingInteractionMode.DIALOG,
        resToastText = R.string.crash_toast_text, // optional, displayed as soon as the crash occurs, before collecting data which can take a few seconds
        resDialogText = R.string.crash_dialog_text,
        resDialogIcon = android.R.drawable.ic_dialog_info, //optional. default is a warning sign
        resDialogTitle = R.string.crash_dialog_title, // optional. default is your application name
        resDialogCommentPrompt = R.string.crash_dialog_comment_prompt, // optional. When defined, adds a user text field input with this text resource as a label
        resDialogOkToast = R.string.crash_dialog_ok_toast // optional. displays a Toast message when the user accepts to send a report.
)
public class MyApplication extends Application {
    static Context baseContext;
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        baseContext=base;
        MultiDex.install(this);
        // The following line triggers the initialization of ACRA
        ACRA.init(this);
    }

    @TargetApi(Build.VERSION_CODES.N)
    public static Locale getCurrentLocale(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            return baseContext.getResources().getConfiguration().getLocales().get(0);
        } else{
            //noinspection deprecation
            return baseContext.getResources().getConfiguration().locale;
        }
    }
    public static Context getContext()
    {
        return baseContext;
    }
}

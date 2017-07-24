package com.bizfit.bizfitUusYritysKeskusAlpha;

import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import static android.view.MotionEvent.ACTION_DOWN;
import static org.acra.ACRA.LOG_TAG;

/**
 * Created by attey on 17/07/2017.
 */

public class OurVoiceRecorder implements View.OnTouchListener {
    View view;
    private MediaRecorder mRecorder = null;
    public void OurVoiceRecorder(View view){
        this.view=view;
    }



    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()){
            case ACTION_DOWN:
                startRecording();
                return true;
            case MotionEvent.ACTION_MOVE:
                return true;
            case MotionEvent.ACTION_UP:
                stopRecording();
                return true;
        }
        return false;
    }
    private void startRecording()
    {
        try {
            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            File file = File.createTempFile(System.currentTimeMillis()+"3gp",null,MyApplication.getContext().getFilesDir());
            mRecorder.setOutputFile(file.getPath());
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        mRecorder.start();
    }
    private void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
    }
}

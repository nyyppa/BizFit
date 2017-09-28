package com.bizfit.release.chat.MessageObjects;

import android.content.Context;
import android.media.MediaPlayer;
import android.view.View;

import com.bizfit.release.chat.FileInfo;
import com.bizfit.release.chat.Message;
import com.bizfit.release.network.FileDownload.FileDownLoader;
import com.bizfit.release.utils.Constants;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by attey on 24/07/2017.
 */

public class VoiceMessageObject extends MessageObject {
    MediaPlayer mediaPlayer;

    public VoiceMessageObject(FileInfo fileInfo) {
        super(fileInfo);
    }


    @Override
    public void downLoadFile() {
        try {
            new FileDownLoader() {
                @Override
                public void onProgressUpdate(Integer... progress) {

                }

                @Override
                public String getFileID() {
                    return fileInfo.fileID;
                }

                @Override
                public void doResult(File result) {
                    path=result.getPath();
                }

                @Override
                public void error(Exception e) {

                }
            }.execute(new URL(Constants.connection_address));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void openFile(Context context) {
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(path);
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener(){
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    System.gc();
                }

                @Override
                protected void finalize() throws Throwable {
                    super.finalize();
                }
            });
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }



    @Override
    public Message.Status getStatus() {
        return null;
    }

    @Override
    public void setStatus(Message.Status status) {

    }

    @Override
    public String getText() {
        return null;
    }

    @Override
    public void setPreview(View view) {

    }
}

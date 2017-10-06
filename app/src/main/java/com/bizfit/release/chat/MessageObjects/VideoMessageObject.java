package com.bizfit.release.chat.MessageObjects;

import android.content.Context;
import android.view.View;

import com.bizfit.release.chat.FileInfo;
import com.bizfit.release.chat.Message;

/**
 * Created by attey on 24/07/2017.
 */

public class VideoMessageObject extends MessageObject {

    public VideoMessageObject(FileInfo fileInfo) {
        super(fileInfo);
    }

    public void downLoadFile() {

    }

    @Override
    public void openFile(Context context) {
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

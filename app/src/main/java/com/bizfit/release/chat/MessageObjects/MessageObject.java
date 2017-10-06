package com.bizfit.release.chat.MessageObjects;

import android.content.Context;
import android.view.View;

import com.bizfit.release.chat.FileInfo;
import com.bizfit.release.chat.Message;

/**
 * Created by attey on 24/07/2017.
 */

public abstract class MessageObject{
    FileInfo fileInfo;
    String path;
    public MessageObject(FileInfo fileInfo){
        this.fileInfo=fileInfo;
    }
    public abstract void downLoadFile();
    public abstract void openFile(Context context);
    public abstract Message.Status getStatus();
    public abstract void setStatus(Message.Status status);
    public abstract String getText();
    public abstract void setPreview(View view);
}

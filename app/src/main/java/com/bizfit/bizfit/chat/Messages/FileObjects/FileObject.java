package com.bizfit.bizfit.chat.Messages.FileObjects;

import android.graphics.drawable.Drawable;

import com.bizfit.bizfit.chat.Message;
import com.bizfit.bizfit.chat.Messages.Message2;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Atte Ylivrronen on 11.9.2017.
 */

public abstract class FileObject <T>{
    Drawable thumpnail;
    String fileName;
    T file;
    String fileID;
    Message2 message;
    public FileObject(Drawable thumpnail, String fileName, T file, Message2 message){
        this.thumpnail=thumpnail;
        this.fileName=fileName;
        this.file=file;
        this.message=message;
    }
    public FileObject(String fileID,Message2 message)
    {
        this.fileID=fileID;
        this.message=message;
    }
    public FileObject(JSONObject jsonObject,Message2 message)
    {
        this.message=message;
        try {
            fileID=jsonObject.getString(getFileID());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public T getFile()
    {
        return file;
    }

    public Drawable getThumpnail() {
        return thumpnail;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFileID() {
        return fileID;
    }
    public JSONObject toJSON()
    {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("fileid",fileID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
}

package com.bizfit.release.chat.Messages.FileObjects;

import android.graphics.drawable.Drawable;

import com.bizfit.release.chat.Messages.Message2;
import com.bizfit.release.network.FileDownload.DrawableDownloader;
import com.bizfit.release.network.FileUpload.DrawableUploader;
import com.bizfit.release.network.FileUpload.FileUpload;
import com.bizfit.release.utils.Constants;

import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Atte Ylivrronen on 11.9.2017.
 */

public class DrawableObject  extends FileObject<Drawable>  {

    public DrawableObject(Drawable thumpnail, String fileName, Drawable file, Message2 message) {
        super(thumpnail, fileName, file, message);
        uploadDrawable();
    }

    public DrawableObject(String fileID, Message2 message) {
        super(fileID, message);
        downloadDrawable(fileID);
    }

    public DrawableObject(JSONObject jsonObject, Message2 message) {
        super(jsonObject, message);
        downloadDrawable(fileID);
    }

    private void uploadDrawable()
    {
        new DrawableUploader() {
            @Override
            public void onPostExecute(FileUpload.ResultCode result) {
                fileID=this.getFileID();
            }

            @Override
            public String getFileName() {
                return fileName;
            }

            @Override
            public String getFileType() {
                return "image";
            }

            @Override
            public String getFileExtension() {
                return null;
            }

            @Override
            public String getUploader() {
                return message.getSender();
            }
        }.execute(new Drawable[]{file});
    }

    private void downloadDrawable(String fileID)
    {
        this.fileID=fileID;
        URL[] urls=new URL[1];
        try {
            urls[0]=new URL(Constants.file_connection_address);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        new DrawableDownloader() {
            @Override
            public void onProgressUpdate(Integer... progress) {

            }

            @Override
            public String getFileID() {
                return DrawableObject.this.fileID;
            }

            @Override
            public void doResult(Drawable result) {
                file=result;
            }

            @Override
            public void error(Exception e) {

            }
        }.execute(urls);
    }
}

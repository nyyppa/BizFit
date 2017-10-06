package com.bizfit.release.network.FileUpload;

import android.graphics.drawable.Drawable;

import com.bizfit.release.utils.Utils;

/**
 * Created by attey on 05/07/2017.
 */

public abstract class DrawableUploader {
    PictureUploader pictureUploader;
    public void execute(Drawable [] drawable)
    {
        pictureUploader=new PictureUploader() {
            @Override
            protected void onPostExecute(ResultCode result) {
                DrawableUploader.this.onPostExecute(result);
            }

            @Override
            public String getFileName() {
                return DrawableUploader.this.getFileName();
            }

            @Override
            public String getFileExtension() {
                return  DrawableUploader.this.getFileExtension();
            }

            @Override
            public String getUploader() {
                return DrawableUploader.this.getUploader();
            }

            @Override
            public String getFileType() {
                return DrawableUploader.this.getFileType();
            }
        };
        pictureUploader.execute(Utils.drawableToBitmap(drawable[0]));
    }
    public abstract void onPostExecute(FileUpload.ResultCode result);
    public abstract String getFileName();
    public abstract String getFileType();
    public abstract String getFileExtension();
    public abstract String getUploader();
    public String getFileID()
    {
        return pictureUploader.getFileID();
    }

}

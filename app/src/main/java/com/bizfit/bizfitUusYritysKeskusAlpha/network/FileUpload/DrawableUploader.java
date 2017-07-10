package com.bizfit.bizfitUusYritysKeskusAlpha.network.FileUpload;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import com.bizfit.bizfitUusYritysKeskusAlpha.utils.Utils;

import java.io.File;

/**
 * Created by attey on 05/07/2017.
 */

public abstract class DrawableUploader {

    public void execute(Drawable [] drawable)
    {
        new PictureUploader() {
            @Override
            protected void onPostExecute(ResultCode result) {
                DrawableUploader.this.onPostExecute(result);
            }
        }.execute(Utils.drawableToBitmap(drawable[0]));
    }
    protected abstract void onPostExecute(FileUpload.ResultCode result);
}

package com.bizfit.bizfitUusYritysKeskusAlpha.network;

import android.graphics.drawable.Drawable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Created by Atte Ylivrronen on 16.6.2017.
 */

public abstract class DrawableDownloader extends DownloadFile<Drawable> {
    @Override
    protected void onPostExecute(File result) {
        try {
            doResult(Drawable.createFromStream( new FileInputStream(result),null));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}

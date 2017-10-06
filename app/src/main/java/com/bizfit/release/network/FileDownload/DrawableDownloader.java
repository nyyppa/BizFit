package com.bizfit.release.network.FileDownload;

import android.graphics.drawable.Drawable;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Created by Atte Ylivrronen on 16.6.2017.
 */

public abstract class DrawableDownloader extends DownloadFile<Drawable> {



    @Override
    protected void onPostExecute(DownloadFile.FileResult result) {
        try {
            doResult(Drawable.createFromStream( new FileInputStream(result.getResult()),"aaa"));
        } catch (FileNotFoundException e) {
            error(e);
        } catch (Exception e) {
            error(e);
        }
    }

}

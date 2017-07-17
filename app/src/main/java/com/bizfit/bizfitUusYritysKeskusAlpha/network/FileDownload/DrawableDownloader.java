package com.bizfit.bizfitUusYritysKeskusAlpha.network.FileDownload;

import android.graphics.drawable.Drawable;

import com.bizfit.bizfitUusYritysKeskusAlpha.network.FileDownload.DownloadFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Created by Atte Ylivrronen on 16.6.2017.
 */

public abstract class DrawableDownloader extends DownloadFile<Drawable> {



    @Override
    protected void onPostExecute(FileResult result) {
        try {
            doResult(Drawable.createFromStream( new FileInputStream(result.getResult()),null));
        } catch (FileNotFoundException e) {
            error(e);
        } catch (Exception e) {
            error(e);
        }
    }

}

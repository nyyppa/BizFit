package com.bizfit.bizfitUusYritysKeskusAlpha.network.FileDownload;

import com.bizfit.bizfitUusYritysKeskusAlpha.network.FileDownload.DownloadFile;

import java.io.File;

/**
 * Created by Atte Ylivrronen on 16.6.2017.
 */

public abstract class FileDownLoader extends DownloadFile<File> {






    @Override
    protected void onPostExecute(FileResult result) {
        try {
            doResult(result.getResult());
        } catch (Exception e) {
            error(e);
        }
    }

}

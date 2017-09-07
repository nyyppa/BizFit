package com.bizfit.bizfit.network.FileDownload;

import java.io.File;

/**
 * Created by Atte Ylivrronen on 16.6.2017.
 */

public abstract class FileDownLoader extends DownloadFile<File> {






    @Override
    protected void onPostExecute(DownloadFile.FileResult result) {
        try {
            doResult(result.getResult());
        } catch (Exception e) {
            error(e);
        }
    }

}

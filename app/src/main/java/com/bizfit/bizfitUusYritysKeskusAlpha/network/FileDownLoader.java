package com.bizfit.bizfitUusYritysKeskusAlpha.network;

import java.io.File;

/**
 * Created by Atte Ylivrronen on 16.6.2017.
 */

public abstract class FileDownLoader extends DownloadFile <File>{


    @Override
    protected void onPostExecute(File result) {
        doResult(result);
    }

}
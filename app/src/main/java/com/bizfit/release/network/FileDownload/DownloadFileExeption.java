package com.bizfit.release.network.FileDownload;

/**
 * Created by attey on 17/07/2017.
 */

public class DownloadFileExeption extends Exception{
    public DownloadFileExeption(String message)
    {
        super(message);
    }
    public DownloadFileExeption(String message, Throwable throwable) {
        super(message, throwable);
    }
    public DownloadFileExeption(Throwable cause) {
        super(cause);
        // TODO Auto-generated constructor stub
    }
}

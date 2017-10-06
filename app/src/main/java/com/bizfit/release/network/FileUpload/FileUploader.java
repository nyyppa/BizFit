package com.bizfit.release.network.FileUpload;

import java.io.File;

/**
 * Created by attey on 05/07/2017.
 */

public abstract class FileUploader extends FileUpload<File> {
    @Override
    public File convertGenerictoFile(File param) {
        return param;
    }
}

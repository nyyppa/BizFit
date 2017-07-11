package com.bizfit.bizfitUusYritysKeskusAlpha.network.FileUpload;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.bizfit.bizfitUusYritysKeskusAlpha.DebugPrinter;
import com.bizfit.bizfitUusYritysKeskusAlpha.MyApplication;
import com.bizfit.bizfitUusYritysKeskusAlpha.network.FileUpload.FileUpload;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by attey on 05/07/2017.
 */

public abstract class PictureUploader extends FileUpload<Bitmap> {

    @Override
    public File convertGenerictoFile(Bitmap param) {
        File f = null;
        try {
            f = File.createTempFile(UUID.randomUUID().toString(),null, MyApplication.getContext().getFilesDir());
            Bitmap bitmap = param;
            DebugPrinter.Debug("Tiedosto muoto:"+param.getConfig().name());
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
            FileOutputStream fos = new FileOutputStream(f);
            byte[] bitmapdata = bos.toByteArray();
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return f;
    }
}

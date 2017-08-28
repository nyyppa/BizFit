package com.bizfit.bizfitUusYritysKeskusAlpha;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.bizfit.bizfitUusYritysKeskusAlpha.network.FileDownload.DrawableDownloader;
import com.bizfit.bizfitUusYritysKeskusAlpha.utils.Constants;

import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

/**
 * Created by iipa on 25.8.2017.
 */

public class Profile {

    // TODO: add information attributes

    Drawable image;
    UUID imageID;

    // TODO: retrieve information from jsonObject
    public Profile(JSONObject jsonObject) {



    }

    public void drawToImgView(final ImageView imageView) throws MalformedURLException {

        if(image != null) {

            imageView.setImageDrawable(image);

        } else {

            new DrawableDownloader() {
                @Override
                public String getFileID() {
                    return imageID.toString();
                }

                @Override
                public void doResult(Drawable result) {
                    imageView.setImageDrawable(result);
                    image = result;
                }

                @Override
                public void error(Exception e) {

                }

            }.execute(new URL[]{new URL(Constants.connection_address)});
        }

    }

    public void downloadImage() {

    }

}

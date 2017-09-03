package com.bizfit.bizfitUusYritysKeskusAlpha;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.bizfit.bizfitUusYritysKeskusAlpha.network.FileDownload.DrawableDownloader;
import com.bizfit.bizfitUusYritysKeskusAlpha.utils.Constants;

import org.json.JSONException;
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
    UUID imageUUID;

    // TODO: retrieve information from jsonObject
    public Profile(JSONObject jsonObject) {

        try {
            // 1. tarkista löytyykö
            // 2. hae tieto

            if(jsonObject.has("firstName")) {
                String name = jsonObject.getString("firstName");
            }
            if(jsonObject.has("uuid")) {
                imageUUID = UUID.fromString(jsonObject.getString("uuid"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void drawToImgView(final ImageView imageView) throws MalformedURLException {

        if(image != null) {

            imageView.setImageDrawable(image);

        } else {

            new DrawableDownloader() {
                @Override
                public String getFileID() {
                    return imageUUID.toString();
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

}

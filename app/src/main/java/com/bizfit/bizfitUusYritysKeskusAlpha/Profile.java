package com.bizfit.bizfitUusYritysKeskusAlpha;

import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
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

    // basic info

    public String firstName;
    public String lastName;

    // profile pic info

    public boolean hasProfilePic;
    public Drawable image;
    public UUID imageUUID;

    // company info

    public boolean ownsCompany;
    public boolean askedCompany;
    public String companyName;
    public String companyField;
    public String companyDesc;

    // expert info

    public boolean isExpert;

    public Profile(JSONObject jsonObject) {

        try {
            // 1. tarkista löytyykö tieto
            // 2. hae tieto

            if(jsonObject.has(Constants.firstName)) {
                firstName = jsonObject.getString(Constants.firstName);
            }

            if(jsonObject.has(Constants.lastName)) {
                lastName = jsonObject.getString(Constants.lastName);
            }

            if(jsonObject.has(Constants.imageUUID)) {
                imageUUID = UUID.fromString(jsonObject.getString(Constants.imageUUID));
            } else {
                hasProfilePic = false;
            }

            if(jsonObject.has(Constants.ownsCompany)) {
                ownsCompany = jsonObject.getBoolean(Constants.ownsCompany);
            }

            if(jsonObject.has(Constants.askedCompany)) {
                askedCompany = jsonObject.getBoolean(Constants.askedCompany);
            }

            if(jsonObject.has(Constants.companyName)) {
                companyName = jsonObject.getString(Constants.companyName);
            }

            if(jsonObject.has(Constants.companyField)) {
                companyField = jsonObject.getString(Constants.companyField);
            }

            if(jsonObject.has(Constants.companyDesc)) {
                companyDesc = jsonObject.getString(Constants.companyDesc);
            }

            if(jsonObject.has(Constants.isExpert)) {
                isExpert = jsonObject.getBoolean(Constants.isExpert);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void drawToImgView(final ImageView imageView) {

        if(hasProfilePic) {

            if (image != null) {

                imageView.setImageDrawable(image);

            } else {

                try {
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
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }

        } else {
            imageView.setImageDrawable(ContextCompat.getDrawable(imageView.getContext(), R.drawable.general_profile));
        }

    }

}

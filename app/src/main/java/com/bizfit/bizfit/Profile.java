package com.bizfit.bizfit;

import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.widget.ImageView;
import android.widget.TextView;

import com.bizfit.bizfit.network.FileDownload.DrawableDownloader;
import com.bizfit.bizfit.utils.Constants;

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
    public String phoneNumber;

    public String profileID;

    // profile pic info

    public Drawable image;
    public UUID imageUUID;
    ImageView imageView;

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

            if(jsonObject.has(Constants.phoneNumber)) {
                phoneNumber = jsonObject.getString(Constants.phoneNumber);
            }

            if(jsonObject.has(Constants.imageUUID)) {
                imageUUID = UUID.fromString(jsonObject.getString(Constants.imageUUID));
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
            if(jsonObject.has(Constants.profile)){
                profileID=jsonObject.getString(Constants.profile);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void drawToImgView(final ImageView imageView, final TextView tv) {

        this.imageView = imageView;

        if(imageUUID != null) {

            if (image != null) {

                imageView.setImageDrawable(image);

            } else {

                try {
                    new DrawableDownloader() {
                        @Override
                        public void onProgressUpdate(Integer... progress) {
                            if(tv!=null)
                            {
                                tv.setText(String.valueOf(progress[0]));
                            }

                        }

                        @Override
                        public String getFileID() {
                            return imageUUID.toString();
                        }

                        @Override
                        public void doResult(Drawable result) {
                            image = result;
                            drawTo(image, Profile.this.imageView);
                        }

                        @Override
                        public void error(Exception e) {
                            if(tv!=null)
                            {
                                tv.setText(e.toString());
                            }
                            e.printStackTrace();
                            drawTo(ContextCompat.getDrawable(imageView.getContext(), R.drawable.general_profile), imageView);
                        }

                    }.execute(new URL[]{new URL(Constants.file_connection_address)});
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }

        } else {
            imageView.setImageDrawable(ContextCompat.getDrawable(imageView.getContext(), R.drawable.general_profile));
        }

    }

    public void drawTo(Drawable d, ImageView iv) {
        iv.setImageDrawable(d);
    }

}

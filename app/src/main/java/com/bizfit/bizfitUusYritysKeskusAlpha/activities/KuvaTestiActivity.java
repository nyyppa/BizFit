package com.bizfit.bizfitUusYritysKeskusAlpha.activities;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.bizfit.bizfitUusYritysKeskusAlpha.R;
import com.bizfit.bizfitUusYritysKeskusAlpha.User;
import com.bizfit.bizfitUusYritysKeskusAlpha.network.FileDownload.DrawableDownloader;
import com.bizfit.bizfitUusYritysKeskusAlpha.network.FileUpload.DrawableUploader;
import com.bizfit.bizfitUusYritysKeskusAlpha.network.FileUpload.FileUpload;
import com.bizfit.bizfitUusYritysKeskusAlpha.utils.Constants;

import java.net.MalformedURLException;
import java.net.URL;

public class KuvaTestiActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kuva_testi);
        ImageView imageView=(ImageView)findViewById(R.id.kuva);
        new DrawableUploader() {
            @Override
            public void onPostExecute(FileUpload.ResultCode result) {

            }

            @Override
            public String getFileName() {
                return null;
            }

            @Override
            public String getFileType() {
                return null;
            }

            @Override
            public String getFileExtension() {
                return null;
            }

            @Override
            public String getUploader() {
                return "atte";
            }
        }.execute(new Drawable[]{imageView.getDrawable()});


        URL[] urls=new URL[1];
        try {
            urls[0]=new URL(Constants.connection_address);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        new DrawableDownloader() {
            @Override
            public String getFileID() {
                return "56ca8a98-8524-49dd-877e-366d5a2295ad845761484";
            }

            @Override
            public void doResult(Drawable result) {
                ImageView imageView=(ImageView)KuvaTestiActivity.this.findViewById(R.id.kuva);
                imageView.setImageDrawable(result);
            }

            @Override
            public void error(Exception e) {

            }
        }.execute(urls);


    }

}

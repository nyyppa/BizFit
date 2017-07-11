package com.bizfit.bizfitUusYritysKeskusAlpha.activities;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.bizfit.bizfitUusYritysKeskusAlpha.R;
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
        /*URL[] urls=new URL[1];
        try {
            urls[0]=new URL(Constants.connection_address);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        new DrawableDownloader() {
            @Override
            public void doResult(Drawable result) {
                ImageView imageView=(ImageView)KuvaTestiActivity.this.findViewById(R.id.kuva);
                imageView.setImageDrawable(result);
            }
        }.execute(urls);

        */
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
        }.execute(new Drawable[]{imageView.getDrawable()});
    }

}

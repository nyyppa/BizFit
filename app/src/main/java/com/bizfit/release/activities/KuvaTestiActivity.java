package com.bizfit.release.activities;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.bizfit.release.R;
import com.bizfit.release.network.FileDownload.DrawableDownloader;
import com.bizfit.release.utils.Constants;

import java.net.MalformedURLException;
import java.net.URL;

public class KuvaTestiActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kuva_testi);
        ImageView imageView=(ImageView)findViewById(R.id.kuva);
        /*new DrawableUploader() {
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

        */
        URL[] urls=new URL[1];
        try {
            urls[0]=new URL(Constants.file_connection_address);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        new DrawableDownloader() {
            @Override
            public void onProgressUpdate(Integer... progress) {

            }

            @Override
            public String getFileID() {
                return "http://bizfit.fi/wp-content/uploads/vfb/2017/08/IMG_20170610_163527_687.jpg";
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

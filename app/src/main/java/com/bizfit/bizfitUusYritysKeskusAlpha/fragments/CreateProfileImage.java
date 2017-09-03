package com.bizfit.bizfitUusYritysKeskusAlpha.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.solver.widgets.Rectangle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.bizfit.bizfitUusYritysKeskusAlpha.Profile;
import com.bizfit.bizfitUusYritysKeskusAlpha.R;
import com.bizfit.bizfitUusYritysKeskusAlpha.activities.CreateProfile;
import com.mikelau.croperino.Croperino;
import com.mikelau.croperino.CroperinoConfig;
import com.mikelau.croperino.CroperinoFileUtil;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

import pub.devrel.easypermissions.EasyPermissions;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static android.os.Environment.getExternalStoragePublicDirectory;

/**
 * Created by iipa on 17.8.2017.
 */

// TODO: hae kuva, aseta kuva, tarkista että defaultista tallennetaan viittaus ei kuvaa

public class CreateProfileImage extends Fragment implements View.OnClickListener {

    CreateProfile parentActivity;

    Button proceed;
    Button skip;

    Button choose;
    Button take;

    ImageView imgView;
    boolean genericImage;

    String mCurrentPhotoPath;

    final int REQUEST_TAKE_PICTURE = 1;
    final int REQUEST_CHOOSE_FROM_GALLERY = 2;

    public CreateProfileImage() {
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        parentActivity = (CreateProfile) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_profile_picture, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        proceed = (Button) getActivity().findViewById(R.id.proceed);
        proceed.setOnClickListener(this);
        proceed.setVisibility(View.GONE);

        skip = (Button) getActivity().findViewById(R.id.skip);
        skip.setOnClickListener(this);

        choose = (Button) getActivity().findViewById(R.id.chooseImage);
        choose.setOnClickListener(this);

        take = (Button) getActivity().findViewById(R.id.takeImage);
        take.setOnClickListener(this);
        if(!getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            take.setVisibility(View.GONE);
        }

        imgView = (ImageView) getActivity().findViewById(R.id.profilePicture);

        if(parentActivity.getProfile() != null) {

            Profile profile = parentActivity.getProfile();
            profile.drawToImgView(imgView);

        }

    }

    @Override
    public void onClick(View v) {

        switch(v.getId()) {
            case R.id.chooseImage:

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (parentActivity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(parentActivity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                    } else {
                        startGallery();
                    }
                } else {
                    startGallery();
                }
                break;

            case R.id.takeImage:

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (parentActivity.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(parentActivity, new String[]{Manifest.permission.CAMERA}, 2);
                    } else if (parentActivity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(parentActivity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                    } else {
                        startCamera();
                    }
                } else {
                    startCamera();
                }

                break;

            case R.id.skip:
                parentActivity.switchPhase(CreateProfile.Phase.PICTURE);
                break;

            case R.id.proceed:
                if(!genericImage) {
                    parentActivity.setImage(imgView.getDrawable());
                }
                parentActivity.switchPhase(CreateProfile.Phase.PICTURE);
                break;
        }

    }

    public void setPicture(Drawable img) {
        if(img != null) {
            imgView.setImageDrawable(img);
            genericImage = false;
            proceed.setVisibility(View.VISIBLE);
        } else {
            imgView.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.general_profile));
            genericImage = true;
        }
    }

    public void startCamera() {
        //Initialize on every usage
        new CroperinoConfig("IMG_" + System.currentTimeMillis() + ".jpg", "/Bizfit/Pictures", Environment.getExternalStorageDirectory().getPath() +  "/Bizfit/Pictures");
        CroperinoFileUtil.verifyStoragePermissions(parentActivity);
        CroperinoFileUtil.setupDirectory(parentActivity);

        //Prepare Camera
        try {
            Croperino.prepareCamera(parentActivity);
        } catch(Exception e) {

        }
    }

    public void startGallery() {
        //Initialize on every usage
        new CroperinoConfig("IMG_" + System.currentTimeMillis() + ".jpg", "/Bizfit/Pictures", Environment.getExternalStorageDirectory().getPath() +  "/Bizfit/Pictures");
        CroperinoFileUtil.verifyStoragePermissions(parentActivity);
        CroperinoFileUtil.setupDirectory(parentActivity);

        Croperino.prepareGallery(parentActivity);
    }

}

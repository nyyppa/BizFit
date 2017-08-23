package com.bizfit.bizfitUusYritysKeskusAlpha.fragments;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.bizfit.bizfitUusYritysKeskusAlpha.R;
import com.bizfit.bizfitUusYritysKeskusAlpha.activities.CreateProfile;
import com.bizfit.bizfitUusYritysKeskusAlpha.network.FileUpload.DrawableUploader;
import com.bizfit.bizfitUusYritysKeskusAlpha.network.FileUpload.FileUpload;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

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

    int ACTIVITY_CHOOSE_IMAGE = 1;
    int ACTIVITY_TAKE_IMAGE = 2;

    String mCurrentPhotoPath;

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
        setPicture(parentActivity.getImage());

    }

    @Override
    public void onClick(View v) {

        switch(v.getId()) {
            case R.id.chooseImage:
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Valitse kuva"), ACTIVITY_CHOOSE_IMAGE);
                break;

            case R.id.takeImage:
                // TODO: start camrera activity for result
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode == ACTIVITY_CHOOSE_IMAGE && resultCode == RESULT_OK)
        {
            Uri imgUri = data.getData();
            Drawable d;

            try {
                InputStream inputStream = getActivity().getContentResolver().openInputStream(imgUri);
                Bitmap bm = BitmapFactory.decodeStream(inputStream);
                bm = Bitmap.createScaledBitmap(bm, 160, 160, true);
                d = new BitmapDrawable(getResources(), bm);
                genericImage = false;
                proceed.setVisibility(View.VISIBLE);
            } catch (FileNotFoundException e) {
                d = ContextCompat.getDrawable(getContext(), R.drawable.general_profile);
                genericImage = true;
            }

            imgView.setImageDrawable(d);
        } else if(requestCode == ACTIVITY_TAKE_IMAGE && resultCode == RESULT_OK) {
            // TODO: scale image, save it and add to view
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

}

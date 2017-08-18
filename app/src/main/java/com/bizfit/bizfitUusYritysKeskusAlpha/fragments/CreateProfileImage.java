package com.bizfit.bizfitUusYritysKeskusAlpha.fragments;

import android.content.Intent;
import android.content.pm.PackageManager;
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
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                // Ensure that there's a camera activity to handle the intent
                if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    // Create the File where the photo should go
                    File photoFile = null;

                    try {
                        photoFile = createImageFile();
                    } catch (IOException ex) {
                        // Error occurred while creating the File
                    }

                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        Uri photoURI = FileProvider.getUriForFile(getActivity(),
                                "com.example.android.fileprovider",
                                photoFile);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        startActivityForResult(takePictureIntent, ACTIVITY_TAKE_IMAGE);
                    }
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode == ACTIVITY_CHOOSE_IMAGE && resultCode == RESULT_OK)
        {
            Uri imgUri = data.getData();
            Drawable d;

            try {
                InputStream inputStream = getActivity().getContentResolver().openInputStream(imgUri);
                d = Drawable.createFromStream(inputStream, imgUri.toString() );
                genericImage = false;
                proceed.setVisibility(View.VISIBLE);
            } catch (FileNotFoundException e) {
                d = ContextCompat.getDrawable(getContext(), R.drawable.general_profile);
                genericImage = true;
            }

            imgView.setImageDrawable(d);
        } else if(requestCode == ACTIVITY_TAKE_IMAGE && resultCode == RESULT_OK) {
            galleryAddPic();
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

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        getActivity().sendBroadcast(mediaScanIntent);
    }

}

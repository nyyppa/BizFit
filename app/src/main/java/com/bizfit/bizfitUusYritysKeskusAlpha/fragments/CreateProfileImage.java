package com.bizfit.bizfitUusYritysKeskusAlpha.fragments;

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
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.solver.widgets.Rectangle;
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
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

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
        setPicture(parentActivity.getImage());

    }

    @Override
    public void onClick(View v) {

        Intent intent;

        switch(v.getId()) {
            case R.id.chooseImage:

                intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent, REQUEST_CHOOSE_FROM_GALLERY);

                break;

            case R.id.takeImage:


                /*
                intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI.getPath());
                startActivityForResult(intent, REQUEST_TAKE_PICTURE);

                */

                CropImage.activity()
                        .setRequestedSize(1024, 1024)
                        .start(getContext(), this);

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

        Bitmap bitmap = null;
        InputStream stream = null;

        switch (requestCode) {

            case REQUEST_TAKE_PICTURE:
                if(resultCode == RESULT_OK) {
                    Bitmap photo = (Bitmap) data.getExtras().get("data");
                    imgView.setImageBitmap(photo);
                }
                break;

            case REQUEST_CHOOSE_FROM_GALLERY:
                if(resultCode == RESULT_OK) {
                    try {
                        // recyle unused bitmaps
                        if (bitmap != null) {
                            bitmap.recycle();
                        }
                        stream = parentActivity.getContentResolver().openInputStream(data.getData());
                        bitmap = BitmapFactory.decodeStream(stream);

                        imgView.setImageBitmap(bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } finally {
                        if (stream != null)
                            try {
                                stream.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                    }
                }
                break;

            case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                if(resultCode == RESULT_OK) {
                    CropImage.ActivityResult result = CropImage.getActivityResult(data);
                    Uri resultUri = result.getUri();
                    imgView.setImageURI(resultUri);
                }
                break;

            default:
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

    public void setPicUri(Uri uri) {
        imgView.setImageURI(uri);
    }

}

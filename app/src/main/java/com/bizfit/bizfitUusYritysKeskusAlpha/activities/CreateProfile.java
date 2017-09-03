package com.bizfit.bizfitUusYritysKeskusAlpha.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.bizfit.bizfitUusYritysKeskusAlpha.Profile;
import com.bizfit.bizfitUusYritysKeskusAlpha.R;
import com.bizfit.bizfitUusYritysKeskusAlpha.User;
import com.bizfit.bizfitUusYritysKeskusAlpha.fragments.CreateProfileCompany;
import com.bizfit.bizfitUusYritysKeskusAlpha.fragments.CreateProfileFinish;
import com.bizfit.bizfitUusYritysKeskusAlpha.fragments.CreateProfileHasCompany;
import com.bizfit.bizfitUusYritysKeskusAlpha.fragments.CreateProfileImage;
import com.bizfit.bizfitUusYritysKeskusAlpha.fragments.CreateProfileIsExpert;
import com.bizfit.bizfitUusYritysKeskusAlpha.fragments.CreateProfileName;
import com.bizfit.bizfitUusYritysKeskusAlpha.fragments.CreateProfileStart;
import com.bizfit.bizfitUusYritysKeskusAlpha.network.FileUpload.DrawableUploader;
import com.bizfit.bizfitUusYritysKeskusAlpha.network.FileUpload.FileUpload;
import com.bizfit.bizfitUusYritysKeskusAlpha.network.NetMessage;
import com.bizfit.bizfitUusYritysKeskusAlpha.network.Network;
import com.bizfit.bizfitUusYritysKeskusAlpha.network.NetworkReturn;
import com.bizfit.bizfitUusYritysKeskusAlpha.utils.Constants;
import com.mikelau.croperino.Croperino;
import com.mikelau.croperino.CroperinoConfig;
import com.mikelau.croperino.CroperinoFileUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

/**
 * Created by iipa on 15.8.2017.
 */

public class CreateProfile extends AppCompatActivity implements NetworkReturn {

    // fragments aka steps of wizard
    Fragment start;
    Fragment name;
    Fragment hasCompany;
    Fragment company;
    CreateProfileImage picture;
    Fragment expert;
    Fragment finish;

    // FragmentManager and FragmentTransaction are used in switching fragments
    FragmentManager manager;
    FragmentTransaction transaction;

    Profile profile;

    // user information
    String firstName;
    String lastName;
    UUID imageUUID;
    Drawable image;
    boolean ownsCompany;
    boolean askedCompany;
    boolean isExpert;
    boolean askedExpert;

    // company information
    String companyName;
    String companyField;
    String companyDesc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);

        // define fragments
        start = new CreateProfileStart();
        name = new CreateProfileName();
        hasCompany = new CreateProfileHasCompany();
        company = new CreateProfileCompany();
        picture = new CreateProfileImage();
        expert = new CreateProfileIsExpert();
        finish = new CreateProfileFinish();

        manager = getSupportFragmentManager();
        transaction = manager.beginTransaction();

        // show the first step
        transaction.replace(R.id.fragmentContainer, start);
        transaction.commit();

        // retrieve possible profile from database

        JSONObject json = new JSONObject();

        try {
            json.put(Constants.job, Constants.load_profile);
            json.put(Constants.profile, User.getLastUser(null, null, null).userName);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        NetMessage netMessage = new NetMessage(null, new NetworkReturn() {
            @Override
            public void returnMessage(String message) {
                if(message.equals(Constants.networkconn_failed)) {

                } else {
                    try {
                        JSONObject jsonObject = new JSONObject(message);
                        profile = new Profile(jsonObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        },json);

        Network.addNetMessage(netMessage);

    }

    public void switchPhase(Phase currentPhase) {

        // check what is the current phase and switch fragment accordingly

        switch(currentPhase) {

            // on start phase this method is called only if user wants to proceed
            case START:
                transaction = manager.beginTransaction();
                transaction.replace(R.id.fragmentContainer, name);
                transaction.commit();
                break;
            case NAME:
                transaction = manager.beginTransaction();
                transaction.replace(R.id.fragmentContainer, hasCompany);
                transaction.commit();
                break;
            case HASCOMPANY:
                if(ownsCompany) {
                    transaction = manager.beginTransaction();
                    transaction.replace(R.id.fragmentContainer, company);
                    transaction.commit();
                } else {
                    transaction = manager.beginTransaction();
                    transaction.replace(R.id.fragmentContainer, picture);
                    transaction.commit();
                }
                break;
            case COMPANY:
                transaction = manager.beginTransaction();
                transaction.replace(R.id.fragmentContainer, picture);
                transaction.commit();
                break;
            case PICTURE:
                transaction = manager.beginTransaction();
                transaction.replace(R.id.fragmentContainer, expert);
                transaction.commit();
                break;
            case ISEXPERT:
                saveProfile();
                transaction = manager.beginTransaction();
                transaction.replace(R.id.fragmentContainer, finish);
                transaction.commit();
                break;
        }

    }

    // setters

    public void setFirstName(String name) { firstName = name; }

    public void setLastName(String name) { lastName = name; }

    public void setOwnsCompany(boolean bool) { ownsCompany = bool; }

    public void setAskedCompany(boolean bool) { askedCompany = bool; }

    public void setCompanyName(String name) { companyName = name; }

    public void setCompanyField(String field) { companyField = field; }

    public void setCompanyDesc(String desc) { companyDesc = desc; }

    public void setImage(Drawable img) { image = img; }

    public void setExpert(boolean bool) { isExpert = bool; }

    public void setAskedExpert(boolean bool) { askedExpert = bool; }

    // getters

    public Profile getProfile() {
        return profile;
    }

    public String getFirstName() { return firstName; }

    public String getLastName() { return lastName; }

    public boolean checkIfOwnsCompany() { return ownsCompany; }

    public boolean checkIfAskedCompany() { return askedCompany; }

    public String getCompanyName() { return companyName; }

    public String getCompanyField() { return companyField; }

    public String getCompanyDesc() { return companyDesc; }

    public Drawable getImage() { return image; }

    public boolean checkIfIsExpert() { return isExpert; }

    public boolean checkIfAskedExpert() { return askedExpert; }

    // method for saving user given information

    public void saveProfile() {

        saveProfilePicture();

        JSONObject json = new JSONObject();

        try {
            json.put(Constants.job, Constants.send_profile);
            json.put(Constants.profile, toJSON());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Network.addNetMessage(new NetMessage(null, this, json));

    }

    public JSONObject toJSON() {
        JSONObject json = new JSONObject();

        try {
            // TODO: make constants
            json.put(Constants.profile, User.getLastUser(null, null, null).userName);
            json.put("firstName", firstName);
            json.put("lastName", lastName);
            json.put("imageUUID", imageUUID.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json;
    }

    public void saveProfilePicture() {

        if(imageUUID == null) {
            imageUUID = UUID.randomUUID();
        }



        new DrawableUploader() {
            @Override
            public void onPostExecute(FileUpload.ResultCode result) {

            }

            @Override
            public String getFileName() {
                return imageUUID.toString();
            }

            @Override
            public String getFileType() {
                return Constants.profile;
            }

            @Override
            public String getFileExtension() {
                return null;
            }

            @Override
            public String getUploader() {
                return User.getLastUser(null, null, null).userName;
            }

        }.execute(new Drawable[]{image});
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case CroperinoConfig.REQUEST_TAKE_PHOTO:
                if (resultCode == Activity.RESULT_OK) {
                    /* Parameters of runCropImage = File, Activity Context, Image is Scalable or Not, Aspect Ratio X, Aspect Ratio Y, Button Bar Color, Background Color */
                    Croperino.runCropImage(CroperinoFileUtil.getmFileTemp(), CreateProfile.this, true, 1, 1, ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark), ContextCompat.getColor(getApplicationContext(), R.color.materialLight));
                }
                break;
            case CroperinoConfig.REQUEST_PICK_FILE:
                if (resultCode == Activity.RESULT_OK) {
                    CroperinoFileUtil.newGalleryFile(data, CreateProfile.this);
                    Croperino.runCropImage(CroperinoFileUtil.getmFileTemp(), CreateProfile.this, true, 1, 1, ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark), ContextCompat.getColor(getApplicationContext(), R.color.materialLight));
                }
                break;
            case CroperinoConfig.REQUEST_CROP_PHOTO:
                if (resultCode == Activity.RESULT_OK) {
                    Uri i = Uri.fromFile(CroperinoFileUtil.getmFileTemp());
                    //The image file can always be retrieved via CroperinoFileUtil.getmFileTemp()
                    Bitmap bm = BitmapFactory.decodeFile(CroperinoFileUtil.getmFileTemp().getPath());
                    bm = Bitmap.createScaledBitmap(bm, 1024, 1024, true);
                    Drawable d = new BitmapDrawable(getResources(), bm);
                    setImage(d);
                    picture.setPicture(d);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                picture.startGallery();
            } else {
                Crouton.makeText(this, "Permission denied", Style.ALERT).show();
            }
        } else if(requestCode == 2) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                picture.startCamera();
            } else {
                Crouton.makeText(this, "Permission denied", Style.ALERT).show();
            }
        }
    }

    @Override
    public void returnMessage(String message) {
        if(message.equals(Constants.networkconn_failed)) {
            // epäonnistui, haluux tehä jottai
        }
    }

    // enum for phase recognition

    public enum Phase {
        START, NAME, HASCOMPANY, COMPANY, PICTURE, ISEXPERT
    }

}

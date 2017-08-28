package com.bizfit.bizfitUusYritysKeskusAlpha.activities;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.bizfit.bizfitUusYritysKeskusAlpha.R;
import com.bizfit.bizfitUusYritysKeskusAlpha.fragments.CreateProfileCompany;
import com.bizfit.bizfitUusYritysKeskusAlpha.fragments.CreateProfileFinish;
import com.bizfit.bizfitUusYritysKeskusAlpha.fragments.CreateProfileHasCompany;
import com.bizfit.bizfitUusYritysKeskusAlpha.fragments.CreateProfileImage;
import com.bizfit.bizfitUusYritysKeskusAlpha.fragments.CreateProfileIsExpert;
import com.bizfit.bizfitUusYritysKeskusAlpha.fragments.CreateProfileName;
import com.bizfit.bizfitUusYritysKeskusAlpha.fragments.CreateProfileStart;

/**
 * Created by iipa on 15.8.2017.
 */

public class CreateProfile extends AppCompatActivity {

    // fragments aka steps of wizard
    Fragment start;
    Fragment name;
    Fragment hasCompany;
    Fragment company;
    Fragment picture;
    Fragment expert;
    Fragment finish;

    // FragmentManager and FragmentTransaction are used in switching fragments
    FragmentManager manager;
    FragmentTransaction transaction;

    // user information
    String firstName;
    String lastName;
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

    }

    // enum for phase recognition

    public enum Phase {
        START, NAME, HASCOMPANY, COMPANY, PICTURE, ISEXPERT
    }

}

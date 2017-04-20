package com.bizfit.bizfit;

import android.content.Intent;
import android.graphics.drawable.Drawable;

import com.bizfit.bizfit.activities.CoachPage;
import com.bizfit.bizfit.utils.Constants;

/**
 * Created by iipa on 20.4.2017.
 */

public class Coach {

    public String name;
    public String coachID;
    public String telNumber="5556";
    public String info = "Something about me. Very peculiar indeed. Contact!";

    public Drawable image;
    public int imageId;
    public int testimonials;

    public Coach(String name, Drawable image, int imageId, int testimonials, String coachID, String telNumber)
    {
        this.image = image;
        this.name = name;
        this.imageId = imageId;
        this.testimonials = testimonials;
        this.coachID = coachID;

        if(telNumber != null){
            this.telNumber = telNumber;
        }

    }

    public Coach(String name, Drawable image, int imageId, int testimonials, String coachID, String telNumber, String info)
    {
        this.image = image;
        this.name = name;
        this.imageId = imageId;
        this.testimonials = testimonials;
        this.coachID = coachID;
        this.info = info;

        if(telNumber != null){
            this.telNumber = telNumber;
        }

    }

    public String getName() {
        return name;
    }

    public int getImageId() {
        return imageId;
    }

    public String getCoachId(){
        return coachID;
    }

    public String getTelNumber(){
        return telNumber;
    }

    public String getInfo() { return info; }

    public void fillIntent(Intent intent) {
        intent.putExtra(CoachPage.FIELD_COACH_NAME, getName());
        intent.putExtra(CoachPage.FIELD_COACH_IMAGE_ID, getImageId());
        intent.putExtra(Constants.coach_id,getCoachId());
        intent.putExtra(Constants.telNumber,getTelNumber());
    }

}

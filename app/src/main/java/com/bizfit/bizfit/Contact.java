package com.bizfit.bizfit;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Base64;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

/**
 * Created by attey on 02/05/2017.
 */

public class Contact {
    Drawable picture;
    String firstName;
    String lastName;
    String coachID;

    public Contact (String firstName,String lastName,String coachID)
    {
        this.firstName=firstName;
        this.lastName=lastName;
        this.coachID=coachID;
    }
    public Contact(JSONObject jsonObject)
    {
        try {
            if(jsonObject.has("lastName"))
            {
                lastName=jsonObject.getString("lastName");
            }
            if(jsonObject.has("firstName"))
            {
                firstName=jsonObject.getString("firstName");
            }
            if(jsonObject.has("picture"))
            {
                stringToPicture(jsonObject.getString("picture"));
            }
            if(jsonObject.has("coachID"))
            {
                coachID=jsonObject.getString("coachID");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    public String getDisplayName()
    {
        String displayName="";
        if(firstName!=null)
        {
            displayName=firstName;
        }
        if(lastName!=null)
        {
            displayName+=" "+lastName;
        }
        if(displayName.isEmpty())
        {
            displayName=coachID;
        }
        return displayName;
    }

    private byte[] pictureToByteArray()
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Bitmap bitmap = ((BitmapDrawable)picture).getBitmap();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] photo = baos.toByteArray();
        return photo;
    }

    private void setPicture(byte[] picture)
    {
        Bitmap bmp = BitmapFactory.decodeByteArray(picture, 0, picture.length);
        this.picture = new BitmapDrawable(MyApplication.getContext().getResources(), bmp);
    }
    public void setPicture(Drawable drawable)
    {
        picture=drawable;
    }

    public String pictureToString()
    {
        Bitmap bitmap = ((BitmapDrawable)picture).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] bitmapdata = stream.toByteArray();
        String pictureString= Base64.encodeToString(bitmapdata,Base64.DEFAULT);
        return pictureString;
    }

    public Drawable stringToPicture(String string)
    {
        byte[] tmp2 = Base64.decode(string,Base64.DEFAULT);
        Bitmap bmp = BitmapFactory.decodeByteArray(tmp2, 0, tmp2.length);
        picture = new BitmapDrawable(MyApplication.getContext().getResources(), bmp);
        return picture;
    }

    public JSONObject toJSON()
    {
        JSONObject jsonObject=new JSONObject();
        try {
            if(lastName!=null)
            {
                jsonObject.put("lastName",lastName);
            }
            if(firstName!=null)
            {
                jsonObject.put("firstName",firstName);
            }
            jsonObject.put("coachID",coachID);
            if(picture!=null)
            {
                jsonObject.put("picture",pictureToString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
}

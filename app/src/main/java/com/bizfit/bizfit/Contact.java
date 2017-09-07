package com.bizfit.bizfit;

import android.database.Cursor;
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
    String userID;

    public Contact (String firstName,String lastName,String userID)
    {
        this.firstName=firstName;
        this.lastName=lastName;
        this.userID = userID;
    }

    /** Made by JariJ
     * Last modified by JariJ 9.5.17
     * @param cursor reader of the data
     */
    public Contact(Cursor cursor)
    {
        cursor.moveToFirst();
        userID = cursor.getString(cursor.getColumnIndex("userID"));
        firstName = cursor.getString(cursor.getColumnIndex("firstName"));
        lastName = cursor.getString(cursor.getColumnIndex("lastName"));
        int columnIndex = cursor.getColumnIndex("picture");
        if(columnIndex > -1)
        {
            byte[]byteArray = cursor.getBlob(columnIndex);
            setPicture(byteArray);
        }
        cursor.close();
    }

    public String getUserID() {
        return userID;
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
            if(jsonObject.has("userID"))
            {
                userID =jsonObject.getString("userID");
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
            displayName= userID;
        }
        return displayName;
    }

    public byte[] pictureToByteArray()
    {
        if(picture==null)
        {
            return null;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Bitmap bitmap = ((BitmapDrawable)picture).getBitmap();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] photo = baos.toByteArray();
        return photo;
    }

    public Drawable getPicture() {
        return picture;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    private void setPicture(byte[] picture)
    {
        if(picture==null)
        {
            return;
        }
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
            jsonObject.put("userID", userID);
            if(picture!=null)
            {
                jsonObject.put("picture",pictureToString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public boolean isValid()
    {
        return firstName!=null&&!firstName.isEmpty()&&lastName!=null&&!lastName.isEmpty();
    }
}

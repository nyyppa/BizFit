package com.bizfit.bizfitUusYritysKeskusAlpha.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.widget.Toast;

import com.bizfit.bizfitUusYritysKeskusAlpha.MyApplication;
import com.bizfit.bizfitUusYritysKeskusAlpha.R;

/**
 * Created by attey on 25/04/2017.
 */

public class Utils {

    static final int CROP_PIC_REQUEST_CODE = 3;

    public static String getCoachName(String coachId)
    {
        String name=null;
        switch (coachId)
        {
            case Constants.atte_email:
                name="Atte Yliverronen";
                break;
            case Constants.jariM_email:
                name="Jari Myllymäki";
                break;
            case Constants.pasi_email:
                name="Pasi Ojanen";
                break;
            case Constants.jari_email:
                name="Jari Järvenpää";
                break;
            case Constants.support_email:
                name = "Customer Support";
                break;
            case Constants.kaj_email:
                name = "Kaj Heiniö";
                break;
            case Constants.taina_email:
                name = "Taina Jormanainen";
                break;
            case Constants.tapani_email:
                name = "Tapani Kaskela";
                break;
            default:
                name=coachId;
                break;

        }
        return name;
    }
    public static int getDrawableID(String coach)
    {
        int id=-1;
        switch (coach)
        {
            case Constants.atte_email:
                id= R.drawable.atte;
                break;
            case Constants.jariM_email:
                id=R.drawable.mylly;
                break;
            case Constants.pasi_email:
                id=R.drawable.pasi;
                break;
            case Constants.jari_email:
                id=R.drawable.jartsa;
                break;
            case Constants.kaj_email:
                id=R.drawable.kaj;
                break;
            case Constants.taina_email:
                id=R.drawable.taina;
                break;
            case Constants.tapani_email:
                id=R.drawable.tapani;
                break;
            default:
                id=R.drawable.general_profile;
                break;

        }
        return id;
    }

    static public String getCoachID(String name)
    {
        String couchID=null;
        switch (name)
        {
            case "Atte Yliverronen":
                couchID=Constants.atte_email;
                break;
            case "Jari Myllymäki":
                couchID=Constants.jariM_email;
                break;
            case "Pasi Ojanen":
                couchID=Constants.pasi_email;
                break;
            case "Jari Järvenpää":
                couchID=Constants.jari_email;
                break;
            case "Kaj Heiniö":
                couchID = Constants.kaj_email;
                break;
            case "Taina Jormanainen":
                couchID = Constants.taina_email;
                break;
            case "Tapani Kaskela":
                couchID = Constants.tapani_email;
                break;
            default:
                couchID="default";
                couchID=name;
                break;

        }
        return couchID;
    }

    public static String getDesc(String coachId) {

        String desc = MyApplication.getContext().getString(R.string.ensimetri_special);

        switch (coachId) {
            case Constants.kaj_email:
                desc = desc + " " + MyApplication.getContext().getString(R.string.ensimetri_kaj);
                break;
            case Constants.taina_email:
                desc = desc + " " + MyApplication.getContext().getString(R.string.ensimetri_taina);
                break;
            case Constants.tapani_email:
                desc = desc + " " + MyApplication.getContext().getString(R.string.ensimetri_tapani);
                break;
            default:
                desc = "Lorem ipsum dolor si amet.";
                break;
        }

        return desc;
    }



    public static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public static String getRealPathFromURI(Context context, Uri contentUri) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return getPathForV19AndUp(context, contentUri);
        } else {
            return getPathForPreV19(context, contentUri);
        }
    }

    public static String getPathForPreV19(Context context, Uri contentUri) {
        String res = null;

        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
        if(cursor.moveToFirst()){;
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();

        return res;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String getPathForV19AndUp(Context context, Uri contentUri) {
        String wholeID = DocumentsContract.getDocumentId(contentUri);

        // Split at colon, use second item in the array
        String id = wholeID.split(":")[1];
        String[] column = { MediaStore.Images.Media.DATA };

        // where id is equal to
        String sel = MediaStore.Images.Media._ID + "=?";
        Cursor cursor = context.getContentResolver().
                query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        column, sel, new String[]{ id }, null);

        String filePath = "";
        int columnIndex = cursor.getColumnIndex(column[0]);
        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        }

        cursor.close();
        return filePath;
    }

    public static Bitmap rotateBitmap(Bitmap bitmap, int orientation) {

        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                return bitmap;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.setScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.setRotate(180);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.setRotate(90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.setRotate(-90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(-90);
                break;
            default:
                return bitmap;
        }
        try {
            Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            return bmRotated;
        }
        catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }

}

package com.bizfit.bizfit.utils;

import com.bizfit.bizfit.Coach;
import com.bizfit.bizfit.R;

/**
 * Created by attey on 25/04/2017.
 */

public class Utils {

    public static String getCouchName(String coachId)
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
            default:
                name="default";
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
            default:
                id=R.drawable.tmp2;
                break;

        }
        return id;
    }

    static public String getCouchID(String name)
    {
        String couachID=null;
        switch (name)
        {
            case "Atte Yliverronen":
                couachID=Constants.atte_email;
                break;
            case "Jari Myllymäki":
                couachID=Constants.jariM_email;
                break;
            case "Pasi Ojanen":
                couachID=Constants.pasi_email;
                break;
            case "Jari Järvenpää":
                couachID=Constants.jari_email;
                break;
            default:
                name="default";
                couachID="default";
                break;

        }
        return couachID;
    }
}

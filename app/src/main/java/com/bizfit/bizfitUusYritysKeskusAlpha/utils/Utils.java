package com.bizfit.bizfitUusYritysKeskusAlpha.utils;

import com.bizfit.bizfitUusYritysKeskusAlpha.MyApplication;
import com.bizfit.bizfitUusYritysKeskusAlpha.R;

/**
 * Created by attey on 25/04/2017.
 */

public class Utils {

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
}

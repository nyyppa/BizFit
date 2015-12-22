package com.bizfit.bizfit.utils;

import android.graphics.Typeface;
import android.net.Uri;
import com.bizfit.bizfit.utils.FontHolder;
import java.util.ArrayList;
import java.util.List;
import com.bizfit.bizfit.activities.MainActivity;

/**
 * Created by Käyttäjä on 22.12.2015.
 */
public class AssetManagerOur {
    private static List<FontHolder>fonts=new ArrayList<FontHolder>(0);
    public static Typeface getFont(String file){
        for(int i=0;i<fonts.size();i++){
            if(fonts.get(i).file.equals(file)){
                return fonts.get(i).font;
            }
        }
        Uri path = Uri.parse("android.resource://com.bizfit.bizfit/fonts"+file);
        Typeface font=Typeface.createFromFile(path.toString());
        FontHolder holder=new FontHolder(font,file);
        fonts.add(holder);
        return holder.font;
    }



}


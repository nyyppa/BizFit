package com.bizfit.bizfit.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bizfit.bizfit.R;
import com.ms.square.android.expandabletextview.ExpandableTextView;

/**
 * Fetches and displays an overview of hireable coaches.
 */
public class CoachPage extends AppCompatActivity
{

    public static final String FIELD_COACH_NAME = "com.bizfit.field.name";
    public static final String FIELD_COACH_IMAGE_ID = "com.bizfit.field.image";
    private static final int IMAGE_NOT_FOUND = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coach_page);
        ExpandableTextView expTv1 = (ExpandableTextView) findViewById(R.id.expand_text_view)
                .findViewById(R.id.expand_text_view);

        TextView fieldOfExpertice=(TextView)findViewById(R.id.textView7);
        String[] jobTitles=getResources().getStringArray(R.array.random_job_title);
        fieldOfExpertice.setText(jobTitles[(int)(Math.random()*jobTitles.length-1)]);

        // TODO Content should be fetched from the server. API specific resource reference.
        expTv1.setText(getString(R.string.dummy));
        Intent intent = getIntent();

        if (intent != null)
        {
            String name = intent.getStringExtra(FIELD_COACH_NAME);
            int imgId = intent.getIntExtra(FIELD_COACH_IMAGE_ID, IMAGE_NOT_FOUND);


            if (imgId != IMAGE_NOT_FOUND)
            {
                //  Fixed by JariJ 25.11.16
                ((ImageView) findViewById(R.id.coach_banner_image)).setImageDrawable(ContextCompat.getDrawable(this, imgId));
            }
            /** Deprecated function getResources()
            // ((ImageView) findViewById(R.id.coach_banner_image)).setImageDrawable(getResources().getDrawable(imgId));
             */
            if (name != null)
                ((TextView) findViewById(R.id.textView5)).setText(name);
        }
    }
}

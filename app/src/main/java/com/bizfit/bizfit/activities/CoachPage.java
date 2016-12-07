package com.bizfit.bizfit.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.PopupWindowCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bizfit.bizfit.R;
import com.ms.square.android.expandabletextview.ExpandableTextView;

import org.w3c.dom.Text;

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
        TextView tvIntro = (TextView) findViewById(R.id.tVIntro)
                .findViewById(R.id.tVIntro);

        //random coach information assignments
        TextView tVfieldOfExpertise=(TextView)findViewById(R.id.tVExpertise);
        String[] jobTitles=getResources().getStringArray(R.array.random_job_title);
        tVfieldOfExpertise.setText(jobTitles[(int)(Math.random()*jobTitles.length-1)]);

        TextView tVplaceofResidence=(TextView)findViewById(R.id.tVPlaceOfResidence);
        String[] countriesAndCities=getResources().getStringArray(R.array.random_country_and_city);
        tVplaceofResidence.setText(countriesAndCities[(int)(Math.random()*countriesAndCities.length-1)]);

        TextView tVSalesPitch=(TextView)findViewById(R.id.tVSalesPitch);
        String[] pitches=getResources().getStringArray(R.array.random_sales_pitch);
        tVSalesPitch.setText(pitches[(int)(Math.random()*pitches.length-1)]);

        TextView tVpriceChat=(TextView)findViewById(R.id.tVChatPrice);
        String[] chatPrices=getResources().getStringArray(R.array.random_price_chat);
        tVpriceChat.setText(chatPrices[(int)(Math.random()*chatPrices.length-1)]);

        TextView tVpriceCall=(TextView)findViewById(R.id.tVCallPrice);
        String[] callPrices=getResources().getStringArray(R.array.random_price_call);
        tVpriceCall.setText(callPrices[(int)(Math.random()*callPrices.length-1)]);


     /*   Button btnClose = (Button) findViewById(R.id.btnAccept);
        btnClose.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                if(v.callOnClick())

            }
        });
        */
        // 7.12.2016 jariJ Making a new popup window when terms of service button is clicked

        Button btnTos = (Button) findViewById(R.id.btnTOS);
        btnTos.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                Dialog dialog = new Dialog(CoachPage.this);
                dialog.setContentView(R.layout.popup_tos);
                // must be shown prior to fetching views and centering
                dialog.show();

                TextView tVTosHL = (TextView) dialog.findViewById(R.id.tVTosHL);
                TextView tVTos = (TextView) dialog.findViewById(R.id.tVTos);
                if (tVTosHL != null)
                {
                    tVTosHL.setGravity(Gravity.CENTER);
                }
                tVTos.setGravity(Gravity.CENTER);
                //Button btnClose = (Button) dialog.findViewById(R.id.btnAccept);

            }

        });

       /*  */


        // TODO Content should be fetched from the server. API specific resource reference.
        tvIntro.setText(getString(R.string.dummy));
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
                ((TextView) findViewById(R.id.tVName)).setText(name);
        }

    }





}

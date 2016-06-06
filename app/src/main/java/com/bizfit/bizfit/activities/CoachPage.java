package com.bizfit.bizfit.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.bizfit.bizfit.R;
import com.ms.square.android.expandabletextview.ExpandableTextView;

/**
 * Fetches and displays an overview of hireable coaches.
 */
public class CoachPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coach_page);

        ExpandableTextView expTv1 = (ExpandableTextView) findViewById(R.id.expand_text_view)
                .findViewById(R.id.expand_text_view);

        // Text content should be fetched from the server.
        expTv1.setText(getString(R.string.dummy));
    }
}

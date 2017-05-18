package com.bizfit.bizfitUusYritysKeskusAlpha.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.bizfit.bizfitUusYritysKeskusAlpha.R;
import com.bizfit.bizfitUusYritysKeskusAlpha.User;
import android.net.Uri;
import android.content.Intent;
import android.util.Log;

/**
 * Created by iipa on 25.4.2017.
 */

public class Support extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support);

        Button cancel = (Button) findViewById(R.id.action_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Button send = (Button) findViewById(R.id.action_send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText et = (EditText) findViewById(R.id.editText);
                String feedback = et.getText().toString();

                CheckBox cb = (CheckBox) findViewById(R.id.checkBox);
                boolean respond = cb.isChecked();

                String email = "";
                if(respond) {
                    email = User.getLastUser(null, null, null).userName;
                }
                sendEmail(feedback);
                // TODO: send information somewhere

                Toast.makeText(getApplicationContext(), getString(R.string.toast_support_sent), Toast.LENGTH_SHORT).show();
                finish();
            }
        });


    }
    protected void sendEmail(String feedBack) {
        Log.i("Send email", "");
        String[] TO = {"bizfithelp@gmail.com"};
        String[] CC = {""};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);

        emailIntent.setData(Uri.parse("mailto:bizfithelp@gmail.com"));
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_CC, CC);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Support");
        emailIntent.putExtra(Intent.EXTRA_TEXT, feedBack);

        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            finish();
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(Support.this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }

}

package com.bizfit.bizfit.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.bizfit.bizfit.R;
import com.bizfit.bizfit.User;

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

                // TODO: send information somewhere

                Toast.makeText(getApplicationContext(), getString(R.string.toast_support_sent), Toast.LENGTH_SHORT).show();
                finish();
            }
        });

    }

}

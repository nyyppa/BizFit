package com.bizfit.bizfit.activities;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Size;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bizfit.bizfit.R;
import com.bizfit.bizfit.RecyclerViews.RecyclerViewAdapterWizard;

import java.util.ArrayList;

/**
 * Created by iipa on 6.3.2017.
 */

public class OrderChat extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private RecyclerViewAdapterWizard mRecyclerViewAdapterWizard;

    // wizard information that is forwarded into request

    // phase 1: category

    // phase 2: need
    // 1 - problem
    // 2 - learn
    int need;

    // phase 3: skill level
    // 1 - beginner
    // 2 - intermediate
    // 3 - expert
    int skillLevel;

    // phase 4: details
    String details;

    // phase 5: sessions per week
    int sessions;

    // phase 6: minutes per session
    int minutes;

    LinearLayout linear;
    View inputView;

    int index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_chat_wizard);

        linear = (LinearLayout) findViewById(R.id.order_chat_wizard_linearlayout);
        inputView = findViewById(R.id.container_input_details);
        inputView.setVisibility(View.GONE);
        startWizard();
    }

    public void startWizard() {
        // TODO: welcome message

        CountDownTimer timer = new CountDownTimer(7000, 1000) {

            int tick = 1;
            String message = "";
            Button proceed;
            Button cancel;

            @Override
            public void onTick(long millisUntilFinished) {
                switch(tick) {
                    case 1:
                        linear.addView(createTextView("Hello!", MessageSender.WIZARD), index);
                        break;

                    case 2:
                        message = "It seems that you are interested in ordering chat coaching from X.";
                        linear.addView(createTextView(message, MessageSender.WIZARD), index);
                        break;

                    case 3:
                        message = "Before you do, I would like to ask you some questions, that help the coach give you assistance in your needs.";
                        linear.addView(createTextView(message, MessageSender.WIZARD), index);
                        break;

                    case 4:
                        message = "Shall we begin?";
                        linear.addView(createTextView(message, MessageSender.WIZARD), index);
                        break;

                    case 5:
                        proceed = new Button(getApplicationContext());
                        proceed.setText("Yes, please!");
                        proceed.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
                        proceed.setTextSize(16f);
                        proceed.setBackgroundResource(R.drawable.message_button_background);
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        lp.setMargins(0, 0, 0, 20);
                        lp.gravity = Gravity.RIGHT;
                        proceed.setLayoutParams(lp);
                        proceed.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Button b = (Button) v;
                                TextView tv = createTextView(b.getText().toString(), MessageSender.USER);
                                linear.removeView(proceed);
                                linear.removeView(cancel);
                                index = index - 2;
                                linear.addView(tv, index);
                                startPhaseOne();
                            }
                        });
                        linear.addView(proceed, index);
                        break;

                    case 6:
                        cancel = new Button(getApplicationContext());
                        cancel.setText("No thanks, I changed my mind.");
                        cancel.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
                        cancel.setTextSize(16f);
                        cancel.setBackgroundResource(R.drawable.message_button_background);
                        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        lp2.setMargins(0, 0, 0, 20);
                        lp2.gravity = Gravity.RIGHT;
                        cancel.setLayoutParams(lp2);
                        cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Button b = (Button) v;
                                TextView tv = createTextView(b.getText().toString(), MessageSender.USER);
                                linear.removeView(proceed);
                                linear.removeView(cancel);
                                index = index - 2;
                                linear.addView(tv, index);
                                message = "Oh, that's unfortunate. You can exit by pressing back button.";
                                linear.addView(createTextView(message, MessageSender.WIZARD));
                            }
                        });
                        linear.addView(cancel, index);
                        break;
                }

                tick++;
                index++;
            }

            @Override
            public void onFinish() { }

        }.start();

    }

    /*

    final Button button = new Button(this);
        button.setText("Push me!");
        button.setTextColor(ContextCompat.getColor(this, R.color.colorAccent));
        button.setTextSize(16f);
        button.setBackgroundResource(R.drawable.message_button_background);
        button.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Click!");
                v.setBackgroundResource(R.drawable.message_sent_background);
                button.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorTextLight));
            }
        });

     */

    public void startPhaseOne() {
        // if coach has several categories, ask for category in dropdown selection
        // else skip this phase and go to second phase
        //startPhaseTwo();
        System.out.println("Phase 1 wohoo!");
    }

    public void startPhaseTwo() {

    }

    public void startPhaseThree() {

    }

    public void startPhaseFour() {

        inputView.setVisibility(View.VISIBLE);
    }

    public void startPhaseFive() {

    }

    public void startPhaseSix() {

    }

    public void recap() {
        // TODO: show given details and confirm order
    }

    public TextView createTextView(String text, MessageSender sender) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setTextSize(16f);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 0, 0, 20);

        if(sender == MessageSender.USER) {
            tv.setBackgroundResource(R.drawable.message_sent_background);
            tv.setTextColor(ContextCompat.getColor(this, R.color.colorTextLight));
            layoutParams.gravity = Gravity.RIGHT;
        } else {
            tv.setBackgroundResource(R.drawable.message_received_background);
            layoutParams.gravity = Gravity.LEFT;
        }

        tv.setLayoutParams(layoutParams);

        return tv;
    }

    /**
     * Method for handling button inputs
     * @param v view where the call for this method came from
     */

    public void onClick(View v) {

        switch(v.getId()) {
            case R.id.order_chat_phase_2_option_a:
                need = 1;
                startPhaseFour();
                break;

            case R.id.order_chat_phase_2_option_b:
                need = 2;
                startPhaseThree();
                break;

            case R.id.order_chat_phase_3_option_a:
                skillLevel = 1;
                startPhaseFour();
                break;

            case R.id.order_chat_phase_3_option_b:
                skillLevel = 2;
                startPhaseFour();
                break;

            case R.id.order_chat_phase_3_option_c:
                skillLevel = 3;
                Button b = (Button) findViewById(R.id.order_chat_phase_3_option_c);
                b.setOnClickListener(null);
                startPhaseFour();
                break;

            case R.id.order_chat_phase_4:
                EditText text = (EditText) findViewById(R.id.details);

                if(!text.getText().toString().trim().isEmpty()) {
                    details = text.getText().toString();
                    inputView.setVisibility(View.GONE);
                    startPhaseFive();
                } else {
                    Toast.makeText(getApplicationContext(), "Plese give an answer", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.order_chat_phase_5:
                startPhaseSix();
                break;

            case R.id.order_chat_phase_6:
                recap();
                break;
        }
    }

    enum MessageSender {
        WIZARD, USER;
    }

}

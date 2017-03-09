package com.bizfit.bizfit.activities;

import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.app.Activity;
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
import android.util.DisplayMetrics;
import android.util.Size;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
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
    EditText description;
    ScrollView scrollView;

    int index = 0;
    boolean wantsToProceed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_chat_wizard);

        scrollView = (ScrollView) findViewById(R.id.order_chat_wizard_scrollview);
        linear = (LinearLayout) findViewById(R.id.order_chat_wizard_linearlayout);
        inputView = findViewById(R.id.container_input_details);
        inputView.setVisibility(View.GONE);
        description = (EditText) findViewById(R.id.order_chat_wizard_details);

        scrollView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int heightDiff = scrollView.getRootView().getHeight() - scrollView.getHeight();
                if (heightDiff > dpToPx(getApplicationContext(), 200)) { // if more than 200 dp, it's probably a keyboard...
                    scrollView.fullScroll(View.FOCUS_DOWN);
                }
            }
        });

        startWizard();
    }

    public void startWizard() {
        wantsToProceed = true;

        /* ABOUT TIMER
           timer's time should be ticks needed + 1
           tick is 500 ms
           ticks after messages:
           1 user options
           2 messages
           4 long messages
         */

        new CountDownTimer(7000, 500) {

            int tick = 0;
            String message = "";
            Button proceed;
            Button cancel;

            @Override
            public void onTick(long millisUntilFinished) {
                switch(tick) {
                    case 1:
                        linear.addView(createTextView("Hello!", MessageSender.WIZARD), index);
                        index++;
                        break;

                    case 3:
                        message = "It seems that you are interested in ordering chat coaching from X.";
                        linear.addView(createTextView(message, MessageSender.WIZARD), index);
                        index++;
                        break;

                    case 5:
                        message = "Before you do, I would like to ask you some questions, that help the coach give you assistance in your needs.";
                        linear.addView(createTextView(message, MessageSender.WIZARD), index);
                        index++;
                        break;

                    case 9:
                        message = "Shall we begin?";
                        linear.addView(createTextView(message, MessageSender.WIZARD), index);
                        index++;
                        break;

                    case 11:
                        proceed = new Button(getApplicationContext());
                        proceed.setText("Yes, please!");
                        proceed.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
                        proceed.setTextSize(16f);
                        proceed.setTransformationMethod(null);
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
                                index++;
                                proceed();
                            }
                        });
                        linear.addView(proceed, index);
                        index++;
                        break;

                    case 12:
                        cancel = new Button(getApplicationContext());
                        cancel.setText("No thanks, I changed my mind.");
                        cancel.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
                        cancel.setTextSize(16f);
                        cancel.setTransformationMethod(null);
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
                                index++;
                                wantsToProceed = false;
                                proceed();
                            }
                        });
                        linear.addView(cancel, index);
                        index++;
                        break;

                    default:
                        break;
                }

                tick++;
            }

            @Override
            public void onFinish() { }

        }.start();

    }

    public void proceed() {
        new CountDownTimer(1000, 500) {

            @Override
            public void onTick(long millisUntilFinished) { }

            @Override
            public void onFinish() {
                String message = "";

                if(wantsToProceed) {
                    message = "Okay, let's get started!";
                    linear.addView(createTextView(message, MessageSender.WIZARD), index);
                    index++;
                    startPhaseOne();
                } else {
                    message = "Oh, that's unfortunate. You can exit by pressing back button.";
                    linear.addView(createTextView(message, MessageSender.WIZARD), index);
                    index++;
                }
            }
        }.start();
    }

    public void startPhaseOne() {

        // TODO: if coach has several categories, ask for category in dropdown selection
        // else skip this phase and go to second phase
        startPhaseTwo();
    }

    public void startPhaseTwo() {

        /* ABOUT TIMER
           timer's time should be ticks needed + 1
           tick is 500 ms
           ticks after messages:
           1 user options
           2 messages
           4 long messages
         */

        new CountDownTimer(3500, 500) {

            int tick = 0;
            String message = "";
            Button problem;
            Button learn;

            @Override
            public void onTick(long millisUntilFinished) {
                switch(tick) {

                    case 2:
                        message = "What do you want to achieve from this coaching?";
                        linear.addView(createTextView(message, MessageSender.WIZARD), index);
                        index++;
                        break;

                    case 4:
                        problem = new Button(getApplicationContext());
                        problem.setText("I have a problem I want to solve.");
                        problem.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
                        problem.setTextSize(16f);
                        problem.setTransformationMethod(null);
                        problem.setBackgroundResource(R.drawable.message_button_background);
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        lp.setMargins(0, 0, 0, 20);
                        lp.gravity = Gravity.RIGHT;
                        problem.setLayoutParams(lp);
                        problem.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Button b = (Button) v;
                                TextView tv = createTextView(b.getText().toString(), MessageSender.USER);
                                linear.removeView(problem);
                                linear.removeView(learn);
                                index = index - 2;
                                linear.addView(tv, index);
                                index++;
                                need = 1;
                                startPhaseFour();
                            }
                        });
                        linear.addView(problem, index);
                        index++;
                        break;

                    case 5:
                        learn = new Button(getApplicationContext());
                        learn.setText("I want to learn from coach's area of expertise.");
                        learn.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
                        learn.setTextSize(16f);
                        learn.setTransformationMethod(null);
                        learn.setBackgroundResource(R.drawable.message_button_background);
                        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        lp2.setMargins(0, 0, 0, 20);
                        lp2.gravity = Gravity.RIGHT;
                        learn.setLayoutParams(lp2);
                        learn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Button b = (Button) v;
                                TextView tv = createTextView(b.getText().toString(), MessageSender.USER);
                                linear.removeView(problem);
                                linear.removeView(learn);
                                index = index - 2;
                                linear.addView(tv, index);
                                index++;
                                need = 2;
                                startPhaseThree();
                            }
                        });
                        linear.addView(learn, index);
                        index++;
                        scrollDown();
                        break;

                    default:
                        break;
                }

                tick++;
            }

            @Override
            public void onFinish() { }

        }.start();

    }

    public void startPhaseThree() {

        final LinearLayout layoutHorizontal = new LinearLayout(getApplicationContext());
        layoutHorizontal.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams lph = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lph.setMargins(0, 0, 0, 20);
        layoutHorizontal.setLayoutParams(lph);
        layoutHorizontal.setGravity(Gravity.CENTER);
        layoutHorizontal.setLayoutTransition(new LayoutTransition());

        final int buttonWidth = (layoutHorizontal.getWidth() - 20 * 2 - 10 * 4) / 3;

        /* ABOUT TIMER
           timer's time should be ticks needed + 1
           tick is 500 ms
           ticks after messages:
           1 user options
           2 messages
           4 long messages
         */

        new CountDownTimer(5500, 500) {

            int tick = 0;
            String message = "";
            Button beginner;
            Button intermediate;
            Button expert;

            @Override
            public void onTick(long millisUntilFinished) {
                switch(tick) {

                    case 2:
                        message = "I'm sure X can teach you a thing or two!";
                        linear.addView(createTextView(message, MessageSender.WIZARD), index);
                        index++;
                        break;

                    case 4:
                        message = "What kind of skill level do you think you have in C?";
                        linear.addView(createTextView(message, MessageSender.WIZARD), index);
                        index++;
                        break;

                    case 7:
                        linear.addView(layoutHorizontal, index);
                        index++;
                        beginner = new Button(getApplicationContext());
                        beginner.setText("Beginner");
                        beginner.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
                        beginner.setTextSize(16f);
                        beginner.setTransformationMethod(null);
                        beginner.setBackgroundResource(R.drawable.message_button_background);
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(buttonWidth, LinearLayout.LayoutParams.WRAP_CONTENT);
                        lp.setMargins(20, 0, 10, 0);
                        beginner.setLayoutParams(lp);
                        beginner.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Button b = (Button) v;
                                TextView tv = createTextView(b.getText().toString(), MessageSender.USER);
                                linear.removeView(layoutHorizontal);
                                index = index - 1;
                                linear.addView(tv, index);
                                index++;
                                skillLevel = 1;
                                scrollDown();
                                startPhaseFour();
                            }
                        });
                        layoutHorizontal.addView(beginner, 0);
                        break;

                    case 8:
                        intermediate = new Button(getApplicationContext());
                        intermediate.setText("Intermediate");
                        intermediate.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
                        intermediate.setTextSize(16f);
                        intermediate.setTransformationMethod(null);
                        intermediate.setBackgroundResource(R.drawable.message_button_background);
                        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(buttonWidth, LinearLayout.LayoutParams.WRAP_CONTENT);
                        lp2.setMargins(10, 0, 10, 0);
                        intermediate.setLayoutParams(lp2);
                        intermediate.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Button b = (Button) v;
                                TextView tv = createTextView(b.getText().toString(), MessageSender.USER);
                                linear.removeView(layoutHorizontal);
                                index = index - 1;
                                linear.addView(tv, index);
                                index++;
                                skillLevel = 2;
                                scrollDown();
                                startPhaseFour();
                            }
                        });
                        layoutHorizontal.addView(intermediate, 1);
                        break;

                    case 9:
                        expert = new Button(getApplicationContext());
                        expert.setText("Expert");
                        expert.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
                        expert.setTextSize(16f);
                        expert.setTransformationMethod(null);
                        expert.setBackgroundResource(R.drawable.message_button_background);
                        LinearLayout.LayoutParams lp3 = new LinearLayout.LayoutParams(buttonWidth, LinearLayout.LayoutParams.WRAP_CONTENT);
                        lp3.setMargins(10, 0, 20, 0);
                        expert.setLayoutParams(lp3);
                        expert.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Button b = (Button) v;
                                TextView tv = createTextView(b.getText().toString(), MessageSender.USER);
                                linear.removeView(layoutHorizontal);
                                index = index - 1;
                                linear.addView(tv, index);
                                index++;
                                skillLevel = 3;
                                scrollDown();
                                startPhaseFour();
                            }
                        });
                        layoutHorizontal.addView(expert, 2);
                        break;

                    default:
                        break;
                }

                tick++;
                scrollDown();
            }

            @Override
            public void onFinish() { }

        }.start();

    }

    public void startPhaseFour() {

        new CountDownTimer(5500, 500) {

            int tick = 0;
            String message = "";
            ImageButton send;

            @Override
            public void onTick(long millisUntilFinished) {

                switch(tick) {

                    case 2:
                        if(need == 1) {
                            message = "I doubt it's anything X can't solve!";
                        } else {
                            switch(skillLevel) {
                                case 1:
                                    message = "Great, you are like an open canvas ready to be filled!";
                                    break;

                                case 2:
                                    message = "Still room to improve, splendid!";
                                    break;

                                case 3:
                                    message = "Woah! Well, there is always something new to learn!";
                                    break;
                            }
                        }
                        linear.addView(createTextView(message, MessageSender.WIZARD), index);
                        index++;
                        break;

                    case 5:
                        if(need == 1) {
                            message = "Could you tell me more about your problem?";
                        } else {
                            message = "Could you tell me about what you want to learn during this coaching?";
                        }
                        linear.addView(createTextView(message, MessageSender.WIZARD), index);
                        index++;
                        break;

                    case 9:
                        inputView.setVisibility(View.VISIBLE);
                        send = (ImageButton) findViewById(R.id.order_chat_wizard_phase_4);
                        description.setOnKeyListener(new View.OnKeyListener() {
                            @Override
                            public boolean onKey(View v, int keyCode, KeyEvent event) {
                                if(description.getText().toString().trim().isEmpty()) {
                                    send.setColorFilter(Color.GRAY);
                                } else {
                                    send.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
                                }
                                return false;
                            }
                        });
                        send.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(!description.getText().toString().trim().isEmpty()) {
                                    message = description.getText().toString().trim();
                                    details = message;
                                    linear.addView(createTextView(message, MessageSender.USER), index);
                                    index++;
                                    inputView.setVisibility(View.GONE);
                                    hideKeyboard(v);
                                    startPhaseFive();
                                }
                            }
                        });
                        break;

                    default:
                        break;
                }

                tick++;
                scrollDown();
            }

            @Override
            public void onFinish() { }
        }.start();
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
        tv.setMaxWidth((linear.getWidth()/4) * 3);

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

    public void scrollDown() {
        ObjectAnimator.ofInt(scrollView, "scrollY",  scrollView.getBottom()).setDuration(1500).start();
    }

    public static float dpToPx(Context context, float valueInDp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, metrics);
    }

    public void hideKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    enum MessageSender {
        WIZARD, USER;
    }

}

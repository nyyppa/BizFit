package com.bizfit.bizfit.activities;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.bizfit.bizfit.ChatRequest;
import com.bizfit.bizfit.R;
import com.bizfit.bizfit.User;
import com.bizfit.bizfit.WizardMessageAdapter;
import com.bizfit.bizfit.fragments.TabConversationRequests;
import com.bizfit.bizfit.utils.Constants;

/**
 * Created by iipa on 16.3.2017.
 */

public class OrderChat extends ListActivity {

    private ListView listView;
    private WizardMessageAdapter mAdapter;
    private View inputView;

    private Phase currentPhase;
    private boolean proceed;
    private boolean optionsShown;

    private String COACH_ID;
    private Need need;
    private Skill skill;
    private String details;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_chat_wizard);

        COACH_ID = getIntent().getStringExtra(Constants.coach_id);

        listView = (ListView) findViewById(android.R.id.list);
        mAdapter = new WizardMessageAdapter(listView, this);
        setListAdapter(mAdapter);

        inputView = findViewById(R.id.container_input_details);
        inputView.setVisibility(View.INVISIBLE);

        if(savedInstanceState != null) {
            currentPhase = Phase.valueOf(savedInstanceState.getString("phase", Phase.BEGIN.toString()));
            need = Need.valueOf(savedInstanceState.getString("need", Need.UNDEFINED.toString()));
            skill = Skill.valueOf(savedInstanceState.getString("skill", Skill.UNDEFINED.toString()));
            details = savedInstanceState.getString("details", "");
            proceed = savedInstanceState.getBoolean("wantsToProceed", true);
            optionsShown = savedInstanceState.getBoolean("optionsShown", false);
        } else {
            currentPhase = Phase.BEGIN;
            optionsShown = false;
            proceed = true;
            need = Need.UNDEFINED;
            skill = Skill.UNDEFINED;
            details = "";
        }

        Button skip = (Button) findViewById(R.id.skipWizard);
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ChatRequest(User.getLastUser(null,null,null).userName, COACH_ID, need.LEARN.toString(), skill.INTERMEDIATE.toString(), "Many details").sendToNet();
                Intent intent = new Intent(OrderChat.this, MainPage.class);
                intent.putExtra("goToTab", 2);
                startActivity(intent);

            }
        });

        restorePhase();
    }

    public void goToPhase() {
        switch(currentPhase) {

            case BEGIN:
                phaseWelcome();
                break;
            case WELCOME:
                phaseProceed();
                break;
            case PROCEED:
                phaseNeed();
                break;
            case NEED:
                if(need == Need.PROBLEM) {
                    phaseDetails();
                } else {
                    phaseSkill();
                }
                break;
            case SKILL:
                phaseDetails();
                break;
            case DETAILS:
                phaseConfirm();
                break;
            case CONFIRM:
                phaseEnd();
                break;
            case END:
                // TODO: go to payment
                // before payment is created, send request and go to chat window
                new ChatRequest(User.getLastUser(null,null,null).userName, COACH_ID, need.toString(), skill.toString(), details).sendToNet();
                MessageActivity.startChat(listView, COACH_ID);
                break;
        }
    }

    public void restorePhase() {
        switch (currentPhase) {

            case BEGIN:
                goToPhase();
                break;

            case WELCOME:
                if(optionsShown) {
                    restorePhaseWelcome();
                    scrollDown();
                } else {
                    phaseWelcome();
                }
                break;

            case PROCEED:
                restorePhaseWelcome();
                scrollDown();
                phaseProceed();
                break;
            case NEED:
                restorePhaseWelcome();
                restorePhaseProceed();
                if(optionsShown) {
                    restorePhaseNeed();
                    scrollDown();
                } else {
                    scrollDown();
                    phaseNeed();
                }
                break;
            case SKILL:
                restorePhaseWelcome();
                restorePhaseProceed();
                restorePhaseNeed();
                if(optionsShown) {
                    restorePhaseSkill();
                    scrollDown();
                } else {
                    scrollDown();
                    phaseSkill();
                }
                break;
            case DETAILS:
                restorePhaseWelcome();
                restorePhaseProceed();
                restorePhaseNeed();
                restorePhaseSkill();
                if(optionsShown) {
                    restorePhaseDetails();
                    scrollDown();
                } else {
                    scrollDown();
                    phaseDetails();
                }
                break;
            case CONFIRM:
                restorePhaseWelcome();
                restorePhaseProceed();
                restorePhaseNeed();
                restorePhaseSkill();
                restorePhaseDetails();
                if(optionsShown) {
                    restorePhaseConfirm();
                    scrollDown();
                } else {
                    scrollDown();
                    phaseConfirm();
                }
                break;

            case END:
                restorePhaseWelcome();
                restorePhaseProceed();
                restorePhaseNeed();
                restorePhaseSkill();
                restorePhaseDetails();
                restorePhaseConfirm();
                if(optionsShown) {
                    restorePhaseEnd();
                    scrollDown();
                } else {
                    scrollDown();
                    phaseEnd();
                }
        }
    }

    public void phaseWelcome() {
        currentPhase = Phase.WELCOME;

        final String message1 = "Hello!";
        final String message2 = "It seems that you are interested in ordering chat coaching from X.";
        final String message3 = "Before you do, I would like to ask you some questions, that help the coach give you assistance in your needs.";
        final String message4 = "Shall we begin?";
        final String message5 = "Yes, please!";
        final String message6 = "No thanks, I changed my mind.";

        new CountDownTimer(11000, 1000) {

            int tick = 0;

            @Override
            public void onTick(long millisUntilFinished) {
                switch (tick) {
                    case 1:
                        mAdapter.addWizardMessage(message1);
                        break;

                    case 2:
                        mAdapter.addWizardMessage(message2);
                        break;

                    case 4:
                        mAdapter.addWizardMessage(message3);
                        break;

                    case 6:
                        mAdapter.addWizardMessage(message4);
                        break;

                    case 8:
                        mAdapter.addButton(message5, "WELCOME", "YES");
                        optionsShown = true;
                        break;

                    case 9:
                        mAdapter.addButton(message6, "WELCOME", "NO");
                        break;
                }

                tick++;
                scrollDown();
            }

            @Override
            public void onFinish() {

            }
        }.start();
    }

    public void restorePhaseWelcome() {
        String message1 = "Hello!";
        String message2 = "It seems that you are interested in ordering chat coaching from X.";
        String message3 = "Before you do, I would like to ask you some questions, that help the coach give you assistance in your needs.";
        String message4 = "Shall we begin?";
        String message5 = "Yes, please!";
        String message6 = "No thanks, I changed my mind.";

        mAdapter.addWizardMessage(message1);
        mAdapter.addWizardMessage(message2);
        mAdapter.addWizardMessage(message3);
        mAdapter.addWizardMessage(message4);

        if(currentPhase == Phase.WELCOME) {
            mAdapter.addButton(message5, "WELCOME", "YES");
            mAdapter.addButton(message6, "WELCOME", "NO");
        } else {
            if(proceed) {
                mAdapter.addUserMessage(message5);
            } else {
                mAdapter.addUserMessage(message6);
            }
        }

    }

    public void phaseProceed() {
        currentPhase = Phase.PROCEED;

        final String message1 = "Okay, let's get started!";
        final String message2 = "Oh, that's unfortunate. You can exit by pressing back button.";

        new CountDownTimer(1000, 500) {

            @Override
            public void onTick(long millisUntilFinished) { }

            @Override
            public void onFinish() {
                if(proceed) {
                    mAdapter.addWizardMessage(message1);
                    goToPhase();
                } else {
                    mAdapter.addWizardMessage(message2);
                }

                scrollDown();
            }
        }.start();


    }

    public void restorePhaseProceed() {
        final String message1 = "Okay, let's get started!";
        final String message2 = "Oh, that's unfortunate. You can exit by pressing back button.";

        if(proceed) {
            mAdapter.addWizardMessage(message1);
        } else {
            mAdapter.addWizardMessage(message2);
        }
    }

    public void phaseNeed() {
        currentPhase = Phase.NEED;

        final String message1 = "What do you want to achieve from this coaching?";
        final String message2 = "I have a problem I want to solve.";
        final String message3 = "I want to learn from coach's area of expertise.";

        new CountDownTimer(7000, 1000) {

            int tick = 0;

            @Override
            public void onTick(long millisUntilFinished) {
                switch(tick) {
                    case 2:
                        mAdapter.addWizardMessage(message1);
                        break;

                    case 4:
                        mAdapter.addButton(message2, "NEED", "PROBLEM");
                        optionsShown = true;
                        break;

                    case 5:
                        mAdapter.addButton(message3, "NEED", "LEARN");
                        break;
                }

                tick++;
                scrollDown();
            }

            @Override
            public void onFinish() { }

        }.start();
    }

    public void restorePhaseNeed() {
        final String message1 = "What do you want to achieve from this coaching?";
        final String message2 = "I have a problem I want to solve.";
        final String message3 = "I want to learn from coach's area of expertise.";

        mAdapter.addWizardMessage(message1);

        if(currentPhase == Phase.NEED) {
            mAdapter.addButton(message2, "NEED", "PROBLEM");
            mAdapter.addButton(message3, "NEED", "LEARN");
        } else {
            if(need == Need.PROBLEM) {
                mAdapter.addUserMessage(message2);
            } else {
                mAdapter.addUserMessage(message3);
            }
        }

    }

    public void phaseSkill() {
        currentPhase = Phase.SKILL;

        final String message1 = "I'm sure X can teach you a thing or two!";
        final String message2 = "What kind of skill level do you think you have in C?";
        final String message3 = "Beginner";
        final String message4 = "Intermediate";
        final String message5 = "Expert";

        new CountDownTimer(10000, 1000) {

            int tick = 0;

            @Override
            public void onTick(long millisUntilFinished) {
                switch(tick) {
                    case 2:
                        mAdapter.addWizardMessage(message1);
                        break;

                    case 4:
                        mAdapter.addWizardMessage(message2);
                        break;

                    case 6:
                        mAdapter.addButton(message3, "SKILL", "BEGINNER");
                        optionsShown = true;
                        break;

                    case 7:
                        mAdapter.addButton(message4, "SKILL", "INTERMEDIATE");
                        break;

                    case 8:
                        mAdapter.addButton(message5, "SKILL", "EXPERT");
                        break;
                }

                tick++;
                scrollDown();
            }

            @Override
            public void onFinish() { }

        }.start();
    }

    public void restorePhaseSkill() {
        if(need == Need.LEARN) {

            final String message1 = "I'm sure X can teach you a thing or two!";
            final String message2 = "What kind of skill level do you think you have in C?";
            final String message3 = "Beginner";
            final String message4 = "Intermediate";
            final String message5 = "Expert";

            mAdapter.addWizardMessage(message1);
            mAdapter.addWizardMessage(message2);

            if(currentPhase == Phase.SKILL) {
                mAdapter.addButton(message3, "SKILL", "BEGINNER");
                mAdapter.addButton(message4, "SKILL", "INTERMEDIATE");
                mAdapter.addButton(message5, "SKILL", "EXPERT");
            } else {
                if(skill == Skill.BEGINNER) {
                    mAdapter.addUserMessage(message3);
                } else if(skill == Skill.INTERMEDIATE) {
                    mAdapter.addUserMessage(message4);
                } else {
                    mAdapter.addUserMessage(message5);
                }
            }
        }
    }

    public void phaseDetails() {
        currentPhase = Phase.DETAILS;

        final String message1 = "I doubt it's anything X can't solve!";
        final String message2 = "Great, you are like an open canvas ready to be filled!";
        final String message3 = "Still room to improve, splendid!";
        final String message4 = "Woah! Well, there is always something new to learn!";
        final String message5 = "Could you tell me more about your problem?";
        final String message6 = "Could you tell me about what you want to learn during this coaching?";

        new CountDownTimer(8000, 1000) {

            int tick = 0;

            @Override
            public void onTick(long millisUntilFinished) {
                switch(tick) {
                    case 2:
                        if(need == Need.PROBLEM) {
                            mAdapter.addWizardMessage(message1);
                        } else {
                            if(skill == Skill.BEGINNER) {
                                mAdapter.addWizardMessage(message2);
                            } else if(skill == Skill.INTERMEDIATE) {
                                mAdapter.addWizardMessage(message3);
                            } else {
                                mAdapter.addWizardMessage(message4);
                            }
                        }
                        break;

                    case 4:
                        if(need == Need.PROBLEM) {
                            mAdapter.addWizardMessage(message5);
                        } else {
                            mAdapter.addWizardMessage(message6);
                        }
                        break;

                    case 6:
                        setUpInputView();
                        break;
                }

                tick++;
                scrollDown();
            }

            @Override
            public void onFinish() { }

        }.start();

    }

    public void restorePhaseDetails() {

        final String message1 = "I doubt it's anything X can't solve!";
        final String message2 = "Great, you are like an open canvas ready to be filled!";
        final String message3 = "Still room to improve, splendid!";
        final String message4 = "Woah! Well, there is always something new to learn!";
        final String message5 = "Could you tell me more about your problem?";
        final String message6 = "Could you tell me about what you want to learn during this coaching?";

        if(need == Need.PROBLEM) {
            mAdapter.addWizardMessage(message1);
            mAdapter.addWizardMessage(message5);
        } else {
            if(skill == Skill.BEGINNER) {
                mAdapter.addWizardMessage(message2);
            } else if(skill == Skill.INTERMEDIATE) {
                mAdapter.addWizardMessage(message3);
            } else {
                mAdapter.addWizardMessage(message4);
            }
            mAdapter.addWizardMessage(message6);
        }

        if(currentPhase == Phase.DETAILS) {
            setUpInputView();
        } else {
            mAdapter.addUserMessage(details);
        }

    }

    public void setUpInputView() {
        final ImageButton send = (ImageButton) findViewById(R.id.order_chat_wizard_send_details);
        final EditText description = (EditText) findViewById(R.id.order_chat_wizard_details);

        send.setVisibility(View.INVISIBLE);
        inputView.setVisibility(View.VISIBLE);

        description.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(description.getText().toString().trim().isEmpty()) {
                    send.setVisibility(View.INVISIBLE);
                } else {
                    send.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!description.getText().toString().trim().isEmpty()) {
                    String message = description.getText().toString().trim();
                    details = message;
                    mAdapter.addUserMessage(message);
                    scrollDown();

                    inputView.setVisibility(View.INVISIBLE);
                    hideKeyboard(v);
                    goToPhase();
                }
            }
        });
    }

    public void phaseConfirm() {
        currentPhase = Phase.CONFIRM;

        final String message1 = "Alright!";
        final String message2 = "Thank you for being patient with me.";
        final String message3 = "You've given me and X good information to plan your coaching.";
        final String message4 = "I have only one thing that I need to ask you now.";
        final String message5 = "There are some terms and conditions that you need to accept to make this coaching a good experience for you and your coach.";
        final String message6 = "I send them to you now. Read them through and we are ready to send the coaching request!";
        final String message7 = "Terms and conditions";
        final String message8 = "I accept the terms and conditions";

        new CountDownTimer(17000, 1000) {

            int tick = 0;

            @Override
            public void onTick(long millisUntilFinished) {
                switch(tick) {
                    case 2:
                        mAdapter.addWizardMessage(message1);
                        break;

                    case 4:
                        mAdapter.addWizardMessage(message2);
                        break;

                    case 6:
                        mAdapter.addWizardMessage(message3);
                        break;

                    case 8:
                        mAdapter.addWizardMessage(message4);
                        break;

                    case 10:
                        mAdapter.addWizardMessage(message5);
                        break;

                    case 12:
                        mAdapter.addWizardMessage(message6);
                        break;

                    case 14:
                        mAdapter.addButton(message7, "CONFIRM", "TERMS");
                        break;

                    case 15:
                        mAdapter.addButton(message8, "CONFIRM", "ACCEPT");
                        break;
                }

                tick++;
                scrollDown();
            }

            @Override
            public void onFinish() { }
        }.start();

    }

    public void restorePhaseConfirm() {

        final String message1 = "Alright!";
        final String message2 = "Thank you for being patient with me.";
        final String message3 = "You've given me and X good information to plan your coaching.";
        final String message4 = "I have only one thing that I need to ask you now.";
        final String message5 = "There are some terms and conditions that you need to accept to make this coaching a good experience for you and your coach.";
        final String message6 = "I send them to you now. Read them through and we are ready to send the coaching request!";
        final String message7 = "Terms and conditions";
        final String message8 = "I accept the terms and conditions";

        mAdapter.addWizardMessage(message1);
        mAdapter.addWizardMessage(message2);
        mAdapter.addWizardMessage(message3);
        mAdapter.addWizardMessage(message4);
        mAdapter.addWizardMessage(message5);
        mAdapter.addWizardMessage(message6);
        mAdapter.addButton(message7, "CONFIRM", "TERMS");

        if(currentPhase == Phase.CONFIRM) {
            mAdapter.addButton(message8, "CONFIRM", "ACCEPT");
        } else {
            mAdapter.addUserMessage(message8);
        }

    }

    public void phaseEnd() {
        currentPhase = Phase.END;

        final String message1 = "Great!";
        final String message2 = "My work here is done.";
        final String message3 = "I'll give you the button to continue to payment and your request will be sent!";
        final String message4 = "Bye for now, have a great day!";
        final String message5 = "Bye bye! (Exit wizard)";

        new CountDownTimer(12000, 1000) {

            int tick = 0;

            @Override
            public void onTick(long millisUntilFinished) {
                switch(tick) {
                    case 2:
                        mAdapter.addWizardMessage(message1);
                        break;
                    case 4:
                        mAdapter.addWizardMessage(message2);
                        break;
                    case 6:
                        mAdapter.addWizardMessage(message3);
                        break;
                    case 8:
                        mAdapter.addWizardMessage(message4);
                        break;
                    case 10:
                        mAdapter.addButton(message5, "END", "BYE");
                }

                tick++;
                scrollDown();
            }

            @Override
            public void onFinish() { }
        }.start();
    }

    public void restorePhaseEnd() {

        final String message1 = "Great!";
        final String message2 = "My work here is done.";
        final String message3 = "I'll give you the button to continue to payment and your request will be sent!";
        final String message4 = "Bye for now, have a great day!";
        final String message5 = "Bye bye! (Exit wizard)";

        mAdapter.addWizardMessage(message1);
        mAdapter.addWizardMessage(message2);
        mAdapter.addWizardMessage(message3);
        mAdapter.addWizardMessage(message4);
        mAdapter.addButton(message5, "END", "BYE");
    }

    public void handleAnswer(String answer) {
        boolean moveAfter = true;

        switch(currentPhase) {
            case BEGIN:
                // no answers to be handled
                break;

            case WELCOME:
                if(answer.equals("YES")) {
                    proceed = true;
                    mAdapter.addUserMessage("Yes, please!");
                } else {
                    proceed = false;
                    mAdapter.addUserMessage("No thanks, I changed my mind.");
                }
                break;

            case PROCEED:
                // no answers to be handled
                break;

            case NEED:
                if(answer.equals("PROBLEM")) {
                    need = Need.PROBLEM;
                    mAdapter.addUserMessage("I have a problem I want to solve.");
                } else {
                    need = Need.LEARN;
                    mAdapter.addUserMessage("I want to learn from coach's area of expertise.");
                }
                break;

            case SKILL:
                if(answer.equals("BEGINNER")) {
                    skill = Skill.BEGINNER;
                    mAdapter.addUserMessage("Beginner");
                } else if(answer.equals("INTERMEDIATE")) {
                    skill = Skill.INTERMEDIATE;
                    mAdapter.addUserMessage("Intermediate");
                } else {
                    skill = Skill.EXPERT;
                    mAdapter.addUserMessage("Expert");
                }
                break;

            case DETAILS:
                // no answers to be handled
                break;

            case CONFIRM:
                if(answer.equals("TERMS")) {
                    moveAfter = false;
                    // TODO: open terms and conditions popup
                } else {
                    mAdapter.addUserMessage("I accept the terms and conditions");
                }
                break;

            case END:
                // no answers to be handled
                break;
        }

        if(moveAfter) {
            optionsShown = false;
            goToPhase();
        }
    }

    public void scrollDown() {
        listView.smoothScrollToPosition(mAdapter.getCount());
    }

    public void hideKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString("phase", currentPhase.toString());
        savedInstanceState.putString("need", need.toString());
        savedInstanceState.putString("skill", skill.toString());
        savedInstanceState.putString("details", details);
        savedInstanceState.putBoolean("proceed", proceed);
        savedInstanceState.putBoolean("optionsShown", optionsShown);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // Always call the superclass so it can restore the view hierarchy
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public ListView getListView() {
        return listView;
    }

    enum Phase {
        BEGIN, WELCOME, PROCEED, NEED, SKILL, DETAILS, CONFIRM, END
    }

    enum Need {
        UNDEFINED, PROBLEM, LEARN
    }

    enum Skill {
        UNDEFINED, BEGINNER, INTERMEDIATE, EXPERT
    }
}

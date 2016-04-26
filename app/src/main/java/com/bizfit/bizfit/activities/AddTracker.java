package com.bizfit.bizfit.activities;

import android.animation.ObjectAnimator;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import com.bizfit.bizfit.R;
import com.bizfit.bizfit.utils.FieldNames;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import uz.shift.colorpicker.LineColorPicker;

/**
 * Takes user input for creation of a new goal.
 */
public class AddTracker extends AppCompatActivity implements View.OnClickListener, View.OnFocusChangeListener {

    /**
     * Field for goals name.
     */
    private static AutoCompleteTextView name;

    /**
     * Field for goal's target date.
     */
    private static EditText date;

    /**
     * Field for target amount.
     */
    private static EditText target;

    /**
     * Is the user attempting to exit using the back button.
     */
    private boolean exitedViaBackButton = false;

    /***
     * Pop up for selecting a date.
     */
    private static DatePickerDialog datePicker;

    /**
     * Formats the selected date for String display.
     */
    private static SimpleDateFormat dateFormatter;

    /**
     * Selected date from the popup.
     */
    private static Calendar setDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_tracker);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_view_tracker);
        setSupportActionBar(toolbar);

        name = (AutoCompleteTextView) findViewById(R.id.target_name);
        date = (EditText) findViewById(R.id.target_date);
        target = (EditText) findViewById(R.id.target_amount);

        dateFormatter = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        date.setOnClickListener(this);
        date.setInputType(InputType.TYPE_NULL);
        date.setOnFocusChangeListener(this);

        Calendar newCalendar = Calendar.getInstance();

        datePicker = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear
                    , int dayOfMonth) {
                setDate = Calendar.getInstance();
                setDate.set(year, monthOfYear, dayOfMonth);
                date.setText(dateFormatter.format(setDate.getTime()));
                date.clearFocus();
            }

        }, newCalendar.get(Calendar.YEAR)
                , newCalendar.get(Calendar.MONTH)
                , newCalendar.get(Calendar.DAY_OF_MONTH));

        datePicker.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);

        datePicker.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(date.getWindowToken(), 0);
                date.clearFocus();
            }
        });

        // Get the string array
        String[] options = getResources().getStringArray(R.array.tracker_name_suggestions);
        // Create the adapter and set it to the AutoCompleteTextView
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, options);
        name.setAdapter(adapter);
        name.setThreshold(1);

        // Set's up custom elevated button to have on click behaviour
        final CardView cardView = (CardView) findViewById(R.id.button_done_container);
        final Button button = (Button) findViewById(R.id.button_done);
        button.setOnTouchListener(new View.OnTouchListener() {
            ObjectAnimator o1 = ObjectAnimator.ofFloat(cardView, "cardElevation", 2, 8)
                    .setDuration
                            (80);
            ObjectAnimator o2 = ObjectAnimator.ofFloat(cardView, "cardElevation", 8, 2)
                    .setDuration
                            (80);

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        o1.start();
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        o2.start();
                        break;
                }
                return false;
            }
        });
    }

    @Override
    public void finish() {

        Intent returnIntent = new Intent();

        // Pack user input if it's valid for creating a new Tracker.
        if (!exitedViaBackButton) {
            returnIntent.putExtra(FieldNames.TRACKERNAME
                    , name.getText().toString());
            returnIntent.putExtra(FieldNames.TARGET
                    , Float.parseFloat(target.getText().toString()));
            returnIntent.putExtra(FieldNames.DAY, setDate.get(Calendar.DAY_OF_MONTH));
            returnIntent.putExtra(FieldNames.MONTH, setDate.get(Calendar.MONTH));
            returnIntent.putExtra(FieldNames.YEAR, setDate.get(Calendar.YEAR));
            int color = ((LineColorPicker) findViewById(R.id.color_picker)).getColor();
            returnIntent.putExtra(FieldNames.COLOR, color);
            setResult(RESULT_OK, returnIntent);
        } else {
            setResult(RESULT_CANCELED);
        }

        super.finish();
    }

    @Override
    public void onBackPressed() {
        exitedViaBackButton = true;
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.target_name:
                showDatePicker();
                break;

            case R.id.button_done:
                attemptToFinish();
                break;

            case R.id.target_date:
                datePicker.show();
                break;

            case R.id.button_done_container:
                attemptToFinish();
                break;
        }
    }

    /**
     * Checks if fields contain valid input.
     */
    private void attemptToFinish() {
        //Check input validity
        boolean nameEmpty = isEmpty(name);
        boolean targetEmpty = isEmpty(target);
        boolean dateEmpty = isEmpty(date);

        // Proceed to create a new tracker if all in order. Displays error
        // messages otherwise.
        if (!nameEmpty && !targetEmpty && !dateEmpty) {
            finish();
        } else {
            if (nameEmpty) name.setError(getString(R.string.error_empty_field));
            if (targetEmpty) target.setError(getString(R.string.error_empty_field));
            if (dateEmpty) date.setError(getString(R.string.error_empty_field));
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        int id = v.getId();
        switch (id) {
            case R.id.target_date:
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(date.getWindowToken(), 0);
                if (hasFocus) showDatePicker();
                break;

            case R.id.target_name:
                break;

            case R.id.target_amount:
                break;
        }
    }

    /**
     * Displays a date picker.
     */
    private void showDatePicker() {
        datePicker.show();
        date.setError(null);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(date.getWindowToken(), 0);
    }

    /**
     * Determines if text field is empty or contains only whitespace.
     *
     * @param et Text field to check.
     * @return Is the field empty.
     */
    private boolean isEmpty(EditText et) {
        return !(et.getText().toString().trim().length() > 0);
    }
}

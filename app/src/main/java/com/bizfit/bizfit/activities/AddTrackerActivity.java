package com.bizfit.bizfit.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.bizfit.bizfit.R;
import com.bizfit.bizfit.utils.FieldNames;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

/**
 * Created by Käyttäjä on 19.12.2015.
 */
public class AddTrackerActivity extends AppCompatActivity implements View.OnClickListener, View.OnFocusChangeListener {
    public static Activity activity = null;
    private static EditText name;
    private static EditText date;
    private static EditText target;
    private static CheckBox checkBox;
    private static View container;

    private static DatePickerDialog datePicker;
    private static SimpleDateFormat dateFormatter;
    private static Calendar setDate;

    private static int year;
    private static int month;
    private static int day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        setContentView(R.layout.activity_add_tracker);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        name = (EditText) findViewById(R.id.target_name);
        date = (EditText) findViewById(R.id.target_date);
        target = (EditText) findViewById(R.id.target_amount);
        checkBox = (CheckBox) findViewById(R.id.recurring);
        container = findViewById(R.id.add_tracker_nested_scroll_view);
        name.requestFocus();
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
                date.clearFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(date.getWindowToken(), 0);
            }
        });
    }

    @Override
    public void finish() {

        Intent returnIntent = new Intent();

        if (!isEmpty(name) && !isEmpty(date) && !isEmpty(target)) {
            returnIntent.putExtra(FieldNames.TRACKERNAME
                    , name.getText().toString());
            returnIntent.putExtra(FieldNames.TARGET
                    , Float.parseFloat(target.getText().toString()));
            returnIntent.putExtra(FieldNames.DAY, setDate.get(Calendar.DAY_OF_MONTH));
            returnIntent.putExtra(FieldNames.MONTH, setDate.get(Calendar.MONTH));
            returnIntent.putExtra(FieldNames.YEAR, setDate.get(Calendar.YEAR));
            returnIntent.putExtra(FieldNames.RECURRING, checkBox.isChecked());
            returnIntent.putExtra(FieldNames.COLOR, randomColor());
            setResult(Activity.RESULT_OK, returnIntent);
        } else {
            setResult(Activity.RESULT_CANCELED, returnIntent);
        }

        super.finish();
    }

    @Override
    public void onClick(View v) {
        if (v.equals(date)) {
            showDatePicker();
        } else if (v instanceof Button) {
            finish();
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (v.equals(date)) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(date.getWindowToken(), 0);
            if(hasFocus) showDatePicker();
        }
    }

    private void showDatePicker() {
        datePicker.show();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(date.getWindowToken(), 0);
    }



    private boolean isEmpty(EditText etText) {
        if (etText.getText().toString().trim().length() > 0) {
            return false;
        } else {
            return true;
        }
    }

    private int randomColor() {
        int[] androidColors = getResources().getIntArray(R.array.trackable_view_alt_colors);
        int randomAndroidColor = androidColors[new Random().nextInt(androidColors.length)];
        return randomAndroidColor;
    }
}

package com.bizfit.bizfit.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import com.bizfit.bizfit.R;
import com.bizfit.bizfit.utils.FieldNames;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Käyttäjä on 19.12.2015.
 */
public class AddTrackerActivity extends AppCompatActivity implements View.OnClickListener {
    public static Activity activity = null;
    private static EditText name;
    private static EditText date;
    private static EditText target;
    private static CheckBox checkBox;

    private static DatePickerDialog datePicker;
    private static SimpleDateFormat dateFormatter;
    private static Calendar setDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        setContentView(R.layout.activity_add_tracker);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(
                R.id.fab);
        name = (EditText) findViewById(R.id.target_name);
        date = (EditText) findViewById(R.id.target_date);
        target = (EditText) findViewById(R.id.target_amount);
        checkBox = (CheckBox) findViewById(R.id.recurring);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        dateFormatter = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        date.setOnClickListener(this);
        date.setInputType(InputType.TYPE_NULL);

        Calendar newCalendar = Calendar.getInstance();

        datePicker = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear
                    , int dayOfMonth) {
                setDate = Calendar.getInstance();
                setDate.set(year, monthOfYear, dayOfMonth);
                date.setText(dateFormatter.format(setDate.getTime()));
            }

        }, newCalendar.get(Calendar.YEAR)
                , newCalendar.get(Calendar.MONTH)
                , newCalendar.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public void finish() {

        Intent returnIntent = new Intent();

        if (!isEmpty(name) && !isEmpty(date)&& !isEmpty(target)) {
            returnIntent.putExtra(FieldNames.TRACKERNAME
                    , name.getText().toString());
            returnIntent.putExtra(FieldNames.TARGET
                    , Float.parseFloat(target.getText().toString()));
            returnIntent.putExtra(FieldNames.DAY, setDate.get(Calendar.DAY_OF_MONTH));
            returnIntent.putExtra(FieldNames.MONTH, setDate.get(Calendar.MONTH));
            returnIntent.putExtra(FieldNames.YEAR, setDate.get(Calendar.YEAR));
            returnIntent.putExtra(FieldNames.RECURRING, checkBox.isChecked());
            setResult(Activity.RESULT_OK, returnIntent);
        } else {
            setResult(Activity.RESULT_CANCELED, returnIntent);
        }

        super.finish();
    }

    @Override
    public void onClick(View v) {
        if (v.equals(date)) {
            datePicker.show();
        }
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            Calendar newDate = Calendar.getInstance();
            newDate.set(year, month, day);
            date.setText(dateFormatter.format(newDate.getTime()));
        }
    }

    private boolean isEmpty(EditText etText) {
        if (etText.getText().toString().trim().length() > 0) {
            return false;
        } else {
            return true;
        }
    }
}

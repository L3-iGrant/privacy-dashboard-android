package io.igrant.igrant_org_sdk.customViews;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.view.View;
import android.widget.DatePicker;
import io.igrant.igrant_org_sdk.R;
import io.igrant.igrant_org_sdk.utils.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;


/**
 * Created by sanif on 08/05/18.
 */


public class EditTextDatePicker implements DatePickerDialog.OnDateSetListener {

    private String minDate;
    CustomEditText _textView;
    private int _day;
    private int _month;
    private int _birthYear;
    private String _hint = "";
    private Context _context;
    private DatePickerListner datePickerListner;

    public EditTextDatePicker(Context context, View editTextViewID) {
        this._textView = (CustomEditText) editTextViewID;
        this._context = context;

        Calendar calendar = Calendar.getInstance(TimeZone.getDefault(), Locale.US);

        DatePickerDialog dialog = new DatePickerDialog(_context, R.style.datepicker, this,
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        dialog.getDatePicker().setMaxDate(new Date().getTime());
        dialog.show();
    }

    public EditTextDatePicker(Context context, View editTextViewID, String minDate) {
        Activity act = (Activity) context;
        this._textView = (CustomEditText) editTextViewID;
        this._context = context;
        this.minDate = minDate;
        Calendar cal = Calendar.getInstance(TimeZone.getDefault(), Locale.US);
        try {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat(DateUtils.DDMMYYYY);
            Date mDate = dateFormat.parse(String.valueOf(minDate));
            cal.setTime(mDate);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar calendar = Calendar.getInstance(TimeZone.getDefault(), Locale.US);

        DatePickerDialog dialog = new DatePickerDialog(_context, R.style.datepicker, this,
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        dialog.getDatePicker().setMaxDate(new Date().getTime());
        dialog.getDatePicker().setMinDate(cal.getTimeInMillis());
        dialog.show();
    }


    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        _birthYear = year;
        _month = monthOfYear;
        _day = dayOfMonth;
        updateDisplay();
    }


    // updates the date in the birth date EditText
    private void updateDisplay() {

        _textView.setError(null);
        _textView.setText(new StringBuilder()
                .append(_hint)
                // Month is 0 based so add 1
                // '/' changed to '-'
                .append(_day < 10 ? 0 : "").append(_day).append("-").append(_month + 1 < 10 ? 0 : "").append(_month + 1).append("-").append(_birthYear));

//        datePickerListner.onDateSet();
    }

    public interface DatePickerListner {
        void onDateSet();
    }
}


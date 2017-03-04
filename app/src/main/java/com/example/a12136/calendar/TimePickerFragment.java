package com.example.a12136.calendar;

/**
 * Created by 56950 on 2016/12/16.
 */

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.app.DialogFragment;
import android.app.Dialog;
import java.util.Calendar;
import android.widget.TimePicker;

public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener{

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        //Use the current time as the default values for the time picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);


        //Create and return a new instance of TimePickerDialog
        /*
            public constructor.....
            TimePickerDialog(Context context, int theme,
            TimePickerDialog.OnTimeSetListener callBack, int hourOfDay, int minute, boolean is24HourView)

            The 'theme' parameter allow us to specify the theme of TimePickerDialog

            .......List of Themes.......
            THEME_DEVICE_DEFAULT_DARK
            THEME_DEVICE_DEFAULT_LIGHT
            THEME_HOLO_DARK
            THEME_HOLO_LIGHT
            THEME_TRADITIONAL

         */
        TimePickerDialog tpd = new TimePickerDialog(getActivity(), AlertDialog.THEME_DEVICE_DEFAULT_DARK
                ,this, hour, minute, DateFormat.is24HourFormat(getActivity()));

        /*.........Set a custom title for picker........*/
        TextView tvTitle = new TextView(getActivity());

        if (getTag().contains("start")) {
            tvTitle.setText("开始时间");
        } else {
            tvTitle.setText("结束时间");
        }
        tvTitle.setBackgroundColor(Color.parseColor("#80CBC4"));
        tvTitle.setTextColor(Color.parseColor("#FFFFFF"));
        tvTitle.setPadding(5, 3, 5, 3);
        tvTitle.setGravity(Gravity.CENTER_HORIZONTAL);
        tpd.setCustomTitle(tvTitle);
        /*.........End custom title section........*/

        return tpd;
    }

    //onTimeSet() callback method
    public void onTimeSet(TimePicker view, int hourOfDay, int minuteOfHour){
        //Do something with the user chosen time
        //Get reference of host activity (XML Layout File) TextView widget

        String hour, minute;
        if (hourOfDay < 10) {
            hour = "0"+hourOfDay;
        } else {
            hour = ""+hourOfDay;
        }
        if (minuteOfHour < 10) {
            minute = "0"+minuteOfHour;
        } else {
            minute = ""+minuteOfHour;
        }
        TextView tv;
        if (getTag()=="add-start") {
            tv = (TextView) getActivity().findViewById(R.id.start_plan_time_tv);
        } else if (getTag()=="add-end"){
            tv = (TextView) getActivity().findViewById(R.id.end_plan_time_tv);
        } else if (getTag()=="main-start") {
            MainActivity mainActivity = (MainActivity) getActivity();
            tv = (TextView) mainActivity.layout.findViewById(R.id.start_plan_time_tv);
        } else if (getTag()=="main-end") {
            MainActivity mainActivity = (MainActivity) getActivity();
            tv = (TextView) mainActivity.layout.findViewById(R.id.end_plan_time_tv);
        } else {
            tv = new TextView(getActivity());
        }
        tv.setText(hour + ":" + minute);

    }
}



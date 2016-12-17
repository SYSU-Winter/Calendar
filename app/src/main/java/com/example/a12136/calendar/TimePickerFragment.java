package com.example.a12136.calendar;

/**
 * Created by 56950 on 2016/12/16.
 */

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.widget.TextView;
import android.app.DialogFragment;
import android.app.Dialog;
import java.util.Calendar;
import android.widget.TimePicker;

public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener{

    private boolean startTimeOrEndTime;
    private AddPlanActivity context;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        //Use the current time as the default values for the time picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        context = (AddPlanActivity) getActivity();
        startTimeOrEndTime = context.setStartTimeOrEndTime;

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

        if (startTimeOrEndTime) {
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
    public void onTimeSet(TimePicker view, int hourOfDay, int minute){
        //Do something with the user chosen time
        //Get reference of host activity (XML Layout File) TextView widget

        //select startTime
        if (startTimeOrEndTime) {
            TextView tv = (TextView) context.findViewById(R.id.start_plan_time_tv);
            tv.setText(String.valueOf(hourOfDay) + ":" + String.valueOf(minute));
        }
        //select endTime
        else {
            TextView tv = (TextView) context.findViewById(R.id.end_plan_time_tv);
            tv.setText(String.valueOf(hourOfDay) + ":" + String.valueOf(minute));
        }

    }
}



package com.example.a12136.calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DialogFragment;

import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.app.AlertDialog;
/**
 * Created by 12136 on 2016/12/15.
 */

public class AddPlanActivity extends Activity implements View.OnClickListener {
    private TextView mCancelTv;
    private TextView mConfirmTv;
    private CustomDate mCustomDate;
    private TextView mPlanContentTv;
    private TextView mStartPlanTimeTv;
    private TextView mEndPlanTimeTv;
    private TextView mNoCancelPlanTv;
    private TextView mConfirmCancelPlanTv;
    private View mShowDialogLayout;
    private View mCancelDialogLayout;
    private myDB db;

    public boolean setStartTimeOrEndTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_plan);
        getIntentData();
        findById();

        db = new myDB(AddPlanActivity.this);
    }

    private void findById() {
        mCancelTv = (TextView) this.findViewById(R.id.cancel_tv);
        mConfirmTv = (TextView)this.findViewById(R.id.confirm_tv);
        mPlanContentTv = (TextView)this.findViewById(R.id.plan_content_tv);
        mStartPlanTimeTv = (TextView)this.findViewById(R.id.start_plan_time_tv);
        mEndPlanTimeTv = (TextView)this.findViewById(R.id.end_plan_time_tv);
        mNoCancelPlanTv = (TextView)this.findViewById(R.id.no_cancel_plan_tv);
        mConfirmCancelPlanTv = (TextView)this.findViewById(R.id.confirm_cancel_plan_tv);
        mShowDialogLayout = this.findViewById(R.id.dialog_show_layout);
        mCancelDialogLayout = this.findViewById(R.id.cancel_dialog_layout);
        setOnClickListener();
    }

    private void setOnClickListener() {

        mCancelTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCancelDialog();
            }
        });

        mConfirmTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String []startTime = mStartPlanTimeTv.getText().toString().split(":");
                String []endTime = mEndPlanTimeTv.getText().toString().split(":");

                if (TextUtils.isEmpty(mPlanContentTv.getText())) {
                    Toast.makeText(AddPlanActivity.this, "活动内容不能为空", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.equals(mStartPlanTimeTv.getText(), "未设置")
                        || TextUtils.equals(mEndPlanTimeTv.getText(), "未设置")) {
                    Toast.makeText(AddPlanActivity.this, "请检查时间设置", Toast.LENGTH_SHORT).show();
                } else if ( (60*Integer.parseInt(startTime[0])+Integer.parseInt(startTime[1]))
                            > (60*Integer.parseInt(endTime[0])+Integer.parseInt(endTime[1])) ) {
                        Toast.makeText(AddPlanActivity.this, "开始时间不能大于结束时间", Toast.LENGTH_SHORT).show();
                } else {
                    CustomDate currentDate =(CustomDate) getIntent().getSerializableExtra(MainActivity.MAIN_ACTIVITY_CLICK_DATE);
                    String currentDate_str = currentDate.toString();
                    //插入数据
                    db.insert2DB(mPlanContentTv.getText().toString(), currentDate_str,
                            mStartPlanTimeTv.getText().toString(), mEndPlanTimeTv.getText().toString());
//                    //设置提醒功能
//                    String []currentDate_divide = currentDate_str.split("-");
//                    setAlarmClock(Integer.parseInt(currentDate_divide[0]),  //year
//                            Integer.parseInt(currentDate_divide[1]),    //month
//                            Integer.parseInt(currentDate_divide[2]),    //date
//                            Integer.parseInt(startTime[0]),     //hour
//                            Integer.parseInt(startTime[1]));    //minute
                    finish();
//                    Cursor c = db.getDataBase().rawQuery("SELECT * FROM activity", null);
//        c.moveToFirst();
//
//        int resultCount = c.getCount();
//        Log.d("db", "count:"+resultCount);
//
//        for (int i = 0; i < resultCount; i++) {
//
//            int _id = c.getInt(c.getColumnIndex("_id"));
//            String name = c.getString(c.getColumnIndex("activity_name"));
//            String date = c.getString(c.getColumnIndex("date"));
//            Log.d("db", "_id=>" + _id + ", name=>" + name + ", age=>" + date);
//            c.moveToNext();
//        }
//        c.close();
                }
            }
        });

        mStartPlanTimeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment newFragment = new TimePickerFragment();
                newFragment.show(getFragmentManager(),"TimePicker");
                setStartTimeOrEndTime = true;
            }
        });

        mEndPlanTimeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment newFragment = new TimePickerFragment();
                newFragment.show(getFragmentManager(),"TimePicker");
                setStartTimeOrEndTime = false;
            }
        });
    }


    private void getIntentData(){
        mCustomDate = (CustomDate)getIntent()
                .getSerializableExtra(MainActivity.MAIN_ACTIVITY_CLICK_DATE);
    }
    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
    }

    @Override
    public void onClick(View v) {
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Log.d("db","back");
            showCancelDialog();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void showCancelDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);  //先得到构造器
        builder.setTitle("提示"); //设置标题
        builder.setMessage("是否退出设置?"); //内容
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() { //设置确定按钮
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() { //设置取消按钮
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        //参数都设置完成了，创建并显示出来
        builder.show();

    }

//    private void setAlarmClock(int year, int month, int date, int hour, int minute) {
//        // 进行闹铃注册
//        Log.d("db","time:"+year+"-"+month+"-"+date+"-"+hour+"-"+minute);
//
//        Intent intent = new Intent(AddPlanActivity.this, AlarmReceiver.class);
//        PendingIntent sender = PendingIntent.getBroadcast(AddPlanActivity.this, 0, intent, 0);
//
//        // 过一定时间 执行这个闹铃
////        Calendar calendar = Calendar.getInstance();
////        calendar.set(year, month, date, hour, minute);
////
////        Log.d("db", "time:"+calendar.getTimeInMillis());
////        AlarmManager manager = (AlarmManager)getSystemService(ALARM_SERVICE);
////        manager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);
//
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTimeInMillis(System.currentTimeMillis());
//        calendar.add(Calendar.SECOND, 10);
//
//        AlarmManager manager = (AlarmManager)getSystemService(ALARM_SERVICE);
//        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, calendar.getTimeInMillis(), sender);
//    }
}
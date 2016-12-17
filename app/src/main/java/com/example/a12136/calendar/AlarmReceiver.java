package com.example.a12136.calendar;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by 56950 on 2016/12/17.
 */

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("db","alarm received");

//        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//        Notification notification=new Notification(R.drawable.circle,"用电脑时间过长了！白痴！"
//                ,System.currentTimeMillis());
//        notification.defaults = Notification.DEFAULT_ALL;
//        manager.notify(1, notification);
        Toast.makeText(context, "闹铃响了, 可以做点事情了~~", Toast.LENGTH_LONG).show();
    }

}

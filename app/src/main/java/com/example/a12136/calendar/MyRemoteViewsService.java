package com.example.a12136.calendar;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.widget.RemoteViewsService;

/**
 * Created by 56950 on 2017/1/2.
 */

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class MyRemoteViewsService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new MyRemoteViewsFactory(this.getApplicationContext(), intent);
    }

}

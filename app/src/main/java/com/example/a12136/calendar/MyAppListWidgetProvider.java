package com.example.a12136.calendar;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * Created by 56950 on 2017/1/2.
 */

public class MyAppListWidgetProvider extends AppWidgetProvider {
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {
        // 获取Widget的组件名
        ComponentName thisWidget = new ComponentName(context,
                MyAppListWidgetProvider.class);

        // 创建一个RemoteView
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                R.layout.widget_demo);

        // 把这个Widget绑定到RemoteViewsService
        Intent intent = new Intent(context, MyRemoteViewsService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[0]);

        // 设置适配器
        remoteViews.setRemoteAdapter(R.id.widget_list, intent);

        // 设置当显示的widget_list为空显示的View
        remoteViews.setEmptyView(R.id.widget_list, R.layout.none_data);

        // 刷新按钮
        final Intent refreshIntent = new Intent(context,
                MyAppListWidgetProvider.class);
        refreshIntent.setAction("refresh");
        final PendingIntent refreshPendingIntent = PendingIntent.getBroadcast(
                context, 0, refreshIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.widget_button,
                refreshPendingIntent);

        // 更新Wdiget
        appWidgetManager.updateAppWidget(thisWidget, remoteViews);

        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        manager.notifyAppWidgetViewDataChanged(appWidgetIds[0], R.id.widget_list);

    }
    /**
     * 接收Intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        String action = intent.getAction();

        if (action.equals("refresh")) {
            // 刷新Widget
            final AppWidgetManager mgr = AppWidgetManager.getInstance(context);
            final ComponentName cn = new ComponentName(context,
                    MyAppListWidgetProvider.class);

            Calendar c = Calendar.getInstance();//首先要获取日历对象
            int mYear = c.get(Calendar.YEAR); // 获取当前年份
            int mMonth = c.get(Calendar.MONTH) + 1;// 获取当前月份
            int mDay = c.get(Calendar.DAY_OF_MONTH);// 获取当日期

            final myDB db = new myDB(context);
            List<Map<String, Object>> listems = db.queryByDate(mYear+"-"+mMonth+"-"+mDay);

            MyRemoteViewsFactory.mList.clear();

            if (listems.size() == 0) {
                MyRemoteViewsFactory.mList.add("今日日程为空");
            } else {
                for (int i = 0; i < listems.size(); i++) {
                    MyRemoteViewsFactory.mList.add(listems.get(i).get("activity_name").toString() + " " + listems.get(i).get("time").toString());
                }
            }



            // 这句话会调用RemoteViewSerivce中RemoteViewsFactory的onDataSetChanged()方法。
            mgr.notifyAppWidgetViewDataChanged(mgr.getAppWidgetIds(cn),
                    R.id.widget_list);

        }
    }

    @Override
    public void onEnabled(Context context) {
        // TODO Auto-generated method stub
        super.onEnabled(context);

        Toast.makeText(context, "用户将widget添加桌面了",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // TODO Auto-generated method stub。
        Toast.makeText(context, "用户将widget从桌面移除了",
                Toast.LENGTH_SHORT).show();
        super.onDeleted(context, appWidgetIds);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
    }
}

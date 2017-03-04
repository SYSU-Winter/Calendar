package com.example.a12136.calendar;

import android.content.Context;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by 56950 on 2017/1/2.
 */

public class MyRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private final Context mContext;
    public static List<String> mList = new ArrayList<String>();

    /*
     * 构造函数
     */
    public MyRemoteViewsFactory(Context context, Intent intent) {

        mContext = context;
    }

    /*
     * MyRemoteViewsFactory调用时执行，这个方法执行时间超过20秒回报错。
     * 如果耗时长的任务应该在onDataSetChanged或者getViewAt中处理
     */
    @Override
    public void onCreate() {
        updateList();
    }

    public void updateList() {
        Calendar c = Calendar.getInstance();//首先要获取日历对象
        int mYear = c.get(Calendar.YEAR); // 获取当前年份
        int mMonth = c.get(Calendar.MONTH) + 1;// 获取当前月份
        int mDay = c.get(Calendar.DAY_OF_MONTH);// 获取当日期

        final myDB db = new myDB(mContext);
        List<Map<String, Object>> listems = db.queryByDate(mYear+"-"+mMonth+"-"+mDay);

        mList.clear();

        if (listems.size() == 0) {
            MyRemoteViewsFactory.mList.add("今日日程为空");
        } else {
            for (int i = 0; i < listems.size(); i++) {
                MyRemoteViewsFactory.mList.add(listems.get(i).get("activity_name").toString() + " " + listems.get(i).get("time").toString());
            }
        }
    }
    /*
     * 当调用notifyAppWidgetViewDataChanged方法时，触发这个方法
     * 例如：MyRemoteViewsFactory.notifyAppWidgetViewDataChanged();
     */
    @Override
    public void onDataSetChanged() {

    }

    /*
     * 这个方法不用多说了把，这里写清理资源，释放内存的操作
     */
    @Override
    public void onDestroy() {
        mList.clear();
    }

    /*
     * 返回集合数量
     */
    @Override
    public int getCount() {
        return mList.size();
    }

    /*
     * 创建并且填充，在指定索引位置显示的View，这个和BaseAdapter的getView类似
     */
    @Override
    public RemoteViews getViewAt(int position) {
        if (position < 0 || position >= mList.size())
            return null;
        String content = mList.get(position);
        // 创建在当前索引位置要显示的View
        final RemoteViews rv = new RemoteViews(mContext.getPackageName(),
                R.layout.my_widget_layout_item);

        // 设置要显示的内容
        rv.setTextViewText(R.id.widget_list_item_tv, content);

        // 填充Intent，填充在AppWdigetProvider中创建的PendingIntent
        Intent intent = new Intent();
        // 传入点击行的数据
        intent.putExtra("content", content);
        rv.setOnClickFillInIntent(R.id.widget_list_item_tv, intent);

        return rv;
    }

    /*
     * 显示一个"加载"View。返回null的时候将使用默认的View
     */
    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    /*
     * 不同View定义的数量。默认为1（本人一直在使用默认值）
     */
    @Override
    public int getViewTypeCount() {
        return 1;
    }

    /*
     * 返回当前索引的。
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /*
     * 如果每个项提供的ID是稳定的，即她们不会在运行时改变，就返回true（没用过。。。）
     */
    @Override
    public boolean hasStableIds() {
        return true;
    }
}

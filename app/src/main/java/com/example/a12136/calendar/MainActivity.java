package com.example.a12136.calendar;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SlidingDrawer;
import android.widget.TextView;

import com.example.a12136.calendar.CalendarView.CallBack;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, CallBack{

    private ViewPager viewPager;
    private CalendarView[] views;
    private TextView showYearView;
    private TextView showMonthView;
    private TextView showWeekView;
    private TextView monthCalendarView;
    private TextView weekCalendarView;
    private CalendarViewBuilder builder = new CalendarViewBuilder();
    private SlidingDrawer mSlidingDrawer;
    private View mContentPager;
    private CustomDate mClickDate;
    private TextView mNowCircleView;
    private TextView mAddCircleView;
    public static final String MAIN_ACTIVITY_CLICK_DATE = "main_click_date";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewbyId();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private void findViewbyId() {
        viewPager = (ViewPager) this.findViewById(R.id.viewpager);
        showMonthView = (TextView)this.findViewById(R.id.show_month_view);
        showYearView = (TextView)this.findViewById(R.id.show_year_view);
        showWeekView = (TextView)this.findViewById(R.id.show_week_view);
        views = builder.createMassCalendarViews(this, 5, this);
        monthCalendarView = (TextView) this.findViewById(R.id.month_calendar_button);
        weekCalendarView = (TextView) this.findViewById(R.id.week_calendar_button);
        mContentPager = this.findViewById(R.id.contentPager);
        mSlidingDrawer = (SlidingDrawer)this.findViewById(R.id.sildingDrawer);
        mNowCircleView = (TextView)this.findViewById(R.id.now_circle_view);
        mAddCircleView = (TextView)this.findViewById(R.id.add_circle_view);
        monthCalendarView.setOnClickListener(this);
        weekCalendarView.setOnClickListener(this);
        mNowCircleView.setOnClickListener(this);
        mAddCircleView.setOnClickListener(this);
        setViewPager();
        setOnDrawListener();
    }


    private void setViewPager() {
        CustomViewPagerAdapter<CalendarView> viewPagerAdapter = new CustomViewPagerAdapter<CalendarView>(views);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setCurrentItem(498);
        viewPager.setOnPageChangeListener(new CalendarViewPagerListener(viewPagerAdapter));
    }

    private void setOnDrawListener() {
        mSlidingDrawer.setOnDrawerOpenListener(new SlidingDrawer.OnDrawerOpenListener() {

            @Override
            public void onDrawerOpened() {
                builder.swtichCalendarViewsStyle(CalendarView.WEEK_STYLE);
            }
        });
        mSlidingDrawer.setOnDrawerScrollListener(new SlidingDrawer.OnDrawerScrollListener() {

            @Override
            public void onScrollStarted() {
                builder.swtichCalendarViewsStyle(CalendarView.MONTH_STYLE);
            }

            @Override
            public void onScrollEnded() {
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void setShowDateViewText(int year ,int month){
        showYearView.setText(year+"");
        showMonthView.setText(month+"月");
        showWeekView.setText(DateUtil.weekName[DateUtil.getWeekDay()-1]);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.month_calendar_button:
                swtichBackgroundForButton(true);
                builder.swtichCalendarViewsStyle(CalendarView.MONTH_STYLE);
                mSlidingDrawer.close();
                break;
            case R.id.week_calendar_button:
                swtichBackgroundForButton(false);
                mSlidingDrawer.open();
                break;
            case R.id.now_circle_view:
                builder.backTodayCalendarViews();
                break;
            case R.id.add_circle_view:
                Intent i = new Intent(this, AddPlanActivity.class);
                Bundle mBundle = new Bundle();
                mBundle.putSerializable(MAIN_ACTIVITY_CLICK_DATE, mClickDate);
                i.putExtras(mBundle);
                startActivity(i);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
    }

    private void swtichBackgroundForButton(boolean isMonth){
        if(isMonth){
            monthCalendarView.setBackgroundResource(R.drawable.press_left_text_bg);
            weekCalendarView.setBackgroundColor(Color.TRANSPARENT);
        }else{
            weekCalendarView.setBackgroundResource(R.drawable.press_right_text_bg);
            monthCalendarView.setBackgroundColor(Color.TRANSPARENT);
        }
    }


    @Override
    public void onMesureCellHeight(int cellSpace) {
        mSlidingDrawer.getLayoutParams().height = mContentPager.getHeight() - cellSpace;
    }

    @Override
    public void clickDate(CustomDate date) {
        mClickDate = date;
        //Toast.makeText(this, date.year+"-"+date.month+"-"+date.day, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void changeDate(CustomDate date) {
        setShowDateViewText(date.year,date.month);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

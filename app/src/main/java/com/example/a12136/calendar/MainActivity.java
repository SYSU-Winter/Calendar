package com.example.a12136.calendar;

import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SlidingDrawer;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.example.a12136.calendar.CalendarView.CallBack;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.os.Message;
/**
 * Created by 12136 on 2016/12/15.
 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener, CallBack{

    private ViewPager viewPager;
    private CalendarView[] views;
    private TextView showYearView;
    private TextView showMonthView;
    private TextView showWeekView;
    private TextView monthCalendarView;
    private TextView weekCalendarView;
    private CalendarViewBuilder builder = new CalendarViewBuilder();
    private MySlidingDrawer mSlidingDrawer;
    private LinearLayout lvv;

    private View mContentPager;
    private CustomDate mClickDate;
    private TextView mNowCircleView;
    public static final String MAIN_ACTIVITY_CLICK_DATE = "main_click_date";
    private TextView mSlidingTitle;
    private ImageView imageView_hand;

    String name = null;
    String city_name;//获取定位之后确定在哪个城市

    private LocationManager locationManager;
    private Location currentBestLocation = null;

    //按返回键退出程序
    private long exitTime = 0;

    private SimpleAdapter simplead;
    private ListView lv;
    List<Map<String, Object>> listems;

    private TextView mStartPlanTimeTv;
    private TextView mEndPlanTimeTv;
    private TextView mPlanContentTv;

    private int activity_id;

    private AlertDialog alertDialog;
    public View layout;

    private static final String w_url = "http://ws.webxml.com.cn/WebServices/WeatherWS.asmx/getWeather";
    private static final int UPDATE_CONTENT = 0;
    TextView city, detail, average_t, field_t, humidity, air, wind;
    ImageView w_image, w_image2;


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
            if((System.currentTimeMillis()-exitTime) > 2000){
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);

        //设置位置提供器
        setLocationManager();

        findViewById();

        if (name != null)
            city_name = name;
        else city_name = "广州";
        getweather(city_name);

        // 设置add，添加任务的事件监听按钮，点击发生时跳转到任务添加界面
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AnimationSet animationSet = new AnimationSet(true);
                RotateAnimation rotateAnimation = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                rotateAnimation.setDuration(1000);
                animationSet.addAnimation(rotateAnimation);
                FloatingActionButton fab1 = (FloatingActionButton) findViewById(R.id.fab);
                fab1.startAnimation(animationSet);
                final Intent i = new Intent(MainActivity.this, AddPlanActivity.class);
                Bundle mBundle = new Bundle();
                // 使用putSerializable将当前点击的日期传过去
                mBundle.putSerializable(MAIN_ACTIVITY_CLICK_DATE, mClickDate);
                i.putExtras(mBundle);
                Timer timer = new Timer();
                TimerTask task = new TimerTask() {
                    @Override
                    public void run() {
                        startActivity(i);
                        // 重写activity切换效果，你们看看需不需要
                        // 这里是由右边滑到左边
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    }
                };
                timer.schedule(task, 1200);
            }
        });
    }

/////////////////////////////////////////////////
    private void getweather(String city_name) {

        city = (TextView)findViewById(R.id.city);
        detail = (TextView)findViewById(R.id.detail);
        average_t = (TextView)findViewById(R.id.average_temperature);
        field_t = (TextView)findViewById(R.id.field_temperature);
        humidity = (TextView)findViewById(R.id.humidity);
        air = (TextView)findViewById(R.id.air);
        wind = (TextView)findViewById(R.id.wind);
        w_image = (ImageView)findViewById(R.id.w_image);
        w_image2 = (ImageView)findViewById(R.id.w_image2);
        city.setText(city_name);

        ConnectivityManager connectivityManager = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null) {
            Toast.makeText(MainActivity.this, "当前没有可用网络！", Toast.LENGTH_SHORT).show();
        }
        else {
            sendRequestWithHttpURLConnection();
        }
    }

    private void sendRequestWithHttpURLConnection() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ConnectivityManager connectivityManager = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                HttpURLConnection connection = null;
                try {
                    //Log.i("key", "Begin the connection");
                    connection = (HttpURLConnection)((new URL(w_url.toString()).openConnection()));
                    connection.setRequestMethod("POST");
                    connection.setReadTimeout(8000);
                    connection.setConnectTimeout(8000);
                    DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
                    String request = city_name;
                    request = URLEncoder.encode(request, "utf-8");
                    //65d8870adcb84d5586ac0a43b0169abd
                    outputStream.writeBytes("theCityCode=" + request + "&theUserID=");

                    InputStream inputStream = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    Message message = new Message();
                    message.what = UPDATE_CONTENT;
                    message.obj = parseXMLWithPull(response.toString());
                    w_handler.sendMessage(message);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (connection != null)
                        connection.disconnect();
                }
            }
        }).start();
    }

    private ArrayList<String> parseXMLWithPull(String xml) {
        ArrayList<String> list = new ArrayList<>();
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new StringReader(xml));

            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if ("string".equals(parser.getName())) {
                            String str = parser.nextText();
                            list.add(str);
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                    default:
                        break;
                }
                eventType=parser.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }


    private Handler w_handler = new Handler() {
        public void handleMessage(Message message) {
            switch (message.what) {
                case UPDATE_CONTENT:
                    ArrayList<String> list = (ArrayList<String>)message.obj;
                    LinearLayout l1 = (LinearLayout)findViewById(R.id.l1);
                    l1.setVisibility(View.VISIBLE);
                    city.setText(list.get(1));
                    field_t.setText(list.get(8));
                    handleawh(list.get(4));
                    handleair(list.get(5));
                    handleimage(list.get(10), list.get(11));
                    handledetail(list.get(7));
                default:
                    break;
            }
        }
    };

    void handleawh(String str) {
        int a = str.indexOf('：');
        str = str.substring(a+1);
        a = str.indexOf('：');
        int b = str.indexOf('；');
        average_t.setText(str.substring(a + 1, b));
        str = str.substring(b+1);
        a = str.indexOf('：');
        b = str.indexOf('；');
        wind.setText(str.substring(a + 1, b));
        str = str.substring(b+1);
        humidity.setText(str);
    }

    void handleair(String str) {
        int a = str.indexOf('。');
        air.setText(str.substring(a+1, str.length() - 1));
    }

    void handleimage(String a, String b) {
        String c = "a_" + a.substring(0, 1);
        String d = "a_" + b.substring(0, 1);
        Context ctx = getBaseContext();
        int resId1 = getResources().getIdentifier(c, "mipmap" , ctx.getPackageName());
        w_image.setImageResource(resId1);
        if (!c.equals(d)) {
            w_image2.setVisibility(View.VISIBLE);
            int resId2 = getResources().getIdentifier(d, "mipmap", ctx.getPackageName());
            w_image2.setImageResource(resId2);
        }
    }

    void handledetail(String x) {
        String de = x.substring(x.indexOf("日") + 2, x.length());
        detail.setText(de);
    }
//////////////////////////////////////

    private void setLocationManager() {
        String provider;
        locationManager=(LocationManager)getSystemService(Context.LOCATION_SERVICE);
        // 通过GPS定位，较精确，也比较耗电
        LocationProvider gpsProvider = locationManager.getProvider(LocationManager.GPS_PROVIDER);
        // 通过网络定位，较不准确，省电
        LocationProvider netProvider = locationManager.getProvider(LocationManager.NETWORK_PROVIDER);
        // 首选network，然后如果gps收到了信号就替换掉
        if (netProvider != null) {
            provider = LocationManager.NETWORK_PROVIDER;
        } else if (gpsProvider != null) {
            provider = LocationManager.GPS_PROVIDER;
        } else {
            //当前没有可用的位置提供器时，弹出Toast提示
            Toast.makeText(this,"没有可用的位置提供器",Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            Location location = locationManager.getLastKnownLocation(provider);
            if (location != null) {
                navigateTo(location);
            }
            else {
                // 如果getLastKnownLocation返回空，则需要重新获取
                if (provider.equals(LocationManager.NETWORK_PROVIDER))
                    locationManager.requestLocationUpdates(provider, 0, 0, networklocationListener);
                else
                    locationManager.requestLocationUpdates(provider, 0, 0, gpslocationListener);
            }
            //.e("provider", provider);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private void findViewById() {
        viewPager = (ViewPager) this.findViewById(R.id.viewpager);
        showMonthView = (TextView)this.findViewById(R.id.show_month_view);
        showYearView = (TextView)this.findViewById(R.id.show_year_view);
        showWeekView = (TextView)this.findViewById(R.id.show_week_view);
        views = builder.createMassCalendarViews(this, 5, this);
        monthCalendarView = (TextView) this.findViewById(R.id.month_calendar_button);
        weekCalendarView = (TextView) this.findViewById(R.id.week_calendar_button);
        mContentPager = this.findViewById(R.id.contentPager);

        mSlidingDrawer = (MySlidingDrawer)this.findViewById(R.id.sildingDrawer);
        // 传入抽屉手柄的id
        mSlidingDrawer.setHandleId(R.id.handlerText);
        mSlidingDrawer.setTouchableIds(new int[]{R.id.lvx});
        lvv = (LinearLayout) findViewById(R.id.lvv);

        mNowCircleView = (TextView)this.findViewById(R.id.now_circle_view);
        mSlidingTitle = (TextView)this.findViewById(R.id.main_sliding_title);
        // 设置日，就是那个定位图标里面的日子
        mNowCircleView.setText(Integer.toString(DateUtil.getCurrentMonthDay()));

        imageView_hand = (ImageView)this.findViewById(R.id.handle);

        monthCalendarView.setOnClickListener(this);
        weekCalendarView.setOnClickListener(this);
        mNowCircleView.setOnClickListener(this);
        setViewPager();
        setOnDrawListener();
    }

    // 这里viewpager就是日历内容，使用viewPagerAdapter进行内容填充
    private void setViewPager() {
        CustomViewPagerAdapter<CalendarView> viewPagerAdapter = new CustomViewPagerAdapter<>(views);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setCurrentItem(498);
        viewPager.setOnPageChangeListener(new CalendarViewPagerListener(viewPagerAdapter));
        imageView_hand.setImageResource(R.mipmap.up_array);
    }

    // 上下拉条的监听器，实现的功能是月视图和周视图的切换
    private void setOnDrawListener() {
        // 周视图
        mSlidingDrawer.setOnDrawerOpenListener(new SlidingDrawer.OnDrawerOpenListener() {
            @Override
            public void onDrawerOpened() {
                builder.swtichCalendarViewsStyle(CalendarView.WEEK_STYLE);
                builder.backTodayCalendarViews();
                switchBackgroundForButton(false);
            }
        });
        // 月视图
        mSlidingDrawer.setOnDrawerScrollListener(new SlidingDrawer.OnDrawerScrollListener() {
            @Override
            public void onScrollStarted() {
                builder.swtichCalendarViewsStyle(CalendarView.MONTH_STYLE);
                switchBackgroundForButton(true);
            }

            @Override
            public void onScrollEnded() {
            }
        });

        // 真实抽屉，非头部部分，在打开状态点击时关闭抽屉
//        lvv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (mSlidingDrawer.isOpened()) {
//                    mSlidingDrawer.animateClose();
//                }
//            }
//        });
    }

    private void navigateTo(Location location) {
        // 将GPS设备采集的原始GPS坐标转换成百度坐标
        CoordinateConverter converter = new CoordinateConverter();
        converter.from(CoordinateConverter.CoordType.GPS);
        // sourceLatLng待转换坐标
        converter.coord(new LatLng(location.getLatitude(), location.getLongitude()));
        LatLng desLatLng = converter.convert();
        java.text.DecimalFormat df = new java.text.DecimalFormat("#.######");
        System.out.println("纬度：" + df.format(desLatLng.latitude));
        System.out.println("经度：" + df.format(desLatLng.longitude));
        //String ak = "nUvlnh8wk6cDupYs5foacyXz";
        String ak = "aqMF0m8fvB7KepcoNmzspZwSWkscdoU8";
        String url = "http://api.map.baidu.com/geocoder/v2/?ak=" +
                ak + "&callback=renderReverse&location=" +
                df.format(desLatLng.latitude) + "," + df.format(desLatLng.longitude) + "&output=xml&pois=1";

        dodo(url);
    }

    private void dodo(final String url) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //http请求要求
                HttpURLConnection connection = null;
                try {
                    Log.i("key", "begin to connection");
                    connection = (HttpURLConnection) ((new URL(url).openConnection()));
                    connection.setRequestMethod("GET");
                    connection.setDoInput(true); //打开输入流，以便从服务器获取数据
                    connection.setDoOutput(true); //打开输出流，以便向服务器提交数据
                    connection.setUseCaches(false); // 不使用缓存
                    connection.setReadTimeout(8000);
                    connection.setConnectTimeout(8000);
                    int response = connection.getResponseCode(); //状态码为200表示连接成功
                    Log.d("state","The response is: "+ response);

                    InputStream in = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder responses = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        responses.append(line);
                    }
                    //Log.i("content", responses.toString());
                    XmlPullParser(responses.toString());
                } catch (IOException e) {
                    Log.i("error", "failed to connect");
                    e.printStackTrace();
                }
                // XmlPullParser操作
                // Message消息传递
            }
        }).start();
    }

    private void XmlPullParser(String xmlData) {
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new StringReader(xmlData));

            int eventType = parser.getEventType();
            String cityname = null;
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT: //开始读取XML文档
                        break;
                    case XmlPullParser.START_TAG:
                        String name = parser.getName();
                        if ("city".equals(name)) {
                            String str = parser.nextText();
                            Log.i("key", str);
                            cityname = str;
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                    default:
                        break;
                }
                if (cityname != null) break;
                eventType = parser.next();
            }

            Message message = new Message();
            message.what = 0;
            message.obj = cityname;
            handler.sendMessage(message);
        } catch (Exception e) {
            Log.i("error", "cannot parse xml");
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    name = (String) msg.obj;
                    name = name.substring(0, name.length() - 1);
                    System.out.println("解析成功的城市名： " + name);
            }
        }
    };

    LocationListener gpslocationListener = new LocationListener() {
        private boolean isRemove = false;//判断网络监听是否移除

        @Override
        public void onLocationChanged(Location location) {
            if (isBetterLocation(location, currentBestLocation)) {
                Toast.makeText(MainActivity.this, "GPS定位更新", Toast.LENGTH_SHORT).show();
                currentBestLocation = location;
                navigateTo(currentBestLocation);
            }
            // 获得GPS服务后，移除network监听  
            if (location != null && !isRemove) {
                try {
                    locationManager.removeUpdates(networklocationListener);
                    isRemove = true;
                } catch (SecurityException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
            if (LocationProvider.OUT_OF_SERVICE == i) {
                Toast.makeText(MainActivity.this,"GPS服务丢失,切换至网络定位",
                        Toast.LENGTH_SHORT).show();
                try {
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER, 0, 100000,
                            networklocationListener);
                } catch (SecurityException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onProviderEnabled(String s) {}

        @Override
        public void onProviderDisabled(String s) {}
    };


    LocationListener networklocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            //if(locationManager!=null)
            //    navigateTo(location);
            if (isBetterLocation(location, currentBestLocation)) {
                Toast.makeText(MainActivity.this, "NETWORK定位更新", Toast.LENGTH_SHORT).show();
                //ischanging = true;
                currentBestLocation = location;
                navigateTo(currentBestLocation);
            }
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
            if (LocationProvider.OUT_OF_SERVICE == i) {
                Toast.makeText(MainActivity.this,"NETWORK服务丢失,切换至GPS定位",
                        Toast.LENGTH_SHORT).show();
                try {
                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER, 0, 100000,
                            gpslocationListener);
                } catch (SecurityException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onProviderEnabled(String s) {}

        @Override
        public void onProviderDisabled(String s) {}
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(locationManager!=null){
            try {
                locationManager.removeUpdates(gpslocationListener);
                locationManager.removeUpdates(networklocationListener);
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(locationManager!=null){
            try {
                locationManager.removeUpdates(gpslocationListener);
                locationManager.removeUpdates(networklocationListener);
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 100000, networklocationListener);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 100000, gpslocationListener);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        if (mClickDate != null) {
            setActivities(mClickDate);
        }
    }

    /** Determines whether one Location reading is better than the current Location fix
     * @param location  The new Location that you want to evaluate
     * @param currentBestLocation  The current Location fix, to which you want to compare the new one
     */
    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > 1000 * 60 * 2;
        boolean isSignificantlyOlder = timeDelta < -1000 * 60 * 2;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    /** Checks whether two providers are the same */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

    // 设置标题栏那里的日期
    public void setShowDateViewText(int year ,int month){
        showYearView.setText(year+"");
        showMonthView.setText(month+"月");
        showWeekView.setText(DateUtil.weekName[DateUtil.getWeekDay()-1]);
    }


    // 对3个按钮的点击事件的处理，周视图，月视图以及当前日期定位
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // 切换月视图的button处理
            case R.id.month_calendar_button:
                switchBackgroundForButton(true);
                builder.swtichCalendarViewsStyle(CalendarView.MONTH_STYLE);
                mSlidingDrawer.close();
                break;
            // 切换周视图的button处理
            case R.id.week_calendar_button:
                switchBackgroundForButton(false);
                mSlidingDrawer.open();
                break;
            // 定位当前日期的点击事件处理，点击之后视图回到当前日期
            case R.id.now_circle_view:
                builder.backTodayCalendarViews();
                break;
        }
    }

    // 设置“月周”那个button的切换时的背景，选中哪个的时候，背景会变灰一点,磨砂效果
    // 就是看起来有切换的效果
    private void switchBackgroundForButton(boolean isMonth){
        if(isMonth){
            monthCalendarView.setBackgroundResource(R.drawable.press_left_text_bg);
            weekCalendarView.setBackgroundColor(Color.TRANSPARENT);
            //weather_button.setVisibility(View.INVISIBLE);
            imageView_hand.setImageResource(R.mipmap.up_array);
        }else{
            weekCalendarView.setBackgroundResource(R.drawable.press_right_text_bg);
            monthCalendarView.setBackgroundColor(Color.TRANSPARENT);

            //weather_button.setVisibility(View.VISIBLE);
            imageView_hand.setImageResource(R.mipmap.down_array);
        }
    }
    
    @Override
    public void onMeasureCellHeight(int cellSpace) {
        mSlidingDrawer.getLayoutParams().height = mContentPager.getHeight() - cellSpace;
    }

    @Override
    public void clickDate(CustomDate date) {
        if (mClickDate == null || mClickDate.year != date.year || mClickDate.month != date.month
                || mClickDate.day != date.day) {
            mClickDate = date;
            setActivities(date);
        }

    }

    //填充listView中的活动
    public void setActivities(CustomDate date) {
        final myDB db = new myDB(MainActivity.this);
        listems = db.queryByDate(date.toString());

        if (listems.size() == 0 && mSlidingTitle != null) {
            mSlidingTitle.setText("今日无日程安排");
        } else if (mSlidingTitle != null){
            mSlidingTitle.setText("今日日程安排");
        }

        simplead = new SimpleAdapter(this, listems,
                R.layout.activity_item, new String[] { "_id", "time", "activity_name"},
                new int[] {R.id.activity_id, R.id.activity_item_time, R.id.activity_item_name});

        Log.d("test", date.year+"-"+date.month+"-"+date.day);

        lv = (ListView) findViewById(R.id.lv);
        lv.setAdapter(simplead);
        lv.setClickable(true);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                LayoutInflater inflater = getLayoutInflater();
                layout = inflater.inflate(R.layout.activity_edit_dialog,
                        (ViewGroup) findViewById(R.id.activity_edit_dialog));

                mPlanContentTv = (TextView) layout.findViewById(R.id.plan_content_tv);
                mStartPlanTimeTv = (TextView) layout.findViewById(R.id.start_plan_time_tv);
                mEndPlanTimeTv = (TextView) layout.findViewById(R.id.end_plan_time_tv);

                //获取当前活动信息，填充到编辑窗口
                TextView id_tv = (TextView) view.findViewById(R.id.activity_id);
                TextView name_tv = (TextView) view.findViewById(R.id.activity_item_name);
                TextView time_tv = (TextView) view.findViewById(R.id.activity_item_time);

                activity_id = Integer.parseInt(id_tv.getText().toString());

                mPlanContentTv.setText(name_tv.getText());
                String [] startAndEndTime = time_tv.getText().toString().split("-");
                mStartPlanTimeTv.setText(startAndEndTime[0]);
                mEndPlanTimeTv.setText(startAndEndTime[1]);

                //添加时间设置的点击事件
                setOnClickListener();

                //弹出编辑窗口
                alertDialog = new AlertDialog.Builder(MainActivity.this).setTitle("编辑活动").setView(layout)
                        .setPositiveButton("确定", null)
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() { //设置取消按钮
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                alertDialog.dismiss();
                            }
                        }).show();

                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener                                                                  (new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String []startTime = mStartPlanTimeTv.getText().toString().split(":");
                        String []endTime = mEndPlanTimeTv.getText().toString().split(":");

                        if (TextUtils.isEmpty(mPlanContentTv.getText())) {
                            Toast.makeText(MainActivity.this, "活动内容不能为空", Toast.LENGTH_SHORT).show();
                        } else if (TextUtils.equals(mStartPlanTimeTv.getText(), "未设置")
                                || TextUtils.equals(mEndPlanTimeTv.getText(), "未设置")) {
                            Toast.makeText(MainActivity.this, "请检查时间设置", Toast.LENGTH_SHORT).show();
                        } else if ( (60*Integer.parseInt(startTime[0])+Integer.parseInt(startTime[1]))
                                > (60*Integer.parseInt(endTime[0])+Integer.parseInt(endTime[1])) ) {
                            Toast.makeText(MainActivity.this, "开始时间不能大于结束时间", Toast.LENGTH_SHORT).show();
                        } else {
                            String currentDate_str = mClickDate.toString();

                            myDB db = new myDB(MainActivity.this);
                            //更新数据
                            db.update(activity_id, mPlanContentTv.getText().toString(), currentDate_str,
                                    mStartPlanTimeTv.getText().toString(), mEndPlanTimeTv.getText().toString());
                            Toast.makeText(MainActivity.this, "编辑成功", Toast.LENGTH_SHORT).show();
                            alertDialog.dismiss();

                            setActivities(mClickDate);
                        }
                    }
                });

            }
        });

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                //show dialog
                String msg = "确认删除该活动吗？";
                final int index = i;
                final View view_ = view;
                alertDialog = new AlertDialog.Builder(MainActivity.this)
                        .setTitle("删除活动").setMessage(msg).setPositiveButton("确认",
                        new DialogInterface.OnClickListener() {
                            //delete and update listView
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                listems.remove(index);
                                simplead.notifyDataSetChanged();

                                TextView id_tv = (TextView) view_.findViewById(R.id.activity_id);
                                activity_id = Integer.parseInt(id_tv.getText().toString());

                                myDB db = new myDB(MainActivity.this);
                                db.delete(activity_id);

                            }
                        }).setNegativeButton("取消",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                alertDialog.dismiss();
                            }
                        }).show();
                return true;
            }
        });

    }

    // 改变标题栏哪里的日期
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

    private void setOnClickListener() {

        mStartPlanTimeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment newFragment = new TimePickerFragment();
                newFragment.show(getFragmentManager(),"main-start");
            }
        });

        mEndPlanTimeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment newFragment = new TimePickerFragment();
                newFragment.show(getFragmentManager(),"main-end");
            }
        });
    }

}

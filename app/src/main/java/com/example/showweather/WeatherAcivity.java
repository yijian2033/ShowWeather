package com.example.showweather;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;




/**
 * Created by Administrator on 2016/5/31 0031.
 */
public class WeatherAcivity extends Activity {

    private GridView forecastGridView;//天气预报
    private ForecastButtonAdapter forecastButtonAdapter;
    private TextView weatherLocCity;//定位城市
    private TextView humidity;//湿度
    private TextView windSpeed;//风速
    private TextView presuretext;//气压
    private TextView bigTmpTxt;//中间的温度大图
    private ImageView bigWeatherImg;//中间的天气大图
    private TextView bigWeatherInfo;//中间的天气描述
    private TextView currentDateText;//当前的日期
    private TextView AMOrPMText;//上午还是下午
    private LinearLayout linearLayout;

    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = new AMapLocationClientOption();
    private LocationManager mlocation;//用系统的GPS获取当前位置信息

    private List<WeatherInfo> forecastList;
    private List<WeatherInfo> defaultList;
    private WeatherInfo nowDate;
    private Map<String, String> mapAllNameID;


    private static String locationUrl = "http://lbs.juhe.cn/api/getaddressbylngb?lngx=";//获取城市名称URL
    //private static String weatherUrl = "http://cdn.weather.hao.360.cn/api_weather_info.php?app=hao360&_jsonp=data&code=";//根据城市获取天气信息URL
    private static String weatherUrl = "http://service.envicloud.cn:8082/v2/weatherforecast/ZMF3ZWLNDWKXNDKZMZU5ODQ4NZE2/";//根据城市获取天气信息URL
    private static final String ACTION_TIMEZONE_CHANGED = Intent.ACTION_TIME_TICK;//监听时区变化的广播
    private boolean isEnableClick = false;

    private MsgRecieve mRecieve;//广播监听
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 11;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏 
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
        setContentView(R.layout.weatherlayout);
//        startLocationService();
        initCityCodeTable();
        regisReceive();
        changeAMOrPM();
        getDefaultGridData();
        initLocation();
        startLocation();
        if(!TextUtils.isEmpty(Utils.getCity(getApplicationContext())))
        	getWeatherInfoByCityCode(weatherUrl + mapAllNameID.get(Utils.getCity(getApplicationContext())));
//        getCityName(getUrl(Utils.getLongitude(getApplicationContext()), Utils.getLatitude(getApplicationContext())));
    }

    
    /**
     * ��ʼ����λ
     *
     * @since 2.8.0
     * @author hongming.wang
     *
     */
    private void initLocation(){
        //初始化client
        locationClient = new AMapLocationClient(this.getApplicationContext());
        //设置定位参数
        locationClient.setLocationOption(getDefaultOption());
        // 设置定位监听
        locationClient.setLocationListener(locationListener);
    }

    /**
     * Ĭ�ϵĶ�λ����
     * @since 2.8.0
     * @author hongming.wang
     *
     */
    private AMapLocationClientOption getDefaultOption(){
        AMapLocationClientOption mOption = new AMapLocationClientOption();
        mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
        mOption.setGpsFirst(false);//可选，设置是否gps优先，只在高精度模式下有效。默认关闭
        mOption.setHttpTimeOut(30000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
        mOption.setInterval(2000);//可选，设置定位间隔。默认为2秒
        mOption.setNeedAddress(true);//可选，设置是否返回逆地理地址信息。默认是true
        mOption.setOnceLocation(false);//可选，设置是否单次定位。默认是false
        mOption.setOnceLocationLatest(false);//可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
        AMapLocationClientOption.setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTP);//可选， 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
        mOption.setSensorEnable(false);//可选，设置是否使用传感器。默认是false
        mOption.setWifiScan(true); //可选，设置是否开启wifi扫描。默认为true，如果设置为false会同时停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
        return mOption;
    }

    AMapLocationListener locationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation loc) {
            if (null != loc && loc.getErrorCode() == 0) {
                //解析定位结果
                Log.i("ljwtest:", "定位成功,城市名：" + loc);
                Utils.setCity(getApplicationContext(), loc.getCity().substring(0,  loc.getCity().length() - 1));
            } else {
                if (null != loc && loc.getErrorCode() != 0){
                    Log.i("ljwtest:", "定位失败，"+loc.getErrorInfo());
                }else
                Log.i("ljwtest:", "定位失败，loc is null");
            }
        }
    };

    /**
     * 开始定位
     *
     * @since 2.8.0
     * @author hongming.wang
     *
     */
    private void startLocation(){
        //根据控件的选择，重新设置定位参数
        resetOption();
        // 设置定位参数
        locationClient.setLocationOption(locationOption);
        // 启动定位
        locationClient.startLocation();
    }

    // 根据控件的选择，重新设置定位参数
    private void resetOption() {
        // 设置是否需要显示地址信息
        locationOption.setNeedAddress(true);
        /**
         * 设置是否优先返回GPS定位结果，如果30秒内GPS没有返回定位结果则进行网络定位
         * 注意：只有在高精度模式下的单次定位有效，其他方式无效
         */
        locationOption.setGpsFirst(false);
        // 设置是否开启缓存
        locationOption.setLocationCacheEnable(false);
        //设置是否等待设备wifi刷新，如果设置为true,会自动变为单次定位，持续定位时不要使用
//        locationOption.setOnceLocationLatest(cbOnceLastest.isChecked());
        //设置是否使用传感器
        locationOption.setSensorEnable(true);
        //设置是否开启wifi扫描，如果设置为false时同时会停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
//        String strInterval = etInterval.getText().toString();
//        if (!TextUtils.isEmpty(strInterval)) {
//            try{
//                // 设置发送定位请求的时间间隔,最小值为1000，如果小于1000，按照1000算
//                locationOption.setInterval(Long.valueOf(strInterval));
//            }catch(Throwable e){
//                e.printStackTrace();
//            }
//        }

//        String strTimeout = etHttpTimeout.getText().toString();
//        if(!TextUtils.isEmpty(strTimeout)){
//            try{
//                // 设置网络请求超时时间
//                locationOption.setHttpTimeOut(Long.valueOf(strTimeout));
//            }catch(Throwable e){
//                e.printStackTrace();
//            }
//        }
    }

    /**
     * 停止定位
     *
     * @since 2.8.0
     * @author hongming.wang
     *
     */
    private void stopLocation(){
        // 停止定位
        locationClient.stopLocation();
    }

    /**
     * 销毁定位
     *
     * @since 2.8.0
     * @author hongming.wang
     *
     */
    private void destroyLocation(){
        if (null != locationClient) {
            /**
             * 如果AMapLocationClient是在当前Activity实例化的，
             * 在Activity的onDestroy中一定要执行AMapLocationClient的onDestroy
             */
            locationClient.onDestroy();
            locationClient = null;
            locationOption = null;
        }
    }
    

    /**
     * 没获取到网络数据时的死数据
     */
    private void getDefaultGridData() {
        WeatherInfo wi;
        defaultList = new ArrayList<WeatherInfo>();
        for (int i = 0; i < 5; ++i) {
            wi = new WeatherInfo();
            wi.setWhichDayOfWeek("星期天");
            wi.setWeatherTmp("25℃");
            wi.setCurrentDate("1月1日");
            wi.setWeatherWind("西南风");
            wi.setWeatherImg(R.drawable.forecast_sun);
            wi.setWeatherInfo("晴天");
            wi.setWeatherHum("湿度50%");
            wi.setWeatherPress("气压1011hPa");
            defaultList.add(wi);
        }
        initGridView(defaultList);
    }


//    /**
//     * 开启定位服务
//     */
//    private void startLocationService() {
//        Intent intent = new Intent(this, LocationService.class);
//        startService(intent);
//    }
//
//    /**
//     * 关闭定位服务
//     */
//    private void stopLocationService() {
//        Intent intent = new Intent(this, LocationLocalService.class);
//        stopService(intent);
//    }

    /**
     * 初始化城市代码
     */
    private void initCityCodeTable() {
        mapAllNameID = new HashMap<String, String>();
        NameIdMap nameIDMap = new NameIdMap();
        mapAllNameID = nameIDMap.getMapAllNameID();
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        forecastGridView = (GridView) findViewById(R.id.forecastgridview);
        weatherLocCity = (TextView) findViewById(R.id.weatherloccity);
        humidity = (TextView) findViewById(R.id.humiditytext);
        windSpeed = (TextView) findViewById(R.id.windspeedtext);
        presuretext = (TextView) findViewById(R.id.presuretext);
        bigTmpTxt = (TextView) findViewById(R.id.weathertmp);
        bigWeatherImg = (ImageView) findViewById(R.id.weatherimg);
        bigWeatherInfo = (TextView) findViewById(R.id.weatherinfo);
        currentDateText = (TextView) findViewById(R.id.currentdate);
        AMOrPMText = (TextView) findViewById(R.id.amOrpmtext);
        linearLayout = (LinearLayout) findViewById(R.id.weatherandtmpandicon);
        forecastList = new ArrayList<WeatherInfo>();
    }

    /**
     * 初始化天气预报的gridview数据
     */
    private void initGridView(final List<WeatherInfo> weatherlist) {
        forecastButtonAdapter = new ForecastButtonAdapter(this, weatherlist);
        forecastGridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        forecastGridView.setAdapter(forecastButtonAdapter);
        forecastGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               
            			notifyUpdateUI(weatherlist.get(position));
            }
        });
    }

    /**
     * 注册广播接收器并开启位置服务
     */
    private void regisReceive() {
        mRecieve = new MsgRecieve();
        IntentFilter locationFilter = new IntentFilter();
        locationFilter.addAction(ACTION_TIMEZONE_CHANGED);
        locationFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mRecieve, locationFilter);
    }

    /**
     * 接收广播
     */
    public class MsgRecieve extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            log_i("Action:" + action);
            if (ACTION_TIMEZONE_CHANGED.equals(action)) {
                log_i("本地时间已改变");
                changeAMOrPM();
            }
            if(!TextUtils.isEmpty(Utils.getCity(getApplicationContext())))
            	getWeatherInfoByCityCode(weatherUrl + mapAllNameID.get(Utils.getCity(getApplicationContext())));

        }
    }

    /**
     * 改变时间的AM或者PM的显示(上午或者下午)
     */
    private void changeAMOrPM() {
        Calendar c = Calendar.getInstance();
        AMOrPMText.setText((c.get(Calendar.HOUR_OF_DAY)) > 12 ? "pm" : "am");
    }

    /**
     * 根据当前的位置获取城市名URL
     */
    private String getUrl(String lon, String lat) {
        return (TextUtils.isEmpty(lon) || TextUtils.isEmpty(lat)) ?
                locationUrl + Utils.QINGHUA_LON + "&lngy=" + Utils.QINGHUA_LAT :
                locationUrl + lon + "&lngy=" + lat;
    }


    /**
     * 根据当前位置来获取城市名，如果当前城市和默认城市是一样的，则不保存，否则保存为默认城市
     *
     * @parms 请求的URL
     */
    private void getCityName(String url) {
    	Log.i("ljwtest:", "获取城市url是" + url);
        HttpUtils mUtils = new HttpUtils();
        mUtils.send(HttpRequest.HttpMethod.POST, url, new RequestCallBack<String>() {
            @Override
            public void onFailure(HttpException e, String s) {
                log_i(e.toString());
                e.printStackTrace();
            }

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String res = responseInfo.result;
                try {
                    JSONObject jo = new JSONObject(res).getJSONObject("row")
                            .getJSONObject("result")
                            .getJSONObject("addressComponent");
                    String cityName = jo.getString("city");
                    log_i("当前的城市:" + cityName);
                    getWeatherInfoByCityCode(weatherUrl + mapAllNameID.get(cityName.substring(0, cityName.length() - 1)));
                    //若获取到当前的城市和本地储存的不一样则更新为当地的城市
//                    if (!Utils.getCity(getApplicationContext()).equals(cityName.substring(0, cityName.length() - 1)))
//                        Utils.setCity(getApplicationContext(), cityName.substring(0, cityName.length() - 1));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 根据当前的城市代码获取相应的天气信息
     */
    private void getWeatherInfoByCityCode(String url) {
        HttpUtils mUtils = new HttpUtils();
        log_i("城市代码的url:" + url);
        mUtils.send(HttpRequest.HttpMethod.GET, url, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                log_i("原始数据:" + responseInfo.result);
              //  String weatherinfo = cutJsonString(Utils.unicodeToString(Utils.formatJsonString(responseInfo.result)));
                String weatherinfo = responseInfo.result;
                log_i(weatherinfo);
                parseWeatherInfo(weatherinfo);
            }

            @Override
            public void onFailure(HttpException e, String s) {
                log_i("onFailure" + s);
            }
        });
    }

    /**
     * 剪切字符串，这个url返回的JSON数据有个包头没用
     */
    private String cutJsonString(String info) {
        return info.substring(5, info.length() - 2);
    }


    /**
     * 解析更新天气信息
     */
    private void parseWeatherInfo(String info) {
        parseCityInfo(info);
        for (int i = 0; i < 5; ++i) {
            getAndAddForecastGridView(parseWhichDayForcastInfo(info, i));
        }
            initGridView(forecastList);
            notifyUpdateUI(forecastList.get(0));
    }

    /**
     * 解析更新城市
     */
    private void parseCityInfo(String info) {
        try {
            JSONObject jo = new JSONObject(info);
            JSONArray ja = jo.getJSONArray("area");
            JSONArray ja1 = (JSONArray) ja.get(2);
            log_i("解析的城市;" + ja1.get(0).toString());
            updateperCitys(ja1.get(0).toString());
        } catch (JSONException e) {
            log_i("解析出错" + e.toString());
            e.printStackTrace();
        }
    }

    /**
     * 取得第几天天气预报信息
     */
    private JSONObject parseWhichDayForcastInfo(String info, int which) {
        try {
            JSONObject jo = new JSONObject(info);
            JSONArray ja = jo.getJSONArray("forecast");
            return (JSONObject) ja.get(which);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 解析并存入GridView的Item的数据
     */
    private void getAndAddForecastGridView(JSONObject jsonObject) {
        if (jsonObject == null)
            return;
        nowDate = new WeatherInfo();
        try {
        //   {"wind":{"dir":"西风","deg":"234","sc":"微风","spd":"1"},"hum":"58","pcpn":"1.0",
            // "astro":{"mr":"06:42","sr":"05:12","ms":"20:31","ss":"18:31"},"uv":"5","tmp":{"min":"15","max":"25"},
            // "pop":"100","date":"2017-04-28","pres":"1015","cond":{"cond_n":"阴","cond_d":"阴"},"vis":"18"}

        //    {"date":"2017-03-30","info":{"day":["1","多云","27","无持续风向","微风"],"night":["1","多云","22","无持续风向","微风"]}}
            log_i("日期是:" + Utils.formatDate(jsonObject.get("date").toString()));
            log_i("气压是:" + jsonObject.get("pres").toString());
            log_i("湿度是:" + jsonObject.get("hum").toString());
            JSONObject windObject = jsonObject.getJSONObject("wind");
            log_i("风向是:" + windObject.get("dir").toString() +" "+ windObject.get("sc").toString());
            JSONObject condObject = jsonObject.getJSONObject("cond");
            log_i("天气是:" + condObject.get("cond_d").toString());
            JSONObject tmpObject = jsonObject.getJSONObject("tmp");
            log_i("温度是:" + tmpObject.get("min").toString()+ " "+tmpObject.get("max").toString());

            nowDate.setWeatherInfo(condObject.get("cond_d").toString());
            nowDate.setWeatherImg(getWeathersmallImg(condObject.get("cond_d").toString()));
            if (windObject.get("dir").toString().equals("无持续风向")){
                nowDate.setWeatherWind( windObject.get("sc").toString());
            }else {
                nowDate.setWeatherWind(windObject.get("dir").toString() +" "+ windObject.get("sc").toString());
            }

            nowDate.setCurrentDate(Utils.formatDate(jsonObject.get("date").toString()));
            nowDate.setWeatherTmp(tmpObject.get("min").toString()+ "-"+tmpObject.get("max").toString() + "℃");
            nowDate.setWhichDayOfWeek(Utils.getWeekDays(jsonObject.get("date").toString()));
            nowDate.setWeatherHum("湿度"+jsonObject.get("hum").toString()+"%");
            nowDate.setWeatherPress("气压"+jsonObject.get("pres").toString()+"hPa");

           /* JSONObject jsonObject1 = jsonObject.getJSONObject("info");
            JSONArray jsonArray = jsonObject1.has("day") ? jsonObject1.getJSONArray("day") : jsonObject1.getJSONArray("night");
            log_i("天气是:" + jsonArray.get(1).toString());
            log_i("温度是:" + jsonArray.get(2).toString());
            log_i("风向是:" + jsonArray.get(4).toString());
            log_i(Utils.getWeekDays(jsonObject.get("date").toString()));
            nowDate.setWeatherInfo(jsonArray.get(1).toString());
            nowDate.setWeatherImg(getWeathersmallImg(jsonArray.get(1).toString()));
            nowDate.setWeatherWind(jsonArray.get(4).toString());
            nowDate.setCurrentDate(Utils.formatDate(jsonObject.get("date").toString()));
            nowDate.setWeatherTmp(jsonArray.get(2).toString() + "℃");
            nowDate.setWhichDayOfWeek(Utils.getWeekDays(jsonObject.get("date").toString()));*/
            forecastList.add(nowDate);
        } catch (JSONException e) {
            log_i("解析错误:" + e.toString());
            e.printStackTrace();
        }
    }


    /**
     * 根据天气信息设置天气图片（大图）
     *
     * @param cond 天气信息
     * @return 对应的天气图片id
     */
    private int getWeatherImg(String cond) {
        int img = 0;
        int length = cond.length();
        if (cond.contains("晴"))
            img = R.drawable.weather_sun;
        else if (cond.contains("多云"))
            img = R.drawable.weather_cloud;
        else if (cond.contains("阴"))
            img = R.drawable.weather_overcast;
        else if (cond.contains("雷"))
            img = R.drawable.weather_thunderandrain;
        else if (cond.contains("雨")) {
            if (cond.contains("小雨"))
                img = R.drawable.weather_smallrain;
            else if (cond.contains("中雨"))
                img = R.drawable.weather_middlerain;
            else if (cond.contains("大雨"))
                img = R.drawable.weather_bigrain;
            else if (cond.contains("雨夹雪"))
                img = R.drawable.weather_rainandsnow;
            else if (cond.contains("暴雨"))
                img = R.drawable.weather_stormrain;
            else
                img = R.drawable.weather_smallrain;
        } else if (cond.contains("雪")) {
            if (cond.contains("小雪"))
                img = R.drawable.weather_smallsnow;
            else if (cond.contains("中雪"))
                img = R.drawable.weather_middlesnow;
            else
                img = R.drawable.weather_smallsnow;
        } else
            img = R.drawable.weather_sun;
        return img;
    }

    /**
     * 根据天气信息设置天气图片（小图）
     *
     * @param cond 天气信息
     * @return 对应的天气图片id
     */
    private int getWeathersmallImg(String cond) {
        int img = 0;
        int length = cond.length();
        if (cond.contains("晴") && length <= 2)
            img = R.drawable.forecast_sun;
        else if (cond.contains("多云"))
            img = R.drawable.forecast_cloud;
        else if (cond.contains("阴") && length <= 2)
            img = R.drawable.forecast_overcast;
        else if (cond.contains("雷"))
            img = R.drawable.forecast_thunderandrain;
        else if (cond.contains("雨")) {
            if (cond.contains("小雨"))
                img = R.drawable.forecast_smallrain;
            else if (cond.contains("中雨"))
                img = R.drawable.forecast_middlerain;
            else if (cond.contains("大雨"))
                img = R.drawable.forecast_bigrain;
            else if (cond.contains("雨夹雪"))
                img = R.drawable.forecast_rainandsnow;
            else if (cond.contains("暴雨"))
                img = R.drawable.forecast_stormrain;
            else
                img = R.drawable.forecast_smallrain;
        } else if (cond.contains("雪")) {
            if (cond.contains("小雪"))
                img = R.drawable.forecast_smallsnow;
            else if (cond.contains("中雪"))
                img = R.drawable.forecast_middlesnow;
            else
                img = R.drawable.forecast_smallsnow;
        } else
            img = R.drawable.forecast_sun;
        return img;
    }

    /**
     * 动态更新界面其他的UI元素
     */
    private void notifyUpdateUI(WeatherInfo weatherInfo) {
        windSpeed.setText(weatherInfo.getWeatherWind());
        bigWeatherImg.setImageResource(getWeatherImg(weatherInfo.getWeatherInfo()));
        bigTmpTxt.setText(weatherInfo.getWeatherTmp());
        bigWeatherInfo.setText(weatherInfo.getWeatherInfo());
        currentDateText.setText(weatherInfo.getCurrentDate() + " " + weatherInfo.getWhichDayOfWeek());
        presuretext.setText(weatherInfo.getWeatherPress());
        humidity.setText(weatherInfo.getWeatherHum());
        bigWeatherImg.setImageResource(getWeatherImg(weatherInfo.getWeatherInfo()));
        bigWeatherInfo.setText(weatherInfo.getWeatherInfo());
        bigTmpTxt.setText(weatherInfo.getWeatherTmp());
    }

    /**
     * 更新城市UI
     */
    private void updateperCitys(String cityname) {
        weatherLocCity.setText(cityname);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mRecieve);
        destroyLocation();
//        stopLocationService();
        log_i("onDestroy");
    }

    private void log_i(String s) {
        Log.i("ljwtest:", s);
    }

}

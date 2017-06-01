package com.example.showweather.view.fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.showweather.R;
import com.example.showweather.WeatherApplication;
import com.example.showweather.activity.CityManagerActivity;
import com.example.showweather.activity.component.DaggerWeatherFragmentComponent;
import com.example.showweather.activity.module.WeatherFragmentModule;
import com.example.showweather.contract.WeatherFragmentContract;
import com.example.showweather.main.ForecastButtonAdapter;
import com.example.showweather.main.Utils;
import com.example.showweather.main.WeatherInfo;
import com.example.showweather.model.db.entities.minimalist.Weather;
import com.example.showweather.model.db.entities.minimalist.WeatherLive;
import com.example.showweather.presenter.WeatherFragmentPresenter;
import com.example.showweather.utils.DateConvertUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WeatherFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WeatherFragment extends Fragment implements WeatherFragmentContract.View {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    @BindView(R.id.swiprefresh)
    SwipeRefreshLayout mRefreshLayout;

    // TODO: Rename and change types of parameters
    private String mParam1 = "";
    private String mParam2 = "";

    @BindView(R.id.forecastgridview)
    GridView forecastGridView;//天气预报
    private ForecastButtonAdapter forecastButtonAdapter;
    @BindView(R.id.weatherloccity)
    TextView weatherLocCity;//定位城市
    @BindView(R.id.humiditytext)
    TextView humidity;//湿度
    @BindView(R.id.windspeedtext)
    TextView windSpeed;//风速
    @BindView(R.id.presuretext)
    TextView presuretext;//气压
    @BindView(R.id.weathertmp)
    TextView bigTmpTxt;//中间的温度大图
    @BindView(R.id.weatherimg)
    ImageView bigWeatherImg;//中间的天气大图
    @BindView(R.id.weatherinfo)
    TextView bigWeatherInfo;//中间的天气描述
    @BindView(R.id.currentdate)
    TextView currentDateText;//当前的日期
    @BindView(R.id.amOrpmtext)
    TextView AMOrPMText;//上午还是下午
    @BindView(R.id.weatherandtmpandicon)
    LinearLayout linearLayout;

    private Context mContent;
    private static String locationUrl = "http://lbs.juhe.cn/api/getaddressbylngb?lngx=";//获取城市名称URL
    //private static String weatherUrl = "http://cdn.weather.hao.360.cn/api_weather_info.php?app=hao360&_jsonp=data&code=";//根据城市获取天气信息URL
    private static String weatherUrl = "http://service.envicloud.cn:8082/v2/weatherforecast/ZMF3ZWLNDWKXNDKZMZU5ODQ4NZE2/";//根据城市获取天气信息URL
    private static final String ACTION_TIMEZONE_CHANGED = Intent.ACTION_TIME_TICK;//监听时区变化的广播
    private boolean isEnableClick = false;

    @Inject
    WeatherFragmentPresenter presenter;

    private MsgRecieve mRecieve;//广播监听
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 11;

    @OnClick(R.id.weatherloccity)
    void SelectCity() {
        //   Intent intent = new Intent(WeatherActivity.this, SelectCityActivity.class);
        Intent intent = new Intent(mContent, CityManagerActivity.class);
        startActivity(intent);
    }

    private List<WeatherInfo> forecastList;
    private List<WeatherInfo> defaultList;
    private WeatherInfo nowDate;
    private String cityId;

    public WeatherFragment() {
        // Required empty public constructor
    }

    public WeatherFragment(String cityId) {
        // Required empty public constructor
        this.cityId = cityId;
    }

    Weather weather;

    public WeatherFragment(Weather weather) {
        // Required empty public constructor
        this.weather = weather;

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WeatherFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WeatherFragment newInstance(String param1, String param2) {
        WeatherFragment fragment = new WeatherFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContent = getActivity();
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);

        }
        forecastList = new ArrayList<WeatherInfo>();
        //    Toast.makeText(mContent, "showWerather fragment onCreate "+mParam1+mParam2, Toast.LENGTH_LONG).show();
        DaggerWeatherFragmentComponent.builder()
                .applicationComponent(WeatherApplication.getInstance().getApplicationComponent())
                .weatherFragmentModule(new WeatherFragmentModule(this))
                .build().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_weather, container, false);
        ButterKnife.bind(this, view);
        if (getArguments() != null)
            updateperCitys(mParam2);
        regisReceive();
        changeAMOrPM();
        getDefaultGridData();

        if (mRefreshLayout != null) {
            mRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                    android.R.color.holo_green_light,
                    android.R.color.holo_orange_light,
                    android.R.color.holo_red_light);
            mRefreshLayout.setSize(0);
            mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                                                    @Override
                                                    public void onRefresh() {

                                                        mRefreshLayout.postDelayed(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                setRefreshing(true);
                                                                presenter.updateWeather(mParam1);
                                                            }
                                                        },1000);
                                                    }
                                                }
                    /*
                    () -> mRefreshLayout.postDelayed(presenter.updateWeather(mParam1), 1000)*/);
        }
        //   Toast.makeText(mContent, "showWerather fragment onCreateView "+mParam1+mParam2, Toast.LENGTH_LONG).show();
        return view;
    }

    @Override
    public void onDestroyView() {
        //    Toast.makeText(mContent, "showWerather fragment onDestroyView "+mParam1+mParam2, Toast.LENGTH_LONG).show();
        super.onDestroyView();
    }

    @Override
    public void onResume() {
        super.onResume();
//        showWerather(weather);
        //   Toast.makeText(mContent, "showWerather fragment onResume "+mParam1+mParam2, Toast.LENGTH_LONG).show();
        presenter.updateWeather(mParam1);
    }

    @Override
    public void onDestroy() {
        //   Toast.makeText(mContent, "showWerather fragment onDestroy "+mParam1+mParam2, Toast.LENGTH_LONG).show();
        super.onDestroy();
        mContent.unregisterReceiver(mRecieve);

        presenter.unSubscribe();
    }

    @Override
    public void setPresenter(WeatherFragmentContract.Presenter presenter) {
        //  this.presenter = (WeatherFragmentPresenter) presenter;
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
            wi.setWeatherPress("气压 1015 hPa");
            wi.setWeatherLiveImg(R.drawable.forecast_sun);
            wi.setWeatherLiveTmp("25℃");
            wi.setWeatherLiveInfo("晴天");
            defaultList.add(wi);
        }
        initGridView(defaultList);
    }

    /**
     * 初始化天气预报的gridview数据
     */
    private void initGridView(final List<WeatherInfo> weatherlist) {
        forecastButtonAdapter = new ForecastButtonAdapter(mContent, weatherlist);
        forecastGridView.setNumColumns(weatherlist.size());
        forecastGridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        forecastGridView.setAdapter(forecastButtonAdapter);
        forecastGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                notifyUpdateUI(weatherlist.get(position), position);
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
        mContent.registerReceiver(mRecieve, locationFilter);
    }

    /**
     * 接收广播
     */
    public class MsgRecieve extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_TIMEZONE_CHANGED.equals(action)) {
                changeAMOrPM();
            }
            //     presenter.subscribe();

        }
    }


    /**
     * 改变时间的AM或者PM的显示(上午或者下午)
     */
    private void changeAMOrPM() {
        Calendar c = Calendar.getInstance();
        AMOrPMText.setText((c.get(Calendar.HOUR_OF_DAY)) > 12 ? "pm" : "am");
    }

    @Override
    public void showWerather(Weather weather) {
        //   Toast.makeText(mContent, "showWerather fragment", Toast.LENGTH_LONG).show();
        mRefreshLayout.setRefreshing(false);
     //   updateperCitys(weather.getCityName());
        forecastList.clear();
        for (int i = 0; i < weather.getWeatherForecasts().size() && i < 5; ++i) {
            nowDate = new WeatherInfo();
            nowDate.setWeatherInfo(weather.getWeatherForecasts().get(i).getWeather());
            nowDate.setWeatherImg(getWeathersmallImg(weather.getWeatherForecasts().get(i).getWeather()));
            nowDate.setWeatherWind(weather.getWeatherForecasts().get(i).getWind());
            nowDate.setCurrentDate(Utils.formatDate(weather.getWeatherForecasts().get(i).getData()));
            nowDate.setWeatherTmp(weather.getWeatherForecasts().get(i).getTempMin() + "-" + weather.getWeatherForecasts().get(i).getTempMax() + "℃");
            nowDate.setWhichDayOfWeek(weather.getWeatherForecasts().get(i).getWeek());
            nowDate.setWeatherHum("湿度" + weather.getWeatherForecasts().get(i).getHum() + "%");
            nowDate.setWeatherPress("气压" + weather.getWeatherForecasts().get(i).getPres() + "hPa");
            if (weather.getWeatherLive().getTemp().contains("."))
                nowDate.setWeatherLiveTmp(Integer.parseInt(weather.getWeatherLive().getTemp().substring(0, weather.getWeatherLive().getTemp().indexOf("."))) + "℃");
            else
                nowDate.setWeatherLiveTmp(weather.getWeatherLive().getTemp() + "℃");

            nowDate.setWeatherLiveInfo(weather.getWeatherLive().getWeather());
            nowDate.setWeatherLiveImg(getWeathersmallImg(weather.getWeatherLive().getWeather()));
            forecastList.add(nowDate);
        }
        initGridView(forecastList);
        if (forecastList.size()>0){
            notifyUpdateUI(forecastList.get(0), 0);
        }else {//有的城市查不到天气预报，只有实时天气（比如内蒙古*鄂尔多斯*伊和乌素）
            if (weather.getWeatherLive()!=null){
                notifyUpdateUI(weather.getWeatherLive());
            }

        }
    }

    @Override
    public void setRefreshing(boolean b) {
        mRefreshLayout.setRefreshing(b);
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
        if (cond.contains("雨")) {
            if (cond.contains("雷"))
                img = R.drawable.weather_thunderandrain;
            else if (cond.contains("小雨"))
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
        } else if (cond.contains("晴")&& length <= 2)
            img = R.drawable.weather_sun;
        else if (cond.contains("多云"))
            img = R.drawable.weather_cloud;
        else if (cond.contains("阴")&& length <= 2)
            img = R.drawable.weather_overcast;
        else if (cond.contains("雷"))
            img = R.drawable.weather_thunderandrain;
        else
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
        if (cond.contains("雨")) {
            if (cond.contains("雷"))
                img = R.drawable.forecast_thunderandrain;
            else if (cond.contains("小雨"))
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
        } else if (cond.contains("晴") && length <= 2)
            img = R.drawable.forecast_sun;
        else if (cond.contains("多云"))
            img = R.drawable.forecast_cloud;
        else if (cond.contains("阴") && length <= 2)
            img = R.drawable.forecast_overcast;
        else if (cond.contains("雷"))
            img = R.drawable.forecast_thunderandrain;
        else
            img = R.drawable.forecast_sun;
        return img;
    }

    /**
     * 动态更新界面其他的UI元素
     */
    private void notifyUpdateUI(WeatherInfo weatherInfo, int day) {
        windSpeed.setText(weatherInfo.getWeatherWind());
        bigWeatherImg.setImageResource(getWeatherImg(weatherInfo.getWeatherInfo()));
        bigTmpTxt.setText(weatherInfo.getWeatherTmp());
        bigWeatherInfo.setText(weatherInfo.getWeatherInfo());
        currentDateText.setText(weatherInfo.getCurrentDate() + " " + weatherInfo.getWhichDayOfWeek());
        presuretext.setText(weatherInfo.getWeatherPress());
        humidity.setText(weatherInfo.getWeatherHum());
        bigWeatherImg.setImageResource(getWeatherImg(weatherInfo.getWeatherInfo()));
        bigWeatherInfo.setText(weatherInfo.getWeatherInfo());
        if (day == 0) {
            bigTmpTxt.setText(weatherInfo.getWeatherLiveTmp());
            bigWeatherImg.setImageResource(getWeatherImg(weatherInfo.getWeatherLiveInfo()));
            bigWeatherInfo.setText(weatherInfo.getWeatherLiveInfo());
        } else {
            bigTmpTxt.setText(weatherInfo.getWeatherTmp());
        }

    }
    /**
     * 动态更新界面其他的UI元素
     */
    private void notifyUpdateUI(WeatherLive weatherInfo) {
        windSpeed.setText(weatherInfo.getWind());
        bigWeatherImg.setImageResource(getWeatherImg(weatherInfo.getWeather()));
        if (weatherInfo.getTemp().contains("."))
            bigTmpTxt.setText(Integer.parseInt(weatherInfo.getTemp().substring(0, weatherInfo.getTemp().indexOf("."))) + "℃");
        else
            bigTmpTxt.setText(weatherInfo.getTemp() + "℃");
        bigWeatherInfo.setText(weatherInfo.getWeather());
        currentDateText.setText(Utils.formatDate(DateConvertUtils.timeStampToDate(weatherInfo.getTime(),DateConvertUtils.DATA_FORMAT_PATTEN_YYYY_MMMM_DD_HH_MM))
                + " " + Utils.getWeekDays(DateConvertUtils.timeStampToDate(weatherInfo.getTime(),DateConvertUtils.DATA_FORMAT_PATTEN_YYYY_MMMM_DD_HH_MM)));
        presuretext.setText("气压 1011hPa");
        humidity.setText("湿度"+weatherInfo.getHumidity()+"%");
        bigWeatherImg.setImageResource(getWeatherImg(weatherInfo.getWeather()));
        bigWeatherInfo.setText(weatherInfo.getWeather());

    }
    /**
     * 更新城市UI
     */
    private void updateperCitys(String cityname) {
        weatherLocCity.setText(cityname);
    }

    @OnClick(R.id.weatherandtmpandicon)
    public void onViewClicked() {
        presenter.updateWeather(mParam1);
    }
}

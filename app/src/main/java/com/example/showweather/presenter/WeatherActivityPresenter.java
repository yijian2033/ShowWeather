package com.example.showweather.presenter;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.text.TextUtils;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.example.showweather.ApplicationModule;
import com.example.showweather.activity.WeatherActivity;
import com.example.showweather.contract.WeatherActivityContract;
import com.example.showweather.main.NameIdMap;
import com.example.showweather.main.Utils;
import com.example.showweather.model.db.dao.WeatherDao;
import com.example.showweather.model.db.entities.minimalist.Weather;
import com.example.showweather.model.db.entities.minimalist.WeatherCities;
import com.example.showweather.model.preference.PreferenceHelper;
import com.example.showweather.model.preference.WeatherSettings;
import com.example.showweather.model.repository.WeatherDataRepository;
import com.example.showweather.presenter.component.DaggerPresenterComponent;
import com.example.showweather.utils.ActivityScoped;
import com.example.showweather.utils.NetworkUtils;

import java.io.InvalidClassException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;


/**
 * ljw：Administrator on 2017/5/18 0018 10:55
 */
@ActivityScoped
public class WeatherActivityPresenter implements WeatherActivityContract.Presenter{
    private final Context context;
    private  WeatherActivityContract.View weatherView;

    private CompositeDisposable compositeDisposable;


    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = new AMapLocationClientOption();
    private LocationManager mlocation;//用系统的GPS获取当前位置信息

    private Map<String, String> mapAllNameID;

    @Inject
    WeatherDao weatherDao;
    public WeatherActivityPresenter(Context context, WeatherActivityContract.View weatherView) {
        this.context = context;
       // Toast.makeText(context,"WeatherActivityPresenter",Toast.LENGTH_LONG).show();
        this.weatherView = weatherView;
        weatherView.setPresenter(this);
        this.compositeDisposable = new CompositeDisposable();

      DaggerPresenterComponent.builder()
                .applicationModule(new ApplicationModule(context))
                .build().inject(this);
      //  initLocation();
    //    startLocation();
        initCityCodeTable();
    }

    @Override
    public void subscribe() {


    }

    @Override
    public void unSubscribe() {
     //   Toast.makeText(context,"WeatherActivityPresenter  unSubscribe",Toast.LENGTH_LONG).show();
        compositeDisposable.clear();
     //   destroyLocation();
        if (weatherView != null)
        weatherView = null;
    }

    /**
     * 初始化城市代码
     */
    private void initCityCodeTable() {
        mapAllNameID = new HashMap<String, String>();
        NameIdMap nameIDMap = new NameIdMap();
        mapAllNameID = nameIDMap.getMapAllNameID();
    }

    /**
     * ��ʼ����λ
     *
     * @author hongming.wang
     * @since 2.8.0
     */
    private void initLocation() {
        //初始化client
        locationClient = new AMapLocationClient(context);
        //设置定位参数
        locationClient.setLocationOption(getDefaultOption());
        // 设置定位监听
        locationClient.setLocationListener(locationListener);
    }

    /**
     * Ĭ�ϵĶ�λ����
     *
     * @author hongming.wang
     * @since 2.8.0
     */
    private AMapLocationClientOption getDefaultOption() {
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
    private String cityName ="";//定位到的城市名
    AMapLocationListener locationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation loc) {
            if (null != loc && loc.getErrorCode() == 0) {
                //解析定位结果
                Log.i("ljwtest:", "定位成功,城市名：" + loc);
                if (!cityName.equals(loc.getCity())){//定位到的城市有变动时
                    cityName = loc.getCity();
                    String cityId = mapAllNameID.get(loc.getCity().substring(0, loc.getCity().length() - 1));
                    WeatherCities city ;
                    try {
                        city =  weatherDao.querySelectedCity(cityId);
                    } catch (SQLException e) {
                        e.printStackTrace();
                        city = null;
                    }
                    if (null == city){
                        city = new WeatherCities(cityId,loc.getCity().substring(0, loc.getCity().length() - 1),"");
                        weatherDao.insertSelectedCity(city);
                        try {
                            PreferenceHelper.savePreference(WeatherSettings.SETTINGS_CURRENT_CITY_ID, cityId);
                        } catch (InvalidClassException e) {
                            e.printStackTrace();
                        }

                        Intent intent = new Intent(context, WeatherActivity.class);
                        context.startActivity(intent);
                    }

                }
                Utils.setCity(context, loc.getCity().substring(0, loc.getCity().length() - 1));
            } else {
                if (null != loc && loc.getErrorCode() != 0) {
                    Log.i("ljwtest:", "定位失败，" + loc.getErrorInfo());
                } else
                    Log.i("ljwtest:", "定位失败，loc is null");
            }
        }
    };

    /**
     * 开始定位
     *
     * @author hongming.wang
     * @since 2.8.0
     */
    private void startLocation() {
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
     * @author hongming.wang
     * @since 2.8.0
     */
    private void stopLocation() {
        // 停止定位
        locationClient.stopLocation();
    }

    /**
     * 销毁定位
     *
     * @author hongming.wang
     * @since 2.8.0
     */
    private void destroyLocation() {
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

}

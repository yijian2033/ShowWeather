package com.example.showweather.presenter;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.example.showweather.ApplicationModule;
import com.example.showweather.activity.WeatherActivity;
import com.example.showweather.contract.WeatherFragmentContract;
import com.example.showweather.main.NameIdMap;
import com.example.showweather.main.Utils;
import com.example.showweather.model.db.dao.WeatherDao;
import com.example.showweather.model.db.entities.minimalist.Weather;
import com.example.showweather.model.db.entities.minimalist.WeatherCities;
import com.example.showweather.model.preference.PreferenceHelper;
import com.example.showweather.model.preference.WeatherSettings;
import com.example.showweather.model.repository.WeatherDataRepository;
import com.example.showweather.presenter.component.DaggerPresenterComponent;
import com.example.showweather.utils.NetworkUtils;

import org.reactivestreams.Subscriber;

import java.io.InvalidClassException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


/**
 * ljw：Administrator on 2017/5/20 0020 11:44
 */
public class WeatherFragmentPresenter implements WeatherFragmentContract.Presenter{
    private final Context context;
    private  WeatherFragmentContract.View weatherView;

    private CompositeDisposable compositeDisposable;


    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = new AMapLocationClientOption();
    private LocationManager mlocation;//用系统的GPS获取当前位置信息

    @Inject
    WeatherDao weatherDao;
    @Inject
    WeatherFragmentPresenter(Context context, WeatherFragmentContract.View weatherView) {
        this.context = context;
        // Toast.makeText(context,"WeatherActivityPresenter",Toast.LENGTH_LONG).show();
        this.weatherView = weatherView;
        this.weatherView.setPresenter(this);
        compositeDisposable = new CompositeDisposable();
        DaggerPresenterComponent.builder()
                .applicationModule(new ApplicationModule(context))
                .build().inject(this);
    }

    @Override
    public void subscribe() {
        String cityId = PreferenceHelper.getSharedPreferences().getString(WeatherSettings.SETTINGS_CURRENT_CITY_ID.getId(), "101280601");

        updateWeather(cityId);

    }

    @Override
    public void unSubscribe() {
        //   Toast.makeText(context,"WeatherActivityPresenter  unSubscribe",Toast.LENGTH_LONG).show();
        compositeDisposable.clear();
        if (weatherView != null)
            weatherView = null;
    }

    public void updateWeather(String cityId) {
        Log.i("更新天气", "更新天气开始"+cityId);
     //   if (!TextUtils.isEmpty(Utils.getCity(context)))
        {
            Log.i("更新天气", "更新天气开始1 "+cityId);
            //	getWeatherInfoByCityCode(weatherUrl + mapAllNameID.get(Utils.getCity(getApplicationContext())));
            Disposable disposable = WeatherDataRepository.getWeather(context, cityId/*mapAllNameID.get(Utils.getCity(context))*/, weatherDao)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    //  .doOnRequest((l)->weatherView.setRefreshing(true))
                    .doOnTerminate(() -> weatherView.setRefreshing(false))
                    .subscribe(weather -> {
                        if (!NetworkUtils.isNetworkConnected(context)&&weather == null){//没有网路时本地数据库也查不到天气的情况
                            Toast.makeText(context,"网络不通，暂时无法获取天气数据",Toast.LENGTH_SHORT).show();
                        }else if (weatherView != null){
                            weatherView.showWerather(weather);
                        }
                    }
                            /*new Subscriber<Weather>() {
                                   @Override
                                   public void onCompleted() {

                                   }

                                   @Override
                                   public void onError(Throwable e) {
                                       //  Toast.makeText(WeatherActivity.this,"发生错误"+e.getMessage(),Toast.LENGTH_SHORT).show();
                                       Log.e("发生错误", "发生错误" + e.getMessage());

                                       e.printStackTrace();
                                   }

                                   @Override
                                   public void onNext(Weather weather) {
                                       //   Toast.makeText(WeatherActivity.this,"更新天气成功",Toast.LENGTH_SHORT).show();
                                       Log.i("更新天气成功", "更新天气成功");
                                       if (!NetworkUtils.isNetworkConnected(context)&&weather == null){//没有网路时本地数据库也查不到天气的情况
                                           Toast.makeText(context,"网络不通，暂时无法获取天气数据",Toast.LENGTH_SHORT).show();
                                       }else if (weatherView != null)
                                           weatherView.showWerather(weather);
                                   }
                               }
                    */);
            compositeDisposable.add(disposable);
        }
    }




}

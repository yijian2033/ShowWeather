package com.example.showweather.model.repository;

import android.content.Context;
import android.text.TextUtils;


import com.example.showweather.main.Utils;
import com.example.showweather.model.db.dao.WeatherDao;
import com.example.showweather.model.db.entities.adapter.EnvironmentCloudWeatherAdapter;
import com.example.showweather.model.db.entities.minimalist.Weather;
import com.example.showweather.model.db.entities.minimalist.WeatherLive;
import com.example.showweather.model.http.ApiClient;
import com.example.showweather.model.http.entity.envicloud.EnvironmentCloudWeatherLive;
import com.example.showweather.utils.NetworkUtils;

import org.reactivestreams.Subscriber;

import java.sql.SQLException;
;import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.annotations.NonNull;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.schedulers.Schedulers;

/**
 * @author baronzhang (baron[dot]zhanglei[at]gmail[dot]com ==>> baronzhang.com)
 *         2016/12/10
 */
public class WeatherDataRepository {

    public static Observable<Weather> getWeather(Context context, String cityId, WeatherDao weatherDao) {
        //从数据库获取天气数据
        Observable<Weather> observableForGetWeatherFromDB = Observable.create(new ObservableOnSubscribe<Weather>() {
                                                                                  @Override
                                                                                  public void subscribe(@NonNull ObservableEmitter<Weather> e) throws Exception {
                                                                                      try {
                                                                                          Weather weather = weatherDao.queryWeather(cityId);
                                                                                          if ( weather!=null )
                                                                                          e.onNext(weather);
                                                                                          e.onComplete();
                                                                                      } catch (SQLException ex) {
                                                                                          throw Exceptions.propagate(ex);
                                                                                      }
                                                                                  }
                                                                              }
                /*new Observable.OnSubscribe<Weather>() {
            @Override
            public void call(Subscriber<? super Weather> subscriber) {
                try {
                    Weather weather = weatherDao.queryWeather(cityId);
                    subscriber.onNext(weather);
                    subscriber.onCompleted();
                } catch (SQLException e) {
                    throw Exceptions.propagate(e);
                }
            }
        }*/);

        if (!NetworkUtils.isNetworkConnected(context))
            return observableForGetWeatherFromDB;
        //从服务端获取天气数据
        Observable<Weather> observableForGetWeatherFromNetWork = Observable.zip(
                ApiClient.environmentCloudWeatherService.getWeatherForecast(cityId),
                ApiClient.environmentCloudWeatherService.getWeatherLive(cityId),
                (environmentCloudForecast,environmentCloudWeatherLive)->new EnvironmentCloudWeatherAdapter(environmentCloudForecast,environmentCloudWeatherLive).getWeather());

        assert observableForGetWeatherFromNetWork != null;
        observableForGetWeatherFromNetWork = observableForGetWeatherFromNetWork.doOnNext(weather -> Schedulers.io().createWorker().schedule(() -> {
            try {
                weatherDao.insertOrUpdateWeather(weather);
            } catch (SQLException e) {
                throw Exceptions.propagate(e);
            }
        }));

        return Observable.concat(observableForGetWeatherFromDB, observableForGetWeatherFromNetWork)
                .filter(weather -> weather != null && !TextUtils.isEmpty(weather.getCityId()))
                .distinct(weather -> weather.getWeatherLive().getTime())
                .takeUntil(weather -> System.currentTimeMillis() - weather.getWeatherLive().getTime() <= 60 * 60 * 1000);
    }
}

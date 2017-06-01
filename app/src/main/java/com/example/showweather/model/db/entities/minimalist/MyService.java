package com.example.showweather.model.db.entities.minimalist;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.example.showweather.ApplicationModule;
import com.example.showweather.DaggerApplicationComponent;
import com.example.showweather.model.db.dao.WeatherDao;

import java.sql.SQLException;

import javax.inject.Inject;

public class MyService extends Service {
    public MyService() {
    }

    @Inject
    WeatherDao weatherDao;
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        /*DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .build().inject(this);*/
        return ibinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .build().inject(this);
    }


    WeatherLive weatherLive ;
    IGetWeatherService.Stub ibinder = new IGetWeatherService.Stub() {
        @Override
        public WeatherLive getWeatherLive(String cityId) throws RemoteException {

           // WeatherDao weatherDao = null;//new WeatherDao(getApplicationContext());
            Log.d("guiguigui","getWeatherLive" );
            try {
                Weather weather = weatherDao.queryWeather(cityId);
                if (weather != null)
                weatherLive = weather.getWeatherLive();
            } catch (SQLException e) {
                Log.e("guiguigui","getWeatherLive 2");
                e.printStackTrace();
            }
            return weatherLive;
        }
    };

}

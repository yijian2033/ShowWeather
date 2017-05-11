package com.example.showweather.model.db.entities.minimalist;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.widget.Toast;

import com.example.showweather.main.Utils;
import com.example.showweather.main.WeatherAcivity;
import com.example.showweather.model.db.dao.WeatherDao;
import com.example.showweather.model.repository.WeatherDataRepository;

import java.sql.SQLException;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MyService extends Service {
    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return ibinder;
    }
    WeatherLive weatherLive = null ;
    IGetWeatherService.Stub ibinder = new IGetWeatherService.Stub() {
        @Override
        public WeatherLive getWeatherLive(String cityId) throws RemoteException {

            WeatherDao weatherDao = new WeatherDao(getApplicationContext());
            try {
                Weather weather = weatherDao.queryWeather(cityId);
                if (weather != null)
                weatherLive = weather.getWeatherLive();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return weatherLive;
        }
    };

}

// IMyAidlInterface.aidl
package com.example.showweather.model.db.entities.minimalist;

// Declare any non-default types here with import statements
import com.example.showweather.model.db.entities.minimalist.WeatherLive;
interface IGetWeatherService{
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
            WeatherLive getWeatherLive(String cityId);
}

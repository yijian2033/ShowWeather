package com.example.showweather.activity.component;

import com.example.showweather.ApplicationComponent;
import com.example.showweather.activity.WeatherActivity;
import com.example.showweather.activity.module.WeatherActivityModule;
import com.example.showweather.utils.ActivityScoped;

import dagger.Component;

/**
 * ljw：Administrator on 2017/5/18 0018 11:47
 */
@ActivityScoped
@Component(modules = {WeatherActivityModule.class},dependencies = {ApplicationComponent.class})
public interface WeatherActivityComponent {
    void inject(WeatherActivity acivity);
}

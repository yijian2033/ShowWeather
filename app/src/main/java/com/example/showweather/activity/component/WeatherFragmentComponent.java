package com.example.showweather.activity.component;

import com.example.showweather.ApplicationComponent;
import com.example.showweather.activity.module.WeatherFragmentModule;
import com.example.showweather.utils.ActivityScoped;
import com.example.showweather.view.fragment.WeatherFragment;

import dagger.Component;

/**
 * ljw：Administrator on 2017/5/20 0020 09:49
 */
@ActivityScoped
@Component(modules = {WeatherFragmentModule.class},dependencies = {ApplicationComponent.class})
public interface  WeatherFragmentComponent{
    void inject(WeatherFragment fragment);
}

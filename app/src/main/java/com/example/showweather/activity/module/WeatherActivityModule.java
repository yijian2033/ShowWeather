package com.example.showweather.activity.module;

import com.example.showweather.contract.WeatherActivityContract;

import dagger.Module;
import dagger.Provides;

/**
 * ljw：Administrator on 2017/5/18 0018 11:42
 */
@Module
public class WeatherActivityModule {
    private WeatherActivityContract.View view;

    public WeatherActivityModule(WeatherActivityContract.View view) {

        this.view = view;
    }

    @Provides
    WeatherActivityContract.View provideHomePageContractView() {
        return view;
    }
}

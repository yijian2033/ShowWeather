package com.example.showweather.activity.module;

import com.example.showweather.contract.WeatherActivityContract;
import com.example.showweather.contract.WeatherFragmentContract;

import dagger.Module;
import dagger.Provides;

/**
 * ljw：Administrator on 2017/5/20 0020 09:48
 */
@Module
public class WeatherFragmentModule {
    private WeatherFragmentContract.View view;

    public WeatherFragmentModule(WeatherFragmentContract.View view) {

        this.view = view;
    }

    @Provides
    WeatherFragmentContract.View provideHomePageContractView() {
        return view;
    }
}

package com.example.showweather;

import android.content.Context;

import com.example.showweather.activity.WeatherActivity;
import com.example.showweather.model.db.entities.minimalist.MyService;

import javax.inject.Singleton;

import dagger.Component;

/**
 * @author 张磊 (baron[dot]zhanglei[at]gmail[dot]com ==>> baronzhang.com)
 *         2016/11/30
 */
@Singleton
@Component(modules = {ApplicationModule.class})
public interface ApplicationComponent {

    WeatherApplication getApplication();

    Context getContext();
    void inject(MyService service);
    void inject(WeatherActivity acivity);
}

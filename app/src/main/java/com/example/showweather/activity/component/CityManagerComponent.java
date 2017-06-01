package com.example.showweather.activity.component;



import com.example.showweather.ApplicationComponent;
import com.example.showweather.activity.CityManagerActivity;
import com.example.showweather.activity.module.CityManagerModule;
import com.example.showweather.utils.ActivityScoped;

import dagger.Component;

/**
 * @author baronzhang (baron[dot]zhanglei[at]gmail[dot]com ==>> baronzhang.com)
 *         2016/11/30
 */
@ActivityScoped
@Component(modules = CityManagerModule.class, dependencies = ApplicationComponent.class)
public interface CityManagerComponent {

    void inject(CityManagerActivity cityManagerActivity);
}

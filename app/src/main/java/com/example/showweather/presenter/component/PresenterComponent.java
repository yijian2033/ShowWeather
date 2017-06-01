package com.example.showweather.presenter.component;

import com.example.showweather.ApplicationModule;
import com.example.showweather.presenter.CityManagerPresenter;
import com.example.showweather.presenter.SelectCityPresenter;
import com.example.showweather.presenter.WeatherActivityPresenter;
import com.example.showweather.presenter.WeatherFragmentPresenter;

import javax.inject.Singleton;

import dagger.Component;

/**
 * @author 张磊 (baron[dot]zhanglei[at]gmail[dot]com ==>> baronzhang.com)
 *         2016/12/2
 */
@Singleton
@Component(modules = {ApplicationModule.class})
public interface PresenterComponent {

    void inject(CityManagerPresenter presenter);
    void inject(WeatherActivityPresenter presenter);
    void inject(SelectCityPresenter presenter);
    void inject(WeatherFragmentPresenter presenter);

}
 
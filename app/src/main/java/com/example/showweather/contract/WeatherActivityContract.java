package com.example.showweather.contract;

import com.example.showweather.BasePresenter;
import com.example.showweather.BaseView;
import com.example.showweather.model.db.entities.minimalist.Weather;

/**
 * ljw：Administrator on 2017/5/18 0018 10:54
 */
public interface WeatherActivityContract {
    interface Presenter extends BasePresenter{

    }
    interface  View extends BaseView<Presenter>{

        void showWerather(Weather weather);
    }
}

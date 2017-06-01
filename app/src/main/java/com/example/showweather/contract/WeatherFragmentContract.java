package com.example.showweather.contract;

import com.example.showweather.BasePresenter;
import com.example.showweather.BaseView;
import com.example.showweather.model.db.entities.minimalist.Weather;

/**
 * ljw：Administrator on 2017/5/19 0019 18:49
 */
public interface WeatherFragmentContract {
    interface Presenter extends BasePresenter {

    }
    interface  View extends BaseView<WeatherFragmentContract.Presenter> {

        void showWerather(Weather weather);

        void setRefreshing(boolean b);
    }
}

package com.example.showweather.contract;



import com.example.showweather.BasePresenter;
import com.example.showweather.BaseView;
import com.example.showweather.model.db.entities.minimalist.Weather;
import com.example.showweather.presenter.CityManagerPresenter;

import java.io.InvalidClassException;
import java.util.List;

/**
 * @author baronzhang (baron[dot]zhanglei[at]gmail[dot]com ==>> baronzhang.com)
 *         16/4/16
 */
public interface CityManagerContract {

    interface View extends BaseView<CityManagerPresenter> {

        void displaySavedCities(List<Weather> weatherList);
    }

    interface Presenter extends BasePresenter {

        void loadSavedCities();

        void deleteCity(String cityId);

        void saveCurrentCityToPreference(String cityId) throws InvalidClassException;
    }
}

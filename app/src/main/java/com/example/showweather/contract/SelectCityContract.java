package com.example.showweather.contract;



import com.example.showweather.BasePresenter;
import com.example.showweather.BaseView;
import com.example.showweather.model.db.entities.City;

import java.util.List;

/**
 * @author baronzhang (baron[dot]zhanglei[at]gmail[dot]com ==>> baronzhang.com)
 */
public interface SelectCityContract {

    interface View extends BaseView<Presenter> {
        void displayCountry(List<City> country);
        void displayCities(List<City> cities);
        void displayProvince(List<City> province);
        void onLocated();
    }

    interface Presenter extends BasePresenter {
        void loadCountry(String city);
        void loadCities(String province);
        void loadProvince();
    }
}

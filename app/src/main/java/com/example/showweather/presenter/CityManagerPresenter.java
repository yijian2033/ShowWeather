package com.example.showweather.presenter;

import android.content.Context;


import com.example.showweather.ApplicationModule;
import com.example.showweather.contract.CityManagerContract;
import com.example.showweather.model.db.dao.WeatherDao;
import com.example.showweather.model.db.entities.minimalist.Weather;
import com.example.showweather.model.preference.PreferenceHelper;
import com.example.showweather.model.preference.WeatherSettings;
import com.example.showweather.presenter.component.DaggerPresenterComponent;
import com.example.showweather.utils.ActivityScoped;

import java.io.InvalidClassException;
import java.sql.SQLException;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


/**
 * @author baronzhang (baron[dot]zhanglei[at]gmail[dot]com ==>> baronzhang.com)
 *         16/4/16
 */
@ActivityScoped
public final class CityManagerPresenter implements CityManagerContract.Presenter {

    private CityManagerContract.View view;


    CompositeDisposable composite ;
    @Inject
    WeatherDao weatherDao;

    @Inject
    CityManagerPresenter(Context context, CityManagerContract.View view) {

        this.view = view;
         this.composite = new CompositeDisposable();
        view.setPresenter(this);

        DaggerPresenterComponent.builder()
                .applicationModule(new ApplicationModule(context))
                .build().inject(this);
    }

    @Override
    public void subscribe() {
        loadSavedCities();
    }

    @Override
    public void unSubscribe() {
        composite.clear();
    }

    @Override
    public void loadSavedCities() {

        try {
            Disposable disposable = Observable.just(weatherDao.queryAllSaveCity())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(weathers -> {
                        view.displaySavedCities(weathers);
                    });
            composite.add(disposable);
          //  subscriptions.add(subscription);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void deleteCity(String cityId) {

        Observable.just(deleteCityFromDBAndReturnCurrentCityId(cityId))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(currentCityId -> {
                    if (currentCityId == null)
                        return;
                    try {
                        PreferenceHelper.savePreference(WeatherSettings.SETTINGS_CURRENT_CITY_ID, currentCityId);
                    } catch (InvalidClassException e) {
                        e.printStackTrace();
                    }
                });
    }

    @Override
    public void saveCurrentCityToPreference(String cityId) throws InvalidClassException{
        PreferenceHelper.savePreference(WeatherSettings.SETTINGS_CURRENT_CITY_ID, cityId);
    }

    private String deleteCityFromDBAndReturnCurrentCityId(String cityId) {
        String currentCityId = PreferenceHelper.getSharedPreferences().getString(WeatherSettings.SETTINGS_CURRENT_CITY_ID.getId(), "");
        try {
            weatherDao.deleteById(cityId);
            if (cityId.equals(currentCityId)) {//说明删除的是当前选择的城市，所以需要重新设置默认城市
                List<Weather> weatherList = weatherDao.queryAllSaveCity();
                if (weatherList != null && weatherList.size() > 0) {
                    currentCityId = weatherList.get(0).getCityId();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return currentCityId;
    }


    public long getCityCount() {
        return  weatherDao.getCityCount();
    }
}

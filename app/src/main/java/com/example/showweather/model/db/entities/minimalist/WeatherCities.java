package com.example.showweather.model.db.entities.minimalist;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * ljw：Administrator on 2017/5/20 0020 14:23
 */
@DatabaseTable(tableName = "WeatherCities")
public class WeatherCities {
    public static final String CITY_ID_FIELD_NAME = "cityId";
    public static final String CITY_NAME_FIELD_NAME = "cityName";
    public static final String CITY_NAME_EN_FIELD_NAME = "cityNameEn";
    @DatabaseField(columnName = CITY_ID_FIELD_NAME, id = true)
    private String cityId;
    @DatabaseField(columnName = CITY_NAME_FIELD_NAME)
    private String cityName;
    @DatabaseField(columnName = CITY_NAME_EN_FIELD_NAME)
    private String cityNameEn;
    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCityNameEn() {
        return cityNameEn;
    }

    public void setCityNameEn(String cityNameEn) {
        this.cityNameEn = cityNameEn;
    }

    public WeatherCities(String cityId, String cityName, String cityNameEn) {
        this.cityId = cityId;
        this.cityName = cityName;
        this.cityNameEn = cityNameEn;
    }
    public WeatherCities() {
    }
}

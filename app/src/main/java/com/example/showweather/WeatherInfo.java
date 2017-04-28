package com.example.showweather;

/**
 * Created by Administrator on 2016/6/2 0002.
 */
public class WeatherInfo {

    private int weatherImg;
    private String whichDayOfWeek;
    private String weatherTmp;
    private String currentDate;
    private String weatherInfo;
    private String weatherWind;

    public String getWeatherWind() {
        return weatherWind;
    }

    public void setWeatherWind(String weatherWind) {
        this.weatherWind = weatherWind;
    }

    public String getWeatherInfo() {
        return weatherInfo;
    }

    public void setWeatherInfo(String weatherInfo) {
        this.weatherInfo = weatherInfo;
    }

    public int getWeatherImg() {
        return weatherImg;
    }

    public void setWeatherImg(int weatherImg) {
        this.weatherImg = weatherImg;
    }

    public String getWhichDayOfWeek() {
        return whichDayOfWeek;
    }

    public void setWhichDayOfWeek(String whichDayOfWeek) {
        this.whichDayOfWeek = whichDayOfWeek;
    }

    public String getWeatherTmp() {
        return weatherTmp;
    }

    public void setWeatherTmp(String weatherTmp) {
        this.weatherTmp = weatherTmp;
    }

    public String getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(String currentDate) {
        this.currentDate = currentDate;
    }

}

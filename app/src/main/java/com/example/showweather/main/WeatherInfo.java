package com.example.showweather.main;

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
    private String weatherHum;
    private String weatherPress;
    private String weatherLiveTmp;
    private  String weatherLiveInfo;	//
    private int weatherLiveImg;

    public String getWeatherLiveInfo() {
        return weatherLiveInfo;
    }

    public void setWeatherLiveInfo(String weatherLiveInfo) {
        this.weatherLiveInfo = weatherLiveInfo;
    }

    public int getWeatherLiveImg() {
        return weatherLiveImg;
    }

    public void setWeatherLiveImg(int weatherLiveImg) {
        this.weatherLiveImg = weatherLiveImg;
    }

    public String getWeatherLiveTmp() {
        return weatherLiveTmp;
    }

    public void setWeatherLiveTmp(String weatherLiveTmp) {
        this.weatherLiveTmp = weatherLiveTmp;
    }


    public String getWeatherPress() {
        return weatherPress;
    }

    public void setWeatherPress(String weatherPress) {
        this.weatherPress = weatherPress;
    }

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

    public void setWeatherHum(String weatherHum) {
        this.weatherHum = weatherHum;
    }

    public String getWeatherHum() {
        return weatherHum;
    }

}

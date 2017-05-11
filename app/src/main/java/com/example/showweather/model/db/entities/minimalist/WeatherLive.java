package com.example.showweather.model.db.entities.minimalist;

import android.os.Parcel;
import android.os.Parcelable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * 天气实况
 *
 * @author baronzhang (baron[dot]zhanglei[at]gmail[dot]com ==>> baronzhang.com)
 *         16/2/25
 */
@DatabaseTable(tableName = "WeatherLive")
public class WeatherLive implements Parcelable{
 //   insert stmt on object
 // RealTime{cityId='101280601', weather='大雨', temp='23.0', humidity='99.0', wind='西北风', windSpeed='1.0', time='1493882100000'}:
 // INSERT INTO `WeatherLive` (`cityId` ,`humidity` ,`temp` ,`time` ,`weather` ,`wind` ,`windSpeed` ) VALUES (?,?,?,?,?,?,?)
    public static final String CITY_ID_FIELD_NAME = "cityId";
    public static final String WEATHER_FIELD_NAME = "weather";
    public static final String TEMP_FIELD_NAME = "temp";
    public static final String HUMIDITY_FIELD_NAME = "humidity";
    public static final String WIND_FIELD_NAME = "wind";
    public static final String WIND_SPEED_FIELD_NAME = "windSpeed";
    public static final String TIME_FIELD_NAME = "time";

    @DatabaseField(columnName = CITY_ID_FIELD_NAME, id = true)
    private String cityId;
    @DatabaseField(columnName = WEATHER_FIELD_NAME)
    private String weather;//天气情况
    @DatabaseField(columnName = TEMP_FIELD_NAME)
    private String temp;//温度
    @DatabaseField(columnName = HUMIDITY_FIELD_NAME)
    private String humidity;//湿度
    @DatabaseField(columnName = WIND_FIELD_NAME)
    private String wind;//风向
    @DatabaseField(columnName = WIND_SPEED_FIELD_NAME)
    private String windSpeed;//风速
    @DatabaseField(columnName = TIME_FIELD_NAME)
    private long time;//发布时间（时间戳）

    public WeatherLive() {
    }

    public WeatherLive(String cityId, String weather, String temp, String humidity, String wind, String windSpeed, long time) {

        this.cityId = cityId;
        this.weather = weather;
        this.temp = temp;
        this.humidity = humidity;
        this.wind = wind;
        this.windSpeed = windSpeed;
        this.time = time;
    }

    protected WeatherLive(Parcel in) {
        cityId = in.readString();
        weather = in.readString();
        temp = in.readString();
        humidity = in.readString();
        wind = in.readString();
        windSpeed = in.readString();
        time = in.readLong();
    }

    public static final Creator<WeatherLive> CREATOR = new Creator<WeatherLive>() {
        @Override
        public WeatherLive createFromParcel(Parcel in) {
            return new WeatherLive(in);
        }

        @Override
        public WeatherLive[] newArray(int size) {
            return new WeatherLive[size];
        }
    };

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public String getWind() {
        return wind;
    }

    public void setWind(String wind) {
        this.wind = wind;
    }

    public String getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(String windSpeed) {
        this.windSpeed = windSpeed;
    }

    @Override
    public String toString() {
        return "RealTime{" +
                "cityId='" + cityId + '\'' +
                ", weather='" + weather + '\'' +
                ", temp='" + temp + '\'' +
                ", humidity='" + humidity + '\'' +
                ", wind='" + wind + '\'' +
                ", windSpeed='" + windSpeed + '\'' +
                ", time='" + time + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(cityId);
        dest.writeString(weather);
        dest.writeString(temp);
        dest.writeString(humidity);
        dest.writeString(wind);
        dest.writeString(windSpeed);
        dest.writeLong(time);
    }
}

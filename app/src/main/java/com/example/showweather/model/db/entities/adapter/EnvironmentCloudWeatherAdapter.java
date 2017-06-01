package com.example.showweather.model.db.entities.adapter;

import com.example.showweather.main.Utils;
import com.example.showweather.model.db.entities.minimalist.AirQualityLive;
import com.example.showweather.model.db.entities.minimalist.LifeIndex;
import com.example.showweather.model.db.entities.minimalist.WeatherForecast;
import com.example.showweather.model.db.entities.minimalist.WeatherLive;
import com.example.showweather.model.http.entity.envicloud.EnvironmentCloudForecast;
import com.example.showweather.model.http.entity.envicloud.EnvironmentCloudWeatherLive;
import com.example.showweather.utils.DateConvertUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * ljw：Administrator on 2017/5/4 0004 14:58
 */
public class EnvironmentCloudWeatherAdapter extends WeatherAdapter{
    private EnvironmentCloudForecast environmentCloudForecast;
    private EnvironmentCloudWeatherLive environmentCloudWeatherLive;

    public EnvironmentCloudWeatherAdapter(EnvironmentCloudForecast forecast,EnvironmentCloudWeatherLive weatherLive){
        environmentCloudForecast = forecast;
        environmentCloudWeatherLive = weatherLive;
    }
    @Override
    public String getCityId() {
        return environmentCloudForecast.getCitycode();
    }

    @Override
    public String getCityName() {
        return environmentCloudForecast.getCityname();
    }

    @Override
    public String getCityNameEn() {
        return "";
    }

   @Override
    public WeatherLive getWeatherLive() {
       if(environmentCloudWeatherLive.getRcode()!=200){
           EnvironmentCloudForecast.ForecastEntity entity = environmentCloudForecast.getForecast().get(0);
           String weather ;//天气状况
           if (environmentCloudForecast.getForecast().get(0).getCond().getCond_d().equals(environmentCloudForecast.getForecast().get(0).getCond().getCond_n())){
               weather= environmentCloudForecast.getForecast().get(0).getCond().getCond_d();
           }else {
               weather= environmentCloudForecast.getForecast().get(0).getCond().getCond_d()+"转"+environmentCloudForecast.getForecast().get(0).getCond().getCond_n();
           }
           String win;
           if (entity.getWind().getDir().equals("无持续风向")){
               if (entity.getWind().getSc().equals("微风"))
                   win = entity.getWind().getSc();
               else
                   win = entity.getWind().getSc()+"级";
           }else {
               if (entity.getWind().getSc().equals("微风"))
                   win = entity.getWind().getDir() +" " +entity.getWind().getSc();
               else
                   win = entity.getWind().getDir() +" " +entity.getWind().getSc()+"级";

           }
           return new WeatherLive(environmentCloudForecast.getCitycode(),
                   weather,
                   entity.getTmp().getMax(),
                   entity.getHum(),
                   win,
                   "5",
                   System.currentTimeMillis());
       }else{
           String win;
           if (environmentCloudWeatherLive.getWinddirect().equals("无持续风向")){
               win = environmentCloudWeatherLive.getWindpower();
           }else {
               win = environmentCloudWeatherLive.getWinddirect() +" " +environmentCloudWeatherLive.getWindpower();

           }
           return new WeatherLive(environmentCloudWeatherLive.getCitycode(),
                   environmentCloudWeatherLive.getPhenomena(),
                   environmentCloudWeatherLive.getTemperature(),
                   environmentCloudWeatherLive.getHumidity(),
                   win,
                   environmentCloudWeatherLive.getWindspeed(),
                   DateConvertUtils.dateToTimeStamp(environmentCloudWeatherLive.getUpdatetime(),DateConvertUtils.DATA_FORMAT_PATTEN_YYYY_MMMM_DD_HH_MM));
       }



    }
    @Override
    public List<WeatherForecast> getWeatherForecasts() {
        List<WeatherForecast> weatherForecasts = new ArrayList<>();
        for (EnvironmentCloudForecast.ForecastEntity entity:environmentCloudForecast.getForecast()){

            String weather ;//天气状况
            if (entity.getCond().getCond_d().equals(entity.getCond().getCond_n())){
                weather= entity.getCond().getCond_d();
            }else {
                weather= entity.getCond().getCond_d()+"转"+entity.getCond().getCond_n();
            }
            String win;
            if (entity.getWind().getDir().equals("无持续风向")){
                if (entity.getWind().getSc().equals("微风"))
                    win = entity.getWind().getSc();
                else
                    win = entity.getWind().getSc()+"级";
            }else {
                if (entity.getWind().getSc().equals("微风"))
                    win = entity.getWind().getDir() +" " +entity.getWind().getSc();
                else
                    win = entity.getWind().getDir() +" " +entity.getWind().getSc()+"级";

            }
            weatherForecasts.add(new WeatherForecast(environmentCloudForecast.getCitycode(),
                    weather,
                    entity.getCond().getCond_d(),
                    entity.getCond().getCond_n(),
                    Integer.parseInt(entity.getTmp().getMax()),
                    Integer.parseInt(entity.getTmp().getMin()),
                    win,
                    entity.getDate(),
                    Utils.getWeekDays(entity.getDate()),
                    entity.getHum(),
                    entity.getPres()));

        }
        return weatherForecasts;
    }

    @Override
    public List<LifeIndex> getLifeIndexes() {
        List<LifeIndex> lifeIndexes = new ArrayList<>();
        LifeIndex lifeIndex = new LifeIndex(environmentCloudForecast.getCitycode(),
                "空气", environmentCloudForecast.getSuggestion().getAir().getBrf(),
                environmentCloudForecast.getSuggestion().getAir().getBrf());
        lifeIndexes.add(lifeIndex);

        lifeIndex.setName("舒适度");
        lifeIndex.setDetails(environmentCloudForecast.getSuggestion().getComf().getBrf());
        lifeIndex.setIndex(environmentCloudForecast.getSuggestion().getComf().getTxt());
        lifeIndexes.add(lifeIndex);

        lifeIndex.setName("洗车");
        lifeIndex.setDetails(environmentCloudForecast.getSuggestion().getCw().getBrf());
        lifeIndex.setIndex(environmentCloudForecast.getSuggestion().getCw().getTxt());
        lifeIndexes.add(lifeIndex);

        lifeIndex.setName("穿衣");
        lifeIndex.setDetails(environmentCloudForecast.getSuggestion().getDrs().getBrf());
        lifeIndex.setIndex(environmentCloudForecast.getSuggestion().getDrs().getTxt());
        lifeIndexes.add(lifeIndex);

        lifeIndex.setName("感冒");
        lifeIndex.setDetails(environmentCloudForecast.getSuggestion().getFlu().getBrf());
        lifeIndex.setIndex(environmentCloudForecast.getSuggestion().getFlu().getTxt());
        lifeIndexes.add(lifeIndex);

        lifeIndex.setName("运动");
        lifeIndex.setDetails(environmentCloudForecast.getSuggestion().getSport().getBrf());
        lifeIndex.setIndex(environmentCloudForecast.getSuggestion().getSport().getTxt());
        lifeIndexes.add(lifeIndex);

        lifeIndex.setName("旅游");
        lifeIndex.setDetails(environmentCloudForecast.getSuggestion().getTrav().getBrf());
        lifeIndex.setIndex(environmentCloudForecast.getSuggestion().getTrav().getTxt());
        lifeIndexes.add(lifeIndex);

        lifeIndex.setName("紫外线");
        lifeIndex.setDetails(environmentCloudForecast.getSuggestion().getUv().getBrf());
        lifeIndex.setIndex(environmentCloudForecast.getSuggestion().getUv().getTxt());
        lifeIndexes.add(lifeIndex);
        return lifeIndexes;
    }


}

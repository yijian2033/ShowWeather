package com.example.showweather.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.showweather.BaseRecyclerViewAdapter;
import com.example.showweather.R;
import com.example.showweather.model.db.entities.minimalist.Weather;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 城市管理页面Adapter
 *
 * @author baronzhang (baron[dot]zhanglei[at]gmail[dot]com ==>> baronzhang.com)
 *         16/3/16
 */
public class CityManagerAdapter extends BaseRecyclerViewAdapter<CityManagerAdapter.ViewHolder> {

    private final List<Weather> weatherList;

    public CityManagerAdapter(List<Weather> weatherList) {
        this.weatherList = weatherList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_city_manager, parent, false);
        return new ViewHolder(itemView, this);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Weather weather = weatherList.get(position);
        holder.city.setText(weather.getCityName());
        if (weather.getWeatherLive()!=null){
            holder.weather.setText(weather.getWeatherLive().getWeather());
            holder.weatherImage.setImageResource(getWeathersmallImg(weather.getWeatherLive().getWeather()));
        }

        if (weather.getWeatherForecasts()!=null&&weather.getWeatherForecasts().size()>0)
        holder.temp.setText(new StringBuilder().append(weather.getWeatherForecasts().get(0).getTempMin()).append("~").append(weather.getWeatherForecasts().get(0).getTempMax()).append("℃").toString());
        holder.deleteButton.setOnClickListener(v -> {
         //   Weather removeWeather = weatherList.get(holder.getAdapterPosition());
         //   weatherList.remove(removeWeather);
        //    notifyItemRemoved(holder.getAdapterPosition());

            if (onItemClickListener != null && onItemClickListener instanceof OnCityManagerItemClickListener) {
                ((OnCityManagerItemClickListener) onItemClickListener).onDeleteClick("",position/*holder.getAdapterPosition()*/);
            }
        });
    }

    @Override
    public int getItemCount() {
        return weatherList == null ? 0 : weatherList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.item_delete)
        ImageButton deleteButton;
        @BindView(R.id.item_tv_city)
        TextView city;
        @BindView(R.id.item_tv_weather)
        TextView weather;
        @BindView(R.id.item_im_weather)
        ImageView weatherImage;
        @BindView(R.id.item_tv_temp)
        TextView temp;

        ViewHolder(View itemView, CityManagerAdapter adapter) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(v -> adapter.onItemHolderClick(ViewHolder.this));
        }
    }

    public interface OnCityManagerItemClickListener extends AdapterView.OnItemClickListener {

        void onDeleteClick(String cityId ,int position);
    }

    /**
     * 根据天气信息设置天气图片（小图）
     *
     * @param cond 天气信息
     * @return 对应的天气图片id
     */
    private int getWeathersmallImg(String cond) {
        int img = 0;
        int length = cond.length();
        if (cond.contains("雨")) {
            if (cond.contains("雷"))
                img = R.drawable.forecast_thunderandrain;
            else if (cond.contains("小雨"))
                img = R.drawable.forecast_smallrain;
            else if (cond.contains("中雨"))
                img = R.drawable.forecast_middlerain;
            else if (cond.contains("大雨"))
                img = R.drawable.forecast_bigrain;
            else if (cond.contains("雨夹雪"))
                img = R.drawable.forecast_rainandsnow;
            else if (cond.contains("暴雨"))
                img = R.drawable.forecast_stormrain;
            else
                img = R.drawable.forecast_smallrain;
        } else if (cond.contains("雪")) {
            if (cond.contains("小雪"))
                img = R.drawable.forecast_smallsnow;
            else if (cond.contains("中雪"))
                img = R.drawable.forecast_middlesnow;
            else
                img = R.drawable.forecast_smallsnow;
        } else if (cond.contains("晴") && length <= 2)
            img = R.drawable.forecast_sun;
        else if (cond.contains("多云"))
            img = R.drawable.forecast_cloud;
        else if (cond.contains("阴") && length <= 2)
            img = R.drawable.forecast_overcast;
        else if (cond.contains("雷"))
            img = R.drawable.forecast_thunderandrain;
        else
            img = R.drawable.forecast_sun;
        return img;
    }

}

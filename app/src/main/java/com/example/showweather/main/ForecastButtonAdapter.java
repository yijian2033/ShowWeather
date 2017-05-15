package com.example.showweather.main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;


import com.example.showweather.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/6/1 0001.
 */
public class ForecastButtonAdapter extends BaseAdapter {
    private Context context;
    private List<WeatherInfo> list = new ArrayList<>();
    private LayoutInflater mLayoutInflater;
    public static final int APP_PAGE_SIZE = 5;//装载的数据大小

    private final class ViewHolder {
        public WeatherImageButton wib;
    }

    public ForecastButtonAdapter(Context context, List<WeatherInfo> list) {
        this.context = context;
        this.list = list;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list.size()/*APP_PAGE_SIZE*/;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = mLayoutInflater.from(context).inflate(R.layout.weatherlayout_item, parent, false);
            viewHolder.wib = (WeatherImageButton) convertView.findViewById(R.id.define_weatherbutton);
            convertView.setTag(viewHolder);
        } else
            viewHolder = (ViewHolder) convertView.getTag();
        if (position==0){
            viewHolder.wib.setImageResource(list.get(position).getWeatherLiveImg(), list.get(position).getWhichDayOfWeek(), list.get(position).getWeatherTmp());
        }else if (position<list.size()){
            viewHolder.wib.setImageResource(list.get(position).getWeatherImg(), list.get(position).getWhichDayOfWeek(), list.get(position).getWeatherTmp());
        }
        return convertView;
    }
}

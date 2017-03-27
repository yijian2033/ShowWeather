package com.example.showweather;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Administrator on 2016/6/1 0001.
 */
public class WeatherImageButton extends LinearLayout{
    public ImageView imageView;
    public TextView weekView;
    public TextView tmpView;

    public WeatherImageButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.weather_item, this);
        imageView = (ImageView) findViewById(R.id.forecastimg);
        weekView = (TextView) findViewById(R.id.weekdaystext);
        tmpView = (TextView) findViewById(R.id.forecasttmp);
    }

    /**
     * 设置图片
     */
    public void setImageResource(int imageId, String weekId, String tmpId) {
        imageView.setImageResource(imageId);
        weekView.setText(weekId);
        tmpView.setText(tmpId);
    }
}

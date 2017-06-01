package com.example.showweather.activity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.widget.Toolbar;


import com.example.showweather.R;
import com.example.showweather.WeatherApplication;
import com.example.showweather.activity.component.DaggerCityManagerComponent;
import com.example.showweather.activity.module.CityManagerModule;
import com.example.showweather.presenter.CityManagerPresenter;
import com.example.showweather.utils.ActivityUtils;
import com.example.showweather.view.fragment.CityManagerFragment;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author baronzhang (baron[dot]zhanglei[at]gmail[dot]com ==>> baronzhang.com)
 */
public class CityManagerActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Inject
    CityManagerPresenter cityManagerPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_manager);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        setTitle("");
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setDisplayShowHomeEnabled(false);
        }
        CityManagerFragment cityManagerFragment = CityManagerFragment.newInstance(4);
        ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), cityManagerFragment, R.id.fragment_container);

        DaggerCityManagerComponent.builder()
                .applicationComponent(WeatherApplication.getInstance().getApplicationComponent())
                .cityManagerModule(new CityManagerModule(cityManagerFragment))
                .build().inject(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}

package com.example.showweather.activity;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;

import com.example.showweather.R;
import com.example.showweather.WeatherApplication;
import com.example.showweather.activity.component.DaggerSelectCityComponent;
import com.example.showweather.activity.module.SelectCityModule;
import com.example.showweather.presenter.SelectCityPresenter;
import com.example.showweather.utils.ActivityUtils;
import com.example.showweather.view.fragment.SelectCityFragment;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SelectCityActivity extends BaseActivity {



    SelectCityFragment selectCityFragment;

    @Inject
    SelectCityPresenter selectCityPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_city);
        ButterKnife.bind(this);


        selectCityFragment = SelectCityFragment.newInstance();
        ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), selectCityFragment, R.id.fragment_container);

        DaggerSelectCityComponent.builder()
                .applicationComponent(WeatherApplication.getInstance().getApplicationComponent())
                .selectCityModule(new SelectCityModule(selectCityFragment))
                .build().inject(this);
    }




    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_BACK){
            selectCityFragment.onKeyeyBack();
            return  true;
        }
        return super.onKeyDown(keyCode, event);
    }
}

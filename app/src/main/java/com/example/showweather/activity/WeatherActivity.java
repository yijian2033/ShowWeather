package com.example.showweather.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.showweather.R;
import com.example.showweather.WeatherApplication;
import com.example.showweather.activity.component.DaggerWeatherActivityComponent;
import com.example.showweather.activity.module.WeatherActivityModule;
import com.example.showweather.contract.WeatherActivityContract;
import com.example.showweather.model.db.dao.WeatherDao;
import com.example.showweather.model.db.entities.minimalist.Weather;
import com.example.showweather.model.db.entities.minimalist.WeatherCities;
import com.example.showweather.model.preference.PreferenceHelper;
import com.example.showweather.model.preference.WeatherSettings;
import com.example.showweather.presenter.WeatherActivityPresenter;
import com.example.showweather.view.adapter.HomePageAdapter;
import com.example.showweather.view.fragment.WeatherFragment;

import java.io.InvalidClassException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by Administrator on 2016/5/31 0031.
 */
public class WeatherActivity extends AppCompatActivity implements WeatherActivityContract.View {

    @BindView(R.id.vertical_viewpager)
    ViewPager homePager;
    @BindView(R.id.layout)
    LinearLayout layout;

    @Inject
    WeatherDao weatherDao;
    // @Inject
    WeatherActivityPresenter presenter;
    @BindView(R.id.fab)
    FloatingActionButton fab;

    private ArrayList<WeatherFragment> homepageFragments;
    private List<ImageView> imgsList;
    List<WeatherCities> weatherCities = new ArrayList<WeatherCities>();
    public static boolean hasCityCountChange = false;//添加或删除城市时变为true

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //  this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        //    this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
        setContentView(R.layout.weatherlayout);
        ButterKnife.bind(this);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        DaggerWeatherActivityComponent.builder()
                .applicationComponent(WeatherApplication.getInstance().getApplicationComponent())
                .weatherActivityModule(new WeatherActivityModule(this))
                .build().inject(this);

       /* DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .build().inject(this);*/
        homepageFragments = new ArrayList<WeatherFragment>();
        imgsList = new ArrayList<ImageView>();
        showWerather1();

        //  weatherDao = new WeatherDao(this);
        presenter = new WeatherActivityPresenter(this, this);
        presenter.subscribe();
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.subscribe();
        if (hasCityCountChange) {
            hasCityCountChange = false;
            showWerather1();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        showWerather1();
    }

    public void showWerather1() {

        try {
            weatherCities = weatherDao.queryAllSelectedCity();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (weatherCities == null || weatherCities.size() == 0) {
            /*Intent intent = new Intent(this, SelectCityActivity.class);
            startActivity(intent);
            finish();
            return;*/
            WeatherCities city = new WeatherCities();
            city.setCityName("上海");
            city.setCityNameEn("shanghai");
            city.setCityId("101020100");
            weatherDao.insertSelectedCity(city);
            weatherCities.add(city);
        }
        layout.removeAllViews();//涉及到实时刷新,所以要将之前的布局清空掉。
        homePager.removeAllViewsInLayout();//removeAllViews();//赋值之前先将Adapter中的
        homepageFragments.clear();
        imgsList.clear();
        int currentcity = 0;
        String currentCityId = PreferenceHelper.getSharedPreferences().getString(WeatherSettings.SETTINGS_CURRENT_CITY_ID.getId(), "");
        if (weatherCities != null && weatherCities.size() > 0) {

            //   describeArr = new String[hotIssuesList.size()];
            //  solutionArr = new String[hotIssuesList.size()];
            for (int i = 0; i < weatherCities.size(); i++) {//hotIssuesList.size()
                //     describeArr[i] = hotIssuesList.get(i).getHotDescirbe();//提取对应pager数据源。
                //     solutionArr[i] = hotIssuesList.get(i).getHotSolution();

                if (currentCityId.equals(weatherCities.get(i).getCityId())) {
                    currentcity = i;
                }
                ImageView img = new ImageView(this);//准备5个小图标。
                img.setScaleType(ImageView.ScaleType.FIT_XY);
                img.setImageResource(R.drawable.unselectpoint);
                imgsList.add(img);
                img.setPadding(6, 3, 6, 3);
                ViewGroup.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layout.addView(img, params);

//              colourFragment = new ColourFragment(hotIssuesList.get(i));
//              homepageFragments.add(colourFragment);
//              homePage2Fragment = new HomePage2Fragment(this, i, hotIssuesList.get(i));
                WeatherFragment weatherFragment = //
                        // new WeatherFragment(weathers.get(i));
                        WeatherFragment.newInstance(weatherCities.get(i).getCityId(), weatherCities.get(i).getCityName());
                //new WeatherFragment(weathers.get(i).getCityId());
                // WeatherFragmentContract.Presenter mpresenter= new WeatherFragmentPresenter(this,weatherFragment);
                //  weatherFragment.setPresenter(mpresenter);
//              homePage2Fragment = new HomePage2Fragment(this, i, new String[]{describeArr[i], solutionArr[i]});
                homepageFragments.add(weatherFragment);
//              viewpagerItemView = new ViewPagerItemView(this, i, new String[]{describeArr[i], solutionArr[i]});
//              homepageViews.add(viewpagerItemView);
            }

            //custom-viewpager
//          MyHomePagerAdapter myAdapter = new MyHomePagerAdapter(this, homepageViews, describeArr, solutionArr);
            HomePageAdapter adapter = new HomePageAdapter(getApplicationContext(), getSupportFragmentManager(), homepageFragments);
            homePager.setAdapter(adapter);

            //wrapper-viewpager-
//          HomePageAdapter adapter = new HomePageAdapter(this, getSupportFragmentManager(), hotIssuesList);
//          PagerAdapter wrappedAdapter = new InfinitePagerAdapter(adapter);
//          homePager.setAdapter(wrappedAdapter);

            homePager.setCurrentItem(currentcity);
            imgsList.get(currentcity).setImageResource(R.drawable.selectpoint);
            homePager.setOnPageChangeListener(new PageListener());
        }
    }


//    /**
//     * 开启定位服务
//     */
//    private void startLocationService() {
//        Intent intent = new Intent(this, LocationService.class);
//        startService(intent);
//    }
//
//    /**
//     * 关闭定位服务
//     */
//    private void stopLocationService() {
//        Intent intent = new Intent(this, LocationLocalService.class);
//        stopService(intent);
//    }


    @Override
    public void onContentChanged() {
        super.onContentChanged();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.unSubscribe();
//        stopLocationService();
        log_i("onDestroy");
    }

    private void log_i(String s) {
        Log.i("ljwtest:", s);
    }

    @Override
    public void showWerather(Weather weather) {

    }

    @Override
    public void setPresenter(WeatherActivityContract.Presenter presenter) {

    }

    @OnClick(R.id.fab)
    public void onViewClicked() {
        Intent intent = new Intent(this,CityManagerActivity.class);
        startActivity(intent);
    }


    private class PageListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            for (ImageView imageView : imgsList) {
                imageView.setImageResource(R.drawable.unselectpoint);
            }
            imgsList.get(position).setImageResource(R.drawable.selectpoint);
            try {
                PreferenceHelper.savePreference(WeatherSettings.SETTINGS_CURRENT_CITY_ID, weatherCities.get(position).getCityId());
            } catch (InvalidClassException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }
}

package com.example.showweather.view.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.showweather.BaseFragment;
import com.example.showweather.R;
import com.example.showweather.activity.WeatherActivity;
import com.example.showweather.contract.SelectCityContract;
import com.example.showweather.model.db.entities.City;
import com.example.showweather.model.preference.PreferenceHelper;
import com.example.showweather.model.preference.WeatherSettings;
import com.example.showweather.presenter.SelectCityPresenter;
import com.example.showweather.utils.NetworkUtils;
import com.example.showweather.view.adapter.CityListAdapter;
import com.example.showweather.view.widget.DividerItemDecoration;

import java.io.InvalidClassException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * @author baronzhang (baron[dot]zhanglei[at]gmail[dot]com ==>> baronzhang.com)
 */
public class SelectCityFragment extends BaseFragment implements SelectCityContract.View {

    public List<City> cities;
    public CityListAdapter cityListAdapter;

    @BindView(R.id.rv_city_list)
    RecyclerView recyclerView;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    private Unbinder unbinder;

    private SelectCityPresenter presenter;
    public static final int LEVEL_PROVINCE = 1;
    public static final int LEVEL_CITY = 2;
    public static final int LEVEL_COUNTRY = 3;
    public static int currentLevel = LEVEL_PROVINCE;
    private LocationDialog locationDialog;
    private String currentCity = "";
    private String currentPro = "";

    public SelectCityFragment() {
    }

    public static SelectCityFragment newInstance() {
        return new SelectCityFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_select_city, container, false);
        unbinder = ButterKnife.bind(this, rootView);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(),5);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
      //  recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        currentLevel = LEVEL_PROVINCE;
        cities = new ArrayList<>();
        cityListAdapter = new CityListAdapter(cities);
        title.setText("省份选择");

        cityListAdapter.setOnItemClickListener((parent, view, position, id) -> {
            if (currentLevel == LEVEL_PROVINCE) {
                currentLevel = LEVEL_CITY;
                currentPro = cityListAdapter.mFilterData.get(position).getRoot();
                presenter.loadCities(currentPro);
                title.setText("城市选择");
            } else if (currentLevel == LEVEL_CITY){
                currentLevel = LEVEL_COUNTRY;
                currentCity = cityListAdapter.mFilterData.get(position).getParent();
                presenter.loadCountry(currentCity);

                title.setText("地区选择");
            }else if (currentLevel == LEVEL_COUNTRY) {
                try {
                    City selectedCity = cityListAdapter.mFilterData.get(position);
                    PreferenceHelper.savePreference(WeatherSettings.SETTINGS_CURRENT_CITY_ID, selectedCity.getCityId() + "");

                    presenter.savecity(selectedCity);
                    Intent intent = new Intent(getActivity(), WeatherActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                } catch (InvalidClassException e) {
                    e.printStackTrace();
                }
            }
        } /*{
            try {
                City selectedCity = cityListAdapter.mFilterData.get(position);
                PreferenceHelper.savePreference(WeatherSettings.SETTINGS_CURRENT_CITY_ID, selectedCity.getCityId() + "");
                Toast.makeText(this.getActivity(), selectedCity.getCityName(), Toast.LENGTH_LONG).show();
                getActivity().finish();
            } catch (InvalidClassException e) {
                e.printStackTrace();
            }
        }*/);
        recyclerView.setAdapter(cityListAdapter);
        presenter.subscribe();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        presenter.unSubscribe();
    }

    @Override
    public void displayCountry(List<City> country) {
        this.cities.clear();
        if (country == null) {

        } else
            this.cities.addAll(country);
        recyclerView.scrollToPosition(0);
        cityListAdapter.notifyDataSetChanged();
    }

    @Override
    public void displayCities(List<City> cities) {
        this.cities.clear();
        for (City city : cities) {
            if (city.getCityName().equals(city.getParent()))
                this.cities.add(city);
        }
        recyclerView.scrollToPosition(0);
        cityListAdapter.notifyDataSetChanged();
    }

    @Override
    public void displayProvince(List<City> province) {
        this.cities.clear();
        if (province == null) {

        } else
            this.cities.addAll(province);

        cityListAdapter.notifyDataSetChanged();

    }

    @Override
    public void onLocated() {
        if (locationDialog!=null&&locationDialog.isShowing())
        locationDialog.dismiss();
        Intent intent = new Intent(getActivity(), WeatherActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    @Override
    public void setPresenter(SelectCityContract.Presenter presenter) {
        this.presenter = (SelectCityPresenter) presenter;
    }


    public void onKeyeyBack() {
        if (currentLevel == LEVEL_COUNTRY) {
            presenter.loadCities(currentPro);
            currentLevel = LEVEL_CITY;
            title.setText("城市选择");
        }else if (currentLevel == LEVEL_CITY) {
            presenter.loadProvince();
            currentLevel = LEVEL_PROVINCE;
            title.setText("省份选择");
        } else {
            getActivity().finish();
        }
    }

    @OnClick(R.id.fab)
    public void onViewClicked() {
        if (NetworkUtils.isNetworkConnected(getActivity())){
            locationDialog = new LocationDialog(getActivity());
            locationDialog.show();
            presenter.startLocation();
        }else {
            Toast.makeText(getActivity(),"网络未连接，无法定位",Toast.LENGTH_SHORT).show();
        }
    }

    class LocationDialog extends Dialog {


        @BindView(R.id.cancel)
        Button cancel;

        public LocationDialog(Context context) {
            super(context);
        }

        public LocationDialog(Context context, int themeResId) {
            super(context, themeResId);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.dialog_location);
            ButterKnife.bind(this);

        }

        @OnClick(R.id.cancel)
        public void onViewClicked() {
            dismiss();
            presenter.stopLocation();
        }
    }

}

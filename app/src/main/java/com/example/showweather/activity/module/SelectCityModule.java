package com.example.showweather.activity.module;



import com.example.showweather.contract.SelectCityContract;

import dagger.Module;
import dagger.Provides;

/**
 * @author baronzhang (baron[dot]zhanglei[at]gmail[dot]com ==>> baronzhang.com)
 *         2016/11/30
 */
@Module
public class SelectCityModule {

    private SelectCityContract.View view;

    public SelectCityModule(SelectCityContract.View view) {
        this.view = view;
    }

    @Provides
    SelectCityContract.View provideSelectCityContractView() {
        return view;
    }
}

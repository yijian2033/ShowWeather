package com.example.showweather.view.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;

import com.example.showweather.model.db.entities.minimalist.Weather;
import com.example.showweather.view.fragment.WeatherFragment;

import java.util.ArrayList;

/**
 * ljw：Administrator on 2017/5/20 0020 10:31
 */
public class HomePageAdapter extends FragmentStatePagerAdapter {//FragmentPagerAdapter

    //  private FragmentManager fm;
    private ArrayList<WeatherFragment> fragments = null;
   // private List<HotIssues> hotIssuesList;
    private Context context;

    public HomePageAdapter(Context context, FragmentManager fm, ArrayList<WeatherFragment> fragments) {
        super(fm);
        this.context = context;
        this.fragments = fragments;
        notifyDataSetChanged();
    }


    @Override
    public Fragment getItem(int arg0) {
        //      Fragment fragment = new ColourFragment();
        //      Bundle args = new Bundle();
        //      args.putInt("title", arg0);
        //      args.putSerializable("content",hotIssuesList.get(arg0));
        //      fragment.setArguments(args);
        //      return fragment;
        return fragments.get(arg0);
    }

    @Override
    public int getItemPosition(Object object) {
        // TODO Auto-generated method stub
        return PagerAdapter.POSITION_NONE;
    }

    @Override
    public int getCount() {
        return fragments.size();//hotIssuesList.size();
    }

}

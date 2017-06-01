package com.example.showweather.model.db.dao;

import android.content.Context;


import com.example.showweather.model.db.CityDatabaseHelper;
import com.example.showweather.model.db.entities.City;
import com.example.showweather.model.db.entities.HotCity;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.List;

import javax.inject.Inject;

/**
 * City表操作类
 *
 * @author baronzhang (baron[dot]zhanglei[at]gmail[dot]com ==>> baronzhang.com)
 *         16/3/13
 */
public class CityDao {

    private Dao<City, Integer> cityDaoOperation;
    private Dao<HotCity, Integer> hotCityDaoOperation;

    @Inject
    CityDao(Context context) {

        this.cityDaoOperation = CityDatabaseHelper.getInstance(context).getCityDao(City.class);
        this.hotCityDaoOperation = CityDatabaseHelper.getInstance(context).getCityDao(HotCity.class);
    }

    /**
     * 根据省份名称查询表中的该省份所有城市
     *
     * @return 城市列表数据
     */
    public List<City> queryCityList(String province) {

        try {
            QueryBuilder<City, Integer> queryBuilder = cityDaoOperation.queryBuilder();
               queryBuilder.where().eq(City.ROOT_FIELD_NAME, province);
          //  queryBuilder.groupBy(City.PARENT_FIELD_NAME);
          //  queryBuilder.where().eq(City.ROOT_FIELD_NAME,"黑龙江");
         //   queryBuilder.where().eq(City.PARENT_FIELD_NAME,City.CITY_NAME_FIELD_NAME);
          //  queryBuilder.distinct();

           // queryBuilder.orderBy(City.ID_FIELD_NAME,true);
          return queryBuilder.query();
          //  return cityDaoOperation.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;


    }
    /**
     * 根据城市名称查询表中的该城市所有地区
     *
     * @return 城市列表数据
     */
    public List<City> queryCountry(String city) {

        try {
            QueryBuilder<City, Integer> queryBuilder = cityDaoOperation.queryBuilder();
            queryBuilder.where().eq(City.PARENT_FIELD_NAME, city);
            //  queryBuilder.groupBy(City.PARENT_FIELD_NAME);
            //  queryBuilder.where().eq(City.ROOT_FIELD_NAME,"黑龙江");
            //   queryBuilder.where().eq(City.PARENT_FIELD_NAME,City.CITY_NAME_FIELD_NAME);
            //  queryBuilder.distinct();

            // queryBuilder.orderBy(City.ID_FIELD_NAME,true);
            return queryBuilder.query();
            //  return cityDaoOperation.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;


    }

    /**
     * 根据城市查询城市信息
     *
     * @param cityId 城市ID
     * @return city
     * @throws SQLException
     */
    public City queryCityById(String cityId) throws SQLException {

        QueryBuilder<City, Integer> queryBuilder = cityDaoOperation.queryBuilder();
        queryBuilder.where().eq(City.CITY_ID_FIELD_NAME, cityId);

        return queryBuilder.queryForFirst();
    }

    /**
     * 根据城市查询城市信息
     *
     * @param cityId 城市ID
     * @return city
     * @throws SQLException
     */
    public List<City> queryProvince(){
        try {

            QueryBuilder<City, Integer> queryBuilder = cityDaoOperation.queryBuilder();
            queryBuilder.orderBy(City.ID_FIELD_NAME,true);
            queryBuilder.groupBy(City.ROOT_FIELD_NAME);

            return queryBuilder.query();
        } catch (SQLException e) {

            e.printStackTrace();
        }

        return null;
    }

    /**
     * 查询所有热门城市
     *
     * @return 热门城市列表
     */
    public List<HotCity> queryAllHotCity() {
        try {
            return hotCityDaoOperation.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}

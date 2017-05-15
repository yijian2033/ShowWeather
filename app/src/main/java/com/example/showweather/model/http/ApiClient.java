package com.example.showweather.model.http;


import com.baronzhang.retrofit2.converter.FastJsonConverterFactory;
import com.example.showweather.BuildConfig;
import com.example.showweather.model.http.service.EnvironmentCloudWeatherService;
import com.facebook.stetho.okhttp3.StethoInterceptor;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;

/**
 * @author baronzhang (baron[dot]zhanglei[at]gmail[dot]com ==>> baronzhang.com)
 *         16/2/25
 */
public final class ApiClient {


    public static EnvironmentCloudWeatherService environmentCloudWeatherService;

    public static void init() {


        String weatherApiHost;
                weatherApiHost = ApiConstants.ENVIRONMENT_CLOUD_WEATHER_API_HOST;
                environmentCloudWeatherService = initWeatherService(weatherApiHost, EnvironmentCloudWeatherService.class);

    }

    private static <T> T initWeatherService(String baseUrl, Class<T> clazz) {

        OkHttpClient.Builder builder = new OkHttpClient().newBuilder();
        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(httpLoggingInterceptor).addNetworkInterceptor(new StethoInterceptor());
        }
        builder.readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60,TimeUnit.SECONDS)
                .connectTimeout(60,TimeUnit.SECONDS);
        OkHttpClient client = builder.build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(FastJsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(client)
                .build();

        return retrofit.create(clazz);
    }

}


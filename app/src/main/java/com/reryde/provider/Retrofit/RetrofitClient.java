package com.reryde.provider.Retrofit;

import com.reryde.provider.Helper.URLHelper;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by CSS on 8/4/2017.
 */

public class RetrofitClient {
    private static Retrofit retrofit = null;
    private static Retrofit retrofit_address = null;

    public static Retrofit getLiveTrackingClient() {
        if (retrofit==null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(URLHelper.base)
                    .build();
        }
        return retrofit;
    }

    public static Retrofit getClient() {
        if (retrofit_address==null) {
            retrofit_address = new Retrofit.Builder()
                    .baseUrl(URLHelper.base)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit_address;
    }
}

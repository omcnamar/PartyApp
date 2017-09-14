package com.olegsagenadatrytwo.partyapp.retrofit;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Admin on 9/13/2017.
 */

public class RetrofitHelper {

    // Retrofit Creator
    public static Retrofit create(String baseUrl) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit;
    }

    public interface ApiService {

    }
}

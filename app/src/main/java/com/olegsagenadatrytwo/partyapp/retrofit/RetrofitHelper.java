package com.olegsagenadatrytwo.partyapp.retrofit;

import com.olegsagenadatrytwo.partyapp.Constant;
import com.olegsagenadatrytwo.partyapp.model.eventbrite.EventbriteEvents;
import com.olegsagenadatrytwo.partyapp.model.geocoding_profile.GeocodingProfile;

import java.io.IOException;

import io.reactivex.Single;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Admin on 9/13/2017.
 */

public class RetrofitHelper {

    private OkHttpClient createOkHttpClient() {
        final OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                final Request original = chain.request();
                final HttpUrl originalHttpUrl = original.url();

                final HttpUrl url = originalHttpUrl.newBuilder()
                        .addQueryParameter("token", Constant.EVENTBRITE_TOKEN)
                        .build();

                // Request Headers
                final Request.Builder requestBuilder = original.newBuilder()
                        .url(url);

                final Request request = requestBuilder.build();
                return chain.proceed(request);
            }
        });

        return httpClient.build();
    }

    private OkHttpClient createOkHttpClientLocale() {
        final OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                final Request original = chain.request();
                final HttpUrl originalHttpUrl = original.url();

                final HttpUrl url = originalHttpUrl.newBuilder()
                        .addQueryParameter("key", Constant.GOOGLE_GEO_API_KEY)
                        .build();

                // Request Headers
                final Request.Builder requestBuilder = original.newBuilder()
                        .url(url);

                final Request request = requestBuilder.build();
                return chain.proceed(request);
            }
        });

        return httpClient.build();
    }

    private Retrofit create(String baseUrl) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(createOkHttpClient())
                .build();

        return retrofit;
    }

    private Retrofit createForLocale(String baseUrl) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(createOkHttpClientLocale())
                .build();

        return retrofit;
    }

    /**
     *
     * @return list instance of ApiService
     */
    public ApiService getEventBriteService() {
        final Retrofit retrofit = create(Constant.EVENTBRITE_BASE_URL);
        return retrofit.create(ApiService.class);
    }

    public ApiService getLocaleService() {
        final Retrofit retrofit = createForLocale(Constant.GEOCODE_BASE_URL);
        return retrofit.create(ApiService.class);
    }

    public interface ApiService {

        /**
         *
         * @param category
         * @return list of events
         */
        @GET(Constant.EVENTBRITE_EVENTS_PATH)
        Single<EventbriteEvents> queryEventList(@Query("categories") String category);

        @GET(Constant.GEOCODE_PATH)
        Call<GeocodingProfile> queryGetLocale(@Query("components") String zip);
    }
}

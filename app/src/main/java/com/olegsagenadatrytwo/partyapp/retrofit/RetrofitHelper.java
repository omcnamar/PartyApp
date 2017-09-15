package com.olegsagenadatrytwo.partyapp.retrofit;

import com.olegsagenadatrytwo.partyapp.Constant;
import com.olegsagenadatrytwo.partyapp.model.eventbrite.EventbriteEvents;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Admin on 9/13/2017.
 */

public class RetrofitHelper {

    public static Retrofit create(String baseUrl) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit;
    }

    /**
     *
     * @return list of events
     */
    public static Call<EventbriteEvents> callEventbriteEvents() {

        Retrofit retrofit = create(Constant.EVENTBRITE_BASE_URL);
        ApiService apiService = retrofit.create(ApiService.class);

        return apiService.getEventList("", Constant.EVENTBRITE_TOKEN);
    }
    
    public interface ApiService {

        /**
         *
         * @param category
         * @param token
         * @return list of events
         */
        @GET(Constant.EVENTBRITE_EVENTS_PATH)
        Call<EventbriteEvents> getEventList(@Query("categories") String category, @Query("token") String token);
    }
}

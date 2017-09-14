package com.olegsagenadatrytwo.partyapp.view.homeactivity;

import android.util.Log;

import com.olegsagenadatrytwo.partyapp.model.eventbrite.EventbriteEvents;
import com.olegsagenadatrytwo.partyapp.retrofit.RetrofitHelper;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Admin on 9/13/2017.
 */

public class HomeActivityPresenter implements HomeActivityContract.presenter {

    private static final String TAG = "HomeActivityPresenter";
    HomeActivityContract.view view;

    public void attachView(HomeActivityContract.view view) {
        this.view = view;
    }

    @Override
    public void removeView() {
        this.view = null;
    }

    /**
     * This method will make a call to the Eventbrite Api and retrieve a list of events
     */
    @Override
    public void fetchEventbriteEvents() {

        retrofit2.Call<EventbriteEvents> callEvents = RetrofitHelper.callEventbriteEvents();
        callEvents.enqueue(new Callback<EventbriteEvents>() {
            @Override
            public void onResponse(Call<EventbriteEvents> call, Response<EventbriteEvents> response) {
                Log.d(TAG, "onResponse: " + response.body().getEvents().toString());
            }

            @Override
            public void onFailure(Call<EventbriteEvents> call, Throwable t) {

            }
        });
    }
}

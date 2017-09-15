package com.olegsagenadatrytwo.partyapp.view.homeactivity;

import android.content.Context;
import com.olegsagenadatrytwo.partyapp.model.eventbrite.EventbriteEvents;
import com.olegsagenadatrytwo.partyapp.retrofit.RetrofitHelper;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HomeActivityPresenter implements HomeActivityContract.presenter {

    private static final String TAG = "HomeActivityPresenter";
    HomeActivityContract.view view;
    private Context context;

    public void attachView(HomeActivityContract.view view) {
        this.view = view;
    }

    public void setContext(Context context){
        this.context = context;
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
                if(response.body() != null) {
                    EventbriteEvents events = response.body();
                    //get the singleton class that will hold the event list
                    PartyLabSingleTon partyLabSingleTon = PartyLabSingleTon.getInstance(context);
                    //set the events list to the events retrieved for further use inside the activity
                    partyLabSingleTon.setEvents(events.getEvents());
                    view.eventsLoadedUpdateUI(events);
                }
            }

            @Override
            public void onFailure(Call<EventbriteEvents> call, Throwable t) {

            }
        });
    }
}

package com.olegsagenadatrytwo.partyapp.view.homeactivity;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.ShareActionProvider;

import com.olegsagenadatrytwo.partyapp.eventbus.LocalEvent;
import com.olegsagenadatrytwo.partyapp.model.custompojos.Party;
import com.olegsagenadatrytwo.partyapp.model.eventbrite.Event;
import com.olegsagenadatrytwo.partyapp.model.eventbrite.EventbriteEvents;
import com.olegsagenadatrytwo.partyapp.model.geocoding_profile.GeocodingProfile;
import com.olegsagenadatrytwo.partyapp.retrofit.RetrofitHelper;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HomeActivityPresenter implements HomeActivityContract.presenter {

    private static final String TAG = "HomeActivityPresenter";
    HomeActivityContract.view view;
    // Query Eventbrite with this
    @NonNull
    RetrofitHelper.ApiService apiService;
    private ShareActionProvider mShareActionProvider;
    private Context context;
    // Collects subscriptions to unsubscribe later
    @NonNull
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    public void attachView(HomeActivityContract.view view) {
        this.view = view;
        // TODO: 9/22/2017 get current area
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public void removeView() {
        compositeDisposable.clear();
        this.view = null;
    }

    /**
     * This method will make a call to the Eventbrite Api and retrieve a list of events uses RxJava
     */
    @Override
    public void rxJavaEventbrite() {

        apiService = new RetrofitHelper().getEventBriteService();
        compositeDisposable.add(apiService.queryEventList("")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Function<EventbriteEvents, List<Event>>() {
                    @Override
                    public List<Event> apply(@NonNull EventbriteEvents eventbriteEvents) throws Exception {

                        return eventbriteEvents.getEvents();
                    }
                })
                .subscribe(new Consumer<List<Event>>() {
                    @Override
                    public void accept(List<Event> events) throws Exception {

                        //get the singleton class that will hold the event list
                        PartyLabSingleTon partyLabSingleTon = PartyLabSingleTon.getInstance(context);
                        List<Party> parties = convertEventsToParties(events);
                        partyLabSingleTon.setEvents(parties);
                        view.eventsLoadedUpdateUI(parties);
                    }
                }));
    }

    @Override
    public void getLocaleRetrofit(String zip) {

        apiService = new RetrofitHelper().getLocaleService();
        retrofit2.Call<GeocodingProfile> getLocale = apiService.queryGetLocale("postal_code:"+zip);
        getLocale.enqueue(new Callback<GeocodingProfile>() {
            @Override
            public void onResponse(Call<GeocodingProfile> call, Response<GeocodingProfile> response) {
                String locale = response.body().getResults().get(0).getAddressComponents().get(1).getShortName();
                EventBus.getDefault().post(new LocalEvent(locale));
            }

            @Override
            public void onFailure(Call<GeocodingProfile> call, Throwable t) {

            }
        });
    }


    private List<Party> convertEventsToParties(List<Event> events) {

        //copy events from API into parties
        List<Party> parties = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Party p = new Party();
            p.setId(UUID.randomUUID().toString());
            p.setPartyName(events.get(i).getName().getText());
            p.setDescription(events.get(i).getDescription().getText());
            p.setStartTime(events.get(i).getStart().getUtc());
            p.setEndTime(events.get(i).getEnd().getUtc());
            p.setCapacity(events.get(i).getCapacity());
            p.setImageURL(events.get(i).getLogo().getUrl());
            p.setLiked(false);
            parties.add(p);

        }
        return parties;
    }
}

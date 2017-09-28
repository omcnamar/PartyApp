package com.olegsagenadatrytwo.partyapp.view.homeactivity;

import android.content.Context;

import com.olegsagenadatrytwo.partyapp.model.custompojos.Party;

import java.util.ArrayList;
import java.util.List;


public class PartyLabSingleTon {

    private static final String TAG = "PartyST";
    private Context context;
    private static PartyLabSingleTon partyLab;
    private static List<Party> events = new ArrayList<>();

    //private constructor
    private PartyLabSingleTon(Context context){
        this.context = context;
    }

    //this method will return an instance of PartyLabSingleton
    public static PartyLabSingleTon getInstance(Context context){
        //if partyLab is null than we create a new Instance and return it
        if(partyLab == null){
            return new PartyLabSingleTon(context);
        }
        //if partyLab is not null than we return the existing instance not new Instance
        else{

            return partyLab;
        }
    }

    //this method will return the list of events
    List<Party> getEvents() {
        return events;
    }

    //this method will set the list of events
    void setEvents(List<Party> events) {
       /* HomeActivityPresenter presenter = new HomeActivityPresenter();
        if(!events.isEmpty()){
            for (Party p: events) {
                if(p.getLatlng() == null && p.getAddress() != null){
                    p.setLatlng(getLatLng(p.getAddress()));
                    if(p.getLatlng() != null) {
                        presenter.setupParty(p, Constant.UPDATE_PARTY);
                    }
                }
            }
        }*/
        /*if(events.isEmpty()){
            Log.d(TAG + " DISTANCE", "getInstance: Is Empty" );
        } else {
            try {
                events = LocationUtilities.setPartyDistances(events, context);
                //events = LocationUtilities.setPartyLatLng(events, context);
                for(Party p : events){
                    //p.setLatlng(LocationUtilities.getPartyLocationLatLng(p));
                    Log.d(TAG + " DISTANCE", "getInstance: " + p.getDistance());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/
        this.events = events;
    }

/*
    private String getLatLng(String address) {
        final String[] latlng = new String[1];
        RetrofitHelper.ApiService apiService = new RetrofitHelper().getLocaleService();
        CompositeDisposable compositeDisposable = new CompositeDisposable();



        retrofit2.Call<GeocodingProfile> getLatLng = apiService.queryGetLatLng(address);
        getLatLng.enqueue(new Callback<GeocodingProfile>() {
            @Override
            public void onResponse(Call<GeocodingProfile> call, Response<GeocodingProfile> response) {
                Location location = response.body().getResults().get(0).getGeometry().getLocation();
                latlng[0] = location.getLat() + "," + location.getLng();
            }

            @Override
            public void onFailure(Call<GeocodingProfile> call, Throwable t) {

            }
        });
        return latlng[0];
    }
*/


}


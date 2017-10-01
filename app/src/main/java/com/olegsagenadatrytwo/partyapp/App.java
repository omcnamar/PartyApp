package com.olegsagenadatrytwo.partyapp;

import android.app.Application;

import com.olegsagenadatrytwo.partyapp.inject.view.ContextModule;
import com.olegsagenadatrytwo.partyapp.inject.view.home_activity.DaggerHomeActivityComponent;
import com.olegsagenadatrytwo.partyapp.inject.view.home_activity.HomeActivityComponent;
import com.olegsagenadatrytwo.partyapp.inject.view.party_fragment.DaggerPartyFragmentComponent;
import com.olegsagenadatrytwo.partyapp.inject.view.party_fragment.PartyFragmentComponent;
import com.olegsagenadatrytwo.partyapp.inject.view.shared_preference.SharedPreferencesModule;

/**
 * Created by Admin on 9/27/2017.
 */

public class App extends Application {

    private HomeActivityComponent component;
    private PartyFragmentComponent partyComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        component = DaggerHomeActivityComponent.builder()
                .sharedPreferencesModule(new SharedPreferencesModule())
                .contextModule(new ContextModule(getApplicationContext()))
                .build();

        partyComponent = DaggerPartyFragmentComponent.builder()
                .sharedPreferencesModule(new SharedPreferencesModule())
                .contextModule(new ContextModule(getApplicationContext()))
                .build();
    }

    public HomeActivityComponent getHomeActivityComponent() {
        return component;
    }

    public PartyFragmentComponent getPartyFragmentComponent() {
        return partyComponent;
    }
}

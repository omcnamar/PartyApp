package com.olegsagenadatrytwo.partyapp.inject.view.home_activity;

import com.olegsagenadatrytwo.partyapp.inject.view.ContextModule;
import com.olegsagenadatrytwo.partyapp.inject.view.shared_preference.SharedPreferencesModule;
import com.olegsagenadatrytwo.partyapp.view.homeactivity.HomeActivity;
import com.olegsagenadatrytwo.partyapp.view.homeactivity.PartyLabSingleTon;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by Admin on 9/13/2017.
 */
@Component(modules = {HomeActivityModule.class, SharedPreferencesModule.class, ContextModule.class })
@Singleton
public interface HomeActivityComponent {

    void inject(HomeActivity homeActivity);

    void inject(PartyLabSingleTon partyLabSingleTon);

}

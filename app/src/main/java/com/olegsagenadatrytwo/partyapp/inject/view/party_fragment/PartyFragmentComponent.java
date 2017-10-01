package com.olegsagenadatrytwo.partyapp.inject.view.party_fragment;

import com.olegsagenadatrytwo.partyapp.inject.view.ContextModule;
import com.olegsagenadatrytwo.partyapp.inject.view.shared_preference.SharedPreferencesModule;
import com.olegsagenadatrytwo.partyapp.view.homeactivity.PartyFragment;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by omcna on 10/1/2017.
 */
@Component(modules = { SharedPreferencesModule.class, ContextModule.class })
@Singleton
public interface PartyFragmentComponent {

    void inject(PartyFragment partyFragment);
}

package com.olegsagenadatrytwo.partyapp.inject.view.home_activity;

import com.olegsagenadatrytwo.partyapp.data.remote.FirebaseHelper;

import dagger.Module;
import dagger.Provides;

@Module
class HomeActivityPresenterModule {

    @Provides
    FirebaseHelper providesFirebaseHelper(){
        return new FirebaseHelper();
    }
}

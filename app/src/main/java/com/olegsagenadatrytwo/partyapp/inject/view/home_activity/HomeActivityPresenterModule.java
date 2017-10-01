package com.olegsagenadatrytwo.partyapp.inject.view.home_activity;

import com.olegsagenadatrytwo.partyapp.data.remote.FirebaseHelper;

import dagger.Module;
import dagger.Provides;

/**
 * Created by omcna on 10/1/2017.
 */
@Module
class HomeActivityPresenterModule {

    @Provides
    FirebaseHelper providesFirebaseHelper(){
        return new FirebaseHelper();
    }
}

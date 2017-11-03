package com.olegsagenadatrytwo.partyapp.inject.view.friends_activity;

import com.olegsagenadatrytwo.partyapp.data.remote.FirebaseHelper;

import dagger.Module;
import dagger.Provides;

@Module
public class FriendsActivityPresenterModule {

    @Provides
    FirebaseHelper providesFirebaseHelper(){
        return new FirebaseHelper();
    }

}

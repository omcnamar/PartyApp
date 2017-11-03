package com.olegsagenadatrytwo.partyapp.inject.view.friends_activity;


import com.olegsagenadatrytwo.partyapp.view.friends_activity.FriendsActivityPresenter;

import dagger.Module;
import dagger.Provides;

@Module
public class FriendsActivityModule {

    @Provides
    FriendsActivityPresenter ProvidesFriendsActivityPresenter() {
        return new FriendsActivityPresenter();
    }

}

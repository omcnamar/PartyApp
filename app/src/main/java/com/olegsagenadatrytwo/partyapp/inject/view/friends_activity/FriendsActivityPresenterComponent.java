package com.olegsagenadatrytwo.partyapp.inject.view.friends_activity;

import com.olegsagenadatrytwo.partyapp.view.friends_activity.FriendsActivityPresenter;

import dagger.Component;

@Component(modules = FriendsActivityPresenterModule.class)
public interface FriendsActivityPresenterComponent {
    void inject(FriendsActivityPresenter homeActivityPresenter);

}

package com.olegsagenadatrytwo.partyapp.inject.view.friends_activity;

import com.olegsagenadatrytwo.partyapp.view.friends_activity.FriendsActivity;

import dagger.Component;

@Component(modules = FriendsActivityModule.class)
public interface FriendsActivityComponent {
    void inject(FriendsActivity friendsActivity);

}

package com.olegsagenadatrytwo.partyapp.view.friends_activity;


import com.olegsagenadatrytwo.partyapp.data.remote.FirebaseHelper;
import com.olegsagenadatrytwo.partyapp.inject.view.friends_activity.DaggerFriendsActivityPresenterComponent;

import javax.inject.Inject;

public class FriendsActivityPresenter implements FriendsActivityContract.presenter {

    private FriendsActivityContract.view view;
    @Inject
    FirebaseHelper firebaseHelper;

    @Override
    public void attachView(FriendsActivityContract.view view) {
        this.view = view;
        DaggerFriendsActivityPresenterComponent.create().inject(this);
    }

    @Override
    public void removeView() {
        this.view = null;
    }

    @Override
    public void getAllUsers() {
        firebaseHelper.getAllUsers();
    }

    @Override
    public void getPendingFriendRequests() {
        firebaseHelper.getPendingFriendRequests();
    }

    @Override
    public void getMyFriends() {
        firebaseHelper.getMyFriends();
    }
}

package com.olegsagenadatrytwo.partyapp.view.friends_activity;

import com.olegsagenadatrytwo.partyapp.BasePresenter;
import com.olegsagenadatrytwo.partyapp.BaseView;

/**
 * Created by omcna on 11/2/2017.
 */

public interface FriendsActivityContract {

    interface view extends BaseView {
        void caregoryChanged(String category);
    }

    interface presenter extends BasePresenter<view> {
        void getAllUsers();
    }

}

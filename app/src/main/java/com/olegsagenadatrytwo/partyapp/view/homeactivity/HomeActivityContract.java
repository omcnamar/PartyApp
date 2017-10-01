package com.olegsagenadatrytwo.partyapp.view.homeactivity;

import com.olegsagenadatrytwo.partyapp.BasePresenter;
import com.olegsagenadatrytwo.partyapp.BaseView;

/**
 * Created by Admin on 9/13/2017.
 */

public interface HomeActivityContract {

    interface view extends BaseView {
        String getCurrentLocation();
    }

    interface presenter extends BasePresenter<view> {

        void getPartiesFromFireBase();
        void getLocaleRetrofit(String zip);
        void getCurrentLocale(String latlng);
    }
}

package com.olegsagenadatrytwo.partyapp.view.homeactivity;

/**
 * Created by Admin on 9/13/2017.
 */

public class HomeActivityPresenter implements HomeActivityContract.presenter {

    HomeActivityContract.view view;

    public void attachView(HomeActivityContract.view view) {
        this.view = view;
    }

    @Override
    public void removeView() {
        this.view = null;
    }

}

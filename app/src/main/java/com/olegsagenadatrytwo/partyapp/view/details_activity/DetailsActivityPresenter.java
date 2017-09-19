package com.olegsagenadatrytwo.partyapp.view.details_activity;

/**
 * Created by Admin on 9/17/2017.
 */

public class DetailsActivityPresenter implements DetailsActivityContract.presenter {

    DetailsActivityContract.view view;

    public void attachView(DetailsActivityContract.view view) {
        this.view = view;
    }

    @Override
    public void removeView() {
        this.view = null;
    }
}

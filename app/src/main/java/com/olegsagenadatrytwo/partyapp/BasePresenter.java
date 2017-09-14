package com.olegsagenadatrytwo.partyapp;

/**
 * Created by omcna on 9/5/2017.
 */

public interface BasePresenter<V extends BaseView> {

    void attachView(V view);
    void removeView();

}

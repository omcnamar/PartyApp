package com.olegsagenadatrytwo.partyapp;

import android.content.Context;

/**
 * Created by omcna on 9/5/2017.
 */

public interface BasePresenter<V extends BaseView> {

    void attachView(V view);
    void removeView();
    void setContext(Context context);
}

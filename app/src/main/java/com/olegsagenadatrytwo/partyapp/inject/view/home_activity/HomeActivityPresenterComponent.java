package com.olegsagenadatrytwo.partyapp.inject.view.home_activity;

import com.olegsagenadatrytwo.partyapp.view.homeactivity.HomeActivityPresenter;

import dagger.Component;

/**
 * Created by omcna on 10/1/2017.
 */
@Component(modules = HomeActivityPresenterModule.class)
public interface HomeActivityPresenterComponent {

    void inject(HomeActivityPresenter homeActivityPresenter);
}

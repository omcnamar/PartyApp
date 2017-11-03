package com.olegsagenadatrytwo.partyapp.inject.view.home_activity;

import com.olegsagenadatrytwo.partyapp.view.homeactivity.HomeActivityPresenter;

import dagger.Component;

@Component(modules = HomeActivityPresenterModule.class)
public interface HomeActivityPresenterComponent {

    void inject(HomeActivityPresenter homeActivityPresenter);
}

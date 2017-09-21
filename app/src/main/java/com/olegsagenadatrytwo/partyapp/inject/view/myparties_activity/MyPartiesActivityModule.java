package com.olegsagenadatrytwo.partyapp.inject.view.myparties_activity;

import com.olegsagenadatrytwo.partyapp.view.mypartiesactivity.MyPartiesActivityPresenter;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Admin on 9/13/2017.
 */
@Module
class MyPartiesActivityModule {

    @Provides
    MyPartiesActivityPresenter provideshMyPartiesActivityPresenter() {
        return new MyPartiesActivityPresenter();
    }

}

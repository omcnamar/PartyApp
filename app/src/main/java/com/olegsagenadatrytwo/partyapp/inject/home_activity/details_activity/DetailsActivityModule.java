package com.olegsagenadatrytwo.partyapp.inject.home_activity.details_activity;

import com.olegsagenadatrytwo.partyapp.view.homeactivity.HomeActivityPresenter;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Admin on 9/13/2017.
 */
@Module
class DetailsActivityModule {

    @Provides
    HomeActivityPresenter ProvideshomeActivityPresenter() {
        return new HomeActivityPresenter();
    }

}
